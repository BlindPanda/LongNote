package leon.longnote.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import leon.longnote.model.NoteItem

/**
 * Created by liyl9 on 2018/3/19.
 */
@Dao
interface NoteDbDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: NoteItem)

    @Query("DELETE FROM note_table WHERE id = :id")
    fun deleteById(id: Int)

    @Query("SELECT * FROM note_table")
    fun loadAllNoteItems(): LiveData<List<NoteItem>>

    @Query("SELECT * FROM note_table WHERE id = :note_id")
    fun loadNote(note_id: Int): LiveData<NoteItem>
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateNote(item: NoteItem)
}
