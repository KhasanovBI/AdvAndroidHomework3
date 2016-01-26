package com.technopark.bulat.advandroidhomework3.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.technopark.bulat.advandroidhomework3.R;
import com.technopark.bulat.advandroidhomework3.models.User;
import com.technopark.bulat.advandroidhomework3.util.Base64Translator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bulat on 07.11.15.
 */
public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> {
    private List<User> userList;
    private OnItemClickListener onItemClickListener;

    public UserListAdapter() {
        userList = new ArrayList<>();
    }

    public List<User> getUserList() {
        return userList;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.mName.setText(user.getNick());
        holder.mAvatar.setImageBitmap(Base64Translator.decodeBase64(user.getPicture()));
    }

    public User getItem(int position) {
        return userList.get(position);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void add(User channel) {
        userList.add(channel);
        notifyItemInserted(getItemCount());
    }

    public void changeUserList(List<User> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, int itemViewId);
    }

    public class UserViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        private final TextView mName;
        private final ImageView mAvatar;
        private final ImageButton mTrashButton;

        public UserViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mName = (TextView) itemView.findViewById(R.id.contact_name);
            mAvatar = (ImageView) itemView.findViewById(R.id.contact_avatar);
            mTrashButton = (ImageButton) itemView.findViewById(R.id.trash_button);

            mTrashButton.setOnClickListener(this);
            mAvatar.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final OnItemClickListener listener = getOnItemClickListener();
            if (listener != null) {
                listener.onItemClick(getAdapterPosition(), v.getId());
            }
        }
    }
}
