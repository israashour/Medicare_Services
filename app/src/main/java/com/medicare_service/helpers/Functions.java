package com.medicare_service.helpers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.medicare_service.R;
import com.medicare_service.controller.activities.auth.SplashActivity;
import com.medicare_service.controller.interfaces.StringInterfaces;
import com.medicare_service.databinding.ContentSnackBarBinding;
import com.medicare_service.databinding.DialogAlertBinding;
import com.medicare_service.model.User;
import com.orhanobut.hawk.Hawk;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Functions {


    public static String formatTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.ENGLISH);
        return format.format(time * 1000);
    }

    public static String toTime(Date date) {
        SimpleDateFormat pattern = new SimpleDateFormat("H:mm aa", Locale.ENGLISH);
        return pattern.format(date.getTime());
    }

    public static String isHeaderVisible(long l) {
        long time = l * 1000;
        return isToday(new Date(time));
    }

    public static String isToday(Date date) {
        SimpleDateFormat pattern = new SimpleDateFormat("MMM, dd yyyy", Locale.ENGLISH);
        Calendar currentTime = Calendar.getInstance();
        boolean isSameDay = pattern.format(date).equals(pattern.format(currentTime.getTime()));
        if (isSameDay) {
            return "Today";
        } else {
            currentTime.add(Calendar.DATE, -1);
            boolean isYesterday = pattern.format(date).equals(pattern.format(currentTime.getTime()));
            if (isYesterday) {
                return "Yesterday";
            } else {
                return pattern.format(date.getTime());
            }
        }
    }

    public static void logout(Activity activity) {
        Functions.pushDeviceToken(getUserId(), "");
        Hawk.delete(Constants.TYPE_USER);
        Hawk.delete(Constants.TYPE_IS_LOGIN);
        Intent intent = new Intent(activity, SplashActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public static User getUser() {
        return Hawk.get(Constants.TYPE_USER);
    }

    public static String getUserId() {
        User user = Hawk.get(Constants.TYPE_USER);
        return user.getId();
    }

    public static String getDeviceToken() {
        User user = Hawk.get(Constants.TYPE_USER);
        return user.getDeviceToken();
    }

    public static void toast(Activity activity, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }

    public static void snackBar(View rootLayout, String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_INDEFINITE).show();
    }

    public static void snackBar(Activity activity, View rootLayout) {
        Snackbar snackBar = Snackbar.make(rootLayout, "", Snackbar.LENGTH_INDEFINITE);
        ContentSnackBarBinding binding = ContentSnackBarBinding.inflate(activity.getLayoutInflater());
        snackBar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackBarLayout = (Snackbar.SnackbarLayout) snackBar.getView();
        snackBarLayout.addView(binding.getRoot(), 0);
        snackBar.show();
        binding.button.setOnClickListener(v -> snackBar.dismiss());
    }

    public static void snackBar(Activity activity, View rootLayout, String message) {
        Snackbar snackBar = Snackbar.make(rootLayout, "", Snackbar.LENGTH_INDEFINITE);
        ContentSnackBarBinding binding = ContentSnackBarBinding.inflate(activity.getLayoutInflater());
        snackBar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackBarLayout = (Snackbar.SnackbarLayout) snackBar.getView();
        snackBarLayout.addView(binding.getRoot(), 0);
        snackBar.show();
        binding.title.setText(message);
        binding.button.setOnClickListener(v -> snackBar.dismiss());
    }

    public static void snackBar(Activity activity, View rootLayout, String message, boolean isLongShowed) {
        int duration = Snackbar.LENGTH_INDEFINITE;
        if (!isLongShowed) {
            duration = Snackbar.LENGTH_LONG;
        }
        Snackbar snackBar = Snackbar.make(rootLayout, "", duration);
        ContentSnackBarBinding binding = ContentSnackBarBinding.inflate(activity.getLayoutInflater());
        snackBar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackBarLayout = (Snackbar.SnackbarLayout) snackBar.getView();
        snackBarLayout.addView(binding.getRoot(), 0);
        snackBar.show();
        binding.title.setText(message);
        binding.button.setOnClickListener(v -> snackBar.dismiss());
    }

    public static void dialog(Activity activity, String okText, String message, StringInterfaces interfaces) {
        DialogAlertBinding binding = DialogAlertBinding.inflate(activity.getLayoutInflater());
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.show();
        binding.ok.setText(okText);
        binding.message.setText(message);
        binding.cancel.setOnClickListener(v -> dialog.dismiss());
        binding.ok.setOnClickListener(v -> {
            dialog.dismiss();
            interfaces.onResult("");
        });
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState()
                == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() ==
                        NetworkInfo.State.CONNECTED);
    }

    public static void matchLayoutParams(View view, int width) {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        layoutParams.width = width;
        view.setLayoutParams(layoutParams);
    }

    public static void getDeviceToken(StringInterfaces interfaces) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                interfaces.onResult(task.getResult());
            }
        });
    }

    public static void pushDeviceToken(String userId, String deviceToken) {
        FirebaseDatabase.getInstance()
                .getReference(Constants.TABLE_USERS).child(userId).child("deviceToken")
                .setValue(deviceToken);
    }

    public static void subscribeToTopic(Activity activity, View root, String topic, String groupName) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Functions.snackBar(
                                activity,
                                root,
                                activity.getString(R.string.subscribe_group) + " (" + groupName + ") بنجاح"
                        );
                    } else {
                        Functions.snackBar(activity, root, activity.getString(R.string.error));
                    }
                });
    }

    public static void unsubscribeFromTopic(Activity activity, View root, String topic, String groupName) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Functions.snackBar(
                        activity,
                        root,
                        activity.getString(R.string.un_subscribe_group) + " (" + groupName + ") بنجاح"
                );
            } else {
                Functions.snackBar(activity, root, activity.getString(R.string.error));
            }
        });
    }


    public static void sendNotificationRequest(Activity activity, JSONObject to) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, Constants.NOTIFICATION_URL, to,
                response -> {

                },
                error -> {

                }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "key=" + Constants.SERVER_KEY);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(request);
    }

}
