package com.technopark.bulat.advandroidhomework3.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.technopark.bulat.advandroidhomework3.R;
import com.technopark.bulat.advandroidhomework3.network.SocketResponseMessage;
import com.technopark.bulat.advandroidhomework3.network.response.GeneralResponse;
import com.technopark.bulat.advandroidhomework3.network.response.welcomeMessage.WelcomeResponse;
import com.technopark.bulat.advandroidhomework3.service.SendService;
import com.technopark.bulat.advandroidhomework3.ui.activity.MainActivity;

import org.json.JSONObject;

/**
 * Created by bulat on 21.01.16.
 */
public abstract class BaseFragment extends Fragment {
    private static final String LOG_TAG = "MyBaseFragment";
    protected BroadcastReceiver responseReceiver;
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
    protected abstract void handleResponse(String action, JSONObject jsonData);

    protected boolean baseHandleResponse(String action, JSONObject jsonData) {
        switch (action) {
            case "welcome":
                new WelcomeResponse(jsonData);
                break;
            default:
                return false;
        }
        return true;
    }
}
