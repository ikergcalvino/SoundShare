package com.muei.soundshare.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.muei.soundshare.R

abstract class BaseAdapter<T>(
    private val items: List<T>,
    private val layoutResId: Int
) : RecyclerView.Adapter<BaseAdapter<T>.BaseViewHolder>() {

    inner class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        return BaseViewHolder(view)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item = items[position]
        bindItem(holder.itemView, item)

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }

        val addButton: MaterialButton? = holder.itemView.findViewById(R.id.user_add)

        addButton?.setOnClickListener {
            onAddButtonClick(item)
        }
    }

    abstract fun bindItem(view: View, item: T)

    abstract fun onItemClick(item: T)
    abstract fun onAddButtonClick(item: T)
}