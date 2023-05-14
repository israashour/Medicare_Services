package com.medicare_service.controller.activities.other;

import androidx.annotation.NonNull;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medicare_service.controller.adapters.GroupMessageAdapter;
import com.medicare_service.databinding.ActivityGroupMessagesBinding;
import com.medicare_service.helpers.BaseActivity;
import com.medicare_service.helpers.Constants;
import com.medicare_service.helpers.Functions;
import com.medicare_service.model.Category;
import com.medicare_service.model.GroupMessage;
import com.orhanobut.hawk.Hawk;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

@SuppressLint("SetTextI18n")
public class GroupMessagesActivity extends BaseActivity {

    ActivityGroupMessagesBinding binding;
    ArrayList<GroupMessage> list = new ArrayList<>();
    GroupMessageAdapter adapter;

    Category category;
    String userName = user.getFirstName() + " " + user.getFamilyName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupMessagesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        Hawk.put(Constants.TYPE_IS_MESSAGES_SCREEN_OPENED, true);

        category = (Category) getIntent().getSerializableExtra(Constants.TYPE_MODEL);
        binding.userName.setText("محادثة مجموعة " + category.getTitle());
        binding.imgBack.setOnClickListener(v -> finish());

        adapter = new GroupMessageAdapter(this);
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.scrollToPosition(adapter.getItemCount() - 1);
        binding.recyclerview.setAdapter(adapter);
        getMessagesRequest();

        binding.imgSent.setOnClickListener(v -> {
            if (!getText(binding.message).isEmpty()) {
                sendMessageRequest();
            } else {
                Functions.snackBar(this, binding.getRoot(), "لا يمكن ارسال رسالة فارغة!", false);
            }
        });
    }

    private void sendMessageRequest() {
        DatabaseReference currentUser = FirebaseDatabase.getInstance()
                .getReference(Constants.TABLE_GROUP_MESSAGES)
                .child(category.getId()).push();

        GroupMessage message = new GroupMessage(
                currentUser.getKey(),
                getText(binding.message),
                userName,
                user.getId(),
                System.currentTimeMillis() / 1000
        );
        currentUser.setValue(message);
        binding.message.setText("");
        sendNotification("محادثة مجموعة " + category.getTitle(), userName + "\n" + message.getMessage());
    }

    private void getMessagesRequest() {
        FirebaseDatabase.getInstance().getReference()
                .child(Constants.TABLE_GROUP_MESSAGES)
                .child(category.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot issue : snapshot.getChildren()) {
                            GroupMessage model = issue.getValue(GroupMessage.class);
                            list.add(model);
                        }
                        adapter.setData(list);
                        binding.recyclerview.scrollToPosition(adapter.getItemCount() - 1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void sendNotification(String title, String body) {
        JSONObject to = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            data.put("title", title);
            data.put("body", body);
            data.put("type", "message");
            to.put("to", "/topics/" + category.getTopic());
            to.put("data", data);
            Functions.sendNotificationRequest(this, to);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        Hawk.put(Constants.TYPE_IS_MESSAGES_SCREEN_OPENED, false);
        super.onStop();
    }
}