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
import com.medicare_service.controller.activities.other.PostsActivity;
import com.medicare_service.controller.adapters.CategoryAdapter;
import com.medicare_service.databinding.FragmentInterestsBinding;
import com.medicare_service.helpers.BaseFragment;
import com.medicare_service.helpers.Constants;
import com.medicare_service.helpers.Functions;
import com.medicare_service.model.Category;

import java.util.ArrayList;
import java.util.Collections;

public class InterestsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    public InterestsFragment() {
        // Required empty public constructor
    }

    FragmentInterestsBinding binding;
    ArrayList<Category> list = new ArrayList<>();
    CategoryAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInterestsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        binding.swipeRefresh.setOnRefreshListener(this);
        adapter = new CategoryAdapter(requireActivity());
        adapter.setListener(model -> {
            Intent intent = new Intent(requireActivity(), PostsActivity.class);
            intent.putExtra(Constants.TYPE_FROM, Constants.COLUMN_INTERESTS);
            intent.putExtra(Constants.TYPE_MODEL, model);
            startActivity(intent);
        });
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setHasFixedSize(true);
        getInterestsRequest();
    }

    private void getInterestsRequest() {
        binding.swipeRefresh.setRefreshing(false);
        binding.stateful.showLoading();
        FirebaseDatabase.getInstance().getReference(Constants.TABLE_USERS)
                .child(Functions.getUserId()).child(Constants.COLUMN_INTERESTS)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            list.clear();
                            for (DataSnapshot data : snapshot.getChildren()) {
                                Category category = data.getValue(Category.class);
                                list.add(category);
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
                        binding.stateful.showError(error.getMessage(), v -> getInterestsRequest());
                    }
                });
    }

    @Override
    public void onRefresh() {
        getInterestsRequest();
    }
}