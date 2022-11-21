package com.ozgortech.artbookwithfragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.ozgortech.artbookwithfragment.databinding.RecyclerRowBinding;

import java.util.List;

public class ArtAdapter extends RecyclerView.Adapter<ArtAdapter.ArtHolder> {

    List<Art> artArrayList;

    public ArtAdapter(List<Art> artArrayList) {
        this.artArrayList = artArrayList;
    }

    @NonNull
    @Override
    public ArtHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new ArtHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtHolder holder, int position) {
        holder.binding.recyclerViewTextView.setText(artArrayList.get(position).getName());
        String name = artArrayList.get(position).getName();
        String artist = artArrayList.get(position).getArtist();
        String year = artArrayList.get(position).getYear();
        String foto = artArrayList.get(position).getFoto();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //NavDirections action = MainFragmentDirections.actionMainFragmentToArtFragment("old");
                //Navigation.findNavController(view).navigate(action);

                MainFragmentDirections.ActionMainFragmentToArtFragment action = MainFragmentDirections.actionMainFragmentToArtFragment("old");
                action.setArt(artArrayList.get(position));
                Navigation.findNavController(view).navigate(action);
            }
        });
    }

    @Override
    public int getItemCount() {
        return artArrayList.size();
    }

    class ArtHolder extends RecyclerView.ViewHolder{

        RecyclerRowBinding binding;
        public ArtHolder(RecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
