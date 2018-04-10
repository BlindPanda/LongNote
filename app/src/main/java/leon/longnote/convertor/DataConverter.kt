package leon.longnote.convertor

import java.sql.Date

/**
 * Created by liyl9 on 2018/3/20.
 */

object DateConverter {

    fun toDate(timestamp: Long?): Date? {
        return if (timestamp == null) null else Date(timestamp)
    }

    fun toTimestamp(date: Date?): Long? {
        return date?.time
    }
}