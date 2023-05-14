package com.medicare_service.controller.activities.other;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.medicare_service.R;
import com.medicare_service.helpers.BaseActivity;
import com.medicare_service.databinding.ActivityAddCategoryBinding;
import com.medicare_service.helpers.Constants;
import com.medicare_service.helpers.Functions;
import com.medicare_service.model.Category;

public class AddCategoryActivity extends BaseActivity {

    ActivityAddCategoryBinding binding;
    String from = Constants.TYPE_ADD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        from = getIntent().getStringExtra(Constants.TYPE_FROM);
        switch (from) {
            case Constants.TYPE_ADD:
                binding.appbar.title.setText(getString(R.string.add_new_category));
                binding.send.setText(getString(R.string.add));
                binding.send.setOnClickListener(v -> addCategoryRequest());
                break;
            case Constants.TYPE_EDIT:
                Category category = (Category) getIntent().getSerializableExtra(Constants.TYPE_MODEL);
                getCategoryForEdit(category);
                break;
        }
        binding.appbar.back.setOnClickListener(v -> finish());
    }

    private void getCategoryForEdit(Category model) {
        binding.appbar.title.setText(getString(R.string.edit_category));
        binding.send.setText(getString(R.string.edit));
        binding.name.setText(model.getTitle());
        binding.description.setText(model.getDescription());
        binding.send.setOnClickListener(v -> editCategoryRequest(model));
        binding.remove.setVisibility(View.VISIBLE);
        binding.remove.setOnClickListener(v -> {
            Functions.dialog(this, getString(R.string.remove), getString(R.string.remove_message), str -> {
                removeCategoryRequest(model);
            });
        });
    }

    private void editCategoryRequest(Category model) {
        if (isNotEmpty(binding.tvName, binding.name)
                && isNotEmpty(binding.tvDescription, binding.description)
        ) {
            showCustomProgress();
            model.setTitle(getText(binding.name));
            model.setDescription(getText(binding.description));
            FirebaseDatabase.getInstance().getReference(Constants.TABLE_CATEGORIES).child(model.getId())
                    .setValue(model).addOnCompleteListener(task -> {
                        dismissCustomProgress();
                        if (task.isSuccessful()) {
                            Functions.toast(this, getString(R.string.edit_category_successfully));
                            onBackPressed();
                        } else {
                            Functions.snackBar(this, binding.getRoot(), getString(R.string.error));
                        }
                    });
        }
    }

    private void removeCategoryRequest(Category model) {
        if (isNotEmpty(binding.tvName, binding.name)
                && isNotEmpty(binding.tvDescription, binding.description)
        ) {
            showCustomProgress();
            FirebaseDatabase.getInstance()
                    .getReference(Constants.TABLE_CATEGORIES).child(model.getId())
                    .removeValue().addOnCompleteListener(task -> {
                        dismissCustomProgress();
                        if (task.isSuccessful()) {
                            Functions.toast(this, getString(R.string.remove_category_successfully));
                            onBackPressed();
                        } else {
                            Functions.snackBar(this, binding.getRoot(), getString(R.string.error));
                        }
                    });
        }
    }

    private void addCategoryRequest() {
        if (isNotEmpty(binding.tvName, binding.name)
                && isNotEmpty(binding.tvDescription, binding.description)
        ) {
            showCustomProgress();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constants.TABLE_CATEGORIES).push();
            Category category = new Category(
                    reference.getKey(),
                    getText(binding.name),
                    getText(binding.description),
                    "topic_" + reference.getKey(),
                    System.currentTimeMillis() / 1000,
                    Functions.getUserId()
            );
            reference.setValue(category).addOnCompleteListener(task -> {
                dismissCustomProgress();
                if (task.isSuccessful()) {
                    Functions.toast(this, getString(R.string.add_category_successfully));
                    onBackPressed();
                } else {
                    Functions.snackBar(this, binding.getRoot(), getString(R.string.error));
                }
            });
        }
    }

}