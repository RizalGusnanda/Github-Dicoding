package com.dicoding.github.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dicoding.github.data.model.UserGithubRespone

@Database(entities = [UserGithubRespone.Item::class], version = 1, exportSchema = false)
abstract class AppDb : RoomDatabase() {
    abstract fun userDao(): UserDao
}