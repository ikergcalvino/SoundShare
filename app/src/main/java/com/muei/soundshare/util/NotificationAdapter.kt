package com.muei.soundshare.util

import android.view.View
import com.muei.soundshare.R
import com.muei.soundshare.databinding.LayoutNotificationBinding
import com.muei.soundshare.entities.Notification

class NotificationAdapter(
    notifications: List<Notification>
) : BaseAdapter<Notification>(notifications, R.layout.layout_notification) {

    override fun bindItem(view: View, item: Notification) {
        val binding = LayoutNotificationBinding.bind(view)

        binding.apply {
            notificationTitle.text = item.message
            notificationImage.visibility = if (item.isRead) View.INVISIBLE else View.VISIBLE
        }
    }

    override fun onItemClick(item: Notification) {
        TODO("Not yet implemented")
    }

    override fun onAddButtonClick(item: Notification) {
        TODO("Not yet implemented")
    }
}