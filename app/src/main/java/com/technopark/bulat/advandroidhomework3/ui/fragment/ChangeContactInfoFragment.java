package com.technopark.bulat.advandroidhomework3.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.technopark.bulat.advandroidhomework3.R;
import com.technopark.bulat.advandroidhomework3.models.User;
import com.technopark.bulat.advandroidhomework3.network.response.messages.SetUserInfoResponse;
import com.technopark.bulat.advandroidhomework3.service.SendServiceHelper;
import com.technopark.bulat.advandroidhomework3.ui.activity.MainActivity;
import com.technopark.bulat.advandroidhomework3.util.Base64Translator;

import org.json.JSONObject;

import java.io.IOException;

public class ChangeContactInfoFragment extends BaseFragment implements OnClickListener {
    private static final int REQUEST_CODE_SELECT_IMAGE = 100;
    private EditText mStatusEditText;
    private EditText mEmailEditText;
    private EditText mPhoneEditText;
    private ImageView mAvatarImageView;
    private SharedPreferences mSharedPreferences;

    private String image;
    private String userStatus;
    private String email;
    private String phone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SELECT_IMAGE:
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    Uri imageUri = data.getData();
                    mAvatarImageView.setImageURI(imageUri);
                }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        prepareView();
        View rootView = inflater.inflate(R.layout.fragment_change_contact_info, container, false);

        mStatusEditText = (EditText) rootView.findViewById(R.id.contact_info_status);
        mEmailEditText = (EditText) rootView.findViewById(R.id.contact_email);
        mPhoneEditText = (EditText) rootView.findViewById(R.id.contact_phone);
        mAvatarImageView = (ImageView) rootView.findViewById(R.id.contact_avatar);

        mSharedPreferences = getActivity().getSharedPreferences("auth_settings", Context.MODE_PRIVATE);

        String image = mSharedPreferences.getString("image", "");
        String status = mSharedPreferences.getString("status", null);
        String email = mSharedPreferences.getString("email", null);
        String phone = mSharedPreferences.getString("phone", null);

        mStatusEditText.setText(status);
        mEmailEditText.setText(email);
        mPhoneEditText.setText(phone);
        if (image.length() > 0) {
            mAvatarImageView.setImageBitmap(Base64Translator.decodeBase64(image));
        }

        rootView.findViewById(R.id.contact_info_save_button).setOnClickListener(this);
        mAvatarImageView.setOnClickListener(this);
        return rootView;
    }

    @Override
    protected void handleResponse(String action, JSONObject jsonData) {
        super.handleResponse(action, jsonData);
        switch (action) {
            case "setuserinfo":
                SetUserInfoResponse setUserInfoResponse = new SetUserInfoResponse(jsonData);
                int status = setUserInfoResponse.getStatus();
                if (status == 0) {
                    String userStatus = mStatusEditText.getText().toString();
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putString("image", image);
                    editor.putString("status", userStatus);
                    editor.putString("email", email);
                    editor.putString("phone", phone);
                    updateDrawer(null, userStatus, image);
                    editor.apply();
                    ((TextView) ((MainActivity) getActivity()).getDrawerLayout().findViewById(R.id.status)).setText(userStatus);
                    Toast.makeText(getActivity().getBaseContext(), R.string.changes_saved, Toast.LENGTH_LONG).show();
                } else {
                    handleConnectionError(status);
                }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ((MainActivity) getActivity()).getDrawerLayout().openDrawer(GravityCompat.START);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contact_info_save_button: {
                image = Base64Translator.encodeToBase64(
                        ((BitmapDrawable) mAvatarImageView.getDrawable()).getBitmap()
                );
                userStatus = mStatusEditText.getText().toString();
                email = mEmailEditText.getText().toString();
                phone = mPhoneEditText.getText().toString();

                User user = new User();
                user.setPicture(image);
                user.setStatus(userStatus);
                user.setEmail(email);
                user.setPhone(phone);

                String cid = mSharedPreferences.getString("cid", null);
                String sid = mSharedPreferences.getString("sid", null);
                SendServiceHelper.getInstance(getActivity()).requestSetUserInfo(cid, sid, user);
                break;
            }
            case R.id.contact_avatar: {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getActivity().getString(R.string.choose_image)), REQUEST_CODE_SELECT_IMAGE);
                break;
            }
        }
    }

    private void prepareView() {
        MainActivity mainActivity = (MainActivity) getActivity();
        ActionBar actionBar = mainActivity.getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.info);
        actionBar.setIcon(R.drawable.ic_person_white_24dp);
    }
}
