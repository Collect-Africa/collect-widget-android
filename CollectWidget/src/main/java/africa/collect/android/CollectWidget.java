package africa.collect.android;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;

import africa.collect.android.Core.Fragments.HomeScreen;
import africa.collect.android.Listeners.OnClose;
import africa.collect.android.Listeners.OnFailed;
import africa.collect.android.Listeners.OnSuccess;
import africa.collect.android.Model.CollectWidgetModel;
import africa.collect.android.Utils.Enviroment;

public class CollectWidget {

    //initialize variables
    String email;
    String firstName;
    String lastName;
    String reference;
    int amount;
    String currency;
    String itemImage;
    String public_key;
    String  environment;

    //Add parameter errors to a List
    List<String> paramErrors = new ArrayList<>();

    public CollectWidget CollectCheckout(String email, String firstName, String lastName, String reference, int amount, String currency, String  itemImage,  String environment, String public_key){
       this.email = email;
       this.firstName = firstName;
       this.lastName = lastName;
       this.reference = reference;
       this.amount = amount;
       this.currency = currency;
       this.itemImage = itemImage;
       this.environment = environment;
        this.public_key = public_key;

        return this;
    }


    public void build(Context context, OnClose onClose, OnFailed onFailed, OnSuccess onSuccess){
        if(isVerified(paramErrors)){
            CollectWidgetModel collectWidgetModel = new CollectWidgetModel(email, firstName, lastName, reference, amount, currency, itemImage, public_key);
            new HomeScreen(collectWidgetModel, onClose, onFailed, onSuccess, environment).show(((FragmentActivity)context).getSupportFragmentManager(), "HomeScreen");
        }else {
            StringBuilder sb=new StringBuilder("The following errors have occurred").append("\n");
            for (int i = 0; i < paramErrors.size(); i++) {
                sb.append(paramErrors.get(i)).append("\n");
            }
            throw  new RuntimeException(sb.toString());
        }
    }

    private boolean isVerified(List<String> errors) {
        boolean verified;
        errors.clear();
        if (email.isEmpty()||email.equalsIgnoreCase("")){
            verified =false;
            errors.add("Email is required");
        }   else if (firstName.isEmpty() || firstName.equalsIgnoreCase("")){
            verified =false;
            errors.add("First Name is required");
        }   else if (lastName.equalsIgnoreCase("")||lastName.isEmpty()){
            verified =false;
            errors.add("Last Name is required");

        }else if (reference.isEmpty()||reference.equalsIgnoreCase("")){
            verified =false;
            errors.add("Reference  is required");

        }else if (currency.isEmpty()||currency.equalsIgnoreCase("")){
            verified =false;
            errors.add("Currency type is missing or invalid");

        }else if (public_key.isEmpty()||public_key.equalsIgnoreCase("")){
            verified =false;
            errors.add("Public key type is missing or invalid");

        } else {
            verified =true;
            errors.clear();
        }
        return verified;
    }


}
