package leon.longnote.viewmodel

import android.app.Application
import android.arch.lifecycle.*
import leon.longnote.data.DataRepository
import leon.longnote.ui.BasicApplication
import leon.longnote.model.NoteItem

/**
 * Created by liyl9 on 2018/3/20.
 */
class NoteItemListViewModel(application: Application, private val mRepository: DataRepository) : AndroidViewModel(application) {

    // MediatorLiveData can observe other LiveData objects and react on their emissions.
    private val mObservableNotes: MediatorLiveData<List<NoteItem>> = MediatorLiveData()

    /**
     * Expose the LiveData Products query so the UI can observe it.
     */
    val products: LiveData<List<NoteItem>>
        get() = mObservableNotes

    init {

        // set by default null, until we get data from the database.
        mObservableNotes.value = null

        val products = (application as BasicApplication).getRepository().getAllNotes()


        // observe the changes of the products from the database and forward them
        mObservableNotes.addSource<List<NoteItem>>(products, Observer<List<NoteItem>> { noteEntities ->
            mObservableNotes.setValue(noteEntities)
        })
    }

    fun deleteNoteById(id:Int){
        mRepository.deleteNoteById(id)
    }


    class ListFactory(private val mApplication: Application) : ViewModelProvider.NewInstanceFactory() {

        private val mRepository: DataRepository = (mApplication as BasicApplication).getRepository()

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            return NoteItemListViewModel(mApplication, mRepository) as T
        }
    }
}
