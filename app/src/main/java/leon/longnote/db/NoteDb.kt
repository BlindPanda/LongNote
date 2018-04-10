package leon.longnote.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import android.os.Environment
import leon.longnote.convertor.Converter
import leon.longnote.convertor.DateConverter
import leon.longnote.dao.NoteDbDao
import leon.longnote.model.NoteItem

/**
 * Created by liyl9 on 2018/3/19.
 */
@Database(
        entities = arrayOf(NoteItem::class),
        version = 2,
        exportSchema = false
)
@TypeConverters(Converter::class)
abstract class NoteDb: RoomDatabase() {
    companion object {
        fun create(context: Context): NoteDb {

            val databaseBuilder = Room.databaseBuilder(context, NoteDb::class.java, "reddit.db")
            return databaseBuilder
                    .fallbackToDestructiveMigration()
                    .build()
        }
    }
    abstract fun note_table(): NoteDbDao

}
