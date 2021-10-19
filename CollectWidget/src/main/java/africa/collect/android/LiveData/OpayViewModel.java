package africa.collect.android.LiveData;

import static africa.collect.android.Utils.Constants.TAG;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.SocketTimeoutException;

import africa.collect.android.Model.ChargeResponse;
import africa.collect.android.Model.ChargeRequest;
import africa.collect.android.Model.CheckoutInitError;
import africa.collect.android.Network.ApiService;
import africa.collect.android.Network.RetrofitInstance;
import africa.collect.android.Utils.Enviroment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OpayViewModel extends ViewModel {

    private MutableLiveData<ChargeResponse> bankTransferResponseMutableLiveData;
    public OpayViewModel(){
        bankTransferResponseMutableLiveData= new MutableLiveData<>();
    }

    public MutableLiveData<ChargeResponse> getStatus(){
        return bankTransferResponseMutableLiveData;
    }

    public MutableLiveData<ChargeResponse> getOpayDetails(){
        return bankTransferResponseMutableLiveData;
    }

    public void getDetails(ChargeRequest chargeRequest, String enviroment){
        ApiService apiService= RetrofitInstance.getRetrofitClient(enviroment).create(ApiService.class);
        Call<ChargeResponse> apiCall = apiService.getOpayDetails(chargeRequest);
        apiCall.enqueue(new Callback<ChargeResponse>() {
            @Override
            public void onResponse(Call<ChargeResponse> call, Response<ChargeResponse> response) {
                if (response.code() == 200){
                    bankTransferResponseMutableLiveData.postValue(response.body());
                }else{
                    try {
                        String error = response.errorBody().string();
                        CheckoutInitError initError = new Gson().fromJson(error, CheckoutInitError.class);
                        ChargeResponse checkoutInit = new ChargeResponse(initError);
                        bankTransferResponseMutableLiveData.postValue(checkoutInit);

                    } catch (IOException | IllegalStateException| JsonSyntaxException e) {
                        bankTransferResponseMutableLiveData.postValue(null);
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ChargeResponse> call, Throwable t) {
                if(t.getCause() instanceof SocketTimeoutException){
                    bankTransferResponseMutableLiveData.postValue(null);
                } else {
                    bankTransferResponseMutableLiveData.postValue(null);
                }

                Log.d(TAG, t.getMessage());
            }
        });
    }

}
