package com.muei.soundshare.util

import android.view.View
import com.muei.soundshare.R
import com.muei.soundshare.databinding.LayoutUserBinding
import com.muei.soundshare.entities.User

class UserAdapter(private val users: List<User>) : BaseAdapter<User>(users, R.layout.layout_user) {
    private var filteredUsers: List<User> = users

    fun filter(query: String) {
        filteredUsers = if (query.isEmpty()) {
            users
        } else {
            users.filter { it.username.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return filteredUsers.size
    }

    override fun bindItem(view: View, item: User) {
        val binding = LayoutUserBinding.bind(view)
        binding.userName.text = item.username
    }

    override fun onItemClick(item: User) {
        TODO("Not yet implemented")
    }

    override fun onAddButtonClick(item: User) {
        TODO("Not yet implemented")
    }
}