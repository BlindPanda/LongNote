package leon.longnote.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.util.Log
import leon.longnote.model.NoteItem

/**
 * Created by liyl9 on 2018/3/20.
 */
class DataRepository private constructor(private val mlocalDataSource: LocalDataSource) {
    private val mObservableProducts: MediatorLiveData<List<NoteItem>>

    init {
        mObservableProducts = MediatorLiveData<List<NoteItem>>()

        mObservableProducts.addSource(mlocalDataSource.getDataBase().note_table().loadAllNoteItems()) { productEntities ->
            mObservableProducts.postValue(productEntities)
        }
    }

    fun loadNoteById(id:Int): LiveData<NoteItem> {
        Log.e("yanlonglong","mDatabase.note_table().loadNote(noteId):")
        val item = mlocalDataSource.getDataBase().note_table().loadNote(id)
        if(item==null){
            Log.e("yanlonglong","query item is null")
        }else{
            Log.e("yanlonglong","query item is not null")
        }
        return item
    }
    fun deleteNoteById(noteId: Int){
        mlocalDataSource.deleteNoteItemById(noteId)
    }
    fun updateNoteById(noteItem: NoteItem){
        mlocalDataSource.updateNoteItemById(noteItem)
    }
    fun insertNote(noteItem: NoteItem){
        mlocalDataSource.insertNoteItem(noteItem)
    }

    fun getAllNotes(): LiveData<List<NoteItem>> {
        return mObservableProducts
    }


    companion object {

        private var sInstance: DataRepository? = null

        fun getInstance(localDataSource: LocalDataSource): DataRepository {
            if (sInstance == null) {
                synchronized(DataRepository::class.java) {
                    if (sInstance == null) {
                        sInstance = DataRepository(localDataSource)
                    }
                }
            }
            return sInstance!!
        }
    }
}
