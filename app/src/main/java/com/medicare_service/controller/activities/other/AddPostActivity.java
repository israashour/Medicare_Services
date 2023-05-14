package com.medicare_service.controller.activities.other;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.medicare_service.R;
import com.medicare_service.helpers.BaseActivity;
import com.medicare_service.databinding.ActivityAddPostBinding;
import com.medicare_service.helpers.Constants;
import com.medicare_service.helpers.Functions;
import com.medicare_service.model.Category;
import com.medicare_service.model.Post;
import com.medicare_service.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

@SuppressLint("SetTextI18n")
public class AddPostActivity extends BaseActivity {

    ActivityAddPostBinding binding;
    String from = Constants.TYPE_ADD;
    Post post = null;
    User user = Functions.getUser();
    Category category;
    String doctorName = "";

    String fileType = "";
    String filePath = "";
    Uri file = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        from = getIntent().getStringExtra(Constants.TYPE_FROM);
        doctorName = user.getFirstName() + " " + user.getFamilyName();
        switch (from) {
            case Constants.TYPE_ADD:
                category = (Category) getIntent().getSerializableExtra(Constants.TYPE_MODEL);
                binding.category.setText(category.getTitle());
                binding.appbar.title.setText(getString(R.string.add_new_post));
                binding.send.setText(getString(R.string.add));
                binding.send.setOnClickListener(v -> {
                    if (isNotEmpty(binding.tvName, binding.name)
                            && isNotEmpty(binding.tvDescription, binding.description)
                    ) {
                        if (file != null) {
                            switch (fileType) {
                                case "image":
                                    filePath = "ImageFiles";
                                    break;
                                case "video":
                                    filePath = "VideoFiles";
                                    break;
                                case "audio":
                                    filePath = "AudioFiles";
                                    break;
                                case "file":
                                    filePath = "OtherFiles";
                                    break;
                            }
                            uploadFileRequest(filePath);
                        } else {
                            showCustomProgress();
                            addPostRequest("");
                        }
                    }
                });
                binding.uploadContainer.setOnClickListener(v -> dialog());
                break;
            case Constants.TYPE_EDIT:
                post = (Post) getIntent().getSerializableExtra(Constants.TYPE_MODEL);
                getPostForEdit();
                break;
        }
        onViewClicked();
    }

    private void onViewClicked() {
        binding.appbar.back.setOnClickListener(v -> finish());
        binding.closeImage.setOnClickListener(v -> {
            file = null;
            fileType = "";
            if (post != null) {
                post.setFileType("");
                post.setFile("");
            }
            binding.uploadContainer.setVisibility(View.VISIBLE);
            binding.imageContainer.setVisibility(View.GONE);
            binding.fileContainer.setVisibility(View.GONE);
        });
        binding.closeFile.setOnClickListener(v -> {
            file = null;
            fileType = "";
            if (post != null) {
                post.setFileType("");
                post.setFile("");
            }
            binding.uploadContainer.setVisibility(View.VISIBLE);
            binding.imageContainer.setVisibility(View.GONE);
            binding.fileContainer.setVisibility(View.GONE);
        });
    }

    private void getPostForEdit() {
        binding.name.setText(post.getTitle());
        binding.description.setText(post.getContent());
        binding.category.setText(post.getCategoryName());
        switch (post.getFileType()) {
            case "image":
                binding.uploadContainer.setVisibility(View.GONE);
                binding.imageContainer.setVisibility(View.VISIBLE);
                Glide.with(this).load(post.getFile()).into(binding.image);
                break;
            case "video":
                binding.uploadContainer.setVisibility(View.GONE);
                binding.fileContainer.setVisibility(View.VISIBLE);
                binding.fileName.setText(post.getFile());
                binding.fileImage.setImageResource(R.drawable.ic_video_file);
                break;
            case "audio":
                binding.uploadContainer.setVisibility(View.GONE);
                binding.fileContainer.setVisibility(View.VISIBLE);
                binding.fileName.setText(post.getFile());
                binding.fileImage.setImageResource(R.drawable.ic_sound_file);
                break;
            case "file":
                binding.uploadContainer.setVisibility(View.GONE);
                binding.fileContainer.setVisibility(View.VISIBLE);
                binding.fileName.setText(post.getFile());
                binding.fileImage.setImageResource(R.drawable.ic_file);
                break;
            default:
                binding.uploadContainer.setVisibility(View.VISIBLE);
                binding.uploadContainer.setVisibility(View.GONE);
                binding.imageContainer.setVisibility(View.GONE);
                break;
        }

        binding.appbar.title.setText(getString(R.string.edit_post));
        binding.send.setText(getString(R.string.edit));
        binding.send.setOnClickListener(v -> {
            if (isNotEmpty(binding.tvName, binding.name)
                    && isNotEmpty(binding.tvDescription, binding.description)
            ) {
                if (file != null) {
                    switch (fileType) {
                        case "image":
                            filePath = "ImageFiles";
                            break;
                        case "video":
                            filePath = "VideoFiles";
                            break;
                        case "audio":
                            filePath = "AudioFiles";
                            break;
                        case "file":
                            filePath = "OtherFiles";
                            break;
                    }
                    uploadFileRequest(filePath);
                } else {
                    showCustomProgress();
                    editPostRequest(post.getFile(), post.getFileType());
                }
            }
        });
        binding.remove.setVisibility(View.VISIBLE);
        binding.remove.setOnClickListener(v -> {
            Functions.dialog(this, getString(R.string.remove), getString(R.string.remove_message), str -> {
                removePostsRequest();
            });
        });
    }

    private void editPostRequest(String file, String fileType) {
        post.setTitle(getText(binding.name));
        post.setContent(getText(binding.description));
        post.setDoctorName(doctorName);
        post.setFile(file);
        post.setFileType(fileType);
        FirebaseDatabase.getInstance().getReference(Constants.TABLE_POSTS).child(post.getId())
                .setValue(post).addOnCompleteListener(task -> {
                    dismissCustomProgress();
                    if (task.isSuccessful()) {
                        Functions.toast(this, getString(R.string.edit_category_successfully));
                        onBackPressed();
                    } else {
                        Functions.snackBar(this, binding.getRoot(), getString(R.string.error));
                    }
                });
    }

    private void removePostsRequest() {
        showCustomProgress();
        FirebaseDatabase.getInstance().getReference(Constants.TABLE_POSTS).child(post.getId())
                .removeValue().addOnCompleteListener(task -> {
                    dismissCustomProgress();
                    if (task.isSuccessful()) {
                        Functions.toast(this, getString(R.string.remove_post_successfully));
                        onBackPressed();
                    } else {
                        Functions.snackBar(this, binding.getRoot(), getString(R.string.error));
                    }
                });
    }

    private void addPostRequest(String file) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constants.TABLE_POSTS).push();
        Post post = new Post(
                reference.getKey(),
                category.getId(),
                category.getTitle(),
                getText(binding.name),
                getText(binding.description),
                file,
                fileType,
                System.currentTimeMillis() / 1000,
                user.getId(),
                user.getId(),
                doctorName
        );
        reference.setValue(post).addOnCompleteListener(task -> {
            dismissCustomProgress();
            if (task.isSuccessful()) {
                initNotification("تم اضافة منشور جديد في موضوع " + category.getTitle(), getText(binding.name));
                Functions.toast(this, getString(R.string.add_post_successfully));
                onBackPressed();
            } else {
                Functions.snackBar(this, binding.getRoot(), getString(R.string.error));
            }
        });
    }

    private void uploadFileRequest(String pathName) {
        try {
            showCustomProgress();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(pathName)
                    .child(category.getTitle() + "_" + UUID.randomUUID().toString() + getFileType(file));
            storageReference.putFile(file).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        if (from.equals(Constants.TYPE_ADD)) {
                            addPostRequest(downloadUrl);
                        } else {
                            editPostRequest(downloadUrl, fileType);
                        }
                    }).addOnFailureListener(e -> {
                        dismissCustomProgress();
                        Functions.snackBar(this, binding.getRoot(), getString(R.string.error), false);
                    });
                } else {
                    dismissCustomProgress();
                    Functions.snackBar(this, binding.getRoot(), getString(R.string.error), false);
                }
            });
        } catch (Exception e) {
            dismissCustomProgress();
            Functions.snackBar(this, binding.getRoot(), getString(R.string.error) + "\n الملف غير صالح", false);
        }
    }

    private void initNotification(String title, String body) {
        JSONObject to = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            data.put("title", title);
            data.put("body", body);
            to.put("to", "/topics/" + category.getTopic());
            to.put("data", data);
            Functions.sendNotificationRequest(this, to);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void dialog() {
        CharSequence[] options = new CharSequence[]{"صورة", "فيديو", "صوت", "ملف"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("اختر نوع الملف");
        builder.setPositiveButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
        builder.setItems(options, (dialog, which) -> {
            Intent intent = new Intent();
            switch (which) {
                case 0:
                    fileType = "image";
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    imageResultLauncher.launch(intent);
                    break;
                case 1:
                    fileType = "video";
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("video/*");
                    videoResultLauncher.launch(intent);
                    break;
                case 2:
                    fileType = "audio";
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("audio/*");
                    audioResultLauncher.launch(intent);
                    break;
                case 3:
                    fileType = "file";
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    fileResultLauncher.launch(intent);
                    break;
            }
        });

        builder.show();
    }

    private String getFileType(Uri videoUri) {
        ContentResolver r = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return "." + mimeTypeMap.getExtensionFromMimeType(r.getType(videoUri));
    }

    ActivityResultLauncher<Intent> imageResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                try {
                    file = result.getData().getData();
                    binding.uploadContainer.setVisibility(View.GONE);
                    binding.imageContainer.setVisibility(View.VISIBLE);
                    binding.image.setImageURI(file);
                } catch (Exception ignored) {
                }
            });

    ActivityResultLauncher<Intent> videoResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                try {
                    file = result.getData().getData();
                    binding.uploadContainer.setVisibility(View.GONE);
                    binding.fileContainer.setVisibility(View.VISIBLE);
                    binding.fileName.setText(file.getPath() + getFileType(file));
                    binding.fileImage.setImageResource(R.drawable.ic_video_file);
                } catch (Exception ignored) {
                }
            });

    ActivityResultLauncher<Intent> audioResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                try {
                    file = result.getData().getData();
                    binding.uploadContainer.setVisibility(View.GONE);
                    binding.fileContainer.setVisibility(View.VISIBLE);
                    binding.fileName.setText(file.getPath() + getFileType(file));
                    binding.fileImage.setImageResource(R.drawable.ic_sound_file);
                } catch (Exception ignored) {
                }
            });

    ActivityResultLauncher<Intent> fileResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                try {
                    file = result.getData().getData();
                    binding.uploadContainer.setVisibility(View.GONE);
                    binding.fileContainer.setVisibility(View.VISIBLE);
                    binding.fileName.setText(file.getPath() + getFileType(file));
                    binding.fileImage.setImageResource(R.drawable.ic_file);
                } catch (Exception ignored) {
                }
            });

}