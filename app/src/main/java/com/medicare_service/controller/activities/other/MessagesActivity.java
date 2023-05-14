package com.medicare_service.controller.activities.other;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medicare_service.controller.adapters.MessageAdapter;
import com.medicare_service.databinding.ActivityMessagesBinding;
import com.medicare_service.helpers.BaseActivity;
import com.orhanobut.hawk.Hawk;
import com.medicare_service.helpers.Constants;
import com.medicare_service.helpers.Functions;
import com.medicare_service.model.Message;
import com.medicare_service.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

@SuppressLint("SetTextI18n")
public class MessagesActivity extends BaseActivity {

    ActivityMessagesBinding binding;
    MessageAdapter adapter;
    User userTo;
    String userId = Functions.getUserId();
    User curUser = Functions.getUser();
    String userName = curUser.getFirstName() + " " + curUser.getFamilyName();
    ArrayList<Message> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessagesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        Hawk.put(Constants.TYPE_IS_MESSAGES_SCREEN_OPENED, true);

        userTo = (User) getIntent().getSerializableExtra(Constants.TYPE_MODEL);
        binding.userName.setText(userTo.getFirstName() + " " + userTo.getFamilyName());
        binding.imgBack.setOnClickListener(v -> finish());
        getUserToData();
        adapter = new MessageAdapter(this);
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
                .getReference(Constants.TABLE_MESSAGES)
                .child(userId).child(userTo.getId()).push();

        DatabaseReference receivedUser = FirebaseDatabase.getInstance()
                .getReference(Constants.TABLE_MESSAGES)
                .child(userTo.getId()).child(userId);

        Message message = new Message(
                currentUser.getKey(),
                getText(binding.message),
                "",
                "",
                userId,
                userTo.getId(),
                false,
                System.currentTimeMillis() / 1000
        );
        currentUser.setValue(message);
        receivedUser.child(currentUser.getKey()).setValue(message);
        binding.message.setText("");
        sendNotification(userName, message.getMessage(), userTo.getDeviceToken());
    }

    private void getMessagesRequest() {
        FirebaseDatabase.getInstance().getReference()
                .child(Constants.TABLE_MESSAGES)
                .child(userId).child(userTo.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot issue : snapshot.getChildren()) {
                            Message model = issue.getValue(Message.class);
                            list.add(model);
                            FirebaseDatabase.getInstance().getReference()
                                    .child(Constants.TABLE_MESSAGES)
                                    .child(userTo.getId())
                                    .child(userId)
                                    .child(model.getId())
                                    .child("seen")
                                    .setValue(true);
                        }
                        adapter.setData(list);
                        binding.recyclerview.scrollToPosition(adapter.getItemCount() - 1);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void getUserToData() {
        FirebaseDatabase.getInstance().getReference()
                .child(Constants.TABLE_USERS).child(userTo.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            userTo = snapshot.getValue(User.class);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

    }

    private void sendNotification(String title, String body, String token) {
        JSONObject to = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            data.put("title", title);
            data.put("body", body);
            data.put("type", "message");
            to.put("to", token);
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