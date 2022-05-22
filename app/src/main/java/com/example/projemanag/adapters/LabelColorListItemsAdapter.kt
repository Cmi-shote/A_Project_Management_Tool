package com.example.projemanag.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projemanag.databinding.ItemLabelColorBinding

class LabelColorListItemsAdapter (private val context: Context,
                                  private var list: ArrayList<String>,
                                  private val mSelectedColor: String):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var onItemClickListener : OnClickListener? = null

    private class MyViewHolder(binding: ItemLabelColorBinding): RecyclerView.ViewHolder(binding.root){
        val vMain = binding.viewMain
        val selectedColor = binding.ivSelectedColor
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(ItemLabelColorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false))

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        if(holder is MyViewHolder){
            holder.vMain.setBackgroundColor(Color.parseColor(item))

            if(item == mSelectedColor){
                holder.selectedColor.visibility = View.VISIBLE
            }else{
                holder.selectedColor.visibility = View.GONE
            }

            holder.itemView.setOnClickListener{
                if(onItemClickListener != null){
                    onItemClickListener!!.onClick(position, item)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnClickListener{
        fun onClick(position: Int, color: String)
    }

}