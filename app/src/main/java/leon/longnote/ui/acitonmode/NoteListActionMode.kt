package leon.longnote.ui.acitonmode

import android.app.AlertDialog
import android.os.Build
import android.view.*
import android.widget.TextView
import android.widget.Toast
import leon.longnote.R
import leon.longnote.ui.BasicApplication
import leon.longnote.ui.NoteListActivity
import leon.longnote.ui.NoteListActivityFragment
import leon.longnote.ui.NoteListAdapter

/**
 * Created by liyl9 on 2018/4/8.
 */
class NoteListActionMode:ActionMode.Callback {
     private var mNoteListAdapter: NoteListAdapter
    private lateinit var mMenuPopupWindow: ActionModeMenuPopupWindow
     private var mActivity:NoteListActivity
    private var mNoteListActivityFragment: NoteListActivityFragment
    private lateinit var mode:ActionMode
    constructor(activity: NoteListActivity, noteListActivityFragment:NoteListActivityFragment,noteListAdapter: NoteListAdapter){
        mNoteListAdapter = noteListAdapter
        mActivity = activity
        mNoteListActivityFragment = noteListActivityFragment

    }
    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.edit_delete -> {
                var delcount = 0
                for (i in 0 until mNoteListAdapter.getItemCount()) {
                    if (mNoteListAdapter.getItemIsChecked(i)) {
                        delcount++
                    }

                }
                if (delcount != 0) {
                    delNoteAlertDialog(mActivity.getString(R.string.isdeletenotes))
                } else {
                    Toast.makeText(mActivity,
                            mActivity.getString(R.string.pleasechoosenotes),
                            Toast.LENGTH_SHORT).show()
                }
            }

        }
        return true
    }

    private fun delNoteAlertDialog(text: String) {
        var delNoteDialog = AlertDialog.Builder(mActivity).create()
        delNoteDialog.show()
        val win = delNoteDialog.getWindow()

        val lp = win!!.attributes
        win!!.setGravity(Gravity.CENTER)
        // lp.alpha = 0.7f;
        win!!.attributes = lp
        win!!.setContentView(R.layout.deldialog)

        val title = win!!.findViewById<TextView>(R.id.dialogtitle)
        title.text = text

        val cancelBtn = win!!.findViewById<TextView>(R.id.cancel)
        cancelBtn.setOnClickListener {
            delNoteDialog.cancel()
        }
        val confirmBtn = win!!.findViewById<TextView>(R.id.confirm)
        confirmBtn.setOnClickListener {
            delNoteDialog.cancel()

            for (i in 0 until mNoteListAdapter.getItemCount()) {
                if (mNoteListAdapter.getItemIsChecked(i)) {
                    if (mNoteListAdapter.getItemId(i).toInt() != -1) {
                        (mActivity.application as BasicApplication).getRepository().deleteNoteById(mNoteListAdapter.getItemId(i).toInt())
                    }
                }
            }
           // notifyList()
            Toast.makeText(mActivity,
                    mActivity.getString(R.string.deletenotesuccess),
                    Toast.LENGTH_SHORT)
                    .show()
            if (this.mode != null) {
                this.mode.finish()
            }
        }
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        this.mode = mode!!
        mActivity.menuInflater.inflate(R.menu.menu_action_mode, menu)
       // mActionModeBackgroundHelper.setActionModeColor()
        setStatusBarColor()
        mMenuPopupWindow = ActionModeMenuPopupWindow(mActivity, mode!!)
        mMenuPopupWindow.setOnItemClickListener(View.OnClickListener { v ->
            when (v.id) {
                android.R.id.selectAll -> {
                    // for (int i = 0; i < mAdapter.getCount(); i++) {
                    // mAdapter.getItem(i).setChecked(true);
                    // mListView.setItemChecked(i, true);
                    // }
                    //
                    mNoteListAdapter.setChecked()
                    mNoteListAdapter.notifyDataSetChanged()
                    setActionModeTitle(mNoteListAdapter.getItemChecked() - mNoteListAdapter.dateCategoryList!!.size,
                            (mNoteListAdapter.getItemChecked() - mNoteListAdapter.dateCategoryList!!.size).toString()+ mActivity.getString(R.string.alreadyselect))
                }
                android.R.id.empty -> {
                    // for (int i = 0; i < mAdapter.getCount(); i++) {
                    // mAdapter.getItem(i).setChecked(false);
                    // mListView.setItemChecked(i, false);
                    // }
                    // setActionModeTitle(0, "0 selected");

                    mNoteListAdapter.clearChecked()
                    mNoteListAdapter.notifyDataSetChanged()
                    setActionModeTitle(mNoteListAdapter.getItemChecked(),
                            mNoteListAdapter.getItemChecked().toString() + mActivity.getString(R.string.alreadyselect))
                }
            }
            mMenuPopupWindow.dismiss()
        })
        //labButton.setVisibility(View.INVISIBLE)
        mNoteListActivityFragment.hideFloatingButton()
        return true
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mActivity.window.statusBarColor = -0x9e9e9f
        }
    }

    private fun recoveryStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mActivity.window.statusBarColor = 0xff3f51b5.toInt()
        }
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        NoteListActivity.mIsActionMode = false
        recoveryStatusBarColor()
        mNoteListActivityFragment.showFloatingButton()
        // mActionModeBackgroundHelper.setNormalColor();
        //
        // if(mListView != null){
        // mListView.clearChoices();
        // mAdapter.notifyDataSetInvalidated();
        // }
        //mActionModeBackgroundHelper.setNormalColor()
        mNoteListAdapter.clearChecked()
        mNoteListAdapter.notifyDataSetChanged()
        // mAdapter.endActionMode();
        // mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
    }

    internal fun setActionModeTitle(selectCount: Int, text: String) {
        if (selectCount == 0) {
            mMenuPopupWindow.setSelectedAllEnable(true)
            mMenuPopupWindow.setCancelSelectedAllEnable(false)
        } else if (selectCount == mNoteListAdapter.itemCount - 1) {
            mMenuPopupWindow.setSelectedAllEnable(false)
            mMenuPopupWindow.setCancelSelectedAllEnable(true)
        } else {
            mMenuPopupWindow.setSelectedAllEnable(true)
            mMenuPopupWindow.setCancelSelectedAllEnable(true)
        }
        mMenuPopupWindow.setActionModeTitle(text)
    }
}