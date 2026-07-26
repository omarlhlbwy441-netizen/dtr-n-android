package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPreferencesDao {
    @Query("SELECT value FROM user_preferences WHERE key = :key LIMIT 1")
    suspend fun getValue(key: String): String?

    @Query("SELECT * FROM user_preferences")
    fun getAllPreferences(): Flow<List<UserPreferencesEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePreference(preference: UserPreferencesEntity)

    @Query("DELETE FROM user_preferences WHERE key = :key")
    suspend fun deleteKey(key: String)

    @Query("DELETE FROM user_preferences")
    suspend fun clearAll()
}
