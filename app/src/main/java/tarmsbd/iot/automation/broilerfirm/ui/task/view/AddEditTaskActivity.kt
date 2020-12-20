package tarmsbd.iot.automation.broilerfirm.ui.task.view

import android.annotation.SuppressLint
import android.os.Bundle
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

        calender_view.minDate = Date().time
        selectedDate = Date().time.convertedDateTime()

        calender_view.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = "$dayOfMonth/$month/$year"
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun Long.convertedDateTime(): String = SimpleDateFormat(pattern).format(Date(this * 1000))

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
            if (it.isSuccessful) super.onBackPressed()
            else Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show()
        }
    }
}