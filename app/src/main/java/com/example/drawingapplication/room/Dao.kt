package com.example.drawingapplication.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DrawingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrawing(drawing: DrawingEntity)

    @Query("DELETE FROM Drawings WHERE id = :drawingId")
    suspend fun deleteDrawingById(drawingId: Int)

    @Query("select * from Drawings order by id desc")
    fun getAllCourses(): Flow<List<DrawingEntity>>
}


