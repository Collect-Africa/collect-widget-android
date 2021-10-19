package africa.collect.android.Model;

import java.util.List;

public class CheckoutModel {
    int id,amount,customer_id,business_id;
    String code,first_name,middle_name,last_name,email,business_name,currency,country,reference,callback_url;
    boolean pass_fee;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public int getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(int business_id) {
        this.business_id = business_id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBusiness_name() {
        return business_name;
    }

    public void setBusiness_name(String business_name) {
        this.business_name = business_name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getCallback_url() {
        return callback_url;
    }

    public void setCallback_url(String callback_url) {
        this.callback_url = callback_url;
    }

    public boolean isPass_fee() {
        return pass_fee;
    }

    public void setPass_fee(boolean pass_fee) {
        this.pass_fee = pass_fee;
    }

    public List<PaymentMethods> getPayment_methods() {
        return payment_methods;
    }

    public void setPayment_methods(List<PaymentMethods> payment_methods) {
        this.payment_methods = payment_methods;
    }

    public WidgetData getWidget_data() {
        return widget_data;
    }

    public void setWidget_data(WidgetData widget_data) {
        this.widget_data = widget_data;
    }

    List<PaymentMethods> payment_methods;
    WidgetData widget_data;

    public CheckoutModel(int id, int amount, int customer_id, int business_id, String code, String first_name, String middle_name, String last_name, String email, String business_name, String currency, String country, String reference, String callback_url, boolean pass_fee, List<PaymentMethods> payment_methods, WidgetData widget_data) {
        this.id = id;
        this.amount = amount;
        this.customer_id = customer_id;
        this.business_id = business_id;
        this.code = code;
        this.first_name = first_name;
        this.middle_name = middle_name;
        this.last_name = last_name;
        this.email = email;
        this.business_name = business_name;
        this.currency = currency;
        this.country = country;
        this.reference = reference;
        this.callback_url = callback_url;
        this.pass_fee = pass_fee;
        this.payment_methods = payment_methods;
        this.widget_data = widget_data;
    }
}
