package africa.collect.android.Model;

public class BankTransferData {
    String status,reference;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public NextAction getNext_action() {
        return next_action;
    }

    public void setNext_action(NextAction next_action) {
        this.next_action = next_action;
    }

    public BankTransferData(NextAction next_action, String status, String reference) {
        this.next_action = next_action;
        this.status = status;
        this.reference = reference;
    }

    NextAction next_action;

}

