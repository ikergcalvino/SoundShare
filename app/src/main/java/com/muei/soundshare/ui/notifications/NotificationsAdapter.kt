package com.muei.soundshare.ui.notifications

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.muei.soundshare.databinding.NotificationListBinding

class NotificationsAdapter(private val notifications: List<Notification>) :
    RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    class ViewHolder(private val binding: NotificationListBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(notification: Notification) {
            binding.notificationText.text = notification.notification
        }
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val binding = NotificationListBinding.inflate(LayoutInflater.from(parent.context), parent, false)


        return ViewHolder(binding);
    }

    override fun getItemCount() = notifications.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notifications[position])
    }

}