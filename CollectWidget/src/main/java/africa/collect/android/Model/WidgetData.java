package africa.collect.android.Model;

import java.util.List;

public class WidgetData {
    String transaction_reference,okra_provider_token,okra_provider_key,env,callback_url;
    List<String> products;

    public String getTransaction_reference() {
        return transaction_reference;
    }

    public void setTransaction_reference(String transaction_reference) {
        this.transaction_reference = transaction_reference;
    }

    public String getOkra_provider_token() {
        return okra_provider_token;
    }

    public void setOkra_provider_token(String okra_provider_token) {
        this.okra_provider_token = okra_provider_token;
    }

    public String getOkra_provider_key() {
        return okra_provider_key;
    }

    public void setOkra_provider_key(String okra_provider_key) {
        this.okra_provider_key = okra_provider_key;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getCallback_url() {
        return callback_url;
    }

    public void setCallback_url(String callback_url) {
        this.callback_url = callback_url;
    }

    public List<String> getProducts() {
        return products;
    }

    public void setProducts(List<String> products) {
        this.products = products;
    }

    public WidgetData(String transaction_reference, String okra_provider_token, String okra_provider_key, String env, String callback_url, List<String> products) {
        this.transaction_reference = transaction_reference;
        this.okra_provider_token = okra_provider_token;
        this.okra_provider_key = okra_provider_key;
        this.env = env;
        this.callback_url = callback_url;
        this.products = products;
    }
}
