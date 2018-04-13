package leon.longnote.ui

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.*
import android.support.v4.app.FragmentActivity
import android.util.Log
import leon.longnote.R
import leon.longnote.utils.CommonUtils


/**
 * Created by liyl9 on 2018/4/12.
 */
class ActiveRegisterListener(private val activity: NoteListActivity) : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        var intentFilter = IntentFilter()
        intentFilter.addAction(NoteListActivityFragment.INTENT_SLIDE_DELETE_ITEM_ACTION)
        activity!!.registerReceiver(slideDeleteReceiver, intentFilter)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        activity!!.unregisterReceiver(slideDeleteReceiver)
    }

    private val slideDeleteReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("yanlonglong", "NoteListActivityFragment onResume onReceive")
            if (intent != null && intent.action == NoteListActivityFragment.INTENT_SLIDE_DELETE_ITEM_ACTION) {
                val msgId = R.string.isdeletenote
                val positiveListener = DialogInterface.OnClickListener { dialog, _ ->
                    if (intent.getIntExtra("delete_id", -1) != -1)
                        (activity.application as BasicApplication).getRepository().deleteNoteById(intent.getIntExtra("delete_id", -1))
                    dialog?.dismiss()
                }
                val nagativeButtonLisener = DialogInterface.OnClickListener { dialog, _ ->
                    dialog?.dismiss()
                }
                if (context != null) {
                    CommonUtils.showSimpleDialog(context, msgId, positiveListener, nagativeButtonLisener)
                }
            }
        }

    }
}