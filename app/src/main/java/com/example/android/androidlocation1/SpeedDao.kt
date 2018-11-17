package com.example.android.androidlocation1

import android.arch.persistence.room.*

@Dao
interface SpeedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSpeed(speed: Speed)

    @Update
    fun updateSpeed(speed: Speed)

    @Delete
    fun deleteSpeed(speed: Speed)

    //@Query("SELECT * FROM Speed WHERE id == :id")
    //fun getGenderByName(name: String): List<Speed>

    @Query("SELECT * FROM Speed")
    fun getSpeeds(): List<Speed>
}