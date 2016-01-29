package com.technopark.bulat.advandroidhomework3.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import com.technopark.bulat.advandroidhomework3.models.Attach;
import com.technopark.bulat.advandroidhomework3.models.User;
import com.technopark.bulat.advandroidhomework3.network.response.events.MessageEventResponse;
import com.technopark.bulat.advandroidhomework3.network.response.messages.MessageResponse;
import com.technopark.bulat.advandroidhomework3.service.SendServiceHelper;
import com.technopark.bulat.advandroidhomework3.ui.activity.MainActivity;
import com.technopark.bulat.advandroidhomework3.util.Base64Translator;

import org.json.JSONObject;

import java.io.IOException;

import static com.technopark.bulat.advandroidhomework3.adapters.ChatAdapter.ANOTHER_USER;
import static com.technopark.bulat.advandroidhomework3.adapters.ChatAdapter.CURRENT_USER;

public class ChatFragment extends BaseFragment implements OnClickListener, ChatAdapter.OnItemClickListener {
    private static final String LOG_TAG = "ChatFragment";
    private static final int REQUEST_CODE_SELECT_IMAGE = 100;
    private Attach attach;
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
        mChatAdapter = new ChatAdapter(getActivity(), mUser);
        mChatAdapter.setOnItemClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        mChatRecyclerView.setAdapter(mChatAdapter);
        mChatRecyclerView.setLayoutManager(linearLayoutManager);
        mChatRecyclerView.setItemAnimator(itemAnimator);

        mMessageEditText = (EditText) rootView.findViewById(R.id.message_text);
        rootView.findViewById(R.id.send_button).setOnClickListener(this);
        rootView.findViewById(R.id.attach_button).setOnClickListener(this);

        return rootView;
    }

    @Override
    protected void handleResponse(String action, JSONObject jsonData) {
        switch (action) {
            case "message":
                MessageResponse sendMessageResponse = new MessageResponse(jsonData);
                break;
            case "ev_message":
                MessageEventResponse messageEvent = new MessageEventResponse(jsonData);
                mChatAdapter.add(messageEvent.getMessage());
                mChatRecyclerView.scrollToPosition(mChatAdapter.getItemCount() - 1);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SELECT_IMAGE: {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    Uri imageUri = data.getData();
                    try {
                        attach = new Attach();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                        attach.setData(Base64Translator.encodeToBase64(bitmap));
                        attach.setMime("image/bmp");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_button:
                String messageText = mMessageEditText.getText().toString();
                if (!messageText.equals("")) {
                    mSharedPreferences = getActivity().getSharedPreferences(
                            "auth_settings",
                            Context.MODE_PRIVATE
                    );
                    String cid = mSharedPreferences.getString("cid", null);
                    String sid = mSharedPreferences.getString("sid", null);
                    SendServiceHelper.getInstance(getActivity()).requestMessage(mUser.getUid(), cid, sid, messageText, attach);
                }
                mMessageEditText.setText("");
                attach = null;
                break;
            case R.id.attach_button:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getActivity().getString(R.string.choose_image)), REQUEST_CODE_SELECT_IMAGE);
                break;
        }
    }

    @Override
    public void onItemClick(ChatAdapter.MessageViewHolder item, int position) {
        Fragment fragment = null;
        switch (mChatAdapter.getItemViewType(position)) {
            case ANOTHER_USER: {
                Fragment contactInfoFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_contact_info);
                if (contactInfoFragment == null) {
                    contactInfoFragment = new ContactInfoFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(User.descriptionKey, mUser);
                    contactInfoFragment.setArguments(bundle);
                }
                fragment = contactInfoFragment;
                break;
            }
            case CURRENT_USER: {
                Fragment changeContactInfoFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_change_contact_info);
                if (changeContactInfoFragment == null) {
                    changeContactInfoFragment = new ChangeContactInfoFragment();
                }
                fragment = changeContactInfoFragment;
                break;
            }
        }
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragments_container, fragment).commit();
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
        actionBar.setTitle(mUser.getNick());
        actionBar.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_white_24dp);
        actionBar.setIcon(R.drawable.ic_public_white_24dp);
    }
}