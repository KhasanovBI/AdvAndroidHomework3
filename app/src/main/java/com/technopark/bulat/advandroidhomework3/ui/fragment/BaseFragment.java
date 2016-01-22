package com.technopark.bulat.advandroidhomework3.ui.fragment;

import android.content.BroadcastReceiver;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.technopark.bulat.advandroidhomework3.R;
import com.technopark.bulat.advandroidhomework3.ui.activity.MainActivity;

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
}
