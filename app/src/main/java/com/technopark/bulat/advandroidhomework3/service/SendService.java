package com.technopark.bulat.advandroidhomework3.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import com.technopark.bulat.advandroidhomework3.models.Attach;
import com.technopark.bulat.advandroidhomework3.models.User;
import com.technopark.bulat.advandroidhomework3.network.SocketCallback;
import com.technopark.bulat.advandroidhomework3.network.SocketClient;
import com.technopark.bulat.advandroidhomework3.network.SocketResponseMessage;
import com.technopark.bulat.advandroidhomework3.network.request.RequestMessage;
import com.technopark.bulat.advandroidhomework3.network.request.messages.AddContactRequest;
import com.technopark.bulat.advandroidhomework3.network.request.messages.AuthRequest;
import com.technopark.bulat.advandroidhomework3.network.request.messages.ContactListRequest;
import com.technopark.bulat.advandroidhomework3.network.request.messages.DelContactRequest;
import com.technopark.bulat.advandroidhomework3.network.request.messages.ImportRequest;
import com.technopark.bulat.advandroidhomework3.network.request.messages.MessageRequest;
import com.technopark.bulat.advandroidhomework3.network.request.messages.RegisterRequest;
import com.technopark.bulat.advandroidhomework3.network.request.messages.SetUserInfoRequest;
import com.technopark.bulat.advandroidhomework3.network.request.messages.UserInfoRequest;

import java.util.List;

public class SendService extends IntentService {
    private static final String LOG_TAG = "MyService";
    public static final String REQUEST_TYPE_EXTRA = "REQUEST_TYPE_EXTRA";
    public static final String SERVICE_CALLBACK_EXTRA = "SERVICE_CALLBACK_EXTRA";

    public static final String REGISTER_LOGIN_EXTRA = "REGISTER_LOGIN_EXTRA";
    public static final String REGISTER_PASS_EXTRA = "REGISTER_PASS_EXTRA";
    public static final String REGISTER_NICK_EXTRA = "REGISTER_NICK_EXTRA";

    public static final String AUTH_LOGIN_EXTRA = "AUTH_LOGIN_EXTRA";
    public static final String AUTH_PASS_EXTRA = "AUTH_PASS_EXTRA";

    private static final String USER_INFO_USER_ID_EXTRA = "USER_INFO_USER_ID_EXTRA";

    private static final String USER_INFO_SID_EXTRA = "USER_INFO_SID_EXTRA";
    private static final String USER_INFO_CID_EXTRA = "USER_INFO_CID_EXTRA";

    private static final String SET_USER_INFO_CID_EXTRA = "SET_USER_INFO_CID_EXTRA";
    private static final String SET_USER_INFO_SID_EXTRA = "SET_USER_INFO_SID_EXTRA";
    private static final String SET_USER_INFO_USER_EXTRA = "SET_USER_INFO_USER_EXTRA";

    private static final String ADD_CONTACT_CID_EXTRA = "ADD_CONTACT_CID_EXTRA";
    private static final String ADD_CONTACT_SID_EXTRA = "ADD_CONTACT_SID_EXTRA";
    private static final String ADD_CONTACT_USER_ID_EXTRA = "ADD_CONTACT_USER_ID_EXTRA";

    private static final String DEL_CONTACT_CID_EXTRA = "DEL_CONTACT_CID_EXTRA";
    private static final String DEL_CONTACT_SID_EXTRA = "DEL_CONTACT_SID_EXTRA";
    private static final String DEL_CONTACT_USER_ID_EXTRA = "DEL_CONTACT_USER_ID_EXTRA";

    private static final String CONTACT_LIST_CID_EXTRA = "CONTACT_LIST_CID_EXTRA";
    private static final String CONTACT_LIST_SID_EXTRA = "CONTACT_LIST_SID_EXTRA";

    private static final String IMPORT_CONTACTS_EXTRA = "IMPORT_CONTACTS_EXTRA";

    public static final String ORIGINAL_INTENT_EXTRA = "ORIGINAL_INTENT_EXTRA";

    private static final String MESSAGE_CID_EXTRA = "MESSAGE_CID_EXTRA";
    private static final String MESSAGE_SID_EXTRA = "MESSAGE_SID_EXTRA";
    private static final String MESSAGE_USER_ID_EXTRA = "MESSAGE_USER_ID_EXTRA";
    private static final String MESSAGE_BODY_EXTRA = "MESSAGE_BODY_EXTRA";
    private static final String MESSAGE_ATTACH_EXTRA = "MESSAGE_ATTACH_EXTRA";

    public static final String BROADCAST_ACTION = "com.technopark.bulat.advandroidhomework3.BROADCAST_ACTION";

    public static final String SOCKET_RESPONSE_MESSAGE_EXTRA = "SOCKET_RESPONSE_MESSAGE_EXTRA";

    private static SocketClient socketClient;
    private Intent mOriginalRequestIntent;
    private ResultReceiver mServiceCallback;

