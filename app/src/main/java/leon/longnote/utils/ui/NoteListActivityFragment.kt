package leon.longnote.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.*
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_note_list.*
import leon.longnote.R
import leon.longnote.databinding.FragmentNoteListBinding
import leon.longnote.model.NoteItem
import leon.longnote.ui.acitonmode.NoteListActionMode
import leon.longnote.utils.CommonUtils
import leon.longnote.viewmodel.NoteItemListViewModel

class NoteListActivityFragment : Fragment(),NoteItemClickInterface {
    companion object {
        val TAG = "NoteListActivityFragment"
        val NOTE_ID = "note_id"
        val INTENT_SLIDE_DELETE_ITEM_ACTION = "intent_slid_delete_action"

        fun forNote(noteId: Int): EditNoteFragment {
            val fragment = EditNoteFragment()
            val args = Bundle()
            args.putInt(NOTE_ID, noteId)
            fragment.setArguments(args)
            return fragment
        }
    }
    lateinit var mBinding: FragmentNoteListBinding
    lateinit var mAdapter:NoteListAdapter
    var mNoteListActionMode: NoteListActionMode? = null
    // var mBinding FragmentNoteListBinding



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
       mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_note_list, container, false)
        //return inflater.inflate(R.layout.fragment_note_list, container, false)
        mAdapter = NoteListAdapter(this.context,this)
        mBinding.noteList.adapter = mAdapter
        mBinding.fab.setOnClickListener { view ->
            (activity as NoteListActivity).show(null)
        }
        registerSlideDeleteListener()
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val factory = NoteItemListViewModel.ListFactory(this.activity.application)
        val viewModel = ViewModelProviders.of(this,factory).get(NoteItemListViewModel::class.java!!)
        subscribeUi(viewModel)
    }

    override fun onDestroy() {
        super.onDestroy()
        unRegisterSlideDeleteListener()
    }

    private fun registerSlideDeleteListener() {
        var intentFilter = IntentFilter()
        intentFilter.addAction(INTENT_SLIDE_DELETE_ITEM_ACTION)
        activity.registerReceiver(slideDeleteReceiver,intentFilter)
    }

    private fun unRegisterSlideDeleteListener(){
        activity.unregisterReceiver(slideDeleteReceiver)

    }
    val slideDeleteReceiver = object:BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("yanlonglong","NoteListActivityFragment onResume onReceive")
            if(intent!=null&& intent.action == INTENT_SLIDE_DELETE_ITEM_ACTION){
                val msgId = R.string.isdeletenote
                val positiveListener = DialogInterface.OnClickListener { dialog, _ ->
                    if (intent.getIntExtra("delete_id", -1) != -1)
                        (activity.application as BasicApplication).getRepository().deleteNoteById(intent.getIntExtra("delete_id", -1))
                    dialog?.dismiss()
                }
                val nagativeLisener = DialogInterface.OnClickListener{ dialog, _ ->
                    dialog?.dismiss()
                }
                if (context != null) {
                    CommonUtils.showSimpleDialog(context,msgId,positiveListener,nagativeLisener)
                }
            }
        }

    }

    private fun subscribeUi(viewModel: NoteItemListViewModel) {
        viewModel.products.observe(this, Observer<List<NoteItem>> { noteEntities ->
            if (noteEntities != null) {
                mBinding.isLoading = false
                mAdapter.setNoteList(noteEntities)
            } else {
                mBinding.isLoading = true
            }
            // espresso does not know how to wait for data binding's loop so we execute changes
            // sync.
            mBinding.executePendingBindings()
        })
    }

    override fun onItemLongClick(item: NoteItem):Boolean {
        startActionMode(item)
        return true
    }

    private fun startActionMode(item: NoteItem) {
        if (!NoteListActivity.mIsActionMode) {
            mNoteListActionMode = NoteListActionMode(activity as NoteListActivity,this,(mBinding.noteList.adapter as NoteListAdapter))
            activity.startActionMode(mNoteListActionMode)
            NoteListActivity.mIsActionMode = true
            // mAdapter.startActionMode(mListView);
            ( mBinding.noteList.adapter as NoteListAdapter).setItemChecked(item,true)
            mNoteListActionMode!!.setActionModeTitle((mBinding.noteList.adapter as NoteListAdapter).getItemChecked(),
                    (mBinding.noteList.adapter as NoteListAdapter).getItemChecked().toString() + context.getString(R.string.alreadyselect))
        }
    }

    override fun onClick(item: NoteItem) {
        if(!NoteListActivity.mIsActionMode)
        (activity as NoteListActivity).show(item)
        else{
            //do action mode thing, do not show item detail
            if(mNoteListActionMode!=null){
                if(( mBinding.noteList.adapter as NoteListAdapter).getItemChecked(item)) {
                    (mBinding.noteList.adapter as NoteListAdapter).setItemChecked(item,false)
                }else{
                    (mBinding.noteList.adapter as NoteListAdapter).setItemChecked(item,true)
                }
                mNoteListActionMode!!.setActionModeTitle(( mBinding.noteList.adapter as NoteListAdapter).getItemChecked(),
                        ( mBinding.noteList.adapter as NoteListAdapter).getItemChecked().toString() + getString(R.string.alreadyselect))
            }
        }
    }

    fun hideFloatingButton() {
       fab!!.visibility = View.GONE
    }

    fun showFloatingButton() {
        fab!!.visibility = View.VISIBLE
    }

}
