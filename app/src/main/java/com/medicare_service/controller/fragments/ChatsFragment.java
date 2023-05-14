package com.medicare_service.controller.fragments;

import android.content.Intent;
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
import com.medicare_service.controller.activities.other.MessagesActivity;
import com.medicare_service.controller.adapters.ContactAdapter;
import com.medicare_service.databinding.FragmentChatsBinding;
import com.medicare_service.helpers.BaseFragment;
import com.medicare_service.helpers.Constants;
import com.medicare_service.model.User;

import java.util.ArrayList;
import java.util.Collections;

public class ChatsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    public ChatsFragment() {
        // Required empty public constructor
    }

    FragmentChatsBinding binding;
    ContactAdapter adapter;
    ArrayList<User> list = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        binding.swipeRefresh.setOnRefreshListener(this);
        adapter = new ContactAdapter();
        adapter.setListener(model -> {
            Intent intent = new Intent(requireActivity(), MessagesActivity.class);
            intent.putExtra(Constants.TYPE_MODEL, model);
            startActivity(intent);
        });
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setHasFixedSize(true);
        getChatsRequest();
    }

    private void getChatsRequest() {
        binding.swipeRefresh.setRefreshing(false);
        binding.stateful.showLoading();
        FirebaseDatabase.getInstance().getReference(Constants.TABLE_CONTACTS).child(user.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            list.clear();
                            for (DataSnapshot data : snapshot.getChildren()) {
                                User model = data.getValue(User.class);
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
                        binding.stateful.showError(error.getMessage(), v -> getChatsRequest());
                    }
                });
    }

    @Override
    public void onRefresh() {
        getChatsRequest();
    }
}