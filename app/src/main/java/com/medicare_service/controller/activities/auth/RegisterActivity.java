package com.medicare_service.controller.activities.auth;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.medicare_service.R;
import com.medicare_service.controller.activities.other.MainActivity;
import com.medicare_service.databinding.ActivityRegisterBinding;
import com.medicare_service.helpers.BaseActivity;
import com.medicare_service.helpers.Constants;
import com.medicare_service.helpers.Functions;
import com.medicare_service.model.User;
import com.orhanobut.hawk.Hawk;

import java.util.Objects;

public class RegisterActivity extends BaseActivity {

    ActivityRegisterBinding binding;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        binding.accountType.setText(type);
        binding.accountType.setOnClickListener(v ->
                dialog(userType -> {
                    binding.accountType.setText(userType);
                    type = userType;
                })
        );
        binding.appbar.title.setText(getString(R.string.signup));
        binding.appbar.back.setOnClickListener(v -> finish());
        binding.birthDate.setOnClickListener(v -> datePickerDialog(binding.birthDate));
        binding.register.setOnClickListener(v -> Functions.getDeviceToken(this::registerRequest));
    }

    private void registerRequest(String deviceToken) {
        if (isNotEmpty(binding.tvFirstName, binding.firstName)
                && isNotEmpty(binding.tvFatherName, binding.fatherName)
                && isNotEmpty(binding.tvFamilyName, binding.familyName)
                && isNotEmpty(binding.tvBirthDate, binding.birthDate)
                && isNotEmpty(binding.tvAddress, binding.address)
                && isNotEmpty(binding.tvPhone, binding.phone)
                && isNotEmpty(binding.tvEmail, binding.email)
                && isValidEmail(binding.tvEmail, binding.email)
                && isNotEmpty(binding.tvPassword, binding.password)
                && isValidPassword(binding.tvPassword, binding.password)
                && isNotEmpty(binding.tvConfirmPassword, binding.confirmPassword)
                && isValidPassword(binding.tvConfirmPassword, binding.confirmPassword)
                && isNotEmpty(binding.tvAccountType, binding.accountType)
        ) {
            showCustomProgress();
            mAuth.createUserWithEmailAndPassword(
                    getText(binding.email),
                    getText(binding.password)
            ).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                    User user = new User();
                    user.setId(userId);
                    user.setFirstName(getText(binding.firstName));
                    user.setFatherName(getText(binding.fatherName));
                    user.setFamilyName(getText(binding.familyName));
                    user.setAddress(getText(binding.address));
                    user.setDateOfBirth(getText(binding.birthDate));
                    user.setPhone(getText(binding.phone));
                    user.setEmail(getText(binding.email));
                    user.setPassword(getText(binding.password));
                    user.setType(getText(binding.accountType));
                    user.setDeviceToken(deviceToken);
                    setUserDataRequest(user);
                } else {
                    dismissCustomProgress();
                    Functions.snackBar(this, binding.getRoot(), getString(R.string.error));
                }
            });
        }
    }

    // جلب بيانات المستخدم
    private void setUserDataRequest(User user) {
        FirebaseDatabase.getInstance()
                .getReference(Constants.TABLE_USERS).child(user.getId())
                .setValue(user).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Hawk.put(Constants.TYPE_USER, user);
                        Hawk.put(Constants.TYPE_DEVICE_TOKEN, user.getDeviceToken());
                        Hawk.put(Constants.TYPE_IS_LOGIN, true);
                        startActivity(MainActivity.class);
                        finish();
                        dismissCustomProgress();
                    } else {
                        dismissCustomProgress();
                        Functions.snackBar(this, binding.getRoot(), getString(R.string.error));
                    }
                });
    }
}