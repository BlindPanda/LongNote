package leon.longnote.data

import leon.longnote.model.NoteItem

/**
 * Created by liyl9 on 2018/3/30.
 */
interface DataInterface {
    fun deleteNoteItemById(id:Int)
    fun updateNoteItemById(item:NoteItem)
    fun insertNoteItem(item:NoteItem)
}
