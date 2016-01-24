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
import android.widget.Toast;

import com.technopark.bulat.advandroidhomework3.R;
import com.technopark.bulat.advandroidhomework3.models.GlobalUserIds;
import com.technopark.bulat.advandroidhomework3.network.response.messages.AuthResponse;
import com.technopark.bulat.advandroidhomework3.network.response.messages.RegisterResponse;
import com.technopark.bulat.advandroidhomework3.network.response.messages.UserInfoResponse;
import com.technopark.bulat.advandroidhomework3.network.response.welcomeMessage.WelcomeResponse;
import com.technopark.bulat.advandroidhomework3.service.SendServiceHelper;
import com.technopark.bulat.advandroidhomework3.ui.activity.MainActivity;

import org.json.JSONObject;

public class RegisterFragment extends BaseFragment implements View.OnClickListener {
    private SharedPreferences mSharedPreferences;
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
            case "welcome": {
                new WelcomeResponse(jsonData);
                break;
            }
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
            case "auth": {
                AuthResponse authResponse = new AuthResponse(jsonData);
                int status = authResponse.getStatus();
                if (status == 0) {
                    String cid = authResponse.getCid();
                    String sid = authResponse.getSid();
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putString("cid", cid);
                    editor.putString("sid", sid);
                    editor.apply();
                    // TODO перенести методы в отдельный класс для стандартной обработки ответов
                    // Пока пусть код дублируется =(
                    // Получить данные для отображения профиля в drawer
                    SendServiceHelper.getInstance(getActivity()).requestUserInfo(cid, cid, sid);
                } else {
                    switch (status) {
                        case 7:
                            Fragment registerFragment = getActivity()
                                    .getSupportFragmentManager()
                                    .findFragmentById(R.id.fragment_register);
                            if (registerFragment == null) {
                                registerFragment = new RegisterFragment();
                            }
                            getActivity()
                                    .getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragments_container, registerFragment)
                                    .commit();
                            break;
                        default:
                            handleErrorFromServer(status);
                            Fragment loginFragment = getActivity()
                                    .getSupportFragmentManager()
                                    .findFragmentById(R.id.fragment_login);
                            if (loginFragment == null) {
                                loginFragment = new LoginFragment();
                            }
                            getActivity()
                                    .getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragments_container, loginFragment)
                                    .commit();
                            break;
                    }
                }
                break;
            }
            case "userinfo": {
                UserInfoResponse userInfoResponse = new UserInfoResponse(jsonData);
                int status = userInfoResponse.getStatus();
                if (status == 0) {

                    String userStatus = userInfoResponse.getUser().getStatus();
                    String nickname = userInfoResponse.getUser().getNick();

                    SharedPreferences.Editor sharedPreferencesEditor = mSharedPreferences.edit();
                    sharedPreferencesEditor.putString("status", userStatus);
                    sharedPreferencesEditor.putString("nickname", nickname);
                    sharedPreferencesEditor.apply();

                    DrawerLayout drawerLayout = getMainActivity().getDrawerLayout();
                    ((TextView) drawerLayout.findViewById(R.id.nickname)).setText(nickname);
                    ((TextView) drawerLayout.findViewById(R.id.status)).setText(userStatus);
                    Fragment contactListFragment = getActivity()
                            .getSupportFragmentManager()
                            .findFragmentById(R.id.fragment_contact_list);
                    if (contactListFragment == null) {
                        contactListFragment = new ContactListFragment();
                    }
                    getActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragments_container, contactListFragment)
                            .commit();
                } else {
                    handleErrorFromServer(userInfoResponse.getStatus());
                }
                break;
            }
        }
    }
}
