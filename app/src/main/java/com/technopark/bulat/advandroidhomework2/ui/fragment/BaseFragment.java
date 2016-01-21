package com.technopark.bulat.advandroidhomework2.ui.fragment;

import android.content.BroadcastReceiver;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.technopark.bulat.advandroidhomework2.R;
import com.technopark.bulat.advandroidhomework2.service.RequestType;
import com.technopark.bulat.advandroidhomework2.ui.activity.MainActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bulat on 21.01.16.
 */
public abstract class BaseFragment extends Fragment {
    private static final String LOG_TAG = "MyBaseFragment";
    protected BroadcastReceiver requestReceiver;
    protected Map<Integer, RequestType> requestsIdMap = new HashMap<>();

    protected MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    protected void handleError(int errorCode) {
        String[] errors = getMainActivity().getResources().getStringArray(R.array.errors);
        if (errorCode >= 0 && errorCode <= 8) {
            Toast.makeText(getContext(), errors[errorCode], Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (requestReceiver != null) {
            try {
                getActivity().unregisterReceiver(requestReceiver);
            } catch (IllegalArgumentException e) {
                Log.e(LOG_TAG, e.getLocalizedMessage(), e);
            }
        }
    }
}
