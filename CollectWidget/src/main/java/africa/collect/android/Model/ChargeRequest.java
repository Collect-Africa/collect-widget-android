package africa.collect.android.Model;

public class ChargeRequest {
    String email;
    String reference;
    String type;

    public ChargeRequest() {
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    int amount;

    public String getEmail() {
        return email;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    String pin;

    String otp;
    String phone_number;
    String public_key;

    public ChargeRequest(String pin, String phone_number, String public_key) {
        this.pin = pin;
        this.phone_number = phone_number;
        this.public_key = public_key;
    }

    public ChargeRequest(String phone_number, String public_key) {
        this.phone_number = phone_number;
        this.public_key = public_key;
    }



    public void setEmail(String email) {
        this.email = email;
    }

    public String getPublic_key() {
        return public_key;
    }

    public void setPublic_key(String public_key) {
        this.public_key = public_key;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    String phonenumber;

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public ChargeRequest(String email, String public_key, String reference, String type, int amount, String phonenumber) {
        this.email = email;
        this.public_key = public_key;
        this.reference = reference;
        this.type = type;
        this.amount = amount;
        this.phonenumber = phonenumber;
    }

    public ChargeRequest(String email, String public_key, String reference, String type, int amount) {
        this.email = email;
        this.public_key = public_key;
        this.reference = reference;
        this.type = type;
        this.amount = amount;
    }
}
