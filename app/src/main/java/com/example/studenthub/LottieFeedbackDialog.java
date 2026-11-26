package com.example.studenthub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class LottieFeedbackDialog {

    public interface OnDismissListener {
        void onDismiss();
    }

    public static void showSuccess(Context context, String title, String message, OnDismissListener listener) {
        show(context, R.raw.success, title, message, listener);
    }

    public static void showError(Context context, String title, String message, OnDismissListener listener) {
        show(context, R.raw.error, title, message, listener);
    }

    private static void show(Context context, int rawResId, String title, String message, OnDismissListener listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_lottie_feedback, null);

        LottieAnimationView lottieView = view.findViewById(R.id.lottieFeedbackView);
        TextView tvTitle = view.findViewById(R.id.tvFeedbackTitle);
        TextView tvMessage = view.findViewById(R.id.tvFeedbackMessage);
        MaterialButton btnClose = view.findViewById(R.id.btnFeedbackClose);

        lottieView.setAnimation(rawResId);
        lottieView.playAnimation();

        tvTitle.setText(title);
        tvMessage.setText(message);

        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setView(view)
                .setCancelable(false)
                .create();

        btnClose.setOnClickListener(v -> {
            dialog.dismiss();
            if (listener != null) {
                listener.onDismiss();
            }
        });

        dialog.show();
    }
}
