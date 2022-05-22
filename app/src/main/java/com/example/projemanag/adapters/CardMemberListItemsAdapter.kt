package com.example.projemanag.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projemanag.R
import com.example.projemanag.databinding.ItemCardSelectedMemberBinding
import com.example.projemanag.models.SelectedMembers

open class CardMemberListItemsAdapter (
    private val context: Context,
    private val list: ArrayList<SelectedMembers>,
    private val assignMembers : Boolean
        ): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var onItemClickListener : OnClickListener? = null

    class MyViewHolder(binding: ItemCardSelectedMemberBinding): RecyclerView.ViewHolder(binding.root){
        val ivAddMember = binding.ivAddMember
        val ivSelectedMemberImage = binding.ivSelectedMemberImage
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CardMemberListItemsAdapter.MyViewHolder(
            ItemCardSelectedMemberBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            if(position == list.size - 1 && assignMembers){
                holder.ivAddMember.visibility = View.VISIBLE
                holder.ivSelectedMemberImage.visibility = View.GONE
            }else{
                holder.ivAddMember.visibility = View.GONE
                holder.ivSelectedMemberImage.visibility = View.VISIBLE
                Glide
                    .with(context)
                    .load(model.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(holder.ivSelectedMemberImage)
            }

            holder.itemView.setOnClickListener {
                if(onItemClickListener != null){
                    onItemClickListener!!.onClick()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onItemClickListener =  onClickListener
    }

    interface OnClickListener{
        fun onClick()
    }
}