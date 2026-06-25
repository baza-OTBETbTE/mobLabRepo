package com.example.photogallery.db

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "favorites")
data class FavoritePhoto(
    @PrimaryKey val id: String,
    val url: String
)

@Dao
interface FavoritePhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: FavoritePhoto)

    @Query("SELECT * FROM favorites")
    fun getAll(): Flow<List<FavoritePhoto>>

    @Query("DELETE FROM favorites")
    suspend fun deleteAll()

    @Query("DELETE FROM favorites WHERE id = :photoId")
    suspend fun deleteById(photoId: String)
}

@Database(entities = [FavoritePhoto::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoritePhotoDao(): FavoritePhotoDao
}