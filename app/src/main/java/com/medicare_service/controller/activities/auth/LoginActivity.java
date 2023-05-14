package com.medicare_service.controller.activities.auth;

import androidx.annotation.NonNull;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medicare_service.R;
import com.medicare_service.controller.activities.other.MainActivity;
import com.medicare_service.databinding.ActivityLoginBinding;
import com.medicare_service.helpers.BaseActivity;
import com.medicare_service.helpers.Constants;
import com.medicare_service.helpers.Functions;
import com.medicare_service.model.User;
import com.orhanobut.hawk.Hawk;

import java.util.Objects;

public class LoginActivity extends BaseActivity {

    ActivityLoginBinding binding;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        statusBarColor(R.color.white);
        statusBarsLight(true, binding.logo);
        binding.signUp.setOnClickListener(v -> startActivity(RegisterActivity.class));
        binding.login.setOnClickListener(v -> Functions.getDeviceToken(this::loginRequest));
    }

    private void loginRequest(String deviceToken) {
        if (isNotEmpty(binding.tvEmail, binding.email)
                && isValidEmail(binding.tvEmail, binding.email)
                && isNotEmpty(binding.tvPassword, binding.password)
                && isValidPassword(binding.tvPassword, binding.password)
        ) {
            showCustomProgress();
            mAuth.signInWithEmailAndPassword(
                    getText(binding.email),
                    getText(binding.password)
            ).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                    getUserData(userId, deviceToken);
                } else {
                    dismissCustomProgress();
                    snackBar(binding.getRoot(), getString(R.string.error), true);
                }
            });
        }
    }

    private void getUserData(String userId, String deviceToken) {
        FirebaseDatabase.getInstance().getReference()
                .child(Constants.TABLE_USERS).child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dismissCustomProgress();
                        if (snapshot.exists()) {
                            Functions.pushDeviceToken(userId, deviceToken);
                            User user = snapshot.getValue(User.class);
                            Hawk.put(Constants.TYPE_USER, user);
                            Hawk.put(Constants.TYPE_DEVICE_TOKEN, deviceToken);
                            Hawk.put(Constants.TYPE_IS_LOGIN, true);
                            startActivity(MainActivity.class);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        dismissCustomProgress();
                        snackBar(binding.getRoot(), getString(R.string.error), true);
                    }
                });

    }

}