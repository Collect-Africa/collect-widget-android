package africa.collect.android.Activity;

import static africa.collect.android.Utils.Enviroment.LIVE;
import static africa.collect.android.Utils.Enviroment.SANDBOX;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.teamapt.monnify.sdk.Monnify;
import com.teamapt.monnify.sdk.MonnifyTransactionResponse;
import com.teamapt.monnify.sdk.data.model.TransactionDetails;
import com.teamapt.monnify.sdk.model.PaymentMethod;
import com.teamapt.monnify.sdk.module.view.activity.SdkActivity;
import com.teamapt.monnify.sdk.service.ApplicationMode;

//import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.ArrayList;

import africa.collect.android.R;

public class MonnifyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMonnify(getIntent().getStringExtra("env"), getIntent().getDoubleExtra("amount", 0.0), getIntent().getStringExtra("name"), getIntent().getStringExtra("email"), getIntent().getStringExtra("ref") );
    }

    private void initMonnify(String env, double amount, String name, String email, String ref) {
        Monnify monnify = Monnify.Companion.getInstance();
        switch (env){
            case "LIVE":
                monnify.setApiKey(getString(R.string.monnify_prod_key));
                monnify.setContractCode(getString(R.string.monnify_prod_contract));
                monnify.setApplicationMode(ApplicationMode.LIVE);
                break;
            case "SANDBOX":
                monnify.setApiKey(getString(R.string.monnify_test_key));
                monnify.setContractCode(getString(R.string.monnify_test_contract));
                monnify.setApplicationMode(ApplicationMode.TEST);
                break;
        }

        TransactionDetails transaction = new TransactionDetails.Builder()
                .amount(new BigDecimal(amount))
                .currencyCode("NGN")
                .customerName(name)
                .customerEmail(email)
                .paymentReference(ref)
                .paymentMethods( new ArrayList<PaymentMethod>() {{
                    add(PaymentMethod.CARD);
                }})
                .paymentDescription("Collect_africa_payment")
                .build();

        monnify.initializePayment(this, transaction, 2, "collect_africa");

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MonnifyTransactionResponse monnifyTransactionResponse = (MonnifyTransactionResponse) data.getParcelableExtra("collect_africa");
        if (monnifyTransactionResponse == null)
            return;
        switch (monnifyTransactionResponse.getStatus()) {
            case PENDING:
            case FAILED:
            case PAYMENT_GATEWAY_ERROR: {
                setResult(302);
                finish();
                break;
            }
            case PAID:
            case OVERPAID:
            case PARTIALLY_PAID: {
                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setResult(301);
                        finish();
                    }
                });

                break;
            }
            default:
                finish();
        }
    }
}
