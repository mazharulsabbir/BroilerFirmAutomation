package tarmsbd.iot.automation.broilerfirm.ui.task.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_edit_task.*
import tarmsbd.iot.automation.broilerfirm.R
import tarmsbd.iot.automation.broilerfirm.data.model.TaskReminder
import tarmsbd.iot.automation.broilerfirm.data.repo.MyFirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "AddEditTaskActivity"
private const val pattern = "dd/MM/yyyy hh:mm:ss"

class AddEditTaskActivity : AppCompatActivity() {
    private var selectedDate: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_task)

        setSupportActionBar(toolbar)

        task_title.editText!!.requestFocus()

        calender_view.minDate = Date().time
        selectedDate = Date().time.convertedDateTime()

        calender_view.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = "$dayOfMonth/${month + 1}/$year"
        }

        val taskReminder = intent.getParcelableExtra<TaskReminder>("update_task_intent_extra_task")
        taskReminder?.let {
            task_btn.text = "Update Task"
            task_title.editText!!.setText(it.name.toString())
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun Long.convertedDateTime(): String = SimpleDateFormat(pattern).format(this)

    fun saveNewTask(view: View) {
        val title = task_title.editText!!.text.toString()
        task_title.error = null

        if (title.isEmpty()) {
            task_title.error = "Title should not be empty"
            return
        }

        MyFirebaseDatabase.addNewTask(
            TaskReminder(
                name = title,
                date = selectedDate
            )
        )?.addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Successfully Saved!", Toast.LENGTH_SHORT).show()
                super.onBackPressed()
            } else {
                Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "saveNewTask: ", it.exception)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) super.onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}