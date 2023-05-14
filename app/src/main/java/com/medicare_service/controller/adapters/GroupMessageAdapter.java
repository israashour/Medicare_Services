package com.medicare_service.controller.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medicare_service.R;
import com.medicare_service.databinding.ItemReceivedGroupMessageBinding;
import com.medicare_service.databinding.ItemSentGroupMessageBinding;
import com.medicare_service.helpers.Functions;
import com.medicare_service.model.GroupMessage;
import com.medicare_service.model.User;

import java.util.ArrayList;
import java.util.Date;

@SuppressLint("NotifyDataSetChanged")
public class GroupMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int ITEM_SENT = 1;
    private final int ITEM_RECEIVE = 2;

    Context context;
    ArrayList<GroupMessage> list;
    User user = Functions.getUser();

    public void setData(ArrayList<GroupMessage> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public GroupMessageAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewReceived = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_received_group_message, parent, false);
        View viewSent = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sent_group_message, parent, false);
        if (viewType == ITEM_RECEIVE) {
            return new ReceivedViewHolder(viewReceived);
        } else {
            return new SentViewHolder(viewSent);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        GroupMessage model = list.get(position);
        if (holder instanceof SentViewHolder) {
            SentViewHolder sentHolder = (SentViewHolder) holder;
            long time = model.getTimestamp() * 1000;
            sentHolder.binding.time.setText(Functions.toTime(new Date(time)));
            sentHolder.binding.header.setText(Functions.isToday(new Date(time)));
            sentHolder.binding.message.setText(model.getMessage());
//            sentHolder.binding.userName.setText(model.getUserName());
            showHeader(position, sentHolder.binding.header);
        }
        if (holder instanceof ReceivedViewHolder) {
            ReceivedViewHolder receivedHolder = (ReceivedViewHolder) holder;
            long time = model.getTimestamp() * 1000;
            receivedHolder.binding.time.setText(Functions.toTime(new Date(time)));
            receivedHolder.binding.header.setText(Functions.isToday(new Date(time)));
            receivedHolder.binding.message.setText(model.getMessage());
            receivedHolder.binding.userName.setText(model.getUserName());
            showHeader(position, receivedHolder.binding.header);
        }
    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    static class ReceivedViewHolder extends RecyclerView.ViewHolder {
        ItemReceivedGroupMessageBinding binding;

        private ReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemReceivedGroupMessageBinding.bind(itemView);
        }
    }

    static class SentViewHolder extends RecyclerView.ViewHolder {
        ItemSentGroupMessageBinding binding;

        private SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSentGroupMessageBinding.bind(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        GroupMessage messages = list.get(position);
        if (messages.getUserId().equals(user.getId())) {
            return ITEM_SENT;
        } else {
            return ITEM_RECEIVE;
        }
    }

    private void showHeader(int position, TextView header) {
        if (position > 0) {
            if (Functions.isHeaderVisible(list.get(position).getTimestamp())
                    .equals(Functions.isHeaderVisible(list.get(position - 1).getTimestamp()))) {
                header.setVisibility(View.GONE);
            } else {
                header.setVisibility(View.VISIBLE);
            }
        } else {
            header.setVisibility(View.VISIBLE);
        }
    }
}