package com.ssanto.warungku.adapter

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssanto.warungku.R
import com.ssanto.warungku.detail_warung
import com.ssanto.warungku.model.warung

class WarungViewHolder(
    var context: Context,
    itemView: View
):RecyclerView.ViewHolder(itemView) {

    private lateinit var nama:TextView
    private lateinit var alamat:TextView
    private lateinit var koordinat:TextView
    private lateinit var showDetail:FrameLayout
    private lateinit var photo:ImageView


    fun setWarung(data: warung) {
        /////// INIT //////////
        nama=itemView.findViewById(R.id.nama)
        alamat=itemView.findViewById(R.id.alamat)
        koordinat=itemView.findViewById(R.id.koordinat)
        photo=itemView.findViewById(R.id.fotowarung)
        showDetail=itemView.findViewById(R.id.showDetail)

        ///// DATA /////////
        nama.text =data.nama
        alamat.text =data.alamat
        koordinat.text = data.koordinat
        Glide.with(context).load("data:image/jpeg;base64,${data.base64}").into(photo)

        ///// EVENT //////
        showDetail.setOnClickListener(View.OnClickListener {
            var i = Intent(context,detail_warung::class.java)
            i.putExtra("CALLER","detail")
            i.putExtra("ID",data.id)
            context.startActivities(arrayOf(i))
        })

    }
}