package leon.longnote.data

import leon.longnote.db.NoteDb
import leon.longnote.model.NoteItem
import leon.longnote.utils.AppExcutors

/**
 * Created by liyl9 on 2018/3/30.
 */
class LocalDataSource(private val database: NoteDb, private val mAppExecutors: AppExcutors) : DataInterface {
    override fun insertNoteItem(item: NoteItem) {
        mAppExecutors.diskIO().execute { database.note_table().insert(item) }
    }

    companion object {

        private var sInstance: LocalDataSource? = null

        fun getInstance(database: NoteDb, mAppExecutors: AppExcutors): LocalDataSource {
            if (sInstance == null) {
                synchronized(LocalDataSource::class.java) {
                    if (sInstance == null) {
                        sInstance = LocalDataSource(database, mAppExecutors)
                    }
                }
            }
            return sInstance!!
        }
    }

    override fun deleteNoteItemById(id: Int) {
        mAppExecutors.diskIO().execute { database.note_table().deleteById(id) }

    }

    override fun updateNoteItemById(item: NoteItem) {
        mAppExecutors.diskIO().execute { database.note_table().updateNote(item) }
    }

    fun getDataBase(): NoteDb {
        return database
    }
}