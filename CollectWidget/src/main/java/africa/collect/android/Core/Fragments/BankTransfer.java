package africa.collect.android.Core.Fragments;

import static android.content.Context.CLIPBOARD_SERVICE;

import static africa.collect.android.Utils.Constants.REPORT;
import static africa.collect.android.Utils.Constants.TAG;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.caverock.androidsvg.SVG;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;


import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import africa.collect.android.Listeners.OnFailed;
import africa.collect.android.Listeners.OnSuccess;
import africa.collect.android.LiveData.BankTransferViewModel;
import africa.collect.android.LiveData.CheckStatus;
import africa.collect.android.Model.ChargeRequest;
import africa.collect.android.Model.ChargeResponse;
import africa.collect.android.R;
import africa.collect.android.Utils.Constants;
import africa.collect.android.Utils.Enviroment;
import africa.collect.android.Utils.Misc.SvgDecoder;
import africa.collect.android.Utils.Misc.SvgDrawableTranscoder;
import africa.collect.android.Utils.Misc.SvgSoftwareLayerSetter;

public class BankTransfer extends BottomSheetDialogFragment {
    BankTransferViewModel bankTransferViewModel;
    ChargeRequest chargeRequest;
    OnSuccess onSuccess;
    OnFailed onFailed;
    String  businessName;
    int amount, percentChargeCap;
    CheckStatus status;
    String enviroment;
    double percentCharge;

    //   Views
    TextView companyName,amountText, countDownTimer,bankName,accountNumber,accountName,checkStatusText;
    Button goBack,checkStatusButton;
    ImageView bank_logo,copyToClipboard;
    ProgressBar progressBar;
    ConstraintLayout bankInfoLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.SheetDialog);
    }
    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bank_transfer, container, false);
        initUI();

        initData();

        copyToClipboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Collect Africa", accountNumber.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), "Account details copied to clipboard!", Toast.LENGTH_SHORT).show();
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendTransactionReport("dismiss");
            }
        });

        checkStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkStatus(progressBar, bankInfoLayout, chargeRequest.getPublic_key(), chargeRequest.getReference());
            }
        });

