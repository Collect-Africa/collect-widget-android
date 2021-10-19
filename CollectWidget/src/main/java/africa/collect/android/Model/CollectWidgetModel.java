package africa.collect.android.Model;

public class CollectWidgetModel {
    String email;
    String firstName;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    String lastName;
    String reference;
    int amount;
    String currency;
    String itemImage;
    String public_key;

    public CollectWidgetModel(String email, String firstName, String lastName, String reference, int amount, String currency, String itemImage, String public_key) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.reference = reference;
        this.amount = amount;
        this.currency = currency;
        this.itemImage = itemImage;
        this.public_key = public_key;
    }
}
