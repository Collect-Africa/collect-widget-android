package africa.collect.android.Model;

public class CollectWidgetModel {
    String email;
    String first_name;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return first_name;
    }

    public void setFirstName(String first_name) {
        this.first_name = first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public void setLastName(String last_name) {
        this.last_name = last_name;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public String getpublic_key() {
        return public_key;
    }

    public void setpublic_key(String public_key) {
        this.public_key = public_key;
    }

    String last_name;
    String reference;
    int amount;
    String currency;
    String itemImage;
    String public_key;

    public CollectWidgetModel(String email, String first_name, String last_name, String reference, int amount, String currency, String itemImage, String public_key) {
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.reference = reference;
        this.amount = amount;
        this.currency = currency;
        this.itemImage = itemImage;
        this.public_key = public_key;
    }
}
