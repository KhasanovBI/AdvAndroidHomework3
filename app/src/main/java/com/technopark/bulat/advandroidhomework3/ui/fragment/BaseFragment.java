package com.technopark.bulat.advandroidhomework3.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.technopark.bulat.advandroidhomework3.R;
import com.technopark.bulat.advandroidhomework3.models.User;
import com.technopark.bulat.advandroidhomework3.network.SocketResponseMessage;
import com.technopark.bulat.advandroidhomework3.network.response.GeneralResponse;
import com.technopark.bulat.advandroidhomework3.network.response.messages.AuthResponse;
import com.technopark.bulat.advandroidhomework3.network.response.messages.UserInfoResponse;
import com.technopark.bulat.advandroidhomework3.network.response.welcomeMessage.WelcomeResponse;
import com.technopark.bulat.advandroidhomework3.service.SendService;
import com.technopark.bulat.advandroidhomework3.service.SendServiceHelper;
import com.technopark.bulat.advandroidhomework3.ui.activity.MainActivity;
import com.technopark.bulat.advandroidhomework3.util.Base64Translator;

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
                    GeneralResponse generalResponse = new GeneralResponse(
                            socketResponseMessage.getAction(),
                            socketResponseMessage.getStringResponse()
                    );
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
                loginWithSavedCredentials();
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
                } else {
                    switch (status) {
                        case 7: {
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
                        }
                        default: {
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
                }
                break;
            }
        }
    }

    protected boolean loginWithSavedCredentials() {
        mSharedPreferences = getActivity().getSharedPreferences("auth_settings", Context.MODE_PRIVATE);
        String login = mSharedPreferences.getString("login", null);
        String password = mSharedPreferences.getString("password", null);
        if (login != null && password != null) {
            SendServiceHelper.getInstance(getContext()).requestAuth(login, password);
            return true;
        }
        return false;
    }

    protected void handleUserInfo(UserInfoResponse userInfoResponse) {
        User user = userInfoResponse.getUser();
        String userStatus = user.getStatus();
        String nickname = user.getNick();
        String email = user.getEmail();
        String image = user.getPicture();
        String phone = user.getPhone();

        SharedPreferences.Editor sharedPreferencesEditor = mSharedPreferences.edit();
        sharedPreferencesEditor.putString("status", userStatus);
        sharedPreferencesEditor.putString("nickname", nickname);
        sharedPreferencesEditor.putString("image", image);
        sharedPreferencesEditor.putString("status", userStatus);
        sharedPreferencesEditor.putString("email", email);
        sharedPreferencesEditor.putString("phone", phone);
        sharedPreferencesEditor.apply();
        updateDrawer(nickname, userStatus, image);
    }

    protected void updateDrawer(String nickname, String userStatus, String image) {
        DrawerLayout drawerLayout = getMainActivity().getDrawerLayout();
        if (nickname != null) {
            ((TextView) drawerLayout.findViewById(R.id.nickname)).setText(nickname);
        }
        ((TextView) drawerLayout.findViewById(R.id.status)).setText(userStatus);
        ((ImageView) drawerLayout.findViewById(R.id.drawer_avatar)).setImageBitmap(Base64Translator.decodeBase64(image));
    }
}
