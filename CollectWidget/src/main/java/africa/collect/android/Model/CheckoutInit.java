package africa.collect.android.Model;

public class CheckoutInit {
    CheckoutModel data;

    public CheckoutInitError getCheckoutInitError() {
        return checkoutInitError;
    }

    public void setCheckoutInitError(CheckoutInitError checkoutInitError) {
        this.checkoutInitError = checkoutInitError;
    }

    public CheckoutInit(CheckoutInitError checkoutInitError) {
        this.checkoutInitError = checkoutInitError;
    }

    CheckoutInitError checkoutInitError;

    public CheckoutModel getData() {
        return data;
    }

    public void setData(CheckoutModel data) {
        this.data = data;
    }

    public CheckoutInit(CheckoutModel data) {
        this.data = data;
    }
}
