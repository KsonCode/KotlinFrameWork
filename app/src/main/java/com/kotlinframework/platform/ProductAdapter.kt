package com.kotlinframework.platform

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.kotlinframework.platform.ProductAdapter.VH
import com.kotlinframework.platform.bean.ProductEntity

class ProductAdapter(val ctx: Context, val list: List<ProductEntity.Result>) : RecyclerView.Adapter<VH>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): VH {
        var view = View.inflate(ctx,R.layout.product_item_layout,null)
        var vh = VH(view)

        return vh
    }

    override fun getItemCount(): Int {
        return  list.size

    }

    override fun onBindViewHolder(viewHolder: VH, pos: Int) {
        viewHolder.tv.text = list[pos].commodityName

        Glide.with(ctx).load(list[pos].masterPic).into(viewHolder.iv)



    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView){
        var iv :ImageView = itemView.findViewById(R.id.iv)
        var tv :TextView = itemView.findViewById(R.id.tv)
    }

}