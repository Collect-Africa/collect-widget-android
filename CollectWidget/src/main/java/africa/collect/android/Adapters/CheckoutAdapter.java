package africa.collect.android.Adapters;

import static africa.collect.android.Utils.Constants.BANK_TRANSFER_ICON;
import static africa.collect.android.Utils.Constants.BARTER_ICON;
import static africa.collect.android.Utils.Constants.CARD_ICON;
import static africa.collect.android.Utils.Constants.DIRECT_DEBIT_ICON;
import static africa.collect.android.Utils.Constants.OPAY_ICON;

import android.content.Context;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.caverock.androidsvg.SVG;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;


import africa.collect.android.Model.PaymentMethods;
import africa.collect.android.R;
import africa.collect.android.Utils.Misc.SvgDecoder;
import africa.collect.android.Utils.Misc.SvgDrawableTranscoder;
import africa.collect.android.Utils.Misc.SvgSoftwareLayerSetter;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.ViewHolder> {

    ArrayList <PaymentMethods> paymentMethods = new ArrayList<>();
    Context context;
    private GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkout_items, null);
        return new ViewHolder(view);
    }

    public CheckoutAdapter(ArrayList <PaymentMethods> paymentMethods, Context context){
        this.paymentMethods = paymentMethods;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PaymentMethods paymentMethod = paymentMethods.get(position);
        String paymentMethodIconUrl ="";
        String title="";
        double charge=0.0;
        switch (paymentMethods.get(position).getName()){
            case "ng_opay_wallet":
                paymentMethodIconUrl = OPAY_ICON;
                title = "Pay with Opay";
                break;
            case "ng_bank_payment_okra":
                title = "Pay with Direct Debit";
                paymentMethodIconUrl = DIRECT_DEBIT_ICON;
                break;
            case "ng_card":
                title = "Pay with Card";

                paymentMethodIconUrl = CARD_ICON;
                break;
            case "ng_bank_transfer":
                title = "Pay with Bank Transfer";
                paymentMethodIconUrl = BANK_TRANSFER_ICON;

                break;
            case "ng_barter":
                title = "Pay with Barter";
                paymentMethodIconUrl = BARTER_ICON;
                break;
            default:
                holder.item.setVisibility(View.GONE);
            break;
        }
        holder.paymentMethod.setText(title);
        int amount = (paymentMethod.getAmount()/100);
        double percentageCharge =  (paymentMethods.get(position).getCharge_percentage()/100 * amount);
        double totalDue;
        if (percentageCharge > paymentMethods.get(position).getCharge_cap()){
            totalDue = paymentMethods.get(position).getCharge_cap() + amount;
        }else{
            totalDue = (paymentMethods.get(position).getCharge_percentage()/100 * amount) + amount;
        }
        if (paymentMethods.get(position).isPassFee()){
            holder.feeText.setText(context.getString(R.string.charge_text, formatAmount(totalDue)));
        }  else{
            holder.feeText.setText(context.getString(R.string.charge_text, formatAmount(amount)));
        }
        // svg transcode
        if (paymentMethodIconUrl.contains("svg")){
            loadPaymentMethodIcon(paymentMethodIconUrl,holder.paymentMethodIcon);
        }else{
            Glide.with(context)
                    .load(paymentMethodIconUrl)
                    .into(holder.paymentMethodIcon);
        }
    }


    public String formatAmount (double amt){
        DecimalFormat decim = new DecimalFormat("#,###.##");
        return decim.format(amt);
    }
    private void loadPaymentMethodIcon(String paymentMethodIconUrl, ImageView paymentMethodIcon) {
        GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder = Glide.with(context)
                .using(Glide.buildStreamModelLoader(Uri.class, context), InputStream.class)
                .from(Uri.class)
                .as(SVG.class)
                .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                .sourceEncoder(new StreamEncoder())
                .cacheDecoder(new FileToStreamDecoder<SVG>(new SvgDecoder()))
                .decoder(new SvgDecoder())
                .animate(android.R.anim.fade_in)
                .listener(new SvgSoftwareLayerSetter<Uri>());
        Uri uri = Uri.parse(paymentMethodIconUrl);
        requestBuilder
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                // SVG cannot be serialized so it's not worth to cache it
                .load(uri)
                .into(paymentMethodIcon);
    }

    @Override
    public int getItemCount() {
        return paymentMethods.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView paymentMethod,feeText;
        ImageView paymentMethodIcon,infoIcon;
        CardView item;
        public ViewHolder(@NonNull View view) {
            super(view);
            paymentMethod = view.findViewById(R.id.payment_method);
            feeText = view.findViewById(R.id.fee_text);
            paymentMethodIcon = view.findViewById(R.id.icon);
            infoIcon = view.findViewById(R.id.info_icon);
            item = view.findViewById(R.id.item);
        }
    }
}
