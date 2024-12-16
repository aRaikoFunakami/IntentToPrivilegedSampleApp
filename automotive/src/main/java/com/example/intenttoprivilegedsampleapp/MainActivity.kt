package com.example.intenttoprivilegedsampleapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var numberPicker: NumberPicker
    private lateinit var temperatureReceiver: BroadcastReceiver
    private lateinit var temperatureReceiver2: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // NumberPicker の設定
        numberPicker = findViewById(R.id.numberPicker)
        numberPicker.minValue = 16
        numberPicker.maxValue = 32
        numberPicker.value = 22

        // Set ボタンの設定
        val buttonSet = findViewById<Button>(R.id.buttonSet)
        buttonSet.setOnClickListener {
            val selectedValue = numberPicker.value
            sendSetTemperatureIntent(selectedValue.toFloat())
        }

        // Get ボタンの設定
        val buttonGet = findViewById<Button>(R.id.buttonGet)
        buttonGet.setOnClickListener {
            sendGetTemperatureIntent()
        }

        // Temperature取得のBroadcastReceiver登録
        temperatureReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val temperature = intent.getFloatExtra("TEMPERATURE_VALUE", -1f)
                if (temperature >= 16f && temperature <= 32f) {
                    numberPicker.value = temperature.toInt()
                    Log.d("ClientApp", "Temperature received: $temperature")
                } else {
                    Log.e("ClientApp", "Invalid temperature received: $temperature")
                }
            }
        }

        temperatureReceiver2 = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val temperature = intent.getFloatExtra("TEMPERATURE_VALUE", -1f)
                if (temperature >= 16f && temperature <= 32f) {
                    numberPicker.value = temperature.toInt()
                    Log.d("ClientApp", "2: TEMPERATURE_VALUE received: $temperature")
                } else {
                    Log.e("ClientApp", "2: Invalid TEMPERATURE_VALUE received: $temperature")
                }
            }
        }

        // 外部アプリからのブロードキャストを受信するために RECEIVER_EXPORTED を指定
        val filter = IntentFilter("com.example.privilegedsampleapp.RESULT_TEMPERATURE")
        registerReceiver(temperatureReceiver, filter, Context.RECEIVER_EXPORTED)

        val filter2 = IntentFilter("com.example.privilegedsampleapp.TEMPERATURE_VALUE")
        registerReceiver(temperatureReceiver2, filter2, Context.RECEIVER_EXPORTED)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(temperatureReceiver) // Receiver の登録解除
    }

    private fun sendSetTemperatureIntent(value: Float) {
        val intent = Intent("com.example.privilegedsampleapp.ACTION_SET_TEMPERATURE")
        intent.setPackage("com.example.privilegedsampleapp")
        intent.putExtra("TEMPERATURE_VALUE", value)
        sendBroadcast(intent)
        Log.d("ClientApp", "Set temperature intent sent: $value")
    }

    private fun sendGetTemperatureIntent() {
        val intent = Intent("com.example.privilegedsampleapp.ACTION_GET_TEMPERATURE")
        intent.setPackage("com.example.privilegedsampleapp")
        sendBroadcast(intent)
        Log.d("ClientApp", "Get temperature intent sent")
    }
}