package com.example.notification

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notification.databinding.ActivityMainBinding
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var binding: ActivityMainBinding
    private var list = ArrayList<NotificationEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initialize()
    }

    private fun initialize() {
        getFirebaseToken()
        getDataFromDatabase()
    }

    private fun getDataFromDatabase() {
        lifecycleScope.launch(Dispatchers.IO) {
            val database = UserDatabase.getDatabase(applicationContext).notificationDao()
            list = ArrayList(database.readAllData())
            Log.e("list size", list.size.toString())
            withContext(Dispatchers.Main) {
               setAdapter()
            }
        }
    }

    private fun setAdapter() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        notificationAdapter = NotificationAdapter(list)
        binding.recyclerView.adapter = notificationAdapter
    }

    private fun getFirebaseToken() {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                Log.e("Token", "Refreshed token: $token")
            }
            .addOnFailureListener { e ->
                Log.e("Token", "Failed to get token: ${e.message}")
            }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(myReceiver)
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onResume() {
        Log.e("Resume", "On resume triggered")
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(myReceiver, IntentFilter("FBR-MESSAGE"), RECEIVER_EXPORTED)

        }else {
            registerReceiver(myReceiver, IntentFilter("FBR-MESSAGE"))
        }
    }

    private var myReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.getStringExtra("action")
            if (action == Constants.MESSAGE_RECEIVED) {
                Log.e("Action", action.toString())
                getDataFromDatabase()
            }
        }
    }
}
