package africa.collect.android.Model;


public class ChargeResponse {
    BankTransferData data;
    CheckoutInitError checkoutInitError;

    public CheckoutInitError getCheckoutInitError() {
        return checkoutInitError;
    }

    public void setCheckoutInitError(CheckoutInitError checkoutInitError) {
        this.checkoutInitError = checkoutInitError;
    }

    public ChargeResponse(CheckoutInitError checkoutInitError) {
        this.checkoutInitError = checkoutInitError;
    }

    public BankTransferData getData() {
        return data;
    }

    public void setData(BankTransferData data) {
        this.data = data;
    }

    public ChargeResponse(BankTransferData data) {
        this.data = data;
    }
}


