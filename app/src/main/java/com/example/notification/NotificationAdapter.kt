package com.example.notification

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotificationAdapter(private var notifications: List<NotificationEntity>) : RecyclerView.Adapter<NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notification_layout, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val currentItem = notifications[position]
        holder.titleTextView.text = currentItem.title
        holder.bodyTextView.text = currentItem.body
        holder.timeTextView.text = currentItem.timestamp.toString()
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newNotifications: List<NotificationEntity>) {
        notifications = newNotifications
        notifyDataSetChanged()
        Log.e("Notifications", "Notified set Change")
    }
}

class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val titleTextView: TextView = itemView.findViewById(R.id.title)
    val bodyTextView: TextView = itemView.findViewById(R.id.body)
    val timeTextView: TextView = itemView.findViewById(R.id.time)
}