package com.technopark.bulat.advandroidhomework3.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.technopark.bulat.advandroidhomework3.R;
import com.technopark.bulat.advandroidhomework3.models.Message;
import com.technopark.bulat.advandroidhomework3.models.User;
import com.technopark.bulat.advandroidhomework3.util.Base64Translator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bulat on 08.11.15.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {
    private final List<Message> messages;
    public final static int ANOTHER_USER = 0;
    public final static int CURRENT_USER = 1;
    private OnItemClickListener onItemClickListener;
    private SharedPreferences mSharedPreferences;
    private User mAnotherUser;

    public ChatAdapter(Context context, User anotherUser) {
        messages = new ArrayList<>();
        mAnotherUser = anotherUser;
        mSharedPreferences = context.getSharedPreferences(
                "auth_settings",
                Context.MODE_PRIVATE
        );
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case ANOTHER_USER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_another_user, parent, false);
                break;
            case CURRENT_USER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_current_user, parent, false);
                break;
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.mMessageText.setText(message.getText());
        holder.mMessageAuthor.setText(message.getUserNick());
        String pictureString = "";
        switch (getItemViewType(position)) {
            case CURRENT_USER:
                pictureString = mSharedPreferences.getString("picture", "");
                break;
            case ANOTHER_USER:
                pictureString = mAnotherUser.getPicture();
                break;
        }
        if (pictureString.length() > 0) {
            holder.mAuthorImage.setImageBitmap(Base64Translator.decodeBase64(pictureString));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getUserId().equals(mAnotherUser.getUid())) {
            return ANOTHER_USER;
        } else
            return CURRENT_USER;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(MessageViewHolder item, int position);
    }

    public void add(Message message) {
        messages.add(message);
        notifyItemInserted(getItemCount());
    }

    public List<Message> getMessages() {
        return messages;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView mMessageText;
        private final TextView mMessageAuthor;
        public final ImageView mAuthorImage;

        public MessageViewHolder(View itemView) {
            super(itemView);
            mMessageText = (TextView) itemView.findViewById(R.id.message_text);
            mMessageAuthor = (TextView) itemView.findViewById(R.id.message_author);
            mAuthorImage = (ImageView) itemView.findViewById(R.id.author_image);
            mAuthorImage.setOnClickListener(this);
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
