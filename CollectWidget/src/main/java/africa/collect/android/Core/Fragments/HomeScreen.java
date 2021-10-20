

package africa.collect.android.Core.Fragments;

import static africa.collect.android.Utils.Constants.REPORT;
import static africa.collect.android.Utils.Constants.TAG;
import static africa.collect.android.Utils.Enviroment.SANDBOX;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.okra.widget.Okra;
import com.okra.widget.activity.OkraWebActivity;
import com.okra.widget.handlers.OkraHandler;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import africa.collect.android.Activity.FlutterWaveActivity;
import africa.collect.android.Activity.MonnifyActivity;
import africa.collect.android.Adapters.CheckoutAdapter;
import africa.collect.android.Listeners.OnClose;
import africa.collect.android.Listeners.OnFailed;
import africa.collect.android.Listeners.OnSuccess;
import africa.collect.android.Listeners.RecyclerItemClickListener;
import africa.collect.android.LiveData.CheckoutViewModel;
import africa.collect.android.Model.ChargeRequest;
import africa.collect.android.Model.CheckoutInit;
import africa.collect.android.Model.CollectWidgetModel;
import africa.collect.android.Model.PaymentMethods;
import africa.collect.android.Model.WidgetData;
import africa.collect.android.R;

public class HomeScreen extends BottomSheetDialogFragment {

    View view;
    CollectWidgetModel collectWidgetModel;
    OnSuccess onSuccess;
    OnFailed onFailed;
    OnClose onClose;
    private CheckoutViewModel checkoutViewModel;
    ArrayList<PaymentMethods> paymentMethods = new ArrayList<>();
    CheckoutAdapter checkoutAdapter;
    CheckoutInit init;
    String environment;
    Analytics analytics;
    boolean passFee;

