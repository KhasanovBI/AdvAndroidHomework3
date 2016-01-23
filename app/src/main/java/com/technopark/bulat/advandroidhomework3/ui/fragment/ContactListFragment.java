package com.technopark.bulat.advandroidhomework3.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.technopark.bulat.advandroidhomework3.R;
import com.technopark.bulat.advandroidhomework3.adapters.UserListAdapter;
import com.technopark.bulat.advandroidhomework3.models.User;
import com.technopark.bulat.advandroidhomework3.network.response.GeneralResponse;
import com.technopark.bulat.advandroidhomework3.network.response.messages.ContactListResponse;
import com.technopark.bulat.advandroidhomework3.network.response.messages.DelContactResponse;
import com.technopark.bulat.advandroidhomework3.ui.activity.MainActivity;

import org.json.JSONObject;

public class ContactListFragment extends BaseFragment implements UserListAdapter.OnItemClickListener {
    private UserListAdapter mUserListAdapter;
    private DialogFragment mChannelAddDialogFragment;

    public ContactListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mChannelAddDialogFragment = new ChannelAddDialogFragment();
        prepareView();
        View rootView = inflater.inflate(R.layout.fragment_channel_list, container, false);
        RecyclerView mChannelListRecyclerView = (RecyclerView) rootView.findViewById(R.id.channel_list_recycler_view);
        mUserListAdapter = new UserListAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        mUserListAdapter.setOnItemClickListener(this);
        mChannelListRecyclerView.setAdapter(mUserListAdapter);
        mChannelListRecyclerView.setLayoutManager(linearLayoutManager);
        mChannelListRecyclerView.setItemAnimator(itemAnimator);

        /* Subscribe to socket messages */
        // SendServiceHelper.getInstance().registerObserver(this);
        // SendServiceHelper.getInstance().performAsyncRequest(new ChannelListRequest(GlobalUserIds.getInstance().cid, GlobalUserIds.getInstance().sid));

        return rootView;
    }

    @Override
    public void onItemClick(UserListAdapter.UserViewHolder item, int position) {
        User channel = mUserListAdapter.getUserList().get(position);
        Fragment chatFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_chat);
        if (chatFragment == null) {
            chatFragment = new ChatFragment();
            Bundle bundle = new Bundle();
           // bundle.putSerializable(Channel.descriptionKey, channel);
            chatFragment.setArguments(bundle);
        }
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragments_container, chatFragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ((MainActivity) getActivity()).getDrawerLayout().openDrawer(GravityCompat.START);
                break;
            case R.id.add_channel_button:
                mChannelAddDialogFragment.show(getActivity().getSupportFragmentManager(), "channelAddDialogFragment");
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        //GlobalSocket.getInstance().registerObserver(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        /* Unsubscribe from socket messages */
        //GlobalSocket.getInstance().removeObserver(this);
    }

    @Override
    protected void handleResponse(String action, JSONObject jsonData) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_channel_list, menu);
    }

    public void handleResponseMessage(GeneralResponse rawResponse) {
        String action = rawResponse.getAction();
        switch (action) {
            case "channellist":
                final ContactListResponse channelListResponse = new ContactListResponse(rawResponse.getJsonData());
                int status = channelListResponse.getStatus();
                if (status == 0) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            for (User channel : channelListResponse.getUsers()) {
                                mUserListAdapter.add(channel);
                            }
                        }
                    });
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity().getBaseContext(), channelListResponse.getError(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
                break;
            case "createchannel":
                final DelContactResponse createChannelResponse = new DelContactResponse(rawResponse.getJsonData());
                if (createChannelResponse.getStatus() == 0) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity().getBaseContext(), "Channel successfully created.", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity().getBaseContext(), createChannelResponse.getError(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
                break;
        }
    }

    private void prepareView() {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.unsetFullScreenFlag();
        ActionBar actionBar = mainActivity.getSupportActionBar();
        assert actionBar != null;
        actionBar.show();
        actionBar.setTitle(R.string.list_of_chats);
        actionBar.setIcon(R.drawable.ic_chat_white_24dp);
        mainActivity.getDrawerToggle().syncState();
    }
}