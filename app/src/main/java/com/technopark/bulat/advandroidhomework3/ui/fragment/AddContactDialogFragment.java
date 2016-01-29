package com.technopark.bulat.advandroidhomework3.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog.Builder;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.technopark.bulat.advandroidhomework3.R;
import com.technopark.bulat.advandroidhomework3.service.SendServiceHelper;

public class AddContactDialogFragment extends DialogFragment implements OnClickListener {
    private static final String LOG_TAG = "AddContactDialogFragment";
    private EditText mUIDEditText;
    private SharedPreferences mSharedPreferences;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_contact_add_dialog, null);

        mUIDEditText = (EditText) view.findViewById(R.id.contact_name);
        mSharedPreferences = getActivity().getSharedPreferences("auth_settings", Context.MODE_PRIVATE);

        Builder alertDialogBuilder = new Builder(getActivity())
                .setTitle(R.string.new_contact)
                .setPositiveButton(R.string.ok, this)
                .setNegativeButton(R.string.cancel, this)
                .setView(view);
        return alertDialogBuilder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                String uid = mUIDEditText.getText().toString();
                if (uid.length() != 0) {
                    String cid = mSharedPreferences.getString("cid", null);
                    String sid = mSharedPreferences.getString("sid", null);
                    Log.d(LOG_TAG, "requestAddContact");
                    SendServiceHelper.getInstance(getActivity()).requestAddContact(uid, cid, sid);
                }
                break;
        }
    }
}
