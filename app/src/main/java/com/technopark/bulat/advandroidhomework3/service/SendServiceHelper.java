package com.technopark.bulat.advandroidhomework3.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by bulat on 21.01.16.
 */
public class SendServiceHelper {
    private static final String LOG_TAG = "MyServiceHelper";
    public static String ACTION_REQUEST_RESULT = "REQUEST_RESULT";
    public static String EXTRA_REQUEST_ID = "EXTRA_REQUEST_ID";
    public static String EXTRA_RESULT_CODE = "EXTRA_RESULT_CODE";

    private static final String REQUEST_ID = "REQUEST_ID";
    private AtomicInteger requestCounter = new AtomicInteger(0);
    private final Map<RequestType, Integer> pendingRequests = new HashMap<>();
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

    public boolean isRequestPending(RequestType requestType) {
        return pendingRequests.containsKey(requestType);
    }

    private int generateRequestId() {
        return requestCounter.getAndIncrement();
    }

    private void handleResponse(int resultCode, Bundle resultData) {
        Log.d(LOG_TAG, "handleResponse");
        Intent originalIntent = resultData.getParcelable(SendService.ORIGINAL_INTENT_EXTRA);
        if (originalIntent != null) {
            int requestId = originalIntent.getIntExtra(REQUEST_ID, -1);
            Log.d(LOG_TAG, "Remove request from map");
            pendingRequests.values().remove(requestId);

            Intent broadcastIntent = new Intent(ACTION_REQUEST_RESULT);
            broadcastIntent.putExtra(EXTRA_REQUEST_ID, requestId);
            broadcastIntent.putExtra(EXTRA_RESULT_CODE, resultCode);
            weakContext.get().sendBroadcast(broadcastIntent);
        }
    }

    private Intent prepareIntent(Context context, int requestId, RequestType requestType) {
        Log.d(LOG_TAG, "prepare intent");
        ResultReceiver serviceCallback = new ResultReceiver(null) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                handleResponse(resultCode, resultData);
            }
        };
        Intent intent = new Intent(context, SendService.class);
        intent.putExtra(REQUEST_ID, requestId);
        intent.putExtra(SendService.REQUEST_TYPE_EXTRA, requestType);
        intent.putExtra(SendService.SERVICE_CALLBACK_EXTRA, serviceCallback);
        return intent;
    }

    public int requestAuth(String login, String pass) {
        RequestType requestType = RequestType.AUTH;
        if (isRequestPending(requestType)) {
            return pendingRequests.get(requestType);
        }
        int requestId = generateRequestId();
        pendingRequests.put(requestType, requestId);

        Context context = weakContext.get();
        Intent intent = prepareIntent(context, requestId, requestType);
        intent.putExtra(SendService.AUTH_LOGIN_EXTRA, login);
        intent.putExtra(SendService.AUTH_PASS_EXTRA, pass);
        context.startService(intent);
        return requestId;
    }
}
