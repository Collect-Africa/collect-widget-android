package africa.collect.android.Model;

public class OpayVerifyOtp {
    String otp,public_key,phone_number;

    public OpayVerifyOtp(String otp, String public_key, String phone_number) {
        this.otp = otp;
        this.public_key = public_key;
        this.phone_number = phone_number;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getPublic_key() {
        return public_key;
    }

    public void setPublic_key(String public_key) {
        this.public_key = public_key;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
