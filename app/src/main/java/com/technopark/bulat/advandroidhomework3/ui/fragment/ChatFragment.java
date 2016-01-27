package com.technopark.bulat.advandroidhomework3.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.technopark.bulat.advandroidhomework3.R;
import com.technopark.bulat.advandroidhomework3.adapters.ChatAdapter;
import com.technopark.bulat.advandroidhomework3.models.Message;
import com.technopark.bulat.advandroidhomework3.models.User;
import com.technopark.bulat.advandroidhomework3.network.response.GeneralResponse;
import com.technopark.bulat.advandroidhomework3.network.response.events.MessageEventResponse;
import com.technopark.bulat.advandroidhomework3.network.response.messages.ImportResponse;
import com.technopark.bulat.advandroidhomework3.network.response.messages.MessageResponse;

import com.technopark.bulat.advandroidhomework3.service.SendServiceHelper;
import com.technopark.bulat.advandroidhomework3.ui.activity.MainActivity;

import org.json.JSONObject;

import java.util.List;

public class ChatFragment extends BaseFragment implements OnClickListener, ChatAdapter.OnItemClickListener {
    private static final String LOG_TAG = "ChatFragment";
    private ChatAdapter mChatAdapter;
    private User mUser;
    private EditText mMessageEditText;
    private RecyclerView mChatRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mUser = getArguments().getParcelable(User.descriptionKey);

        prepareView();
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        mChatRecyclerView = (RecyclerView) rootView.findViewById(R.id.chat_recycler_view);
        mChatAdapter = new ChatAdapter();
        mChatAdapter.setOnItemClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        mChatRecyclerView.setAdapter(mChatAdapter);
        mChatRecyclerView.setLayoutManager(linearLayoutManager);
        mChatRecyclerView.setItemAnimator(itemAnimator);

        mMessageEditText = (EditText) rootView.findViewById(R.id.message_text);
        rootView.findViewById(R.id.send_button).setOnClickListener(this);

        //SendServiceHelper.getInstance(getActivity()).requestUserInfo(mUser.getUid(), );
        return rootView;
    }

    @Override
    protected void handleResponse(String action, JSONObject jsonData) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_button:
                String messageText = mMessageEditText.getText().toString();
                if (!messageText.equals("")) {
                    //GlobalSocket.getInstance().performRequest(new MessageRequest(GlobalUserIds.getInstance().cid, GlobalUserIds.getInstance().sid, mChannel.getId(), messageText));
                }
                break;
        }
    }

    @Override
    public void onItemClick(ChatAdapter.MessageViewHolder item, int position) {
        Fragment contactInfoFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_contact_info);
        if (contactInfoFragment == null) {
            contactInfoFragment = new ContactInfoFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ContactInfoFragment.descriptionKey, mChatAdapter.getMessages().get(position).getUserId());
            contactInfoFragment.setArguments(bundle);
        }
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.fragments_container, contactInfoFragment).commit();
    }

    public void handleResponseMessage(GeneralResponse rawResponse) {
        String action = rawResponse.getAction();
        switch (action) {
            case "enter":
                ImportResponse enterChatResponse = new ImportResponse(rawResponse.getJsonData());
//                List<Message> messageList = enterChatResponse.getUsers();
//                for (Message message : messageList) {
//                    mChatAdapter.add(message);
//                }
                break;
            case "message":
                final MessageResponse sendMessageResponse = new MessageResponse(rawResponse.getJsonData());
                break;
            case "ev_message":
                final MessageEventResponse messageEvent = new MessageEventResponse(rawResponse.getJsonData());
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        mChatAdapter.add(messageEvent.getMessage());
                        mMessageEditText.setText("");
                        mChatRecyclerView.scrollToPosition(mChatAdapter.getItemCount() - 1);
                    }
                });
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    private void prepareView() {
        MainActivity mainActivity = (MainActivity) getActivity();
        ActionBar actionBar = mainActivity.getSupportActionBar();
        assert actionBar != null;
        //actionBar.setTitle(mChannel.getName());
        actionBar.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_white_24dp);
        actionBar.setIcon(R.drawable.ic_public_white_24dp);
    }
}