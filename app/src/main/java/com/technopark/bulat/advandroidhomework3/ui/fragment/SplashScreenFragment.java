package com.technopark.bulat.advandroidhomework3.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.technopark.bulat.advandroidhomework3.R;
import com.technopark.bulat.advandroidhomework3.asyncTasks.OnPreloadTaskDone;
import com.technopark.bulat.advandroidhomework3.asyncTasks.PreloadTask;
import com.technopark.bulat.advandroidhomework3.models.GlobalUserIds;
import com.technopark.bulat.advandroidhomework3.network.SocketResponseMessage;
import com.technopark.bulat.advandroidhomework3.network.request.messages.UserInfoRequest;
import com.technopark.bulat.advandroidhomework3.network.response.BaseResponse;
import com.technopark.bulat.advandroidhomework3.network.response.ErrorResponse;
import com.technopark.bulat.advandroidhomework3.network.response.messages.AuthResponse;
import com.technopark.bulat.advandroidhomework3.network.response.messages.UserInfoResponse;
import com.technopark.bulat.advandroidhomework3.network.socket.GlobalSocket;
import com.technopark.bulat.advandroidhomework3.service.SendService;
import com.technopark.bulat.advandroidhomework3.service.SendServiceHelper;
import com.technopark.bulat.advandroidhomework3.ui.activity.MainActivity;

public class SplashScreenFragment extends BaseFragment implements OnPreloadTaskDone {
    private static final String LOG_TAG = "SplashScreenFragment";
    private PreloadTask mPreloadTask;
    private SharedPreferences mSharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSharedPreferences = getActivity().getSharedPreferences("auth_settings", Context.MODE_PRIVATE);
        String login = mSharedPreferences.getString("login", null);
        String password = mSharedPreferences.getString("password", null);
        if (login != null && password != null) {
            SendServiceHelper.getInstance(getContext()).requestAuth(login, password);
        } else {
            if (mPreloadTask != null) {
                mPreloadTask.cancel(true);
            }
            mPreloadTask = new PreloadTask(SplashScreenFragment.this);
            mPreloadTask.execute();
        }
        return inflater.inflate(R.layout.fragment_splash_screen, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(SendService.BROADCAST_ACTION);
        responseReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SocketResponseMessage socketResponseMessage = (SocketResponseMessage) intent.getSerializableExtra(SendService.SOCKET_RESPONSE_MESSAGE_EXTRA);
                int connectionErrorCode = socketResponseMessage.getConnectionError();
                if (connectionErrorCode != -1) {
                    handleConnectionError(connectionErrorCode);
                }
            }
        };
        getActivity().registerReceiver(responseReceiver, filter);
    }

    @Override
    public void onPreloadTaskDone() {
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragments_container, new LoginFragment())
                .commit();
    }

    public void handleResponseMessage(BaseResponse rawResponse) {
        switch (rawResponse.getAction()) {
            case "auth":
                final AuthResponse authResponse = new AuthResponse(rawResponse.getJsonData());
                int status = authResponse.getStatus();
                if (status == 0) {
                    mSharedPreferences = getActivity().getSharedPreferences("user_settings", Context.MODE_PRIVATE);
                    String cid = mSharedPreferences.getString("cid", null);
                    String sid = mSharedPreferences.getString("sid", null);
                    GlobalUserIds.getInstance().cid = authResponse.getCid();
                    GlobalUserIds.getInstance().sid = authResponse.getSid();
                    GlobalSocket.getInstance().performAsyncRequest(
                            new UserInfoRequest(
                                    GlobalUserIds.getInstance().cid,
                                    GlobalUserIds.getInstance().sid,
                                    GlobalUserIds.getInstance().cid
                            )
                    );
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity().getBaseContext(), authResponse.getError(), Toast.LENGTH_LONG).show();
                        }
                    });
                    switch (status) {
                        case 7:
                            Fragment registerFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_register);
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
                            Fragment loginFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_login);
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
            case "userinfo":
                final UserInfoResponse userInfoResponse = new UserInfoResponse(rawResponse.getJsonData());
                if (userInfoResponse.getStatus() == 0) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            String userStatus = userInfoResponse.getUser().getStatus();
                            String nickname = userInfoResponse.getUser().getNickname();
                            SharedPreferences.Editor sharedPreferencesEditor = mSharedPreferences.edit();
                            sharedPreferencesEditor.putString("status", userStatus);
                            sharedPreferencesEditor.putString("nickname", nickname);
                            sharedPreferencesEditor.apply();
                            DrawerLayout drawerLayout = ((MainActivity) getActivity()).getDrawerLayout();
                            ((TextView) drawerLayout.findViewById(R.id.nickname)).setText(nickname);
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
            case "error":
                final ErrorResponse errorResponse = new ErrorResponse(rawResponse.getJsonData());
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getActivity().getBaseContext(), getResources().getStringArray(R.array.errors)[errorResponse.getErrorCode()], Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }
}