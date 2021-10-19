package africa.collect.android.Core.Fragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import africa.collect.android.Listeners.OnSuccess;
import africa.collect.android.LiveData.CheckStatus;
import africa.collect.android.Model.ChargeResponse;
import africa.collect.android.R;

public class Success extends BottomSheetDialogFragment {
    OnSuccess onSuccess;
    int amount;
    String  reference,companyName, public_key;
    CheckStatus status;

    //    Views
    View view;
    TextView companyNameText,amountText,desc,referenceText, redirectText, timer, status_text;
    ProgressBar progressBar;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.SheetDialog);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.success_no_check, container, false);
        initUI();
        checkStatus();
        return view;
    }

    private void checkStatus() {

        amountText.setText(getString(R.string.amount_text, formatAmount(amount/100)));

        //company name
        companyNameText.setText(getString(R.string.company_name_text, companyName));
        //desc
        desc.setText(getString(R.string.succ_desc, String.valueOf(amount/100), companyName));

        referenceText.setText(reference);
        beginCountDown(timer, 5);

    }

    public String formatAmount (double amt){
        DecimalFormat decim = new DecimalFormat("#,###.##");
        return decim.format(amt);
    }
    private void initUI() {
        companyNameText = view.findViewById(R.id.company_name);
        amountText = view.findViewById(R.id.amt_text);
        desc = view.findViewById(R.id.desc);
        referenceText = view.findViewById(R.id.ref);
        redirectText = view.findViewById(R.id.timer);
        timer = view.findViewById(R.id.timer);
        status_text = view.findViewById(R.id.status_text);
        progressBar = view.findViewById(R.id.progressBar);
        getDialog().setCancelable(false);
    }

    public void beginCountDown(TextView timer, int second){
        timer.setVisibility(View.VISIBLE);
        int tMilliseconds = (int) (second  * 1000);
        new CountDownTimer(tMilliseconds, 1000) {
            public void onTick(long millisUntilFinished) {
                // Used for formatting digit to be in 2 digits only
                NumberFormat f = new DecimalFormat("00");
                long hour = (millisUntilFinished / 3600000) % 24;
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                timer.setText("Redirecting in"+ " "+f.format(sec));
            }
            public void onFinish() {
                onSuccess.OnSuccess(reference, amount/100);
                dismiss();
            }
        }.start();

    }

    public Success(OnSuccess onSuccess, String  companyName, int amount, String reference) {
        this.onSuccess = onSuccess;
        this.companyName = companyName;
        this.amount = amount;
        this.reference = reference;
    }

    private void setupFullHeight(BottomSheetDialog bottomSheetDialog) {
        FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();

        int windowHeight = getWindowHeight();
        if (layoutParams != null) {
            layoutParams.height = windowHeight;
        }
        bottomSheet.setLayoutParams(layoutParams);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private int getWindowHeight() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels - 50;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override public void onShow(DialogInterface dialogInterface) {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                setupFullHeight(bottomSheetDialog);
            }
        });
        return  dialog;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() !=null){
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() !=null){
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        }
    }
}
