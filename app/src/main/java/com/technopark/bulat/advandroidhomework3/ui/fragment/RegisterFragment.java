package com.technopark.bulat.advandroidhomework3.ui.fragment;

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

import com.technopark.bulat.advandroidhomework3.R;
import com.technopark.bulat.advandroidhomework3.network.response.messages.AuthResponse;
import com.technopark.bulat.advandroidhomework3.network.response.messages.RegisterResponse;
import com.technopark.bulat.advandroidhomework3.network.response.messages.UserInfoResponse;
import com.technopark.bulat.advandroidhomework3.network.response.welcomeMessage.WelcomeResponse;
import com.technopark.bulat.advandroidhomework3.service.SendServiceHelper;

import org.json.JSONObject;

public class RegisterFragment extends BaseFragment implements View.OnClickListener {
    private EditText mLoginEditText;
    private EditText mPasswordEditText;
    private EditText mNicknameEditText;
    private String mLogin;
    private String mPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);
        mLoginEditText = (EditText) rootView.findViewById(R.id.login_edit_text);
        mPasswordEditText = (EditText) rootView.findViewById(R.id.password_edit_text);
        mNicknameEditText = (EditText) rootView.findViewById(R.id.nickname_edit_text);

        fillFieldsWithSavedCredentials();

        rootView.findViewById(R.id.register_button).setOnClickListener(this);
        return rootView;
    }

    private void fillFieldsWithSavedCredentials() {
        mSharedPreferences = getActivity().getSharedPreferences("auth_settings", Context.MODE_PRIVATE);
        mLoginEditText.setText(mSharedPreferences.getString("login", ""));
        mPasswordEditText.setText(mSharedPreferences.getString("password", ""));
        mNicknameEditText.setText(mSharedPreferences.getString("nickname", ""));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_button: {

                mLogin = mLoginEditText.getText().toString();
                mPassword = mPasswordEditText.getText().toString();
                String nickname = mNicknameEditText.getText().toString();

                SharedPreferences.Editor sharedPreferencesEditor = mSharedPreferences.edit();
                sharedPreferencesEditor.putString("login", mLogin);
                sharedPreferencesEditor.putString("password", mPassword);
                sharedPreferencesEditor.apply();

                SendServiceHelper.getInstance(getActivity()).requestRegister(mLogin, mPassword, nickname);
            }
        }
    }

    @Override
    protected void handleResponse(String action, JSONObject jsonData) {
        switch (action) {
            case "register": {
                RegisterResponse registrationResponse = new RegisterResponse(jsonData);
                int status = registrationResponse.getStatus();
                if (status == 0) {
                    SendServiceHelper.getInstance(getActivity()).requestAuth(mLogin, mPassword);
                } else {
                    handleErrorFromServer(status);
                }
                break;
            }
            default:
                super.handleResponse(action, jsonData);
        }
    }
}
