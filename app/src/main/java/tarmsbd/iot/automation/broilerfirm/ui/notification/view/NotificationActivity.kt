package tarmsbd.iot.automation.broilerfirm.ui.notification.view

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_add_edit_task.toolbar
import kotlinx.android.synthetic.main.activity_notification.*
import tarmsbd.iot.automation.broilerfirm.R
import tarmsbd.iot.automation.broilerfirm.data.model.Log
import tarmsbd.iot.automation.broilerfirm.ui.notification.adapter.NotificationAdapter

class NotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        setSupportActionBar(toolbar)

        val ref: DatabaseReference = FirebaseDatabase.getInstance().reference
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        val notificationAdapter = NotificationAdapter()

        val notificationRef = ref.child("user/${user?.uid}/notifications")
        notificationRef.orderByChild("time").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChildren()) {
                    val logs = mutableListOf<Log>()

                    snapshot.children.forEach {
                        val notificationFromSnapshot = it.getValue(Log::class.java)
                        notificationFromSnapshot?.let { log -> logs.add(log) }
                    }

                    notificationAdapter.submitList(logs)
                    notificationAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(
                    applicationContext,
                    "Failed to read notifications",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })


        recycler_view_notification.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(context)
            adapter = notificationAdapter
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) super.onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}