    public SendService() {
        super("SendService");
        socketClient = new SocketClient(makeSocketCallback());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOG_TAG, "Handle intent");
        mOriginalRequestIntent = intent;
        RequestType requestType = (RequestType) mOriginalRequestIntent.getSerializableExtra(REQUEST_TYPE_EXTRA);
        mServiceCallback = mOriginalRequestIntent.getParcelableExtra(SERVICE_CALLBACK_EXTRA);
        RequestMessage requestMessage = null;
        switch (requestType) {
            case REGISTER: {
                String login = mOriginalRequestIntent.getStringExtra(REGISTER_LOGIN_EXTRA);
                String pass = mOriginalRequestIntent.getStringExtra(REGISTER_PASS_EXTRA);
                String nick = mOriginalRequestIntent.getStringExtra(REGISTER_NICK_EXTRA);
                requestMessage = new RegisterRequest(login, pass, nick);
                break;
            }
            case AUTH: {
                String login = mOriginalRequestIntent.getStringExtra(AUTH_LOGIN_EXTRA);
                String pass = mOriginalRequestIntent.getStringExtra(AUTH_PASS_EXTRA);
                requestMessage = new AuthRequest(login, pass);
                break;
            }
            case USER_INFO: {
                String cid = mOriginalRequestIntent.getStringExtra(USER_INFO_CID_EXTRA);
                String sid = mOriginalRequestIntent.getStringExtra(USER_INFO_SID_EXTRA);
                String userId = mOriginalRequestIntent.getStringExtra(USER_INFO_USER_ID_EXTRA);
                requestMessage = new UserInfoRequest(cid, sid, userId);
                break;
            }
            case SET_USER_INFO: {
                String cid = mOriginalRequestIntent.getStringExtra(SET_USER_INFO_CID_EXTRA);
                String sid = mOriginalRequestIntent.getStringExtra(SET_USER_INFO_SID_EXTRA);
                User user = (User) mOriginalRequestIntent.getSerializableExtra(SET_USER_INFO_USER_EXTRA);
                requestMessage = new SetUserInfoRequest(cid, sid, user);
                break;
            }
            case ADD_CONTACT: {
                String cid = mOriginalRequestIntent.getStringExtra(ADD_CONTACT_CID_EXTRA);
                String sid = mOriginalRequestIntent.getStringExtra(ADD_CONTACT_SID_EXTRA);
                String userId = mOriginalRequestIntent.getStringExtra(ADD_CONTACT_USER_ID_EXTRA);
                requestMessage = new AddContactRequest(cid, sid, userId);
                break;
            }
            case DEL_CONTACT: {
                String cid = mOriginalRequestIntent.getStringExtra(DEL_CONTACT_CID_EXTRA);
                String sid = mOriginalRequestIntent.getStringExtra(DEL_CONTACT_SID_EXTRA);
                String userId = mOriginalRequestIntent.getStringExtra(DEL_CONTACT_USER_ID_EXTRA);
                requestMessage = new DelContactRequest(cid, sid, userId);
                break;
            }
            case CONTACT_LIST: {
                String cid = mOriginalRequestIntent.getStringExtra(CONTACT_LIST_CID_EXTRA);
                String sid = mOriginalRequestIntent.getStringExtra(CONTACT_LIST_SID_EXTRA);
                requestMessage = new ContactListRequest(cid, sid);
                break;
            }
            case IMPORT: {
                List<User> contacts = mOriginalRequestIntent.getParcelableArrayListExtra(IMPORT_CONTACTS_EXTRA);
                requestMessage = new ImportRequest(contacts);
                break;
            }
            case MESSAGE: {
                String cid = mOriginalRequestIntent.getStringExtra(MESSAGE_CID_EXTRA);
                String sid = mOriginalRequestIntent.getStringExtra(MESSAGE_SID_EXTRA);
                String userId = mOriginalRequestIntent.getStringExtra(MESSAGE_USER_ID_EXTRA);
                String body = mOriginalRequestIntent.getStringExtra(MESSAGE_BODY_EXTRA);
                Attach attach = (Attach) mOriginalRequestIntent.getSerializableExtra(MESSAGE_ATTACH_EXTRA);
                requestMessage = new MessageRequest(cid, sid, userId, body, attach);
                break;
            }
        }
        socketClient.performRequest(requestMessage);
    }

    private SocketCallback makeSocketCallback() {
        Log.d(LOG_TAG, "makeSocketCallback");
        return new SocketCallback() {
            @Override
            public void send(SocketResponseMessage socketResponseMessage) {
                Intent intent = new Intent(BROADCAST_ACTION);
                intent.putExtra(SOCKET_RESPONSE_MESSAGE_EXTRA, socketResponseMessage);
                sendBroadcast(intent);
            }
        };
    }

    @Override
    public void onDestroy() {
        socketClient.stopResponseGetter();
        super.onDestroy();
    }
}
