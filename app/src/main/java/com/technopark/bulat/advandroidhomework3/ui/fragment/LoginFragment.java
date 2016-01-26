package com.technopark.bulat.advandroidhomework3.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.technopark.bulat.advandroidhomework3.R;
import com.technopark.bulat.advandroidhomework3.network.response.messages.AuthResponse;
import com.technopark.bulat.advandroidhomework3.network.response.messages.UserInfoResponse;
import com.technopark.bulat.advandroidhomework3.service.SendServiceHelper;

import org.json.JSONObject;

public class LoginFragment extends BaseFragment implements OnClickListener {
    private EditText mLoginEditText;
    private EditText mPasswordEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        mLoginEditText = (EditText) rootView.findViewById(R.id.login_edit_text);
        mPasswordEditText = (EditText) rootView.findViewById(R.id.password_edit_text);

        mSharedPreferences = getActivity().getSharedPreferences("auth_settings", Context.MODE_PRIVATE);
        mLoginEditText.setText(mSharedPreferences.getString("login", ""));
        mPasswordEditText.setText(mSharedPreferences.getString("password", ""));

        rootView.findViewById(R.id.login_button).setOnClickListener(this);
        rootView.findViewById(R.id.register_button).setOnClickListener(this);
        return rootView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button: {
                Editor sharedPreferencesEditor = mSharedPreferences.edit();
                String login = mLoginEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();
                sharedPreferencesEditor.putString("login", login);
                sharedPreferencesEditor.putString("password", password);
                sharedPreferencesEditor.apply();
                SendServiceHelper.getInstance(getActivity()).requestAuth(login, password);
                break;
            }
            case R.id.register_button: {
                Fragment registerFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_register);
                if (registerFragment == null) {
                    registerFragment = new RegisterFragment();
                }
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragments_container, registerFragment)
                        .commit();
                break;
            }
        }
    }

    @Override
    protected void handleResponse(String action, JSONObject jsonData) {
        super.handleResponse(action, jsonData);
        switch (action) {
            case "auth": {
                AuthResponse authResponse = new AuthResponse(jsonData);
                if (authResponse.getStatus() == 0) {
                    // Получить данные для отображения профиля в drawer
                    String cid = mSharedPreferences.getString("cid", null);
                    String sid = mSharedPreferences.getString("sid", null);
                    SendServiceHelper.getInstance(getActivity()).requestUserInfo(cid, cid, sid);
                }
                break;
            }
            case "userinfo": {
                UserInfoResponse userInfoResponse = new UserInfoResponse(jsonData);
                int status = userInfoResponse.getStatus();
                if (status == 0) {
                    handleUserInfo(userInfoResponse);
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
