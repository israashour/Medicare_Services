package com.medicare_service.controller.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medicare_service.R;
import com.medicare_service.databinding.ItemReceivedMessageBinding;
import com.medicare_service.databinding.ItemSentMessageBinding;
import com.medicare_service.helpers.Functions;
import com.medicare_service.model.Message;

import java.util.ArrayList;
import java.util.Date;

@SuppressLint("NotifyDataSetChanged")
public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int ITEM_SENT = 1;
    private final int ITEM_RECEIVE = 2;

    Context context;
    ArrayList<Message> list;

    public void setData(ArrayList<Message> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public MessageAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewReceived = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_received_message, parent, false);
        View viewSent = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sent_message, parent, false);
        if (viewType == ITEM_RECEIVE) {
            return new ReceivedViewHolder(viewReceived);
        } else {
            return new SentViewHolder(viewSent);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message model = list.get(position);
        if (holder instanceof SentViewHolder) {
            SentViewHolder sentHolder = (SentViewHolder) holder;
            long time = model.getTimestamp() * 1000;
            sentHolder.binding.time.setText(Functions.toTime(new Date(time)));
            sentHolder.binding.header.setText(Functions.isToday(new Date(time)));
            sentHolder.binding.message.setText(model.getMessage());
            Functions.matchLayoutParams(sentHolder.binding.message, -1);
            if (position > 0) {
                if (Functions.isHeaderVisible(list.get(position).getTimestamp())
                        .equals(Functions.isHeaderVisible(list.get(position - 1).getTimestamp()))) {
                    sentHolder.binding.header.setVisibility(View.GONE);
                } else {
                    sentHolder.binding.header.setVisibility(View.VISIBLE);
                }
            } else {
                sentHolder.binding.header.setVisibility(View.VISIBLE);
            }

            if (model.getSeen()) {
                sentHolder.binding.imgSeen.setImageResource(R.drawable.ic_read);
            } else {
                sentHolder.binding.imgSeen.setImageResource(R.drawable.ic_sent);
            }
        }
        if (holder instanceof ReceivedViewHolder) {
            ReceivedViewHolder receivedHolder = (ReceivedViewHolder) holder;
            long time = model.getTimestamp() * 1000;
            receivedHolder.binding.time.setText(Functions.toTime(new Date(time)));
            receivedHolder.binding.header.setText(Functions.isToday(new Date(time)));
            receivedHolder.binding.message.setText(model.getMessage());
            Functions.matchLayoutParams(receivedHolder.binding.message, -1);
            if (position > 0) {
                if (Functions.isHeaderVisible(list.get(position).getTimestamp())
                        .equals(Functions.isHeaderVisible(list.get(position - 1).getTimestamp()))) {
                    receivedHolder.binding.header.setVisibility(View.GONE);
                } else {
                    receivedHolder.binding.header.setVisibility(View.VISIBLE);
                }
            } else {
                receivedHolder.binding.header.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    static class ReceivedViewHolder extends RecyclerView.ViewHolder {
        ItemReceivedMessageBinding binding;

        private ReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemReceivedMessageBinding.bind(itemView);
        }
    }

    static class SentViewHolder extends RecyclerView.ViewHolder {
        ItemSentMessageBinding binding;

        private SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSentMessageBinding.bind(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message messages = list.get(position);
        if (messages.getFromId().equals(Functions.getUserId())) {
            return ITEM_SENT;
        } else {
            return ITEM_RECEIVE;
        }
    }
}