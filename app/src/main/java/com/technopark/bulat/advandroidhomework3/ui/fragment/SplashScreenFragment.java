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
}