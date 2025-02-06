package edu.uiuc.cs427app.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import edu.uiuc.cs427app.R;
import edu.uiuc.cs427app.data.models.ChatMessage;

public class PromptAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ChatMessage> messages;
    private final OnPromptClickListener listener;

    public interface OnPromptClickListener {
        void onPromptClick(String prompt);
    }

    /**
     * Constructor
     * @param messages list of messages
     * @param listener on click listener
     */
    public PromptAdapter(List<ChatMessage> messages, OnPromptClickListener listener) {
        this.messages = messages;
        this.listener = listener;
    }

    private static final int VIEW_TYPE_PROMPT = 0;
    private static final int VIEW_TYPE_USER_MESSAGE = 1;
    private static final int VIEW_TYPE_AI_MESSAGE = 2;

    /**
     * get item view type from position
     * @param position position to query
     * @return item view type
     */
    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messages.get(position);
        switch (message.getMessageType()) {
            case PROMPT:
                return VIEW_TYPE_PROMPT;
            case USER_MESSAGE:
                return VIEW_TYPE_USER_MESSAGE;
            case AI_MESSAGE:
                return VIEW_TYPE_AI_MESSAGE;
            default:
                throw new IllegalArgumentException("Unknown message type");
        }
    }

    /**
     *  on create for the view holder
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_PROMPT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_prompt, parent, false);
            return new PromptViewHolder(view);
        } else if (viewType == VIEW_TYPE_USER_MESSAGE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_message_item, parent, false);
            return new UserMessageViewHolder(view);
        } else if (viewType == VIEW_TYPE_AI_MESSAGE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ai_message_item, parent, false);
            return new AIMessageViewHolder(view);
        } else {
            throw new IllegalArgumentException("Unknown viewType: " + viewType);
        }
    }

    /**
     * on bind for the view holder
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        if (holder instanceof PromptViewHolder) {
            ((PromptViewHolder) holder).bind(message, listener);
        } else if (holder instanceof UserMessageViewHolder) {
            ((UserMessageViewHolder) holder).bind(message);
        } else if (holder instanceof AIMessageViewHolder) {
            ((AIMessageViewHolder) holder).bind(message);
        }
    }

    /**
     * get item count
     * @return size of messages
     */
    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class PromptViewHolder extends RecyclerView.ViewHolder {
        TextView promptText;

        /**
         * constructor
         * @param itemView view
         */
        public PromptViewHolder(View itemView) {
            super(itemView);
            promptText = itemView.findViewById(R.id.promptQuestion);
        }

        /**
         * bind for prompt adapter
         * @param message chat message
         * @param listener listener
         */
        public void bind(ChatMessage message, OnPromptClickListener listener) {
            promptText.setText(message.getText());
            itemView.setOnClickListener(v -> listener.onPromptClick(message.getText()));
        }
    }

    // ViewHolder for user messages
    public static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView userMessageText;

        /**
         * constructor
         * @param itemView view
         */
        public UserMessageViewHolder(View itemView) {
            super(itemView);
            userMessageText = itemView.findViewById(R.id.userMessageText);
        }

        /**
         * bind for usermessage
         * @param message message
         */
        public void bind(ChatMessage message) {
            userMessageText.setText(message.getText());
        }
    }

    // ViewHolder for AI messages
    public static class AIMessageViewHolder extends RecyclerView.ViewHolder {
        TextView aiMessageText;

        /**
         * constructor
         * @param itemView view
         */
        public AIMessageViewHolder(View itemView) {
            super(itemView);
            aiMessageText = itemView.findViewById(R.id.aiMessageText);
        }

        /**
         * bind for ai message
         * @param message chat message
         */
        public void bind(ChatMessage message) {
            aiMessageText.setText(message.getText());
        }
    }
}
