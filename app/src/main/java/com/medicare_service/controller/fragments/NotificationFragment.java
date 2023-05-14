package com.medicare_service.controller.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medicare_service.controller.adapters.NotificationAdapter;
import com.medicare_service.databinding.FragmentNotificationBinding;
import com.medicare_service.helpers.BaseFragment;
import com.medicare_service.helpers.Constants;
import com.medicare_service.model.Notification;

import java.util.ArrayList;
import java.util.Collections;

public class NotificationFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    public NotificationFragment() {
        // Required empty public constructor
    }

    FragmentNotificationBinding binding;
    ArrayList<Notification> list = new ArrayList<>();
    NotificationAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        binding.swipeRefresh.setOnRefreshListener(this);
        adapter = new NotificationAdapter();
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setHasFixedSize(true);
        geNotificationRequest();
    }

    private void geNotificationRequest() {
        binding.swipeRefresh.setRefreshing(false);
        binding.stateful.showLoading();
        FirebaseDatabase.getInstance().getReference(Constants.TABLE_NOTIFICATIONS)
                .orderByChild("userId").equalTo(user.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            list.clear();
                            for (DataSnapshot issue : snapshot.getChildren()) {
                                Notification model = issue.getValue(Notification.class);
                                list.add(model);
                            }
                            Collections.reverse(list);
                            adapter.setData(list);
                            binding.stateful.showContent();
                        } else {
                            binding.stateful.showEmpty();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.stateful.showError(error.getMessage(), v -> geNotificationRequest());
                    }
                });
    }

    @Override
    public void onRefresh() {
        geNotificationRequest();
    }

}