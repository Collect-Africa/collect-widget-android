package africa.collect.android.LiveData;

import static africa.collect.android.Utils.Constants.TAG;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.SocketTimeoutException;

import africa.collect.android.Model.CheckoutInit;
import africa.collect.android.Model.CheckoutInitError;
import africa.collect.android.Model.CollectWidgetModel;
import africa.collect.android.Network.ApiService;
import africa.collect.android.Network.RetrofitInstance;
import africa.collect.android.Utils.Enviroment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutViewModel  extends ViewModel {
    private MutableLiveData<CheckoutInit> checkoutInitMutableLiveData;
    public CheckoutViewModel(){
        checkoutInitMutableLiveData= new MutableLiveData<>();
    }

    public MutableLiveData<CheckoutInit> getPaymentMethodsObserver(){
        return checkoutInitMutableLiveData;
    }

    public void initializeCheckout(CollectWidgetModel body, String enviroment){
        ApiService apiService= RetrofitInstance.getRetrofitClient(enviroment).create(ApiService.class);
        Call<CheckoutInit> apiCall = apiService.initializeCheckout(body);
        apiCall.enqueue(new Callback<CheckoutInit>() {
            @Override
            public void onResponse(Call<CheckoutInit> call, Response<CheckoutInit> response) {
                //observe for changes and mutable the items according.
                if (response.code() == 200){
                    checkoutInitMutableLiveData.postValue(response.body());
                }else{
                    try {
                        String error = response.errorBody().string();
                        CheckoutInitError initError = new Gson().fromJson(error, CheckoutInitError.class);
                        CheckoutInit checkoutInit = new CheckoutInit(initError);
                        checkoutInitMutableLiveData.postValue(checkoutInit);

                    } catch (IOException | IllegalStateException| JsonSyntaxException e) {
                        checkoutInitMutableLiveData.postValue(null);
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CheckoutInit> call, Throwable t) {
                if(t.getCause() instanceof SocketTimeoutException){
                    checkoutInitMutableLiveData.postValue(null);
                } else {
                    checkoutInitMutableLiveData.postValue(null);
                }

                Log.d(TAG, t.getMessage());

            }
        });
    }
}
