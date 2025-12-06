package com.trellorewind.app.data

import androidx.room.TypeConverter
import com.trellorewind.app.data.entity.CardStatus

class Converters {
    
    @TypeConverter
    fun fromCardStatus(status: CardStatus): String {
        return status.name
    }
    
    @TypeConverter
    fun toCardStatus(value: String): CardStatus {
        return CardStatus.valueOf(value)
    }
}

