package leon.longnote.convertor

import android.arch.persistence.room.TypeConverter
import java.sql.Date

/**
 * Created by liyl9 on 2018/3/20.
 */
class Converter {
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return if (timestamp == null) null else Date(timestamp)
    }

    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return date?.time
    }
}
