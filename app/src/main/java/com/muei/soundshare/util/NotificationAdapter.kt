package com.muei.soundshare.util

import android.view.View
import com.muei.soundshare.R
import com.muei.soundshare.databinding.LayoutNotificationBinding
import com.muei.soundshare.entities.Notification

class NotificationAdapter(
    notifications: List<Notification>, clickListener: ItemClickListener<Notification>?
) : BaseAdapter<Notification>(notifications, clickListener, R.layout.layout_notification) {

    override fun bindItem(view: View, item: Notification) {
        val binding = LayoutNotificationBinding.bind(view)

        binding.notificationTitle.text = item.message
        binding.notificationImage.visibility = if (item.isRead) View.INVISIBLE else View.VISIBLE
    }
}