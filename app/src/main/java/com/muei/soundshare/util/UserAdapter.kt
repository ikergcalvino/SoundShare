package com.muei.soundshare.util

import android.view.View
import com.bumptech.glide.Glide
import com.muei.soundshare.R
import com.muei.soundshare.databinding.LayoutUserBinding
import com.muei.soundshare.entities.User

class UserAdapter(
    private var users: List<User>, private val clickListener: ItemClickListener<User>
) : BaseAdapter<User>(users, clickListener, R.layout.layout_user) {

    private var filteredUsers: List<User> = users
    private var friendsList: List<String> = emptyList()
    private var friendRequests: List<String> = emptyList()

    fun updateUsers(newUsers: List<User>) {
        users = newUsers
        filteredUsers = newUsers
        notifyDataSetChanged()
    }

    fun updateFriends(friends: List<String>, requests: List<String>) {
        friendsList = friends
        friendRequests = requests
        notifyDataSetChanged()
    }

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

        // Use Glide to load the profile picture
        if (!item.profilePicture.isNullOrEmpty()) {
            Glide.with(binding.userImage.context).load(item.profilePicture).into(binding.userImage)
        } else {
            binding.userImage.setImageResource(R.drawable.ic_account_circle) // Default image
        }

        binding.userName.text = item.username

        // Adjust button visibility based on the friend status
        when {
            friendsList.contains(item.uid) -> {
                binding.buttonAddFriend.visibility = View.GONE
                binding.buttonRemoveFriend.visibility = View.VISIBLE
            }

            friendRequests.contains(item.uid) -> {
                binding.buttonAddFriend.visibility = View.GONE
                binding.buttonRemoveFriend.visibility = View.VISIBLE
            }

            else -> {
                binding.buttonAddFriend.visibility = View.VISIBLE
                binding.buttonRemoveFriend.visibility = View.GONE
            }
        }

        // Set click listeners for the buttons
        binding.buttonAddFriend.setOnClickListener {
            println("Add Friend Clicked")
            clickListener.onAddFriendButtonClick(item)
        }

        binding.buttonRemoveFriend.setOnClickListener {
            println("Remove Friend Clicked")
            clickListener.onRemoveFriendButtonClick(item)
        }
    }
}