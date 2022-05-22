package com.example.projemanag.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projemanag.R
import com.example.projemanag.databinding.ItemBoardBinding
import com.example.projemanag.models.Board

open class BoardItemsAdapter(private val context: Context, private var list: ArrayList<Board>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var onClickListener : OnClickListener? = null

    class MyViewHolder(binding: ItemBoardBinding) : RecyclerView.ViewHolder(binding.root) {
        val ivBoardImage = binding.ivBoardImage
        val tvName = binding.tvName
        val tvCreatedBy = binding.tvCreatedBy
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            ItemBoardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        )
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder ){
            Glide
                .with(context)//context is the variable given to our adapter
                .load(model.image)//image of the board
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(holder.ivBoardImage)//putting the image board image here

            holder.tvName.text = model.name
            holder.tvCreatedBy.text = "Created by : ${model.createdBy}"


            holder.itemView.setOnClickListener {
                if(onClickListener != null){
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }


    interface OnClickListener{
        fun onClick(position: Int, model: Board)
    }


}
