package africa.collect.android.Model;

public class CheckoutInitError {
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public CheckoutInitError(String message, String code) {
        this.message = message;
        this.code = code;
    }

    String message;
    String code;
}
