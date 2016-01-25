package com.technopark.bulat.advandroidhomework3.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.technopark.bulat.advandroidhomework3.R;
import com.technopark.bulat.advandroidhomework3.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bulat on 07.11.15.
 */
public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> {
    private final List<User> userList;
    private OnItemClickListener onItemClickListener;

    public UserListAdapter() {
        userList = new ArrayList<>();
    }

    public List<User> getUserList() {
        return userList;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.channel, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User channel = userList.get(position);
        //holder.mName.setText(String.format("%s (%d)", channel.getName(), channel.getOnlineCount()));
        //holder.mDescription.setText(channel.getDescription());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void add(User channel) {
        userList.add(channel);
        notifyItemInserted(getItemCount());
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(UserViewHolder item, int position);
    }

    public class UserViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        private final TextView mName;
        private final TextView mDescription;

        public UserViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mName = (TextView) itemView.findViewById(R.id.contact_uid);
            mDescription = (TextView) itemView.findViewById(R.id.channel_description);
        }

        @Override
        public void onClick(View v) {
            final OnItemClickListener listener = getOnItemClickListener();
            if (listener != null) {
                listener.onItemClick(this, getAdapterPosition());
            }
        }
    }
}
