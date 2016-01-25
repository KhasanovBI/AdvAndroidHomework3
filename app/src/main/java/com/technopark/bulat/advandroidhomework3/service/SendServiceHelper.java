package com.technopark.bulat.advandroidhomework3.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import com.technopark.bulat.advandroidhomework3.models.User;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by bulat on 21.01.16.
 */
public class SendServiceHelper {
    private static final String LOG_TAG = "MyServiceHelper";
    private WeakReference<Context> weakContext;
    public static volatile SendServiceHelper sendServiceHelperInstance;

    public static SendServiceHelper getInstance(Context context) {
        SendServiceHelper localInstance = sendServiceHelperInstance;
        if (localInstance == null) {
            synchronized (SendServiceHelper.class) {
                localInstance = sendServiceHelperInstance;
                if (localInstance == null) {
                    sendServiceHelperInstance = localInstance = new SendServiceHelper(context);
                }
            }
        }
        return localInstance;
    }

    private SendServiceHelper(Context context) {
        weakContext = new WeakReference<>(context.getApplicationContext());
    }

    private Intent prepareIntent(Context context, RequestType requestType) {
        Log.d(LOG_TAG, "Prepare intent");
        Intent intent = new Intent(context, SendService.class);
        intent.putExtra(SendService.REQUEST_TYPE_EXTRA, requestType);
        return intent;
    }

    public void requestAuth(String login, String pass) {
        RequestType requestType = RequestType.AUTH;
        Context context = weakContext.get();
        Intent intent = prepareIntent(context, requestType);
        intent.putExtra(SendService.AUTH_LOGIN_EXTRA, login);
        intent.putExtra(SendService.AUTH_PASS_EXTRA, pass);
        context.startService(intent);
    }

    public void requestUserInfo(String uid, String cid, String sid) {
        RequestType requestType = RequestType.USER_INFO;
        Context context = weakContext.get();
        Intent intent = prepareIntent(context, requestType);
        intent.putExtra(SendService.USER_INFO_USER_ID_EXTRA, uid);
        intent.putExtra(SendService.USER_INFO_CID_EXTRA, cid);
        intent.putExtra(SendService.USER_INFO_SID_EXTRA, sid);
        context.startService(intent);
    }

    public void requestRegister(String login, String pass, String nick) {
        RequestType requestType = RequestType.REGISTER;
        Context context = weakContext.get();
        Intent intent = prepareIntent(context, requestType);
        intent.putExtra(SendService.REGISTER_LOGIN_EXTRA, login);
        intent.putExtra(SendService.REGISTER_PASS_EXTRA, pass);
        intent.putExtra(SendService.REGISTER_NICK_EXTRA, nick);
        context.startService(intent);
    }

    public void requestContactList(String cid, String sid) {
        RequestType requestType = RequestType.CONTACT_LIST;
        Context context = weakContext.get();
        Intent intent = prepareIntent(context, requestType);
        intent.putExtra(SendService.CONTACT_LIST_CID_EXTRA, cid);
        intent.putExtra(SendService.CONTACT_LIST_SID_EXTRA, sid);
        context.startService(intent);
    }

    public void requestImport(ArrayList<User> users) {
        RequestType requestType = RequestType.IMPORT;
        Context context = weakContext.get();
        Intent intent = prepareIntent(context, requestType);
        intent.putParcelableArrayListExtra(SendService.IMPORT_CONTACTS_EXTRA, users);
        context.startService(intent);
    }

    public void requestAddContact(String uid, String cid, String sid) {
        RequestType requestType = RequestType.ADD_CONTACT;
        Context context = weakContext.get();
        Intent intent = prepareIntent(context, requestType);
        intent.putExtra(SendService.ADD_CONTACT_USER_ID_EXTRA, uid);
        intent.putExtra(SendService.ADD_CONTACT_CID_EXTRA, cid);
        intent.putExtra(SendService.ADD_CONTACT_SID_EXTRA, sid);
        context.startService(intent);
    }
}
