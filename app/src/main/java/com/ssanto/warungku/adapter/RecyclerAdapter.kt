package com.ssanto.warungku.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssanto.warungku.R
import com.ssanto.warungku.model.warung
import java.util.ArrayList

class RecyclerAdapter (
    private val context: Context,
    private val list: ArrayList<warung> = ArrayList()
): RecyclerView.Adapter<WarungViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WarungViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item, parent, false)
        return WarungViewHolder(context,view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: WarungViewHolder, position: Int) {
        holder.setWarung(list[position])
    }
}