package africa.collect.android.Model;

public class NextAction {
    String account_name,account_number,bank_code,bank_name,duration,type;
    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    public String getAccount_number() {
        return account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    public String getBank_code() {
        return bank_code;
    }

    public void setBank_code(String bank_code) {
        this.bank_code = bank_code;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public NextAction(String account_name, String account_number, String bank_code, String bank_name, String duration, String type) {
        this.account_name = account_name;
        this.account_number = account_number;
        this.bank_code = bank_code;
        this.bank_name = bank_name;
        this.duration = duration;
        this.type = type;
    }
}
