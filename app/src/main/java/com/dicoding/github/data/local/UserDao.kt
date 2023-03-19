package com.dicoding.github.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dicoding.github.data.model.UserGithubRespone

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: UserGithubRespone.Item)

    @Query("SELECT * FROM User")
    fun loadAll(): LiveData<MutableList<UserGithubRespone.Item>>

    @Query("SELECT * FROM User WHERE id LIKE :id LIMIT 1")
    fun findById(id: Int): UserGithubRespone.Item

    @Delete
    fun delete(user: UserGithubRespone.Item)
}