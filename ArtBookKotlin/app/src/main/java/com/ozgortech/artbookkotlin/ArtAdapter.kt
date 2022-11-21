package com.ozgortech.artbookkotlin

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ozgortech.artbookkotlin.databinding.RecyclerRowBinding

class ArtAdapter(var artArrayList: ArrayList<Art>) : RecyclerView.Adapter<ArtAdapter.ArtHolder>() {

    class ArtHolder(var binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtHolder {
        var binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context))
        return ArtHolder(binding)
    }

    override fun onBindViewHolder(holder: ArtHolder, position: Int) {
        holder.binding.recyclerViewTextView.text = artArrayList.get(position).artName
        holder.itemView.setOnClickListener(View.OnClickListener { view ->
            val intent = Intent(holder.itemView.context,ArtActivity::class.java)
            intent.putExtra("art",artArrayList.get(position))
            intent.putExtra("info","old")
            holder.itemView.context.startActivity(intent)
        })
    }

    override fun getItemCount(): Int {
        return artArrayList.size
    }
}