package com.medicare_service.controller.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.FirebaseDatabase;
import com.medicare_service.R;
import com.medicare_service.controller.activities.other.MainActivity;
import com.medicare_service.databinding.FragmentProfileBinding;
import com.medicare_service.helpers.BaseFragment;
import com.medicare_service.helpers.Constants;
import com.medicare_service.helpers.Functions;
import com.orhanobut.hawk.Hawk;

@SuppressLint("SetTextI18n")
public class ProfileFragment extends BaseFragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    FragmentProfileBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }


    private void initView() {
        binding.birthDate.setOnClickListener(v -> datePickerDialog(binding.birthDate));
        binding.firstName.setText(user.getFirstName());
        binding.fatherName.setText(user.getFatherName());
        binding.familyName.setText(user.getFamilyName());
        binding.birthDate.setText(user.getDateOfBirth());
        binding.address.setText(user.getAddress());
        binding.phone.setText(user.getPhone());
        binding.email.setText(user.getEmail());
        binding.accountType.setText(user.getType());

        binding.save.setOnClickListener(v -> {
            if (Functions.isInternetAvailable(requireActivity())) {
                Functions.getDeviceToken(this::updateProfileRequest);
            } else {
                Functions.snackBar(requireActivity(), binding.getRoot());
            }
        });
    }

    private void updateProfileRequest(String deviceToken) {
        if (isNotEmpty(binding.tvFirstName, binding.firstName)
                && isNotEmpty(binding.tvFatherName, binding.fatherName)
                && isNotEmpty(binding.tvFamilyName, binding.familyName)
                && isNotEmpty(binding.tvBirthDate, binding.birthDate)
                && isNotEmpty(binding.tvAddress, binding.address)
                && isNotEmpty(binding.tvPhone, binding.phone)
                && isNotEmpty(binding.tvEmail, binding.email)
                && isValidEmail(binding.tvEmail, binding.email)
        ) {
            showCustomProgress();
            user.setFirstName(getText(binding.firstName));
            user.setFatherName(getText(binding.fatherName));
            user.setFamilyName(getText(binding.familyName));
            user.setAddress(getText(binding.address));
            user.setDateOfBirth(getText(binding.birthDate));
            user.setPhone(getText(binding.phone));
            user.setDeviceToken(deviceToken);
            FirebaseDatabase.getInstance()
                    .getReference(Constants.TABLE_USERS).child(user.getId())
                    .setValue(user).addOnCompleteListener(task -> {
                        dismissCustomProgress();
                        if (task.isSuccessful()) {
                            Hawk.put(Constants.TYPE_USER, user);
                            Hawk.put(Constants.TYPE_DEVICE_TOKEN, user.getDeviceToken());
                            Hawk.put(Constants.TYPE_IS_LOGIN, true);
                            Functions.snackBar(requireActivity(), binding.getRoot(), getString(R.string.profile_update_successfully));

                            MainActivity.email.setText(user.getEmail());
                            MainActivity.userName.setText(user.getFirstName() + " " + user.getFamilyName());
                        } else {
                            Functions.snackBar(requireActivity(), binding.getRoot(), getString(R.string.error));
                        }
                    });
        }
    }
}