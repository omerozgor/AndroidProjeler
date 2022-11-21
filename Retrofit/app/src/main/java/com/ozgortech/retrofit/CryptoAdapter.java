package com.ozgortech.retrofit;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.ozgortech.retrofit.databinding.RecyclerRowBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class CryptoAdapter extends RecyclerView.Adapter<CryptoAdapter.CryptoHolder> {

    ArrayList<CryptoModel> cryptoModelList;
    String[] colors = new String[] {"#0e8523","#2b92eb","#0911e0","#7a00f9","#b47ef4"};
    Activity activity;


    public CryptoAdapter(ArrayList<CryptoModel> cryptoModelList,Activity activity) {
        this.cryptoModelList = cryptoModelList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public CryptoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate
                (LayoutInflater.from(parent.getContext()));
        return new CryptoHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CryptoHolder holder, int position) {
        holder.binding.recyclerViewCurrency.setText(cryptoModelList.get(position).getCurrency());
        holder.binding.recyclerViewPrice.setText("Price : " + cryptoModelList.get(position).getPrice());

        holder.itemView.setBackgroundColor(Color.parseColor(colors[position % 5]));

        if ((cryptoModelList.get(position).getLogoUrl()).contains(".svg")){
            GlideToVectorYou.justLoadImage(activity, Uri.parse(cryptoModelList.get(position).getLogoUrl()),
                    holder.binding.recyclerViewImageView);
        }else{
            Picasso.get().load(cryptoModelList.get(position).getLogoUrl()).into(holder.binding.recyclerViewImageView);
        }



    }

    @Override
    public int getItemCount() {
        return cryptoModelList.size();
    }

    class CryptoHolder extends RecyclerView.ViewHolder{

        RecyclerRowBinding binding;


        public CryptoHolder(RecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }



}
