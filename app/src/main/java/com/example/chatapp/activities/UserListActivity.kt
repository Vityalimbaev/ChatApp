package com.example.chatapp.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.*
import com.example.chatapp.Adapters.UserAdapter
import com.example.chatapp.firebase.FireBaseAccess
import com.example.chatapp.firebase.FireBaseAccess.usersDataBaseReference
import com.example.chatapp.models.User
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError


class UserListActivity : AppCompatActivity() {

    private val users = ArrayList<User>()
    private lateinit var userRecycleView:RecyclerView
    private lateinit var adapter: UserAdapter
    private lateinit var layoutManager:RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)
        usersDataBaseReference.addChildEventListener(getUsersChildEventListener())
        bindRecycleView()

        val dividerItemDecoration = DividerItemDecoration(
            userRecycleView.context,
            DividerItemDecoration.VERTICAL
        )
        userRecycleView.addItemDecoration(dividerItemDecoration)
    }

    private fun getUsersChildEventListener():ChildEventListener{
           return object : ChildEventListener {
                override fun onCancelled(error: DatabaseError) {}

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val user = snapshot.getValue(User::class.java)
                    if(user?.id != FireBaseAccess.auth.uid) {
                        user?.avatarMockUpResource = R.drawable.ic_baseline_person_24
                        users.add(user!!)
                        adapter.notifyDataSetChanged()
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {}

            }
    }

    private fun bindRecycleView() {
        userRecycleView = findViewById(R.id.userListRecycleView)
        userRecycleView.setHasFixedSize(true)
        adapter = UserAdapter(users)
        layoutManager = LinearLayoutManager(this)

        adapter.setOnUserClickListener(object : UserAdapter.OnUserClickListener{
            override fun onUserClick(position: Int) {
                startNewActivity(this@UserListActivity, ChatActivity::class.java,
                        IntentData(KEY_RECIPIENT_ID, users[position].id ),
                        IntentData(KEY_RECIPIENT_NAME, users[position].name)
                )
            }

        })

        userRecycleView.layoutManager= layoutManager
        userRecycleView.adapter = adapter
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
                startNewActivity(this,LoginInActivity::class.java)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}