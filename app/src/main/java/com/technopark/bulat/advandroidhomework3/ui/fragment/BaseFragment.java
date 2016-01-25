package com.technopark.bulat.advandroidhomework3.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.technopark.bulat.advandroidhomework3.R;
import com.technopark.bulat.advandroidhomework3.network.SocketResponseMessage;
import com.technopark.bulat.advandroidhomework3.network.response.GeneralResponse;
import com.technopark.bulat.advandroidhomework3.network.response.messages.AuthResponse;
import com.technopark.bulat.advandroidhomework3.network.response.messages.UserInfoResponse;
import com.technopark.bulat.advandroidhomework3.network.response.welcomeMessage.WelcomeResponse;
import com.technopark.bulat.advandroidhomework3.service.SendService;
import com.technopark.bulat.advandroidhomework3.service.SendServiceHelper;
import com.technopark.bulat.advandroidhomework3.ui.activity.MainActivity;

import org.json.JSONObject;

/**
 * Created by bulat on 21.01.16.
 */
public abstract class BaseFragment extends Fragment {
    private static final String LOG_TAG = "MyBaseFragment";
    protected BroadcastReceiver responseReceiver;
    protected SharedPreferences mSharedPreferences;

    protected MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    protected void handleErrorFromServer(int errorCode) {
        if (errorCode >= 0 && errorCode <= 8) {
            String[] errors = getMainActivity().getResources().getStringArray(R.array.errors);
            Toast.makeText(getContext(), errors[errorCode], Toast.LENGTH_SHORT).show();
        }
    }

    protected void handleConnectionError(int errorCode) {
        if (errorCode >= 0 && errorCode <= 4) {
            String[] errors = getMainActivity().getResources().getStringArray(R.array.connection_errors);
            Toast.makeText(getContext(), errors[errorCode], Toast.LENGTH_SHORT).show();
        }
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
                } else {
                    GeneralResponse generalResponse = new GeneralResponse(socketResponseMessage.getStringResponse());
                    handleResponse(generalResponse.getAction(), generalResponse.getJsonData());
                }
            }
        };
        getActivity().registerReceiver(responseReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (responseReceiver != null) {
            try {
                getActivity().unregisterReceiver(responseReceiver);
            } catch (IllegalArgumentException e) {
                Log.e(LOG_TAG, e.getLocalizedMessage(), e);
            }
        }
    }
    protected void handleResponse(String action, JSONObject jsonData) {
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
            default:
                throw new RuntimeException("Response not handled");
        }
    }
}
