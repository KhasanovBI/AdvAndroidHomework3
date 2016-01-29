package com.technopark.bulat.advandroidhomework3.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.technopark.bulat.advandroidhomework3.R;
import com.technopark.bulat.advandroidhomework3.models.User;
import com.technopark.bulat.advandroidhomework3.network.response.messages.UserInfoResponse;
import com.technopark.bulat.advandroidhomework3.service.SendServiceHelper;
import com.technopark.bulat.advandroidhomework3.ui.activity.MainActivity;
import com.technopark.bulat.advandroidhomework3.util.Base64Translator;

import org.json.JSONObject;

public class ContactInfoFragment extends BaseFragment {
    private static final String LOG_TAG = "ContactInfoFragment";
    private User mUser;
    private ImageView mImageImageView;
    private TextView mNicknameTextView;
    private TextView mStatusTextView;
    private TextView mEmailTextView;
    private TextView mPhoneTextView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mUser = getArguments().getParcelable(User.descriptionKey);

        prepareView();

        View rootView = inflater.inflate(R.layout.fragment_contact_info, container, false);

        mImageImageView = (ImageView) rootView.findViewById(R.id.contact_info_image);
        mNicknameTextView = (TextView) rootView.findViewById(R.id.contact_info_nickname);
        mStatusTextView = (TextView) rootView.findViewById(R.id.contact_info_status);
        mEmailTextView = (TextView) rootView.findViewById(R.id.contact_info_email);
        mPhoneTextView = (TextView) rootView.findViewById(R.id.contact_info_phone);

        // Чтобы получить статус пользователя
        mSharedPreferences = getActivity().getSharedPreferences("auth_settings", Context.MODE_PRIVATE);
        String cid = mSharedPreferences.getString("cid", null);
        String sid = mSharedPreferences.getString("sid", null);
        Log.d(LOG_TAG, "requestUserInfo");
        SendServiceHelper.getInstance(getActivity()).requestUserInfo(mUser.getUid(), cid, sid);

        return rootView;
    }

    @Override
    protected void handleResponse(String action, JSONObject jsonData) {
        switch (action) {
            case "userinfo": {
                UserInfoResponse userInfoResponse = new UserInfoResponse(jsonData);
                int status = userInfoResponse.getStatus();
                if (status == 0) {
                    User user = userInfoResponse.getUser();
                    String stringPicture = user.getPicture();
                    if (stringPicture.length() > 0) {
                        mImageImageView.setImageBitmap(Base64Translator.decodeBase64(stringPicture));
                    }
                    mNicknameTextView.setText(String.format("Ник: %s", user.getNick()));
                    mStatusTextView.setText(String.format("Статус: %s", user.getStatus()));
                    mEmailTextView.setText(String.format("Email: %s", user.getEmail()));
                    mPhoneTextView.setText(String.format("Телефон: %s", user.getPhone()));
                } else {
                    handleErrorFromServer(userInfoResponse.getStatus());
                }
                break;
            }
            default:
                super.handleResponse(action, jsonData);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    private void prepareView() {
        MainActivity mainActivity = (MainActivity) getActivity();
        ActionBar actionBar = mainActivity.getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.info);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_white_24dp);
        actionBar.setIcon(R.drawable.ic_person_white_24dp);
    }
}
