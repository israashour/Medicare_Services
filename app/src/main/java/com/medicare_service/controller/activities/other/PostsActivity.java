package com.medicare_service.controller.activities.other;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medicare_service.R;
import com.medicare_service.controller.adapters.PostAdapter;
import com.medicare_service.databinding.ActivityPostsBinding;
import com.medicare_service.helpers.BaseActivity;
import com.medicare_service.helpers.Constants;
import com.medicare_service.helpers.Functions;
import com.medicare_service.model.Category;
import com.medicare_service.model.Post;

import java.util.ArrayList;

@SuppressLint("SetTextI18n")
public class PostsActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    ActivityPostsBinding binding;
    ArrayList<Post> list = new ArrayList<>();
    PostAdapter adapter;
    Category category;
    String from;
    boolean isSubscribed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        category = (Category) getIntent().getSerializableExtra(Constants.TYPE_MODEL);
        from = getIntent().getStringExtra(Constants.TYPE_FROM);

        binding.appbar.title.setText(getString(R.string.posts_) + " " + category.getTitle());
        binding.swipeRefresh.setOnRefreshListener(this);
        adapter = new PostAdapter(this);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setHasFixedSize(true);
        getPostsRequest();
        onViewClicked();

        if (user.getType().equals(Constants.TYPE_DOCTOR)) {
            if (user.getId().equals(category.getCreateBy())) {
                binding.edit.setVisibility(View.VISIBLE);
            } else {
                binding.edit.setVisibility(View.GONE);
            }
            binding.add.setVisibility(View.VISIBLE);
            binding.fabChat.setVisibility(View.VISIBLE);
        } else {
            binding.subscribe.setVisibility(View.VISIBLE);
            if (from.equals(Constants.COLUMN_INTERESTS)) {
                binding.subscribe.setText(getString(R.string.un_subscribe));
                binding.fabChat.setVisibility(View.VISIBLE);
                isSubscribed = true;
            } else {
                if (category.getUsers() != null) {
                    if (category.getUsers().containsValue(user.getId())) {
                        binding.subscribe.setText(getString(R.string.un_subscribe));
                        binding.fabChat.setVisibility(View.VISIBLE);
                        isSubscribed = true;
                    } else {
                        binding.subscribe.setText(getString(R.string.subscribe));
                        binding.fabChat.setVisibility(View.GONE);
                        isSubscribed = false;
                    }
                } else {
                    binding.subscribe.setText(getString(R.string.subscribe));
                    binding.fabChat.setVisibility(View.GONE);
                    isSubscribed = false;
                }
            }
        }
    }

    private void onViewClicked() {
        binding.appbar.back.setOnClickListener(v -> finish());
        adapter.setListener(model -> {
            Intent intent = new Intent(PostsActivity.this, PostDetailsActivity.class);
            intent.putExtra(Constants.TYPE_MODEL, model);
            startActivity(intent);
        });
        binding.edit.setOnClickListener(v -> {
            Intent intent = new Intent(PostsActivity.this, AddCategoryActivity.class);
            intent.putExtra(Constants.TYPE_FROM, Constants.TYPE_EDIT);
            intent.putExtra(Constants.TYPE_MODEL, category);
            startActivity(intent);
        });
        binding.add.setOnClickListener(v -> {
            Intent intent = new Intent(PostsActivity.this, AddPostActivity.class);
            intent.putExtra(Constants.TYPE_FROM, Constants.TYPE_ADD);
            intent.putExtra(Constants.TYPE_MODEL, category);
            startActivity(intent);
        });
        binding.fabChat.setOnClickListener(v -> {
            Intent intent = new Intent(PostsActivity.this, GroupMessagesActivity.class);
            intent.putExtra(Constants.TYPE_MODEL, category);
            startActivity(intent);
        });
        binding.subscribe.setOnClickListener(v -> {
            if (isSubscribed) {
                isSubscribed = false;
                unsubscribeFromTopic();
                binding.fabChat.setVisibility(View.GONE);
            } else {
                isSubscribed = true;
                subscribeToTopic();
                binding.fabChat.setVisibility(View.VISIBLE);
            }
        });
    }

    private void getPostsRequest() {
        binding.swipeRefresh.setRefreshing(false);
        binding.stateful.showLoading();
        FirebaseDatabase.getInstance().getReference(Constants.TABLE_POSTS)
                .orderByChild("categoryId").equalTo(category.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            list.clear();
                            for (DataSnapshot issue : snapshot.getChildren()) {
                                Post post = issue.getValue(Post.class);
                                list.add(post);
                            }
                            adapter.setData(list);
                            binding.stateful.showContent();
                        } else {
                            binding.stateful.showEmpty();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.stateful.showError(error.getMessage(), v -> getPostsRequest());
                    }
                });
    }

    private void subscribeToTopic() {
        FirebaseDatabase.getInstance().getReference(Constants.TABLE_USERS)
                .child(user.getId()).child(Constants.COLUMN_INTERESTS).child(category.getId())
                .setValue(category);

        FirebaseDatabase.getInstance().getReference(Constants.TABLE_CATEGORIES)
                .child(category.getId()).child(Constants.COLUMN_USERS).child(user.getId())
                .setValue(user.getId());

        Functions.subscribeToTopic(this, binding.getRoot(), category.getTopic(), category.getTitle());
        binding.subscribe.setText(getString(R.string.un_subscribe));
    }

    private void unsubscribeFromTopic() {
        FirebaseDatabase.getInstance().getReference(Constants.TABLE_USERS)
                .child(user.getId()).child(Constants.COLUMN_INTERESTS).child(category.getId())
                .removeValue();
        FirebaseDatabase.getInstance().getReference(Constants.TABLE_CATEGORIES)
                .child(category.getId()).child(Constants.COLUMN_USERS).child(user.getId())
                .removeValue();

        Functions.unsubscribeFromTopic(this, binding.getRoot(), category.getTopic(), category.getTitle());
        binding.subscribe.setText(getString(R.string.subscribe));
    }

    @Override
    public void onRefresh() {
        getPostsRequest();
    }

}