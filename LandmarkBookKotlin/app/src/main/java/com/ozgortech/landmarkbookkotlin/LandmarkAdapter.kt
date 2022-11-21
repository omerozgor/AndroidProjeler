package com.ozgortech.landmarkbookkotlin

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ozgortech.landmarkbookkotlin.databinding.RecyclerRowBinding

class LandmarkAdapter(var landMarkArrayList: ArrayList<Landmark>) : RecyclerView.Adapter<LandmarkAdapter.LandmarkHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LandmarkHolder {
        var binding  = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context))
        return LandmarkHolder(binding)
    }

    override fun onBindViewHolder(holder: LandmarkHolder, position: Int) {
        holder.binding.recyclerViewTextView.text = landMarkArrayList.get(position).name
        holder.itemView.setOnClickListener(View.OnClickListener { view ->
            var intent = Intent(holder.itemView.context,LandmarkActivity::class.java)
            intent.putExtra("landmark",landMarkArrayList.get(position))

            holder.itemView.context.startActivity(intent)
        })
    }

    override fun getItemCount(): Int {
        return landMarkArrayList.size
    }

    class LandmarkHolder(var binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }
}