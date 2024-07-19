package com.example.notification

class NotificationRepository(private val notificationDao: NotificationDao) {
//    val readAllData: LiveData<List<NotificationEntity>> = notificationDao.readAllData()

    suspend fun addNotification(notification: NotificationEntity) {
        notificationDao.insert(notification)
    }

}