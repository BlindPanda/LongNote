package leon.longnote.ui

import leon.longnote.model.NoteItem

/**
 * Created by liyl9 on 2018/3/20.
 */
interface NoteItemClickInterface {
    fun onClick(item: NoteItem)
    fun onItemLongClick(item: NoteItem):Boolean
}