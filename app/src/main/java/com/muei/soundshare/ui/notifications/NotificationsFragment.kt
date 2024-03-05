package com.muei.soundshare.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.muei.soundshare.R
import com.muei.soundshare.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    private val notifications = listOf(
        Notification("Notificacion 1"),
        Notification("Notificacion 2"),
        Notification("Notificacion 3"),
        Notification("Notificacion 4"),
        Notification("Notificacion 5"),
        Notification("Notificacion 6")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notifications, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerNotifications)
        val adapter = NotificationsAdapter(notifications)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}