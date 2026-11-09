package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WalnutDao {
    @Query("SELECT * FROM walnut_progress")
    fun getAllProgressFlow(): Flow<List<WalnutProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(progress: WalnutProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<WalnutProgressEntity>)

    @Query("DELETE FROM walnut_progress WHERE walnutId = :walnutId")
    suspend fun deleteProgress(walnutId: String)

    @Query("DELETE FROM walnut_progress")
    suspend fun clearAll()
}
