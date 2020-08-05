package com.example.chatapp.Adapters

import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.firebase.FireBaseAccess
import com.example.chatapp.models.MessageChatApp


class ChatAdapter : RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    val messages = ArrayList<MessageChatApp>()

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView = itemView.findViewById<TextView>(R.id.nameTextView)
        val messageTextView = itemView.findViewById<TextView>(R.id.messageTextView)
        val photoImageView = itemView.findViewById<ImageView>(R.id.photoImageView)
        val messageContainer = itemView.findViewById<ConstraintLayout>(R.id.messageContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return MessageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val container = holder.messageContainer
        val layoutParams = container.layoutParams as LinearLayout.LayoutParams
        val message = messages[position]

        holder.messageTextView.text = message.message
        holder.nameTextView.visibility = View.GONE
        if (message.imageURL.isNotEmpty()) {
            Glide.with(holder.photoImageView.context).load(message.imageURL).centerInside()
                .into(holder.photoImageView)
        }

        if (message.sender == FireBaseAccess.auth.currentUser?.uid) {
            layoutParams.gravity = Gravity.END
            container.setBackgroundResource(R.drawable.current_user_message_bubble)
        }else{
            layoutParams.gravity = Gravity.START
            container.setBackgroundResource(R.drawable.interlocutor_user_bubble)
        }

        //container.layoutParams = layoutParams
    }

}