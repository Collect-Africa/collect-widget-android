package africa.collect.android.Network;

import africa.collect.android.Model.ChargeRequest;
import africa.collect.android.Model.ChargeResponse;
import africa.collect.android.Model.CheckoutInit;
import africa.collect.android.Model.CollectWidgetModel;
import africa.collect.android.Model.OpayVerifyOtp;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    //Initialize Checkout
    @POST("/payments/checkout/initialize")
    Call<CheckoutInit> initializeCheckout(@Body CollectWidgetModel collectWidgetModel);

    //get Bank details for ng_bank_transfer
    @POST("/payments/charge")
    Call<ChargeResponse> getBankDetails(@Body ChargeRequest chargeRequest);

    //check Bank transfer Status
    @GET("/payments/charge/verify")
    Call<ChargeResponse> getTransferStatus(@Query("public_key") String public_key, @Query("reference") String reference);

    //Opay
    @POST("/payments/charge")
    Call<ChargeResponse> getOpayDetails(@Body ChargeRequest chargeRequest);

    //Opay Verify PIN
    @POST("/payments/{short_code}/opay/verify_pin")
    Call<ChargeResponse> verifyOpayPin(@Path(value="short_code") String short_code, @Body ChargeRequest chargeRequest);

    //Opay Request  OTP
    @POST("/payments/{short_code}/opay/request_otp")
    Call<ChargeResponse> requestOpayOtp(@Path(value="short_code") String short_code, @Body ChargeRequest chargeRequest);

    //Opay Verify OTP
    @POST("/payments/{short_code}/opay/verify_otp")
    Call<ChargeResponse> verifyOpayOtp(@Path(value="short_code") String short_code, @Body OpayVerifyOtp chargeRequest);

    //Opay Verify OTP
    @POST("/payments/{short_code}/opay/verify_otp")
    Call<ChargeResponse> verifyTransaction(@Path(value="short_code") String short_code, @Body OpayVerifyOtp chargeRequest);


}
