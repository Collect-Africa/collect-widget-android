package africa.collect.android.Network;


import static africa.collect.android.Utils.Constants.BASE_URL_PROD;
import static africa.collect.android.Utils.Constants.BASE_URL_TEST;
import static africa.collect.android.Utils.Enviroment.LIVE;

import android.widget.Toast;

import africa.collect.android.Utils.Enviroment;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


//This class is used to initialize retrofit2 for making RESTful API calls

public class RetrofitInstance {

    public static Retrofit retrofit;

    public static  Retrofit getRetrofitClient(String enviroment){
            switch (enviroment){
                case "LIVE":
                    retrofit= new Retrofit.Builder()
                            .baseUrl(BASE_URL_PROD)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    break;
                case "SANDBOX":
                    retrofit= new Retrofit.Builder()
                            .baseUrl(BASE_URL_TEST)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    break;
        }
        return retrofit;
    };
}