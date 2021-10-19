package africa.collect.android.Core.Fragments;

import static africa.collect.android.Utils.Constants.REPORT;
import static africa.collect.android.Utils.Constants.TAG;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.renderscript.ScriptGroup;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.gson.JsonSyntaxException;
import com.mukesh.OtpView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import africa.collect.android.LiveData.CheckoutViewModel;
import africa.collect.android.LiveData.OpayViewModel;
import africa.collect.android.Model.ChargeRequest;
import africa.collect.android.Model.ChargeResponse;
import africa.collect.android.Model.CheckoutInit;
import africa.collect.android.Model.CheckoutInitError;
import africa.collect.android.Model.CollectWidgetModel;
import africa.collect.android.Model.OpayVerifyOtp;
import africa.collect.android.Network.ApiService;
import africa.collect.android.Network.RetrofitInstance;
import africa.collect.android.R;
import africa.collect.android.Utils.Constants;
import africa.collect.android.Utils.Enviroment;
import africa.collect.android.Utils.Misc.SvgDecoder;
import africa.collect.android.Utils.Misc.SvgDrawableTranscoder;
import africa.collect.android.Utils.Misc.SvgSoftwareLayerSetter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaywithOpay extends BottomSheetDialogFragment {

    //views
    View view;
    TextView companyName,amountText, errorText, infoText, count_down_text;
    Button proceed,goBack;
    ProgressBar progressBar;
    ImageView icon;
    EditText editText;
    RelativeLayout countDownLayout;
    OtpView otpView;

    CollectWidgetModel collectWidgetModel;
    CheckoutInit checkoutInit;
    OpayViewModel opayViewModel;
    String environment;
    boolean phoneVerified,pinVerified;
    int amount;
    double percentageCharge;
    String phone_number;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.SheetDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.pay_with_opay, container, false);
        initUI();

        loadPaymentMethodIcon(Constants.OPAY_ICON, icon);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendTransactionReport("dismiss");
            }
        });


        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!phoneVerified){
                    errorText.setVisibility(View.GONE);
                    if (editText.getText().toString().isEmpty()){
                        errorText.setText(R.string.error_phone_required);
                        errorText.setVisibility(View.VISIBLE);
                        proceed.setEnabled(true);
                    }else{
                        proceed.setEnabled(false);
                        errorText.setVisibility(View.GONE);
                        proceed.setText("");
                        progressBar.setVisibility(View.VISIBLE);
                        ChargeRequest chargeRequest = new ChargeRequest(checkoutInit.getData().getEmail(), collectWidgetModel.getpublic_key(),checkoutInit.getData().getReference(), "ng_opay_wallet", collectWidgetModel.getAmount(), editText.getText().toString());
                        getDetails(chargeRequest, environment);
                    }
                }  else {
                    if (editText.getText().toString().isEmpty()){
                        errorText.setText(R.string.error_phone_required);
                        errorText.setVisibility(View.VISIBLE);
                        proceed.setEnabled(true);
                    }else{
                    proceed.setEnabled(false);
                    errorText.setVisibility(View.GONE);
                    proceed.setText("");
                    progressBar.setVisibility(View.VISIBLE);
                    ChargeRequest chargeRequest = new ChargeRequest(editText.getText().toString(),phone_number,collectWidgetModel.getpublic_key());
                    verifyOpayPin(chargeRequest, checkoutInit.getData().getCode(), environment);
                    }
                }

                if (pinVerified){
                    proceed.setEnabled(false);
                    errorText.setVisibility(View.GONE);
                    proceed.setText("");
                    progressBar.setVisibility(View.VISIBLE);
                    OpayVerifyOtp otp = new OpayVerifyOtp(otpView.getText().toString(), collectWidgetModel.getpublic_key(), phone_number);
                    verifyOpayOtp( otp, checkoutInit.getData().getCode());
                }



            }
        });

        countDownLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count_down_text.getText().toString().contains("Resend")){
                    proceed.setEnabled(false);
                    errorText.setVisibility(View.GONE);
                    proceed.setText("");
                    progressBar.setVisibility(View.VISIBLE);
                    requestOpayOtp(phone_number, checkoutInit.getData().getCode(), collectWidgetModel.getpublic_key());
                }
            }
        });

        return  view;
    }

    //todo
    private void verifyOpayPin(ChargeRequest chargeRequest, String public_key, String environment) {
        ApiService apiService= RetrofitInstance.getRetrofitClient(environment).create(ApiService.class);
        Call<ChargeResponse> apiCall = apiService.verifyOpayPin(public_key,chargeRequest);
        apiCall.enqueue(new Callback<ChargeResponse>() {
            @Override
            public void onResponse(Call<ChargeResponse> call, Response<ChargeResponse> response) {
                proceed.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                if (response.code() == 200){
                    pinVerified = true;
                    phone_number = editText.getText().toString();
                    requestOpayOtp(phone_number, checkoutInit.getData().getCode(), collectWidgetModel.getpublic_key());
                }else{
                    try {
                        String error = response.errorBody().string();
                        CheckoutInitError initError = new Gson().fromJson(error, CheckoutInitError.class);
                        ChargeResponse checkoutInit = new ChargeResponse(initError);
                        proceed.setText("Login");
                        errorText.setVisibility(View.VISIBLE);
                        errorText.setText(checkoutInit.getCheckoutInitError().getMessage());

                    } catch (IOException | IllegalStateException| JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ChargeResponse> call, Throwable t) {
                sendTransactionReport("network_error");
            }
        });
    }

    public void requestOpayOtp(String phone_number, String short_code, String public_key){
        ChargeRequest chargeRequest = new ChargeRequest(phone_number, public_key);
        ApiService apiService= RetrofitInstance.getRetrofitClient(environment).create(ApiService.class);
        Call<ChargeResponse> apiCall = apiService.requestOpayOtp(short_code, chargeRequest);
        apiCall.enqueue(new Callback<ChargeResponse>() {
            @Override
            public void onResponse(Call<ChargeResponse> call, Response<ChargeResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.code() == 200){
                    proceed.setText("Verify OTP");
                    editText.setVisibility(View.GONE);
                    otpView.setVisibility(View.VISIBLE);
                    infoText.setText("Enter the OTP sent to you");
                    countDownLayout.setVisibility(View.VISIBLE);
                    beginCountDown(count_down_text,10);
                }else{
                    try {
                        String error = response.errorBody().string();
                        CheckoutInitError initError = new Gson().fromJson(error, CheckoutInitError.class);
                        ChargeResponse checkoutInit = new ChargeResponse(initError);
                        proceed.setText("Login");
                        errorText.setVisibility(View.VISIBLE);
                        errorText.setText(checkoutInit.getCheckoutInitError().getMessage());

                    } catch (IOException | IllegalStateException| JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ChargeResponse> call, Throwable t) {
                sendTransactionReport("network_error");
            }
        });
    }

public void verifyOpayOtp(OpayVerifyOtp verifyOtp, String short_code){
    ApiService apiService= RetrofitInstance.getRetrofitClient(environment).create(ApiService.class);
    apiService.verifyOpayOtp(short_code, verifyOtp).enqueue(new Callback<ChargeResponse>() {
        @Override
        public void onResponse(Call<ChargeResponse> call, Response<ChargeResponse> response) {
            progressBar.setVisibility(View.GONE);
            if (response.code() == 200){
              // check if successful
                if(response.body().getData().getStatus().equalsIgnoreCase("success")){
                    //show payment successful
                    sendTransactionReport("success_no_check");

                }
            }else{
                try {
                    String error = response.errorBody().string();
                    CheckoutInitError initError = new Gson().fromJson(error, CheckoutInitError.class);
                    ChargeResponse checkoutInit = new ChargeResponse(initError);
                    proceed.setText("Login");
                    errorText.setVisibility(View.VISIBLE);
                    errorText.setText(checkoutInit.getCheckoutInitError().getMessage());

                } catch (IOException | IllegalStateException| JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(Call<ChargeResponse> call, Throwable t) {
            sendTransactionReport("network_error");

        }
    });

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
               timer.setText("Resend OTP");
            }
        }.start();

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
    public void sendTransactionReport(String s){
        Bundle result = new Bundle();
        result.putString(REPORT, s);
        getParentFragmentManager().setFragmentResult(Constants.TAG, result);
        dismiss();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        sendTransactionReport("dismiss");

    }

    private void initUI() {
        companyName = view.findViewById(R.id.company_name);
        amountText = view.findViewById(R.id.amt_text);
        errorText = view.findViewById(R.id.error_text);
        infoText = view.findViewById(R.id.textView11);
        count_down_text = view.findViewById(R.id.count_down_text);
        proceed = view.findViewById(R.id.button);
        goBack = view.findViewById(R.id.button2);
        progressBar = view.findViewById(R.id.progressBar);
        icon = view.findViewById(R.id.icon);
        countDownLayout = view.findViewById(R.id.count_down);
        otpView = view.findViewById(R.id.otp_view);
        editText = view.findViewById(R.id.editText);
        opayViewModel = ViewModelProviders.of(this).get(OpayViewModel.class);

        int amountInNaira = amount/100;
        double totalDue =  (percentageCharge/100 * amountInNaira) + amountInNaira;
        amountText.setText(getString(R.string.amount_text, formatAmount(totalDue)));

        //company name
        companyName.setText(getString(R.string.company_name_text, checkoutInit.getData().getBusiness_name()));
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


    public PaywithOpay(CollectWidgetModel collectWidgetModel, CheckoutInit init,double  percentageCharge, String enviroment, int amount) {
        this.collectWidgetModel = collectWidgetModel;
        this.checkoutInit = init;
        this.percentageCharge = percentageCharge;
        this.environment = enviroment;
        this.amount = amount;
    }


    public String formatAmount (double amt){
        DecimalFormat decim = new DecimalFormat("#,###.##");
        return decim.format(amt);
    }


    public void getDetails(ChargeRequest chargeRequest, String enviroment){
        ApiService apiService= RetrofitInstance.getRetrofitClient(enviroment).create(ApiService.class);
        Call<ChargeResponse> apiCall = apiService.getOpayDetails(chargeRequest);
        apiCall.enqueue(new Callback<ChargeResponse>() {
            @Override
            public void onResponse(Call<ChargeResponse> call, Response<ChargeResponse> response) {
                proceed.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                if (response.code() == 200){
                    phone_number = editText.getText().toString();
                    if(response.body().getData().getStatus().equalsIgnoreCase("start")){
                            //show payment successful
                            phoneVerified = true;
                            editText.setHint("Enter Password");
                            editText.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                            infoText.setText("Enter Opay Password");
                            sendTransactionReport("success");
                        }

                }else{
                    try {
                        String error = response.errorBody().string();
                        CheckoutInitError initError = new Gson().fromJson(error, CheckoutInitError.class);
                        ChargeResponse checkoutInit = new ChargeResponse(initError);
                        proceed.setText("Login");
                        errorText.setVisibility(View.VISIBLE);
                        errorText.setText(checkoutInit.getCheckoutInitError().getMessage());

                    } catch (IOException | IllegalStateException| JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ChargeResponse> call, Throwable t) {
                if(t.getCause() instanceof SocketTimeoutException){
                    sendTransactionReport("network_error");

                } else {
                    sendTransactionReport("network_error");

                }

                Log.d(TAG, t.getMessage());
            }
        });
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
