package com.ozgortech.instagramclone;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ozgortech.instagramclone.databinding.RecyclerRowBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {

    private ArrayList<Post> postArrayList;

    public PostAdapter(ArrayList<Post> postArrayList) {
        this.postArrayList = postArrayList;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        PostHolder postHolder = new PostHolder(RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        return postHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        holder.binding.recyclerViewUsername.setText(postArrayList.get(position).getUsername());
        holder.binding.recyclerViewComment.setText(postArrayList.get(position).getComment());
        System.out.println(postArrayList.get(position).getImageUrl());

        Picasso.get().load(postArrayList.get(position).getImageUrl()).into(holder.binding.recyclerVEwImage);
        // picaso internetteki bir url'yi herhangi bir görünüme atayablimemiz için geliştirilmiş
        // dış bir kütüphane, gradle' da implemente edip burada elimizdeki url'yi imageView'e vermek
        // için kullanıyoruz
    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    class PostHolder extends RecyclerView.ViewHolder{

        RecyclerRowBinding binding;
        public PostHolder(RecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
