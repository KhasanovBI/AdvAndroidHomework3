package com.technopark.bulat.advandroidhomework2.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.technopark.bulat.advandroidhomework2.R;
import com.technopark.bulat.advandroidhomework2.models.GlobalUserIds;
import com.technopark.bulat.advandroidhomework2.network.request.messages.AuthRequest;
import com.technopark.bulat.advandroidhomework2.network.request.messages.RegisterRequest;
import com.technopark.bulat.advandroidhomework2.network.request.messages.UserInfoRequest;
import com.technopark.bulat.advandroidhomework2.network.response.RawResponse;
import com.technopark.bulat.advandroidhomework2.network.response.messages.AuthResponse;
import com.technopark.bulat.advandroidhomework2.network.response.messages.RegisterResponse;
import com.technopark.bulat.advandroidhomework2.network.response.messages.UserInfoResponse;
import com.technopark.bulat.advandroidhomework2.ui.activity.MainActivity;

public class RegisterFragment extends BaseFragment implements View.OnClickListener {
    private SharedPreferences mSharedPreferences;
    private EditText mLoginEditText;
    private EditText mPasswordEditText;
    private EditText mNicknameEditText;
    private String mLogin;
    private String mPassword;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);
        mLoginEditText = (EditText) rootView.findViewById(R.id.login_edit_text);
        mPasswordEditText = (EditText) rootView.findViewById(R.id.password_edit_text);
        mNicknameEditText = (EditText) rootView.findViewById(R.id.nickname_edit_text);

        mSharedPreferences = getActivity().getSharedPreferences("auth_settings", Context.MODE_PRIVATE);
        mLoginEditText.setText(mSharedPreferences.getString("login", ""));
        mPasswordEditText.setText(mSharedPreferences.getString("password", ""));
        mNicknameEditText.setText(mSharedPreferences.getString("nickname", ""));

        rootView.findViewById(R.id.register_button).setOnClickListener(this);
        return rootView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_button: {
                SharedPreferences.Editor sharedPreferencesEditor = mSharedPreferences.edit();
                mLogin = mLoginEditText.getText().toString();
                mPassword = mPasswordEditText.getText().toString();
                String nickname = mNicknameEditText.getText().toString();
                sharedPreferencesEditor.putString("login", mLogin);
                sharedPreferencesEditor.putString("password", mPassword);
                sharedPreferencesEditor.putString("nickname", nickname);
                sharedPreferencesEditor.apply();
                GlobalSocket.getInstance().performAsyncRequest(new RegisterRequest(mLogin, mPassword, nickname));
            }
        }
    }

    @Override
    public void handleResponseMessage(RawResponse rawResponse) {
        switch (rawResponse.getAction()) {
            case "register":
                final RegisterResponse registrationResponse = new RegisterResponse(rawResponse.getJsonData());
                if (registrationResponse.getStatus() == 0) {
                    GlobalSocket.getInstance().performAsyncRequest(new AuthRequest(mLogin, mPassword));
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity().getBaseContext(), registrationResponse.getError(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
                break;
            case "auth":
                final AuthResponse authResponse = new AuthResponse(rawResponse.getJsonData());
                int status = authResponse.getStatus();
                if (status == 0) {
                    GlobalUserIds.getInstance().cid = authResponse.getCid();
                    GlobalUserIds.getInstance().sid = authResponse.getSid();
                    GlobalSocket.getInstance().performAsyncRequest(
                            new UserInfoRequest(
                                    GlobalUserIds.getInstance().cid,
                                    GlobalUserIds.getInstance().cid,
                                    GlobalUserIds.getInstance().sid
                            )
                    );
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity().getBaseContext(), authResponse.getError(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
                break;
            case "userinfo":
                final UserInfoResponse userInfoResponse = new UserInfoResponse(rawResponse.getJsonData());
                if (userInfoResponse.getStatus() == 0) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            String userStatus = userInfoResponse.getUser().getStatus();
                            //String nickname = userInfoResponse.getUser().getNickname();
                            SharedPreferences.Editor sharedPreferencesEditor = mSharedPreferences.edit();
                            sharedPreferencesEditor.putString("status", userStatus);
                            //sharedPreferencesEditor.putString("nickname", nickname);
                            sharedPreferencesEditor.apply();
                            DrawerLayout drawerLayout = ((MainActivity) getActivity()).getDrawerLayout();
                            //((TextView) drawerLayout.findViewById(R.id.nickname)).setText(nickname);
                            ((TextView) drawerLayout.findViewById(R.id.status)).setText(userStatus);
                        }
                    });
                    Fragment channelListFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_channel_list);
                    if (channelListFragment == null) {
                        channelListFragment = new ContactListFragment();
                    }
                    getActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragments_container, channelListFragment)
                            .commit();
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity().getBaseContext(), userInfoResponse.getError(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
                break;
        }
    }
}
