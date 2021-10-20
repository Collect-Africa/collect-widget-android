package africa.collect.android.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.util.UUID;

import africa.collect.android.CollectWidget;
import africa.collect.android.Listeners.OnClose;
import africa.collect.android.Listeners.OnFailed;
import africa.collect.android.Listeners.OnSuccess;
import africa.collect.android.Utils.Enviroment;


public class MainActivity extends AppCompatActivity  {
    Button initSdk;
    Spinner environmentPicker;
    EditText public_key;
    String environment;

    TextView status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        initSdk= findViewById(R.id.initSdk);
        environmentPicker = findViewById(R.id.spinner);
        public_key = findViewById(R.id.public_key);
        status = findViewById(R.id.check_status);
        environmentPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                environment = adapterView.getSelectedItem().toString();
                public_key.setText("");

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Initialize SDK on Button Click
        initSdk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status.setText("");
                if (public_key.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(MainActivity.this, "Public Key is Required", Toast.LENGTH_LONG).show();
                }else{
                    if(environmentPicker.getSelectedItemPosition() != 0){
                    new CollectWidget()
                            .CollectCheckout(
                                    "john@examlple.com",
                                    "John",
                                    "Doe",
                                    generateRef(),
                                    10000, "NGN",
                                    "",
                                    environmentPicker.getSelectedItem().
                                            toString().equalsIgnoreCase("Sandbox") ? Enviroment.SANDBOX : Enviroment.LIVE,
                                    public_key.getText().toString()
                            )
                            .build(MainActivity.this, new OnClose() {
                                @Override
                                public void OnClose() {
                                    //Modal Closed
                                }
                            }, new OnFailed() {
                                @Override
                                public void OnFailed(String error) {
                                    //An Error has Occurred
                                    status.setText(error);

                                }
                            }, new OnSuccess() {
                                @Override
                                public void OnSuccess(String reference, int amount) {
                                    status.setText("Amount Paid:" + " "+ amount +"\n\n"+"Reference:"+" "+reference);
                                }
                            });
                        }else{
                        Toast.makeText(MainActivity.this, "Please Select Environment", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });


    }

    public String generateRef(){
        String uuid = String.format("%040d", new BigInteger(UUID.randomUUID().toString().replace("-", ""), 16));
        return uuid.substring(uuid.length() - 15);
    }
}