    //Views
    ProgressBar loader;
    RelativeLayout security_footnote;
    RecyclerView recyclerView;
    ConstraintLayout content,parent;
    TextView amountText,companyName;
    ImageView close;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.SheetDialog);

        getParentFragmentManager().setFragmentResultListener(TAG, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                String result = bundle.getString(REPORT);
                security_footnote.setVisibility(View.VISIBLE);
                switch (result){
                    case "failed":
                        new africa.collect.android.Utils.Analytics().Track(getContext(), "Payment Failed",  "email", collectWidgetModel.getEmail());
                        onFailed.OnFailed("Payment failed");
                        dismissAllowingStateLoss();
                        break;
                    case "network_error":
                        onFailed.OnFailed("A network exception has occurred");
                        dismissAllowingStateLoss();
                        break;
                    case "success":
                        new africa.collect.android.Utils.Analytics().Track(getContext(), "Payment Success",  "email", collectWidgetModel.getEmail());
                        new VerifyTransaction( onSuccess, onFailed, init.getData().getBusiness_name(), collectWidgetModel.getAmount(), init.getData().getReference(), collectWidgetModel.getpublic_key(), environment, collectWidgetModel.getEmail()).show(getFragmentManager(), "success!");
                        dismissAllowingStateLoss();
                        break;
                        case "success_no_check":
                        new Success(onSuccess, init.getData().getBusiness_name(), collectWidgetModel.getAmount(), init.getData().getReference()).show(getFragmentManager(), "success!");
                        dismissAllowingStateLoss();
                        break;
                }
            }
        });
    }
    public HomeScreen(CollectWidgetModel collectWidgetModel, OnClose onClose, OnFailed onFailed, OnSuccess onSuccess, String environment) {
        this.collectWidgetModel = collectWidgetModel;
        this.onClose = onClose;
        this.onFailed = onFailed;
        this.onSuccess = onSuccess;
        this.environment = environment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_screen, container, false);
        initUI();
        initAnalytics(environment);
        initData();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                environment = "";
                new africa.collect.android.Utils.Analytics().Track(getContext(), "Checkout closed",  "email", collectWidgetModel.getEmail());
                dismiss();
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int amount;
                double totalDue,percentageCharge;

                switch (paymentMethods.get(position).getName()){
                    case "ng_bank_transfer":
                        //  pay with bank transfer
                        amount = (collectWidgetModel.getAmount() / 100);
                         percentageCharge =  (paymentMethods.get(position).getCharge_percentage()/100 * amount);
                        ChargeRequest chargeRequest = new ChargeRequest(init.getData().getEmail(), collectWidgetModel.getpublic_key(), init.getData().getReference(), "ng_bank_transfer", collectWidgetModel.getAmount());
                        new BankTransfer(chargeRequest, init.getData().getBusiness_name(),init.getData().getAmount(),paymentMethods.get(position).getCharge_percentage(), onSuccess, onFailed, environment, init.getData().getPayment_methods().get(position).getCharge_cap(), passFee).show(getFragmentManager(), "bank_transfer");
                        security_footnote.setVisibility(View.GONE);
                        com.segment.analytics.Analytics.with(getContext()).track("Payment Method Clicked", new Properties().putValue("payment_method", paymentMethods.get(position).getName()));
                        break;
                    case "ng_barter":
                        amount = (collectWidgetModel.getAmount() / 100);
                        percentageCharge = (paymentMethods.get(position).getCharge_percentage() / 100 * amount);
                        initBarter(init.getData().getEmail(), init.getData().getFirst_name(), init.getData().getLast_name(), init.getData().getCode(), collectWidgetModel.getCurrency(), amount, percentageCharge, paymentMethods.get(position).getCharge_cap(), passFee);
                        Analytics.with(getContext()).track("Payment Method Clicked", new Properties().putValue("payment_method", paymentMethods.get(position).getName()));
                        break;

                    case "ng_card":
                        amount = (collectWidgetModel.getAmount() / 100);
                        percentageCharge = (paymentMethods.get(position).getCharge_percentage() / 100 * amount);
                        initCardPayment(amount, init.getData().getFirst_name()+" "+ init.getData().getLast_name(), init.getData().getEmail(), init.getData().getCode(), passFee, percentageCharge, paymentMethods.get(position).getCharge_cap());
                        com.segment.analytics.Analytics.with(getContext()).track("Payment Method Clicked", new Properties().putValue("payment_method", paymentMethods.get(position).getName()));
                        break;
                    case "ng_bank_payment_okra":
                        //Direct Debit
                        initDirectDebit(init.getData().getBusiness_name(), init.getData().getWidget_data());
                        com.segment.analytics.Analytics.with(getContext()).track("Payment Method Clicked", new Properties().putValue("payment_method", paymentMethods.get(position).getName()));
                        break;
                    case "ng_opay_wallet":
                        security_footnote.setVisibility(View.GONE);
                        new PaywithOpay(collectWidgetModel, init, paymentMethods.get(position).getCharge_percentage(), environment, collectWidgetModel.getAmount()).show(getFragmentManager(), "opay_wallet");
                        com.segment.analytics.Analytics.with(getContext()).track("Payment Method Clicked", new Properties().putValue("payment_method", paymentMethods.get(position).getName()));
                        break;
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
        return view;
    }

    private void initAnalytics(String environment) {
        switch (environment){
            case "LIVE":
                try {
                    analytics = new Analytics.Builder(getContext(), getString(R.string.segment_key_prod)).build();
                    Analytics.setSingletonInstance(analytics);
                    new africa.collect.android.Utils.Analytics().Track(getContext(), "Checkout Opened",  "email", collectWidgetModel.getEmail());
                    break;
                } catch (Exception e){
                    Log.d(TAG, e.getMessage());
                }

            case "SANDBOX":
                try {
                    analytics = new Analytics.Builder(getContext(), getString(R.string.segment_key_sandbox)).build();
                    Analytics.setSingletonInstance(analytics);
                    new africa.collect.android.Utils.Analytics().Track(getContext(), "Checkout Opened",  "email", collectWidgetModel.getEmail());
                    break;
                } catch (Exception e){
                    Log.d(TAG, e.getMessage());
                }
        }
    }



    private void initCardPayment(double amount, String name, String email, String ref, boolean passFee, double percentageCharge, int chargeCap) {

        double totalDue,chargeAmount;
        if (percentageCharge >chargeCap){
            totalDue = chargeCap + amount;
        }else{
            totalDue = (percentageCharge/100 * amount) + amount;
        }
        if (passFee){
            chargeAmount = totalDue;
        }   else {
            chargeAmount = amount;
        }
        Intent intent = new Intent(getContext(), MonnifyActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("email", email);
        intent.putExtra("amount", chargeAmount);
        intent.putExtra("ref", ref);
        if (environment.equalsIgnoreCase(SANDBOX)){
            intent.putExtra("env", "SANDBOX");
        }else{
            intent.putExtra("env", "LIVE");
        }
        startActivityForResult(intent, 300);

    }

    private void initBarter(String email, String fName, String lName, String ref, String currency, double amount, double percentageCharge,  int chargeCap, boolean passFee) {

        double totalDue;
        if (percentageCharge > chargeCap){
            totalDue = chargeCap + amount;
        }else{
            totalDue = (percentageCharge/100 * amount) + amount;
        }

        String pub_key = "", enc_key="";
        switch (environment){
            case "LIVE":
                pub_key = getString(R.string.flutter_wave_pub_key_prod);
                enc_key = getString(R.string.flutter_wave_enc_key_prod);
                break;
            case "SANDBOX":
                pub_key = getString(R.string.flutter_wave_pub_key_test);
                enc_key = getString(R.string.flutter_wave_enc_key_test);
                break;
        }
        Intent intent  = new Intent(getContext(), FlutterWaveActivity.class);
        if (passFee){
            intent.putExtra("amount", totalDue);
        }else{
            intent.putExtra("amount", amount);
        }
        intent.putExtra("currency", currency);
        intent.putExtra("email", email);
        intent.putExtra("fName", fName);
        intent.putExtra("lName", lName);
        intent.putExtra("desc", "collect_africa_payment");
        intent.putExtra("pub_key", pub_key);
        intent.putExtra("ref", ref);
        intent.putExtra("enc_key", enc_key);
        if (environment == SANDBOX){
            intent.putExtra("env", true);
        }  else{
            intent.putExtra("env", false);
        }
        startActivityForResult(intent, 400);
    }



    private void initData() {
        com.segment.analytics.Analytics.with(getContext()).track("Checkout initialized");
        checkoutViewModel= ViewModelProviders.of(this).get(CheckoutViewModel.class);
        checkoutViewModel.getPaymentMethodsObserver().observe(this, new Observer<CheckoutInit>() {
            @Override
            public void onChanged(CheckoutInit checkoutInit) {
                loader.setVisibility(View.GONE);
                if (checkoutInit == null){
                    dismiss();
                    onFailed.OnFailed("A network exception has occurred");
                }else  if (checkoutInit.getData() == null){
                    dismiss();
                    onFailed.OnFailed(checkoutInit.getCheckoutInitError().getMessage());
                }else{
                    reArrangeItems(checkoutInit.getData().getPayment_methods());
                    init = checkoutInit;
                    paymentMethods.addAll(checkoutInit.getData().getPayment_methods());
                    passFee = checkoutInit.getData().isPass_fee();

                    for (int a =0 ; a<paymentMethods.size(); a++){
                        checkoutInit.getData().getPayment_methods().get(a).setAmount(collectWidgetModel.getAmount());
                        checkoutInit.getData().getPayment_methods().get(a).setPassFee(passFee);
                    }

                    recyclerView.setAdapter(checkoutAdapter);

                    //convert Kobo to naira
                    int amount = collectWidgetModel.getAmount()/100;
                    amountText.setText(getString(R.string.amount_text, checkoutAdapter.formatAmount(amount)));

                    //company name
                    companyName.setText(getString(R.string.company_name_text, checkoutInit.getData().getBusiness_name()));
                    security_footnote.setVisibility(View.VISIBLE);
                    close.setVisibility(View.VISIBLE);
                    parent.setBackgroundResource(R.drawable.rounded_bg);
                    content.setVisibility(View.VISIBLE);
                }
            }
        });

        checkoutViewModel.initializeCheckout(collectWidgetModel, environment);
    }

    private void reArrangeItems(List<PaymentMethods> methodsList) {
        for(int i=0; i<methodsList.size(); i++){
            if(methodsList.get(i).getName().equalsIgnoreCase("ng_bank_transfer")){
                Collections.swap(methodsList, i, 0);
            }
            if(methodsList.get(i).getName().equalsIgnoreCase("ng_opay_wallet")){
                Collections.swap(methodsList, i, 1);
            }

        }
    }

    private void initUI() {
        // Views
        loader = view.findViewById(R.id.loader);
        companyName = view.findViewById(R.id.company_name);
        amountText = view.findViewById(R.id.amt_text);
        security_footnote = view.findViewById(R.id.security_footnote);
        recyclerView = view.findViewById(R.id.checkout_recycler_view);
        content = view.findViewById(R.id.content);
        parent = view.findViewById(R.id.view);
        close = view.findViewById(R.id.close);
        parent.setBackground(null);

        checkoutAdapter = new CheckoutAdapter(paymentMethods, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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

    public void initDirectDebit(String clientName, WidgetData widgetData){

//        amount = (collectWidgetModel.getAmount() / 100);
//        percentageCharge = (paymentMethods.get(position).getCharge_percentage() / 100 * amount);
//        if (percentageCharge > paymentMethods.get(position).getCharge_cap()){
//            totalDue = paymentMethods.get(position).getCharge_cap() + amount;
//        }else{
//            totalDue = (paymentMethods.get(position).getCharge_percentage()/100 * amount) + amount;
//        }

        final Map<String, Object> charge = new HashMap<>();
        charge.put("type", "one-time");
        charge.put("amount",collectWidgetModel.getAmount());
        charge.put("account",widgetData.getOkra_provider_key());
        Log.d(TAG, new Gson().toJson(charge));

        Map<String, Object> dataMap  = new HashMap<String, Object>() {{
            put("products", widgetData.getProducts());
            put("key", widgetData.getOkra_provider_key());
            put("token", widgetData.getOkra_provider_token());
            put("env", widgetData.getEnv());
            put("clientName", clientName);
            put("payment", true);
            put("charge", charge);
            put("color", "#953ab7");
            put("connectMessage", "Which account do you want to connect with?");
            put("callback_url", "");
            put("logo", "https://cdn.okra.ng/images/icon.svg");
            put("widget_failed", "Which account do you want to connect with?");
            put("currency", "NGN");
        }};


        Intent intent = new Intent(getContext(), OkraWebActivity.class);
        intent.putExtra("okraOptions", (Serializable) dataMap);
        startActivityForResult(intent, 1);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case  300:
                if(resultCode == 301 || resultCode == 302){
                // Monnify status
                new VerifyTransaction( onSuccess, onFailed, init.getData().getBusiness_name(), collectWidgetModel.getAmount(), init.getData().getReference(), collectWidgetModel.getpublic_key(), environment, collectWidgetModel.getEmail()).show(getFragmentManager(), "success!");
                dismissAllowingStateLoss();
            }else if(resultCode == 0){
                    // Monnify cancelled
                    onFailed.OnFailed("transaction cancelled");
                }
                break;
            case 400 :
                if( resultCode == 401 || resultCode == 402){
                    // Rave status

                    new VerifyTransaction(onSuccess, onFailed, init.getData().getBusiness_name(), collectWidgetModel.getAmount(), init.getData().getReference(), collectWidgetModel.getpublic_key(), environment, collectWidgetModel.getEmail()).show(getFragmentManager(), "success!");
                    dismissAllowingStateLoss();
                } else if(resultCode == 403){
                    // flutterwave failed
                    onFailed.OnFailed("transaction cancelled");
                }
                break;
            case 1:
                if(resultCode == Activity.RESULT_OK){
                    OkraHandler okraHandler = (OkraHandler) data.getSerializableExtra("okraHandler");
                    String rr = okraHandler.getData();
                    if (okraHandler.getIsSuccessful()){
                        new VerifyTransaction(onSuccess, onFailed, init.getData().getBusiness_name(), collectWidgetModel.getAmount(), init.getData().getReference(), collectWidgetModel.getpublic_key(), environment, collectWidgetModel.getEmail()).show(getFragmentManager(), "success!");
                        dismissAllowingStateLoss();
                    }
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    onFailed.OnFailed("transaction cancelled");

                }
                break;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        environment = "";
        new africa.collect.android.Utils.Analytics().Track(getContext(), "Checkout closed",  "email", collectWidgetModel.getEmail());
        onClose.OnClose();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        environment = "";
        new africa.collect.android.Utils.Analytics().Track(getContext(), "Checkout closed",  "email", collectWidgetModel.getEmail());
        onClose.OnClose();
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

