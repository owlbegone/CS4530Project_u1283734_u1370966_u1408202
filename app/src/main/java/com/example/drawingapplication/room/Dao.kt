package com.example.drawingapplication.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.drawingapplication.Model.Stroke
import kotlinx.coroutines.flow.Flow

@Dao
interface DrawingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrawing(drawing: DrawingEntity)

    @Query("SELECT drawingPath FROM drawings WHERE id = :drawingId")
    suspend fun getDrawingById(drawingId: Int): String

    @Query("DELETE FROM Drawings WHERE id = :drawingId")
    suspend fun deleteDrawingById(drawingId: Int)

    @Query("select * from Drawings order by id desc")
    fun getAllDrawings(): Flow<List<DrawingEntity>>

    @Insert
    suspend fun insertDrawingAndReturnId(drawing: DrawingEntity): Long
}


