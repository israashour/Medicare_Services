package com.medicare_service.helpers;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.medicare_service.R;
import com.medicare_service.model.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class BaseFragment extends Fragment {

    protected User user = Functions.getUser();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleUtils.onCreate(requireActivity(), "ar");
    }

    @Override
    public void onResume() {
        super.onResume();
        LocaleUtils.onCreate(requireActivity(), "ar");
        user = Functions.getUser();
    }

    protected Dialog dialog;

    protected void showCustomProgress() {
        if (dialog == null) {
            dialog = new Dialog(requireActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            dialog.setContentView(R.layout.content_loading_screen);
            dialog.setCancelable(true);
            dialog.show();
        } else {
            dialog.show();
        }
    }

    protected void dismissCustomProgress() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    protected void startActivity(Class<?> activity) {
        Intent intent = new Intent(requireActivity(), activity);
        startActivity(intent);
    }

    protected void datePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int cYear = calendar.get(Calendar.YEAR);
        int cMonth = calendar.get(Calendar.MONTH);
        int cDay = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(),
                R.style.Theme_MedicareService_StyleMaterialCalendar, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            editText.setText(sdf.format(calendar.getTime()));
        }, cYear, cMonth, cDay);
        datePickerDialog.show();
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
    }

    protected boolean isNotEmpty(TextInputLayout textLayout, TextInputEditText editText) {
        if (Objects.requireNonNull(editText.getText()).toString().trim().isEmpty()) {
            textLayout.setError("");
            textLayout.setError(getString(R.string.invalid_field));
            textLayout.setErrorEnabled(true);
            return false;
        } else {
            textLayout.setErrorEnabled(false);
            return true;
        }
    }

    protected boolean isValidEmail(TextInputLayout textLayout, TextInputEditText editText) {
        if (!Patterns.EMAIL_ADDRESS.matcher(Objects.requireNonNull(editText.getText()).toString()).matches()) {
            textLayout.setError(getString(R.string.invalid_email));
            textLayout.setErrorEnabled(true);
            return false;
        } else {
            textLayout.setErrorEnabled(false);
            return true;
        }
    }

    protected String getText(TextInputEditText editText) {
        return Objects.requireNonNull(editText.getText()).toString().trim();
    }
    protected String getText(EditText editText) {
        return Objects.requireNonNull(editText.getText()).toString().trim();
    }

}
