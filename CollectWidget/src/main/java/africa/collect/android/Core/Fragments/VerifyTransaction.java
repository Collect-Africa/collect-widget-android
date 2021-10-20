package africa.collect.android.Core.Fragments;

import static africa.collect.android.Utils.Constants.REPORT;
import static africa.collect.android.Utils.Constants.TAG;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import africa.collect.android.Listeners.OnFailed;
import africa.collect.android.Listeners.OnSuccess;
import africa.collect.android.LiveData.CheckStatus;
import africa.collect.android.Model.ChargeResponse;
import africa.collect.android.R;

public class VerifyTransaction extends BottomSheetDialogFragment {
    OnSuccess onSuccess;
    int amount;
    String  reference,companyName, public_key, environment,email;
    CheckStatus status;
    OnFailed onFailed;

//    Views
    View view;
    TextView companyNameText,amountText,desc,referenceText, redirectText, timer, status_text;
    ProgressBar progressBar;
    ConstraintLayout constraintLayout;
    ImageView statusImg;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.SheetDialog);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.success, container, false);
        initUI();
        checkStatus();
        return view;
    }

    private void checkStatus() {
        new africa.collect.android.Utils.Analytics().Track(getContext(), "Payment Processing",  "email", email);
        amountText.setText(getString(R.string.amount_text, formatAmount(amount/100)));
        //company name
        companyNameText.setText(getString(R.string.company_name_text, companyName));

        status = ViewModelProviders.of(this).get(CheckStatus.class);
        status.getStatus().observe(this, new Observer<ChargeResponse>() {
            @Override
            public void onChanged(ChargeResponse chargeResponse) {

                if (chargeResponse == null){
                    sendTransactionReport("network_error");
                }else if (chargeResponse.getData()==null){
                    sendTransactionReport(chargeResponse.getCheckoutInitError().getMessage());
                }else{
                    if(chargeResponse.getData().getStatus().equalsIgnoreCase("success")){
                        progressBar.setVisibility(View.GONE);
                        status_text.setVisibility(View.GONE);
                        constraintLayout.setVisibility(View.VISIBLE);

                        //desc
                        desc.setText(getString(R.string.succ_desc, String.valueOf(amount/100), companyName));

                        referenceText.setText(reference);
                        beginCountDown(timer, 5, 1);
                    }else if (chargeResponse.getData().getStatus().equalsIgnoreCase("failed")){
                        //show failed
                        statusImg.setImageResource(R.drawable.ic_outline_cancel_24);
                        status_text.setText(R.string.payment_failed_txt);
                        desc.setText(getString(R.string.failed_desc, String.valueOf(amount/100)));
                        beginCountDown(timer, 5, 0);
                    }
                }

            }
        });

        status.getTransferStatus(public_key, reference,environment);


    }

    public String formatAmount (double amt){
        DecimalFormat decim = new DecimalFormat("#,###.##");
        return decim.format(amt);
    }

    public void sendTransactionReport(String s){
        Bundle result = new Bundle();
        result.putString(REPORT, s);
        getParentFragmentManager().setFragmentResult(TAG, result);
        dismiss();
    }

    private void initUI() {
        companyNameText = view.findViewById(R.id.company_name);
        amountText = view.findViewById(R.id.amt_text);
        constraintLayout = view.findViewById(R.id.success_content);
        desc = view.findViewById(R.id.desc);
        referenceText = view.findViewById(R.id.ref);
        redirectText = view.findViewById(R.id.timer);
        timer = view.findViewById(R.id.timer);
        status_text = view.findViewById(R.id.status_text);
        progressBar = view.findViewById(R.id.progressBar);
        getDialog().setCanceledOnTouchOutside(false);
        statusImg = view.findViewById(R.id.imageView2);
    }

    public void beginCountDown(TextView timer, int second, int type){
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
                if (type == 1){
                    onSuccess.OnSuccess(reference, amount/100);
                    dismiss();
                }else{
                    onFailed.OnFailed("payment failed");
                    dismiss();
                }

            }
        }.start();

    }

    public VerifyTransaction(OnSuccess onSuccess, OnFailed onFailed, String  companyName, int amount, String reference, String public_key, String environment, String email) {
        this.onSuccess = onSuccess;
        this.companyName = companyName;
        this.amount = amount;
        this.reference = reference;
        this.public_key =public_key;
        this.onFailed = onFailed;
        this.environment = environment;
        this.email = email;
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
