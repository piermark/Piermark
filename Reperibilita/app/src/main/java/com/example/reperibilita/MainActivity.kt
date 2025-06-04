package com.example.reperibilita

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var phoneEditText: EditText
    private lateinit var intervalsTextView: TextView
    private val intervals = mutableListOf<Interval>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        phoneEditText = findViewById(R.id.phoneEditText)
        intervalsTextView = findViewById(R.id.intervalsTextView)
        val selectContactButton: Button = findViewById(R.id.selectContactButton)
        val addIntervalButton: Button = findViewById(R.id.addIntervalButton)
        val scheduleButton: Button = findViewById(R.id.scheduleButton)
        val activateButton: Button = findViewById(R.id.activateButton)
        val deactivateButton: Button = findViewById(R.id.deactivateButton)

        selectContactButton.setOnClickListener { pickContact() }
        addIntervalButton.setOnClickListener { addInterval() }
        scheduleButton.setOnClickListener { schedule() }
        activateButton.setOnClickListener { ForwardingService.activate(this, phoneEditText.text.toString()) }
        deactivateButton.setOnClickListener { ForwardingService.deactivate(this) }

        requestPermissionsIfNeeded()
    }

    private fun pickContact() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        contactResultLauncher.launch(intent)
    }

    private val contactResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val number = getPhoneFromUri(uri)
                phoneEditText.setText(number)
            }
        }
    }

    private fun getPhoneFromUri(uri: Uri): String {
        var number = ""
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                number = it.getString(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
            }
        }
        return number
    }

    private fun addInterval() {
        val startDatePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.select_start_date))
            .build()
        startDatePicker.addOnPositiveButtonClickListener { startMillis ->
            val start = Calendar.getInstance().apply { timeInMillis = startMillis }
            val startTimePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setTitleText(getString(R.string.select_start_time))
                .build()
            startTimePicker.addOnPositiveButtonClickListener {
                start.set(Calendar.HOUR_OF_DAY, startTimePicker.hour)
                start.set(Calendar.MINUTE, startTimePicker.minute)

                val endDatePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText(getString(R.string.select_end_date))
                    .build()
                endDatePicker.addOnPositiveButtonClickListener { endMillis ->
                    val end = Calendar.getInstance().apply { timeInMillis = endMillis }
                    val endTimePicker = MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setTitleText(getString(R.string.select_end_time))
                        .build()
                    endTimePicker.addOnPositiveButtonClickListener {
                        end.set(Calendar.HOUR_OF_DAY, endTimePicker.hour)
                        end.set(Calendar.MINUTE, endTimePicker.minute)
                        intervals.add(Interval(start, end))
                        updateIntervalsText()
                    }
                    endTimePicker.show(supportFragmentManager, "end_time")
                }
                endDatePicker.show(supportFragmentManager, "end_date")
            }
            startTimePicker.show(supportFragmentManager, "start_time")
        }
        startDatePicker.show(supportFragmentManager, "start_date")
    }

    private fun updateIntervalsText() {
        intervalsTextView.text = intervals.joinToString("\n") { it.toString() }
    }

    private fun schedule() {
        val number = phoneEditText.text.toString()
        if (number.isNotBlank() && intervals.isNotEmpty()) {
            ScheduleManager.schedule(this, intervals, number)
        }
    }

    private fun requestPermissionsIfNeeded() {
        val permissions = arrayOf(
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CONTACTS
        )
        val toRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (toRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, toRequest.toTypedArray(), 0)
        }
    }
}
