package com.medicare_service.controller.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medicare_service.controller.activities.other.AddCategoryActivity;
import com.medicare_service.controller.activities.other.PostsActivity;
import com.medicare_service.controller.adapters.CategoryAdapter;
import com.medicare_service.databinding.FragmentCategoryBinding;
import com.medicare_service.helpers.BaseFragment;
import com.medicare_service.helpers.Constants;
import com.medicare_service.helpers.Functions;
import com.medicare_service.model.Category;

import java.util.ArrayList;
import java.util.Collections;

public class CategoryFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    public CategoryFragment() {
        // Required empty public constructor
    }

    FragmentCategoryBinding binding;
    ArrayList<Category> list = new ArrayList<>();
    CategoryAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCategoryBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {

        if (Functions.getUser().getType().equals(Constants.TYPE_DOCTOR)) {
            binding.fab.setVisibility(View.VISIBLE);
            binding.fab.setOnClickListener(v -> {
                Intent intent = new Intent(requireActivity(), AddCategoryActivity.class);
                intent.putExtra(Constants.TYPE_FROM, Constants.TYPE_ADD);
                startActivity(intent);
            });
        }

        binding.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter.getData() != null) {
                    adapter.getFilter().filter(s.toString());
                    if (adapter.getData().isEmpty()) {
                        binding.stateful.showEmpty();
                    } else {
                        binding.stateful.showContent();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable text) {

            }
        });
        binding.swipeRefresh.setOnRefreshListener(this);
        adapter = new CategoryAdapter(requireActivity());
        adapter.setListener(model -> {
            Intent intent = new Intent(requireActivity(), PostsActivity.class);
            intent.putExtra(Constants.TYPE_MODEL, model);
            intent.putExtra(Constants.TYPE_FROM, Constants.TABLE_CATEGORIES);
            startActivity(intent);
        });
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setHasFixedSize(true);
        getCategoriesRequest();
    }

    private void getCategoriesRequest() {
        binding.swipeRefresh.setRefreshing(false);
        binding.stateful.showLoading();
        FirebaseDatabase.getInstance().getReference(Constants.TABLE_CATEGORIES)
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
                        binding.stateful.showError(error.getMessage(), v -> getCategoriesRequest());
                    }
                });
    }

    @Override
    public void onRefresh() {
        getCategoriesRequest();
    }

}