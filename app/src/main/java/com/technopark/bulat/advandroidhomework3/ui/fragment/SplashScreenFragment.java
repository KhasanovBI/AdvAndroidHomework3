package com.technopark.bulat.advandroidhomework3.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.technopark.bulat.advandroidhomework3.R;
import com.technopark.bulat.advandroidhomework3.application.MyApplication;
import com.technopark.bulat.advandroidhomework3.asyncTasks.OnPreloadTaskDone;
import com.technopark.bulat.advandroidhomework3.asyncTasks.PreloadTask;
import com.technopark.bulat.advandroidhomework3.network.response.messages.AuthResponse;
import com.technopark.bulat.advandroidhomework3.network.response.messages.UserInfoResponse;
import com.technopark.bulat.advandroidhomework3.service.SendServiceHelper;

import org.json.JSONObject;

public class SplashScreenFragment extends BaseFragment implements OnPreloadTaskDone {
    private static final String LOG_TAG = "MySplashScreenFragment";
    private PreloadTask mPreloadTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (MyApplication.isFirstConnect()) {
            SendServiceHelper.getInstance(getActivity()).requestConnect();
            MyApplication.setIsFirstConnect(false);
        } else {
            loginWithSavedCredentials();
        }
        if (mPreloadTask != null) {
            mPreloadTask.cancel(true);
        }
        mPreloadTask = new PreloadTask(this);
        mPreloadTask.execute();
        return inflater.inflate(R.layout.fragment_splash_screen, container, false);
    }

    @Override
    public void onPreloadTaskDone() {
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragments_container, new LoginFragment())
                .commit();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPreloadTask != null) {
            mPreloadTask.cancel(true);
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