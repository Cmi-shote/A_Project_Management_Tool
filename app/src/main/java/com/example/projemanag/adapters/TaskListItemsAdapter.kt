package com.example.projemanag.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projemanag.activities.TaskListActivity
import com.example.projemanag.databinding.ItemTaskBinding
import com.example.projemanag.models.Task
import java.util.*

open class TaskListItemsAdapter(private val context: Context, private var list: ArrayList<Task>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var mPositionDraggedFrom = -1
    private var mPositionDraggedTo = -1

    class MyViewHolder(binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root){
        val tvAddTaskList = binding.tvAddTaskList
        val llTaskItem = binding.llTaskItem
        val tvTaskListTitle = binding.tvTaskListTitle
        val cvAddTaskListName = binding.cvAddTaskListName
        val ibCloseListName = binding.ibCloseListName
        val ibDoneListName = binding.ibDoneListName
        val etTaskListName = binding.etTaskListName
        val etEditTaskListName = binding.etEditTaskListName
        val ibEditListName = binding.ibEditListName
        val llTitleView = binding.llTitleView
        val cvEditTaskListName = binding.cvEditTaskListName
        val ibCloseEditableView = binding.ibCloseEditableView
        val ibDoneEditListName = binding.ibDoneEditListName
        val delete = binding.ibDeleteList
        val tvAddCard = binding.tvAddCard
        val cvAddCard = binding.cvAddCard
        val ibCloseCardName = binding.ibCloseCardName
        val ibDoneCardName= binding.ibDoneCardName
        val etCardName = binding.etCardName
        val rvCardList = binding.rvCardList



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            ItemTaskBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if(holder is MyViewHolder){

            if(holder.adapterPosition == list.size - 1){
                holder.tvAddTaskList.visibility = View.VISIBLE
                holder.llTaskItem.visibility = View.GONE
            }else{
                holder.tvAddTaskList.visibility = View.GONE
                holder.llTaskItem.visibility = View.VISIBLE
            }

            holder.tvTaskListTitle.text = model.title

            holder.tvAddTaskList.setOnClickListener {

                holder.tvAddTaskList.visibility = View.GONE
                holder.cvAddTaskListName.visibility = View.VISIBLE
            }

            holder.ibCloseListName.setOnClickListener {
                holder.tvAddTaskList.visibility = View.VISIBLE
                holder.cvAddTaskListName.visibility = View.GONE
            }


            holder.ibDoneListName.setOnClickListener {
                val listName = holder.etTaskListName.text.toString()

                if(listName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.createTaskList(listName)
                    }
                }else{
                    Toast.makeText(context, "please enter list name",Toast.LENGTH_SHORT).show()
                }
            }

            holder.ibEditListName.setOnClickListener {
                holder.etEditTaskListName.setText(model.title)
                holder.llTitleView.visibility = View.GONE
                holder.cvEditTaskListName.visibility = View.VISIBLE
            }

            holder.ibCloseEditableView.setOnClickListener {
                holder.llTitleView.visibility = View.VISIBLE
                holder.cvEditTaskListName.visibility = View.GONE
            }


            holder.ibDoneEditListName.setOnClickListener {
                val listName = holder.etEditTaskListName.text.toString()

                if(listName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.updateTaskList(holder.adapterPosition, listName, model)
                    }
                }else{
                    Toast.makeText(context, "please enter list name",Toast.LENGTH_SHORT).show()
                }
            }


            holder.delete.setOnClickListener {
                alertDialog(holder.adapterPosition, model.title)
            }


            holder.tvAddCard.setOnClickListener{
                holder.tvAddCard.visibility = View.GONE
                holder.cvAddCard.visibility = View.VISIBLE
            }

            holder.ibCloseCardName.setOnClickListener {
                holder.tvAddCard.visibility = View.VISIBLE
                holder.cvAddCard.visibility = View.GONE
            }

            holder.ibDoneCardName.setOnClickListener {
                val cardName = holder.etCardName.text.toString()

                if(cardName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.addCardToTaskList(holder.adapterPosition, cardName)
                    }
                }else{
                    Toast.makeText(context, "Please enter a card name",Toast.LENGTH_SHORT).show()
                }
            }

            holder.rvCardList.layoutManager = LinearLayoutManager(context)

            holder.rvCardList.setHasFixedSize(true)

            val adapter = CardListItemAdapter(context, model.cards)
            holder.rvCardList.adapter = adapter


            adapter.setOnClickListener(
                object : CardListItemAdapter.OnClickListener{

                    override fun onClick(cardPosition: Int) {
                        if(context is TaskListActivity){
                            context.cardDetails(holder.adapterPosition, cardPosition)
                        }
                    }
                }
            )

            //implementing the drag and drop function
            val dividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            holder.rvCardList.addItemDecoration(dividerItemDecoration)

            val help = ItemTouchHelper(
                object : ItemTouchHelper.SimpleCallback(
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
                ){
                    override fun onMove(
                        recyclerView: RecyclerView,
                        dragged: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        val draggedPosition = dragged.adapterPosition
                        val targetPosition = target.adapterPosition

                        if(mPositionDraggedFrom == -1){
                            mPositionDraggedFrom = draggedPosition
                        }

                        mPositionDraggedTo = targetPosition

                        Collections.swap(list[holder.adapterPosition].cards,
                            draggedPosition, targetPosition)

                        adapter.notifyItemMoved(draggedPosition, targetPosition)
                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    }

                    //this function is called when the dragging and dropping is over
                    override fun clearView(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder
                    ) {
                        super.clearView(recyclerView, viewHolder)

                        if (mPositionDraggedFrom != -1 && mPositionDraggedTo != -1
                            && mPositionDraggedFrom != mPositionDraggedTo
                        ) {
                            (context as TaskListActivity).upDateCardsInTaskList(
                                holder.adapterPosition,
                                list[holder.adapterPosition].cards
                            )
                            //reset values
                            mPositionDraggedFrom = -1
                            mPositionDraggedTo = -1
                        }
                    }
                }
            )
            /*Attaches the ItemTouchHelper to the provided RecyclerView. If TouchHelper is already
            attached to a RecyclerView, it will first detach from the previous one.*/
            help.attachToRecyclerView(holder.rvCardList)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun alertDialog(position: Int, title: String){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to delete $title")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("yes"){dialog, _ ->
            dialog.dismiss()
            if(context is TaskListActivity){
            context.deleteTaskList(position)
        }
        }

        builder.setNegativeButton("No"){dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()

    }
}