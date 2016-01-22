package com.technopark.bulat.advandroidhomework3.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.technopark.bulat.advandroidhomework3.R;
import com.technopark.bulat.advandroidhomework3.models.GlobalUserIds;
import com.technopark.bulat.advandroidhomework3.network.request.messages.SetUserInfoRequest;
import com.technopark.bulat.advandroidhomework3.network.response.BaseResponse;
import com.technopark.bulat.advandroidhomework3.network.response.messages.SetUserInfoResponse;
import com.technopark.bulat.advandroidhomework3.network.socket.GlobalSocket;
import com.technopark.bulat.advandroidhomework3.network.socket.socketObserver.Observer;
import com.technopark.bulat.advandroidhomework3.ui.activity.MainActivity;

public class ChangeContactInfoFragment extends BaseFragment implements Observer, OnClickListener {
    private EditText mStatusEditText;
    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        prepareView();
        View rootView = inflater.inflate(R.layout.fragment_change_contact_info, container, false);
        mStatusEditText = (EditText) rootView.findViewById(R.id.contact_info_status);
        mSharedPreferences = getActivity().getSharedPreferences("auth_settings", Context.MODE_PRIVATE);
        String status = mSharedPreferences.getString("status", null);
        mStatusEditText.setText(status);
        rootView.findViewById(R.id.contact_info_save_button).setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        /* Subscribe to socket messages */
        GlobalSocket.getInstance().registerObserver(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        /* Unsubscribe from socket messages */
        GlobalSocket.getInstance().removeObserver(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ((MainActivity) getActivity()).getDrawerLayout().openDrawer(GravityCompat.START);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contact_info_save_button:
                GlobalSocket.getInstance().performAsyncRequest(
                        new SetUserInfoRequest(
                                GlobalUserIds.getInstance().cid,
                                GlobalUserIds.getInstance().sid,
                                mStatusEditText.getText().toString()
                        )
                );
                break;
        }
    }

    @Override
    public void handleResponseMessage(BaseResponse rawResponse) {
        if (rawResponse.getAction().equals("setuserinfo")) {
            final SetUserInfoResponse setUserInfoResponse = new SetUserInfoResponse(rawResponse.getJsonData());
            if (setUserInfoResponse.getStatus() == 0) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        String status = mStatusEditText.getText().toString();
                        mSharedPreferences.edit().putString("status", status).apply();
                        ((TextView) ((MainActivity) getActivity()).getDrawerLayout().findViewById(R.id.status)).setText(status);
                        Toast.makeText(getActivity().getBaseContext(), R.string.changes_saved, Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getActivity().getBaseContext(), setUserInfoResponse.getError(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    private void prepareView() {
        MainActivity mainActivity = (MainActivity) getActivity();
        ActionBar actionBar = mainActivity.getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.info);
        actionBar.setIcon(R.drawable.ic_person_white_24dp);
    }
}
