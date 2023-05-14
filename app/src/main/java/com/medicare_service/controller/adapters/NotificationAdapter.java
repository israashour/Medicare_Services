package com.medicare_service.controller.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medicare_service.R;
import com.medicare_service.databinding.ItemNotificationBinding;
import com.medicare_service.helpers.Functions;
import com.medicare_service.model.Notification;

import java.util.ArrayList;

@SuppressLint("NotifyDataSetChanged")
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    ArrayList<Notification> list;

    public void setData(ArrayList<Notification> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public NotificationAdapter() {
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification model = list.get(position);
        holder.onBind(model);
    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ItemNotificationBinding binding;

        private NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemNotificationBinding.bind(itemView);
        }

        private void onBind(Notification model) {
            String createAt = Functions.formatTime(model.getCreateAt());
            binding.title.setText(model.getTitle());
            binding.content.setText(model.getContent());
            binding.time.setText(createAt);
        }
    }

}