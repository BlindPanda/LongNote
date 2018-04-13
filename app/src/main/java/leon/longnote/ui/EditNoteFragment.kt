package leon.longnote.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import leon.longnote.R
import leon.longnote.databinding.EditnoteBinding
import leon.longnote.model.NoteItem
import leon.longnote.presenter.EditNotePresenter
import leon.longnote.viewmodel.NoteItemViewModel
import java.text.SimpleDateFormat

class EditNoteFragment : Fragment() {

    companion object {
        val TAG = "EditNoteFragment"
        val NOTE_ID = "note_id"

    }

    lateinit var mBinding: EditnoteBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.editnote, container, false)
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val factory = NoteItemViewModel.Factory(this.activity.application, arguments.getInt(NOTE_ID))
        val viewModel = ViewModelProviders.of(this, factory).get(NoteItemViewModel::class.java!!)
        subscribeToModel(viewModel)
        mBinding.noteItemViewMode = viewModel
        mBinding.sdf = SimpleDateFormat("yyyy-MM-dd      HH:mm")
        if (arguments.getInt(NOTE_ID) == 0) {
            viewModel.isNew.set(true)
        } else {
            viewModel.isNew.set(false)
        }
        mBinding.presenter = EditNotePresenter(this, this.activity.windowManager, mBinding.noteItemViewMode)
    }

    private fun subscribeToModel(viewModel: NoteItemViewModel) {
        viewModel.getObservableProduct().observe(this, Observer<NoteItem> { noteEntitie ->
            Log.e("yanlonglong", "noteEntitie " + (noteEntitie == null))
            //livedate发生了变化，ObservableField setNoteItem及时更新，它又绑定了xml view
            if (noteEntitie != null) {
                viewModel.setNoteItem(noteEntitie)
                mBinding.presenter.doInit(noteEntitie)
            }
            // espresso does not know how to wait for data binding's loop so we execute changes
            // sync.
            mBinding.executePendingBindings()
        })
    }

    fun getPresenter(): EditNotePresenter {
        return mBinding.presenter
    }

    override fun onDestroyView() {
        getPresenter().onDestory()
        super.onDestroyView()
    }

}
