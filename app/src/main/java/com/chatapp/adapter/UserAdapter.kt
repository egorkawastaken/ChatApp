package com.chatapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.databinding.UserRowLayoutBinding
import io.getstream.chat.android.client.models.User
import android.text.format.DateFormat

interface OnItemClickCallback {

    fun onItemClick(id: String)

}

class UserAdapter(private val onItemClickCallback: OnItemClickCallback): RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private val differCallBack = object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }
    }

    val differ = AsyncListDiffer(this,differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = UserRowLayoutBinding.inflate(inflater,parent,false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class UserViewHolder(val binding: UserRowLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(currentUser: User) {
            binding.avatarImageView.setUserData(currentUser)
            binding.usernameTextView.text = currentUser.id
            binding.lastActiveTextView.text = convertDate(currentUser.lastActive?.time!!)
            binding.rootLayout.setOnClickListener {
                onItemClickCallback.onItemClick(currentUser.id)
            }
        }
    }

    private fun convertDate(milliseconds: Long): String {
        return DateFormat.format("dd/MM/yyyy hh:mm a", milliseconds).toString()
    }


}