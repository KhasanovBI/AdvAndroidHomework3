package com.technopark.bulat.advandroidhomework3.ui.fragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.technopark.bulat.advandroidhomework3.network.response.messages.AddContactResponse;
import com.technopark.bulat.advandroidhomework3.network.response.messages.ContactListResponse;
import com.technopark.bulat.advandroidhomework3.network.response.messages.ImportResponse;
import com.technopark.bulat.advandroidhomework3.service.SendServiceHelper;
import com.technopark.bulat.advandroidhomework3.ui.activity.MainActivity;
import com.technopark.bulat.advandroidhomework3.util.ContactConstants;

import org.json.JSONObject;

import java.util.ArrayList;

public class ContactListFragment extends BaseFragment implements UserListAdapter.OnItemClickListener, ContactConstants {
    private UserListAdapter mUserListAdapter;
    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private DialogFragment mAddContactDialogFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mAddContactDialogFragment = new AddContactDialogFragment();
        prepareView();
        View rootView = inflater.inflate(R.layout.fragment_channel_list, container, false);
        RecyclerView mChannelListRecyclerView = (RecyclerView) rootView.findViewById(R.id.contact_list_recycler_view);
        mUserListAdapter = new UserListAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        mUserListAdapter.setOnItemClickListener(this);
        mChannelListRecyclerView.setAdapter(mUserListAdapter);
        mChannelListRecyclerView.setLayoutManager(linearLayoutManager);
        mChannelListRecyclerView.setItemAnimator(itemAnimator);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                "auth_settings",
                Context.MODE_PRIVATE
        );
        String cid = sharedPreferences.getString("cid", null);
        String sid = sharedPreferences.getString("sid", null);
        SendServiceHelper.getInstance(getActivity()).requestContactList(cid, sid);

        return rootView;
    }

    @Override
    public void onItemClick(UserListAdapter.UserViewHolder item, int position) {
        //TODO
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

    private void importContacts() {
        ArrayList<User> users = getContacts();
        if (users.size() > 0) {
            SendServiceHelper.getInstance(getActivity()).requestImport(users);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ((MainActivity) getActivity()).getDrawerLayout().openDrawer(GravityCompat.START);
                break;
            case R.id.add_person_button:
                mAddContactDialogFragment.show(getActivity().getSupportFragmentManager(), "channelAddDialogFragment");
                break;
            case R.id.import_group_button:
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                } else {
                    importContacts();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    @Override
    protected void handleResponse(String action, JSONObject jsonData) {
        switch (action) {
            case "contactlist": {
                ContactListResponse contactListResponse = new ContactListResponse(jsonData);
                int status = contactListResponse.getStatus();
                if (status == 0) {
                    for (User channel : contactListResponse.getUsers()) {
                        mUserListAdapter.add(channel);
                    }
                } else {
                    handleErrorFromServer(contactListResponse.getStatus());
                }
                break;
            }
            case "import": {
                ImportResponse importResponse = new ImportResponse(jsonData);
                int status = importResponse.getStatus();
                if (status == 0) {
                    int count = importResponse.getUsers().size();
                    Toast.makeText(
                            getActivity().getApplicationContext(),
                            String.format(
                                    getActivity().getString(R.string.successfully_import_contacts),
                                    count
                            ),
                            Toast.LENGTH_SHORT).show();
                } else {
                    handleErrorFromServer(status);
                }
                break;
            }
            case "addcontact": {
                final AddContactResponse addContactResponse = new AddContactResponse(jsonData);
                int status = addContactResponse.getStatus();
                if (status == 0) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.contact_successfully_added, Toast.LENGTH_LONG).show();
                } else {
                    handleErrorFromServer(status);
                }
                break;
            }
            case "delcontact": {
                //TODO
                break;
            }
            default:
                super.handleResponse(action, jsonData);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_channel_list, menu);
    }

    private void prepareView() {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.unsetFullScreenFlag();
        ActionBar actionBar = mainActivity.getSupportActionBar();
        assert actionBar != null;
        actionBar.show();
        actionBar.setTitle(R.string.list_of_contacts);
        actionBar.setIcon(R.drawable.ic_chat_white_24dp);
        mainActivity.getDrawerToggle().syncState();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case ContactListFragment.MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    importContacts();
                } else {
                    Toast.makeText(getActivity(), R.string.sorry_permission_denied, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private ArrayList<User> getContacts() {
        ContentResolver contentResolver = getActivity().getContentResolver();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                "auth_settings",
                Context.MODE_PRIVATE
        );
        String myid = sharedPreferences.getString("cid", null);

        ArrayList<User> users = new ArrayList<>();
        Cursor cursor = contentResolver.query(CONTACT_CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String contact_id = cursor.getString(cursor.getColumnIndex(CONTACT_ID));
                String nick = cursor.getString(cursor.getColumnIndex(CONTACT_DISPLAY_NAME));

                ArrayList<User> phoneUsers = new ArrayList<>();
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(CONTACT_HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    Cursor phoneCursor = contentResolver.query(
                            PHONE_CONTENT_URI,
                            null,
                            PHONE_CONTACT_ID + " = ?",
                            new String[]{contact_id},
                            null
                    );
                    if (phoneCursor != null) {
                        while (phoneCursor.moveToNext()) {
                            User phoneUser = new User();
                            String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(PHONE_NUMBER));
                            phoneUser.setPhone(phoneNumber);
                            phoneUsers.add(phoneUser);
                        }
                        phoneCursor.close();
                    }
                } else {
                    User phoneUser = new User();
                    phoneUser.setPhone("");
                    phoneUsers.add(phoneUser);
                }

                ArrayList<User> phoneEmailUsers = new ArrayList<>();
                for (User phoneUser : phoneUsers) {
                    Cursor emailCursor = contentResolver.query(
                            EMAIL_CONTENT_URI,
                            null,
                            EMAIL_CONTACT_ID + " = ?",
                            new String[]{contact_id},
                            null
                    );
                    if (emailCursor != null) {
                        while (emailCursor.moveToNext()) {
                            User phoneEmailUser = new User();
                            phoneEmailUser.setPhone(phoneUser.getPhone());
                            String email = emailCursor.getString(emailCursor.getColumnIndex(EMAIL_DATA));
                            phoneEmailUser.setEmail(email);
                            phoneEmailUsers.add(phoneEmailUser);
                        }
                        emailCursor.close();
                    } else {
                        User phoneEmailUser = new User();
                        phoneEmailUser.setPhone(phoneUser.getPhone());
                        phoneEmailUser.setEmail("");
                        phoneEmailUsers.add(phoneEmailUser);
                    }
                }

                for (User phoneEmailUser : phoneEmailUsers) {
                    phoneEmailUser.setNick(nick);
                    phoneEmailUser.setMyid(myid);
                    users.add(phoneEmailUser);
                }
            }
            cursor.close();
        }
        return users;
    }
}