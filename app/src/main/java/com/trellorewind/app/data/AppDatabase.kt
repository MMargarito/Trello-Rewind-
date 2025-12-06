package com.trellorewind.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.trellorewind.app.data.dao.BoardDao
import com.trellorewind.app.data.dao.CardDao
import com.trellorewind.app.data.entity.BoardEntity
import com.trellorewind.app.data.entity.CardEntity

@Database(
    entities = [BoardEntity::class, CardEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun boardDao(): BoardDao
    abstract fun cardDao(): CardDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kanhero_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

