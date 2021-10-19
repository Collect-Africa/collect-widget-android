package africa.collect.android.Model;

public class PaymentMethods {
    String name;
    int charge_cap;
    Double charge_percentage;
    int amount;
    boolean passFee;

    public boolean isPassFee() {
        return passFee;
    }

    public void setPassFee(boolean passFee) {
        this.passFee = passFee;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getCharge_percentage() {
        return charge_percentage;
    }

    public void setCharge_percentage(Double charge_percentage) {
        this.charge_percentage = charge_percentage;
    }

    public int getCharge_cap() {
        return charge_cap;
    }

    public void setCharge_cap(int charge_cap) {
        this.charge_cap = charge_cap;
    }

    public PaymentMethods(String name, Double charge_percentage, int charge_cap, boolean passFee) {
        this.name = name;
        this.charge_percentage = charge_percentage;
        this.charge_cap = charge_cap;
        this.passFee = passFee;
    }
}
