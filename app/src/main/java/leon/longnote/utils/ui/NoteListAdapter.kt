package leon.longnote.ui

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import leon.longnote.R
import leon.longnote.convertor.DateConverter
import leon.longnote.databinding.RecylceItemEndBinding
import leon.longnote.databinding.RecylceItemXBinding
import leon.longnote.databinding.RecylceItemYBinding
import leon.longnote.databinding.RecylceItemZBinding
import leon.longnote.model.NoteItem
import leon.longnote.model.Type
import leon.longnote.presenter.NoteListItemPresenter
import leon.longnote.ui.NoteListActivity.Companion.mIsActionMode
import leon.longnote.utils.ItemSlideHelper
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by liyl9 on 2018/3/20.
 */
class NoteListAdapter(private val mContext: Context, private val mNoteClickCallback: NoteItemClickInterface?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mNoteItemList: List<NoteItem>? = null
    var dateCategoryList: ArrayList<String>? = null
    private var mRecyclerView: RecyclerView? = null
    fun setNoteList(notes: List<NoteItem>) {
        if (notes == null) {
            Log.d("longlong", "notes is null")
        } else {
            Log.d("longlong", "notes is not null " + notes.size)
        }
        val newNotes = preProcessList(notes)
        if (mNoteItemList == null) {
            Log.d("longlong", "mNoteItemList == null ")
            mNoteItemList = newNotes
            notifyItemRangeInserted(0, newNotes.size)
        } else {
            Log.d("longlong", "mNoteItemList != null "+ mNoteItemList!!.size)
            val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return mNoteItemList!!.size
                }

                override fun getNewListSize(): Int {
                    return newNotes.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val old = mNoteItemList!![oldItemPosition]
                    val note = newNotes[newItemPosition]
                    return old.id === note.id
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val old = mNoteItemList!![oldItemPosition]
                    val note = newNotes[newItemPosition]
                    val result = (old.id === note.id
                            && old.textx === note.textx
                            && old.image1 === note.image1
                            && old.image2 == note.image2
                            && old.image3 == note.image3
                            && old.image4 == note.image4
                            && old.ismedia == note.ismedia
                            && old.voicepath == note.voicepath
                            && old.date == note.date
                            && old.type == note.type)
                    Log.d("longlong", "old.textx "+ old.textx+" new text："+note.textx+result)
                    return result
                }
            })
            mNoteItemList = newNotes
            diffResult.dispatchUpdatesTo(this)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
        mRecyclerView!!.addOnItemTouchListener(ItemSlideHelper(mRecyclerView!!.context, object:ItemSlideHelper.Callback{
            override fun getHorizontalRange(holder: RecyclerView.ViewHolder?): Int {
                if (holder!!.itemView is LinearLayout) {
                    val viewGroup = holder.itemView as ViewGroup
                    if (viewGroup.childCount == 2) {
                        return viewGroup.getChildAt(1).layoutParams.width
                    }
                }


                return 0
            }

            override fun getChildViewHolder(childView: View?): RecyclerView.ViewHolder {
                return mRecyclerView!!.getChildViewHolder(childView)
            }

            override fun findTargetView(x: Float, y: Float): View {
                return mRecyclerView!!.findChildViewUnder(x, y)
            }

        }, mIsActionMode))
    }

    private fun preProcessList(notes: List<NoteItem>): List<NoteItem> {
        //add head and end item here
        var newNotes = ArrayList<NoteItem>()
        dateCategoryList = ArrayList<String>()
        notes.forEach { noteItem ->
            val dateString = SimpleDateFormat("yyyy-MM-dd").format(noteItem.date)
            if(!dateCategoryList!!.contains(dateString)){
                dateCategoryList!! .add(dateString)
                newNotes.add(NoteItem(Type.Y_TYPE.ordinal,"","","","","",noteItem.date,"",false))
            }
            newNotes.add(noteItem)
        }
        newNotes.add(NoteItem(Type.END_TYPE.ordinal,"","","","","", DateConverter.toDate(System.currentTimeMillis())!!,"",false))
        return newNotes
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        val binding = when (viewType) {
            Type.Y_TYPE.ordinal -> DataBindingUtil.inflate<RecylceItemYBinding>(LayoutInflater.from(parent.context), R.layout.recylce_item_y, parent, false)
            Type.X_TYPE.ordinal -> DataBindingUtil.inflate<RecylceItemXBinding>(LayoutInflater.from(parent.context), R.layout.recylce_item_x, parent, false)
            Type.Z_TYPE.ordinal -> DataBindingUtil.inflate<RecylceItemZBinding>(LayoutInflater.from(parent.context), R.layout.recylce_item_z, parent, false)
            Type.END_TYPE.ordinal -> DataBindingUtil.inflate<RecylceItemZBinding>(LayoutInflater.from(parent.context), R.layout.recylce_item_end, parent, false)
            else -> null
        }
        if (binding != null) {
            when (binding) {
                is RecylceItemXBinding -> {
                    binding.callback = mNoteClickCallback
                    return NoteViewHolderX(binding as RecylceItemXBinding)
                }
                is RecylceItemYBinding -> return NoteViewHolderY(binding as RecylceItemYBinding)
                is RecylceItemZBinding -> {
                    binding.callback = mNoteClickCallback
                    return NoteViewHolderZ(binding as RecylceItemZBinding)
                }
                is RecylceItemEndBinding -> return NoteViewHolderEnd(binding as RecylceItemEndBinding)
                else -> {
                }
            }
        }
        return null
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder != null) {
            when (holder) {
                is NoteViewHolderX -> {
                    holder.binding.note = mNoteItemList!![position]
                    holder.binding.sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
                    holder.binding.presenter = NoteListItemPresenter(mContext)
                    holder.binding.executePendingBindings()
                }
                is NoteViewHolderY -> {
                    holder.binding.note = mNoteItemList!![position]
                    holder.binding.sdf = SimpleDateFormat("yyyy-MM-dd")
                    holder.binding.executePendingBindings()
                }
                is NoteViewHolderZ -> {
                    holder.binding.note = mNoteItemList!![position]
                    holder.binding.sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
                    holder.binding.presenter = NoteListItemPresenter(mContext)
                    holder.binding.presenter.playingVoicePath.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                        override fun onPropertyChanged(p0: Observable?, p1: Int) {
                            Log.e("yanlonglong"," onPropertyChanged")
                            holder.binding.presenter.setIsAudioPlaying(holder.binding.note)
                        }

                    })
                    holder.binding.executePendingBindings()
                }
                is NoteViewHolderEnd -> {
                    holder.binding.note = mNoteItemList!![position]
                    holder.binding.executePendingBindings()
                }
            }
        }

    }

    override fun getItemId(position: Int): Long {
        return if (mNoteItemList == null) -1 else mNoteItemList!!.get(position).id!!.toLong()
    }

    override fun getItemCount(): Int {
        return if (mNoteItemList == null) 0 else mNoteItemList!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return mNoteItemList!!.get(position).type
    }
    fun getItemIsChecked(position:Int):Boolean{
        return mNoteItemList!!.get(position).isChecked.get()
    }

    fun setItemChecked(position:Int){
         mNoteItemList!!.get(position).isChecked .set(true)
    }
    fun setItemChecked(item:NoteItem,value:Boolean){
        var _index = -1
        for(index in mNoteItemList!!.indices) {
            if(item.id ==mNoteItemList!!.get(index).id){
                _index = index!!
                break
            }
        }
        if(_index!=-1) {//find the right item
            mNoteItemList!!.get(_index).isChecked .set( value)
        }
    }
    fun getItemChecked(item:NoteItem):Boolean{
        var _index = -1
        for(index in mNoteItemList!!.indices) {
            if(item.id ==mNoteItemList!!.get(index).id){
                _index = index!!
                break
            }
        }
        if(_index!=-1) {//find the right item
           return  mNoteItemList!!.get(_index).isChecked .get()
        }else return false
    }
    fun clearChecked() {
        for (i in mNoteItemList!!.indices) {
            mNoteItemList!!.get(i).isChecked.set(false)
        }
    }

    fun setChecked() {
        for (i in mNoteItemList!!.indices) {
            mNoteItemList!!.get(i).isChecked .set(true)
        }
    }

    fun getItemChecked(): Int {
        var ischeckedcount = 0
        for (i in mNoteItemList!!.indices) {
            if (mNoteItemList!!.get(i).isChecked.get() && mNoteItemList!!.get(i).type !== 3) {
                ischeckedcount++
            }
        }
        return ischeckedcount
    }

    class NoteViewHolderX(val binding: RecylceItemXBinding) : RecyclerView.ViewHolder(binding.getRoot())
    class NoteViewHolderY(val binding: RecylceItemYBinding) : RecyclerView.ViewHolder(binding.getRoot())
    class NoteViewHolderZ(val binding: RecylceItemZBinding) : RecyclerView.ViewHolder(binding.getRoot())
    class NoteViewHolderEnd(val binding: RecylceItemEndBinding) : RecyclerView.ViewHolder(binding.getRoot())
}
