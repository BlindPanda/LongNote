package leon.longnote.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableField
import android.util.Log
import leon.longnote.data.DataRepository
import leon.longnote.model.NoteItem
import leon.longnote.ui.BasicApplication
import java.sql.Date

/**
 * Created by liyl9 on 2018/3/27.
 */

class NoteItemViewModel(application: Application, private val repository: DataRepository,
                        mProductId: Int) : AndroidViewModel(application) {

    private val mObservableProduct: LiveData<NoteItem> = repository.loadNoteById(mProductId)

    var item = ObservableField<NoteItem>()
    var isNew = ObservableField<Boolean>(true)
    var currentDate = ObservableField<Date>(Date(System.currentTimeMillis()))

    fun getObservableProduct():LiveData<NoteItem>{
        return mObservableProduct
    }

    init {
        if(mObservableProduct==null){
            Log.e("yanlonglong","query mObservableProduct is null")
        }else{
            Log.e("yanlonglong","query mObservableProduct is not null"+ (mObservableProduct.value==null))
        }
    }

    fun setNoteItem(item: NoteItem) {
        Log.e("yanlonglong","item:"+item.id)
        this.item.set(item)
    }

    fun deleteNoteById(id:Int){
        repository.deleteNoteById(id)
    }
    fun updateNoteById(item: NoteItem){
        repository.updateNoteById(item)
    }
    fun insertNoteItem(item:NoteItem){
        repository.insertNote(item)
    }

    /**
     * A creator is used to inject the product ID into the ViewModel
     *
     *
     * This creator is to showcase how to inject dependencies into ViewModels. It's not
     * actually necessary in this case, as the product ID can be passed in a public method.
     */
    class Factory(private val mApplication: Application, private val mProductId: Int) : ViewModelProvider.NewInstanceFactory() {

        private val mRepository: DataRepository = (mApplication as BasicApplication).getRepository()

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            return NoteItemViewModel(mApplication, mRepository, mProductId) as T
        }
    }
}
