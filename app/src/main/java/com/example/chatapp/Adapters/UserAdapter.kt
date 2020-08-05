package com.example.chatapp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.models.User

class UserAdapter(private val users: ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var onUserClickListener: OnUserClickListener? = null

    interface OnUserClickListener {
        fun onUserClick(position: Int)
    }

    init {
        onUserClickListener
    }

    fun setOnUserClickListener(listener: OnUserClickListener) {
        this.onUserClickListener = listener
    }

    class UserViewHolder(itemView: View, userListener: OnUserClickListener?) :
        RecyclerView.ViewHolder(itemView) {
        val avatarImageView: ImageView = itemView.findViewById(R.id.avatarImageView)
        val userNameTextView: TextView = itemView.findViewById(R.id.userNameTextView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    userListener?.onUserClick(position)
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(
            view,
            onUserClickListener
        )
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        user.avatarMockUpResource?.let { holder.avatarImageView.setImageResource(it) }
        holder.userNameTextView.text = user.name
    }
}