package com.myhomework.finalproject

import android.app.TimePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.myhomework.finalproject.databinding.ActivityAzkarBinding
import java.util.*

class AzkarActivity : AppCompatActivity() {
    lateinit var binding: ActivityAzkarBinding
    private lateinit var preferences: SharedPreferences
    private lateinit var editor: Editor

    private var isServiceOff = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAzkarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = getSharedPreferences("MyPref", MODE_PRIVATE)
        editor = preferences.edit()
        isServiceOff = preferences.getBoolean("isServiceOff", true)
        val morningTime = preferences.getString("morningTime", "08:00")
        val eveningTime = preferences.getString("eveningTime", "20:00")
        val morningBrightness = preferences.getInt("morningBrightness", 20)
        val eveningBrightness = preferences.getInt("eveningBrightness", 15)

        binding.tvMorningTime.setText(morningTime)
        binding.tvEveningTime.setText(eveningTime)
        binding.tvMorningBrightness.setText(morningBrightness.toString())
        binding.tvEveningBrightness.setText(eveningBrightness.toString())

        handleUI()
    }

    override fun onResume() {
        super.onResume()

        binding.tvMorningTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog =
                TimePickerDialog(this, { view, h, m ->
                    binding.tvMorningTime.setText("$h:$m")
                }, hour, minute, true)
            timePickerDialog.show()
        }

        binding.tvEveningTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog =
                TimePickerDialog(this, { view, h, m ->
                    binding.tvEveningTime.setText("$h:$m")
                }, hour, minute, true)
            timePickerDialog.show()
        }

        binding.btnAzkarService.setOnClickListener {

            val i = Intent(this, AzkarService::class.java)
            if (isServiceOff) {
                isServiceOff = false
                editor.putBoolean("isServiceOff", false)

                editor.putString("morningTime", binding.tvMorningTime.text.toString())
                editor.putString("eveningTime", binding.tvEveningTime.text.toString())
                editor.putInt("morningBrightness", binding.tvMorningBrightness.text.toString().toInt())
                editor.putInt("eveningBrightness", binding.tvEveningBrightness.text.toString().toInt())
                editor.apply()

                ContextCompat.startForegroundService(this, i)
                handleUI()

            } else {
                isServiceOff = true
                editor.putBoolean("isServiceOff", true).apply()

                stopService(i)
                handleUI()
            }
        }
    }

    private fun handleUI(){
        if(!isServiceOff){
            binding.btnAzkarService.text = "Stop Service"
            binding.btnAzkarService.setBackgroundColor(Color.RED)
            binding.tvMorningTime.isEnabled = false
            binding.tvEveningTime.isEnabled = false
            binding.tvMorningBrightness.isEnabled = false
            binding.tvEveningBrightness.isEnabled = false
        }else{
            binding.btnAzkarService.text = "Start Service"
            binding.btnAzkarService.setBackgroundColor(Color.GREEN)
            binding.tvMorningTime.isEnabled = true
            binding.tvEveningTime.isEnabled = true
            binding.tvMorningBrightness.isEnabled = true
            binding.tvEveningBrightness.isEnabled = true
        }
    }

}