package africa.collect.android.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RaveUiManager;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;

//import org.greenrobot.eventbus.EventBus;


public class FlutterWaveActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initRave(getIntent().getDoubleExtra("amount",0.0), getIntent().getStringExtra("currency"),
                getIntent().getStringExtra("email"), getIntent().getStringExtra("fName"), getIntent().getStringExtra("lName"),
                getIntent().getStringExtra("desc"), getIntent().getStringExtra("pub_key"),
                getIntent().getStringExtra("ref"),  getIntent().getStringExtra("enc_key"), getIntent().getBooleanExtra("env", false));

    }

    private void initRave(double amount, String currency, String email, String fName,
                          String lName, String narration, String publicKey, String ref, String encryptionKey, boolean isSandbox ) {

        new RaveUiManager(this).setAmount(amount)
                .setCurrency(currency)
                .setEmail(email)
                .setfName(fName)
                .setlName(lName)
                .setNarration(fName + " "+ lName)
                .setPublicKey(publicKey)
                .setEncryptionKey(encryptionKey)
                .setTxRef(ref)
                    .acceptCardPayments(true)
                    .acceptUgMobileMoneyPayments(false)
                    .acceptZmMobileMoneyPayments(false)
                    .acceptRwfMobileMoneyPayments(false)
                    .acceptSaBankPayments(false)
                    .acceptUkPayments(false)
                    .acceptBankTransferPayments(true)
                    .acceptUssdPayments(true)
                    .acceptBarterPayments(true)
                    .allowSaveCardFeature(false)
                    .onStagingEnv(isSandbox)
                .shouldDisplayFee(true)
                    .initialize();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                    super.onActivityResult(requestCode, resultCode, data);

        /*
         *  We advise you to do a further verification of transaction's details on your server to be
         *  sure everything checks out before providing service or goods.
         */
        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                setResult(401);
                finish();
            }
            else if (resultCode == RavePayActivity.RESULT_ERROR) {
                setResult(402);
                finish();
            }
            else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                setResult(403);
                finish();
            }
        }
        else {
        }
    }
}
