package com.example.chatapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.*
import com.example.chatapp.Adapters.ChatAdapter
import com.example.chatapp.firebase.FireBaseAccess
import com.example.chatapp.firebase.FireBaseAccess.messagesDataBaseReference
import com.example.chatapp.firebase.FireBaseAccess.storageDataBaseReference
import com.example.chatapp.firebase.FireBaseAccess.usersDataBaseReference
import com.example.chatapp.models.MessageChatApp
import com.example.chatapp.models.User
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError


class ChatActivity : AppCompatActivity() {

    private val RC_IMAGE_PICKER = 123

    private lateinit var recipientUserId: String
    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var arrayAdapter: ChatAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var messageEditText: EditText
    private var userName = "Default User"
    private var recipientUserName = "Default User"

    private lateinit var usersChildEventListener: ChildEventListener
    private lateinit var messagesChildEventListener: ChildEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        intent.getStringExtra(KEY_USER_NAME)?.let { userName = it }
        intent.getStringExtra(KEY_RECIPIENT_ID)?.let{ recipientUserId = it }
        intent.getStringExtra(KEY_RECIPIENT_NAME)?.let{recipientUserName = it}
        title = recipientUserName

        progressBar = findViewById(R.id.progressBar)
        messageEditText = findViewById(R.id.messageEditView)
        messageRecyclerView= findViewById(R.id.listView)
        progressBar.visibility = ProgressBar.INVISIBLE
        messageEditText.filters = arrayOf(InputFilter.LengthFilter(500))

        bindChatAdapter()
        initDataBaseReferencesAndListeners()
    }

    private fun bindChatAdapter() {
        messageRecyclerView = findViewById(R.id.listView)
        messageRecyclerView.setHasFixedSize(true)
        arrayAdapter = ChatAdapter()
        val layoutManager = LinearLayoutManager(this)

        messageRecyclerView.adapter = arrayAdapter
        messageRecyclerView.layoutManager = layoutManager

        messageRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState != AbsListView.OnScrollListener.SCROLL_STATE_FLING && newState != AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    hideSoftKeyboard()
                }
            }
        })
    }

    private fun initDataBaseReferencesAndListeners(){
        messagesChildEventListener = getMessagesEventListener()
        messagesDataBaseReference.addChildEventListener(messagesChildEventListener)
        usersChildEventListener = getUsersEventListener()
        usersDataBaseReference.addChildEventListener(usersChildEventListener)
    }

    fun onSendMessageButtonClick(view: View){
        if(messageEditText.text.isNotEmpty()){
            val message = MessageChatApp()
            message.message = messageEditText.text.toString()
            message.name = userName
            message.imageURL = String()
            message.sender = FireBaseAccess.auth.currentUser!!.uid
            message.recipient = recipientUserId
            messagesDataBaseReference.push().setValue(message)
        }
        messageEditText.setText(String())
    }

    fun onSendImageViewClick(view: View){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        startActivityForResult(Intent.createChooser(intent, "Choose an image"), RC_IMAGE_PICKER)
    }

    private fun getMessagesEventListener() : ChildEventListener{
        return object : ChildEventListener{
            override fun onCancelled(error: DatabaseError) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(MessageChatApp::class.java)
                if((message?.sender.equals(FireBaseAccess.auth.currentUser?.uid) and message?.recipient.equals(recipientUserId))
                            or (message?.recipient.equals(FireBaseAccess.auth.currentUser?.uid) and message?.sender.equals(recipientUserId))) {
                    message?.let { arrayAdapter.messages.add(it) }
                    arrayAdapter.notifyDataSetChanged()
                    messageRecyclerView.scrollToPosition(arrayAdapter.itemCount-1)
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
        }
    }

    private fun getUsersEventListener() : ChildEventListener{
        return object : ChildEventListener{
            override fun onCancelled(error: DatabaseError) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val user = snapshot.getValue(User::class.java)
                if(user?.id.equals(FireBaseAccess.auth.currentUser?.uid))
                    userName = user?.name.toString()
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val user = snapshot.getValue(User::class.java)
                if(user?.id.equals(FireBaseAccess.auth.currentUser?.uid))
                    userName = user?.name.toString()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.sign_out -> {
                FireBaseAccess.auth.signOut()
                startNewActivity(this, LoginInActivity::class.java)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            RC_IMAGE_PICKER -> if(resultCode == Activity.RESULT_OK) {
                val uri = data?.data
                val imageReference  = storageDataBaseReference.child(uri?.lastPathSegment!!)

                val uploadTask = imageReference.putFile(uri)

                val urlTask = uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {throw it}
                    }
                    imageReference.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        val messageImage =
                            MessageChatApp()
                        messageImage.imageURL = downloadUri.toString()
                        messageImage.name = userName
                        messagesDataBaseReference.push().setValue(messageImage)
                    }
                }
            }
        }
    }

}