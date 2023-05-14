package com.medicare_service.controller.activities.other;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.medicare_service.R;
import com.medicare_service.controller.fragments.CategoryFragment;
import com.medicare_service.controller.fragments.ChatsFragment;
import com.medicare_service.controller.fragments.InterestsFragment;
import com.medicare_service.controller.fragments.NotificationFragment;
import com.medicare_service.controller.fragments.ProfileFragment;
import com.medicare_service.databinding.ActivityMainBinding;
import com.medicare_service.helpers.BaseActivity;
import com.medicare_service.helpers.Constants;
import com.medicare_service.helpers.Functions;
import com.medicare_service.model.Category;

import java.util.function.BiConsumer;

@SuppressLint({"NonConstantResourceId", "SetTextI18n", "StaticFieldLeak"})
public class MainActivity extends BaseActivity {

    ActivityMainBinding binding;
    public static TextView userName, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {

        binding.appbar.menu.setOnClickListener(v -> {
            if (binding.drawer.isOpen()) {
                binding.drawer.close();
            } else {
                binding.drawer.open();
            }
        });

        if (user.getType().equals(Constants.TYPE_DOCTOR)) {
            binding.navView.inflateMenu(R.menu.nav_menu_doctor);
        } else {
            binding.navView.inflateMenu(R.menu.nav_menu_patient);
        }
        View hView = binding.navView.getHeaderView(0);
        userName = hView.findViewById(R.id.userName);
        email = hView.findViewById(R.id.email);
        email.setText(user.getEmail());
        userName.setText(user.getFirstName() + " " + user.getFamilyName());

        binding.navView.setCheckedItem(R.id.nav_categories);
        replaceFragment(new CategoryFragment(), R.string.categories);
        initNavViewListener();
        subscribeToTopic();
    }

    private void initNavViewListener() {
        binding.navView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_categories:
                    itemHome = 1;
                    replaceFragment(new CategoryFragment(), R.string.categories);
                    break;
                case R.id.nav_interests:
                    itemHome = 2;
                    replaceFragment(new InterestsFragment(), R.string.interests);
                    break;
                case R.id.nav_chats:
                    itemHome = 2;
                    replaceFragment(new ChatsFragment(), R.string.chats);
                    break;
                case R.id.nav_notifications:
                    itemHome = 2;
                    replaceFragment(new NotificationFragment(), R.string.notifications);
                    break;
                case R.id.nav_profile:
                    itemHome = 2;
                    replaceFragment(new ProfileFragment(), R.string.my_profile);
                    break;
                case R.id.nav_logout:
                    Functions.dialog(this, getString(R.string.logout), getString(R.string.logout_message), v -> {
                        unSubscribeToTopic();
                        Functions.logout(this);
                    });
                    return false;
            }
            binding.navView.setCheckedItem(item.getItemId());
            binding.drawer.close();
            return false;
        });
    }

    private void replaceFragment(Fragment fragment, int title) {
        binding.appbar.title.setText(getString(title));
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.host_fragment, fragment);
        transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.commit();
    }

    private void subscribeToTopic() {
        if (user.getInterests() != null) {
            user.getInterests().forEach(new BiConsumer<String, Object>() {
                @Override
                public BiConsumer<String, Object> andThen(BiConsumer<? super String, ? super Object> after) {
                    return BiConsumer.super.andThen(after);
                }

                @Override
                public void accept(String s, Object object) {
                    Log.e("response", "object: " + object);
                    Gson gson = new Gson();
                    String json = gson.toJson(object);
                    Category categoryObject = gson.fromJson(json, Category.class);
                    FirebaseMessaging.getInstance().subscribeToTopic(categoryObject.getTopic());
                }
            });

        }
    }

    private void unSubscribeToTopic() {
        if (user.getInterests() != null) {
            user.getInterests().forEach(new BiConsumer<String, Object>() {
                @Override
                public BiConsumer<String, Object> andThen(BiConsumer<? super String, ? super Object> after) {
                    return BiConsumer.super.andThen(after);
                }

                @Override
                public void accept(String s, Object object) {
                    Log.e("response", "object: " + object);
                    Gson gson = new Gson();
                    String json = gson.toJson(object);
                    Category categoryObject = gson.fromJson(json, Category.class);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(categoryObject.getTopic());
                }
            });

        }
    }

    private Toast backToasty;
    private long backPressedTime;
    private int itemHome = 1;

    @Override
    public void onBackPressed() {
        if (binding.drawer.isOpen()) {
            binding.drawer.close();
        } else {
            if (itemHome == 2) {
                itemHome = 1;
                binding.navView.setCheckedItem(R.id.nav_categories);
                replaceFragment(new CategoryFragment(), R.string.categories);
            } else {
                if (backPressedTime + 2000 > System.currentTimeMillis()) {
                    backToasty.cancel();
                    finishAffinity();
                    return;
                } else {
                    backToasty = Toast.makeText(this, getString(R.string.back_exit), Toast.LENGTH_SHORT);
                    backToasty.show();
                }
                backPressedTime = System.currentTimeMillis();
            }
        }
    }
}