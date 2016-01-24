package com.technopark.bulat.advandroidhomework3.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.technopark.bulat.advandroidhomework3.R;
import com.technopark.bulat.advandroidhomework3.asyncTasks.OnPreloadTaskDone;
import com.technopark.bulat.advandroidhomework3.asyncTasks.PreloadTask;
import com.technopark.bulat.advandroidhomework3.network.response.messages.AuthResponse;
import com.technopark.bulat.advandroidhomework3.network.response.messages.UserInfoResponse;
import com.technopark.bulat.advandroidhomework3.network.response.welcomeMessage.WelcomeResponse;
import com.technopark.bulat.advandroidhomework3.service.SendServiceHelper;

import org.json.JSONObject;

public class SplashScreenFragment extends BaseFragment implements OnPreloadTaskDone {
    private static final String LOG_TAG = "MySplashScreenFragment";
    private PreloadTask mPreloadTask;
    private SharedPreferences mSharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!loginWithSavedCredentials()) {
            if (mPreloadTask != null) {
                mPreloadTask.cancel(true);
            }
            mPreloadTask = new PreloadTask(this);
            mPreloadTask.execute();
        }
        return inflater.inflate(R.layout.fragment_splash_screen, container, false);
    }

    private boolean loginWithSavedCredentials() {
        mSharedPreferences = getActivity().getSharedPreferences("auth_settings", Context.MODE_PRIVATE);
        String login = mSharedPreferences.getString("login", null);
        String password = mSharedPreferences.getString("password", null);
        if (login != null && password != null) {
            SendServiceHelper.getInstance(getContext()).requestAuth(login, password);
            return true;
        }
        return false;
    }

    @Override
    public void onPreloadTaskDone() {
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragments_container, new LoginFragment())
                .commit();
    }

    public void handleResponse(String action, JSONObject jsonData) {
        switch (action) {
            case "welcome": {
                new WelcomeResponse(jsonData);
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