//
//        view.setFocusableInTouchMode(true);
//        view.requestFocus();
//        view.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//                if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
//                    Toast.makeText(getContext(), "Hello", Toast)
//                    getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                    return true;
//                }
//                return false;
//            }
//        });
        return view;
    }

    private void checkStatus(ProgressBar progressBar, ConstraintLayout bankInfoLayout, String public_key, String ref) {
        progressBar.setVisibility(View.VISIBLE);
        bankInfoLayout.setVisibility(View.GONE);
        checkStatusButton.setVisibility(View.VISIBLE);
        checkStatusButton.setVisibility(View.GONE);
        goBack.setVisibility(View.GONE);
        countDownTimer.setVisibility(View.GONE);
        checkStatusText.setVisibility(View.VISIBLE);
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
                        //show payment successful
                        sendTransactionReport("success_no_check");
                    }
                }

            }
        });

        status.getTransferStatus(public_key, ref,enviroment);
    }


    public void sendTransactionReport(String s){
        Bundle result = new Bundle();
        result.putString(REPORT, s);
        getParentFragmentManager().setFragmentResult(TAG, result);
        dismiss();
    }

    public void beginCountDown(TextView timer, Double minutes){

        timer.setVisibility(View.VISIBLE);
        int tMilliseconds = (int) (minutes * 60 * 1000);
        new CountDownTimer(tMilliseconds, 1000) {
            public void onTick(long millisUntilFinished) {
                // Used for formatting digit to be in 2 digits only
                NumberFormat f = new DecimalFormat("00");
                long hour = (millisUntilFinished / 3600000) % 24;
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                timer.setText( f.format(min) + ":" + f.format(sec));
            }
            public void onFinish() {
            }
        }.start();

    }
    private void initUI() {
        companyName = view.findViewById(R.id.company_name);
        amountText = view.findViewById(R.id.amt_text);
        countDownTimer = view.findViewById(R.id.timer);
        bankName = view.findViewById(R.id.bank_name);
        accountNumber = view.findViewById(R.id.account_number);
        accountName = view.findViewById(R.id.account_name);
        goBack = view.findViewById(R.id.button2);
        checkStatusButton = view.findViewById(R.id.button);
        bank_logo = view.findViewById(R.id.bank_logo);
        copyToClipboard = view.findViewById(R.id.imageView);
        progressBar = view.findViewById(R.id.progressBar);
        bankInfoLayout = view.findViewById(R.id.bank_details_layout);
        checkStatusText = view.findViewById(R.id.check_status);


        //pre-populated
        //convert Kobo to naira
        int amountInNaira = amount/100;
        double totalDue;
        DecimalFormat format = new DecimalFormat("0.#");
        double percentageCharge = (percentCharge/100 * amountInNaira);
        if (percentageCharge > percentChargeCap){
            totalDue =percentChargeCap + amount;
        }else{
            totalDue  =  (percentCharge/100 * amountInNaira) + amountInNaira;
        }

        amountText.setText(getString(R.string.amount_text, formatAmount(totalDue)));

        //company name
        companyName.setText(getString(R.string.company_name_text, businessName));
    }


    public String formatAmount (double amt){
        DecimalFormat decim = new DecimalFormat("#,###.##");
        return decim.format(amt);
    }
    public BankTransfer(ChargeRequest chargeRequest, String businessName, int amount, double percentCharge, OnSuccess onSuccess, OnFailed onFailed, String enviroment, int percentChargeCap){
        this.chargeRequest = chargeRequest;
        this.businessName=businessName;
        this.amount=amount;
        this.percentCharge = percentCharge;
        this.onSuccess=onSuccess;
        this.onFailed = onFailed;
        this.enviroment =enviroment;
        this.percentChargeCap = percentChargeCap;
    }


    private void initData() {
        bankTransferViewModel =  ViewModelProviders.of(this).get(BankTransferViewModel.class);
        bankTransferViewModel.getBankDetails().observe(this, new Observer<ChargeResponse>() {
            @Override
            public void onChanged(ChargeResponse chargeResponse) {
                Log.d(TAG, new Gson().toJson(chargeResponse));
                if (chargeResponse == null){
                    sendTransactionReport("network_error");
                }else  if (chargeResponse.getData() == null){
                    sendTransactionReport(chargeResponse.getCheckoutInitError().getMessage());
                }else{
                    progressBar.setVisibility(View.GONE);
                    goBack.setVisibility(View.VISIBLE);
                    checkStatusButton.setVisibility(View.VISIBLE);
                    bankInfoLayout.setVisibility(View.VISIBLE);
                    countDownTimer.setVisibility(View.VISIBLE);

                    //populate data
                    beginCountDown(countDownTimer, Double.parseDouble(chargeResponse.getData().getNext_action().getDuration()));
                    bankName.setText(chargeResponse.getData().getNext_action().getBank_name());
                    accountNumber.setText(chargeResponse.getData().getNext_action().getAccount_number());
                    accountName.setText(chargeResponse.getData().getNext_action().getAccount_name());
                    loadPaymentMethodIcon(Constants.BANK_TRANSFER_ICON, bank_logo);

                }
            }
        });

        bankTransferViewModel.initializeCall(chargeRequest, enviroment );
    }

    private void loadPaymentMethodIcon(String paymentMethodIconUrl, ImageView paymentMethodIcon) {
        GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder = Glide.with(getContext())
                .using(Glide.buildStreamModelLoader(Uri.class, getContext()), InputStream.class)
                .from(Uri.class)
                .as(SVG.class)
                .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                .sourceEncoder(new StreamEncoder())
                .cacheDecoder(new FileToStreamDecoder<SVG>(new SvgDecoder()))
                .decoder(new SvgDecoder())
                .animate(android.R.anim.fade_in)
                .listener(new SvgSoftwareLayerSetter<Uri>());
        Uri uri = Uri.parse(paymentMethodIconUrl);
        requestBuilder
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                // SVG cannot be serialized so it's not worth to cache it
                .load(uri)
                .into(paymentMethodIcon);
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
    public void onDetach() {
        super.onDetach();
        sendTransactionReport("dismiss");
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
