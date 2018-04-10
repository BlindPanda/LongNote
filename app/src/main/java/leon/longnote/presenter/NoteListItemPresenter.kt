package leon.longnote.presenter

import android.content.Context
import android.content.Intent
import android.databinding.ObservableField
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.support.v4.content.ContextCompat
import android.util.Log
import leon.longnote.R
import leon.longnote.model.NoteItem
import leon.longnote.ui.NoteListActivityFragment.Companion.INTENT_SLIDE_DELETE_ITEM_ACTION
import java.io.File


/**
 * Created by liyl9 on 2018/3/23.
 */
class NoteListItemPresenter (private val mContext: Context){
    var playingVoicePath = ObservableField<String>("")
    var voiceStatusIcon = ObservableField<Drawable>(ContextCompat.getDrawable(mContext, R.drawable.voice_button_stop))
    var showOrHideImageLayout =ObservableField<Boolean>(false)

    val media by lazy {
        MediaPlayer()
    }
    fun getContentImageUrl(note: NoteItem): String {
        var result = ""
        if (!note.image1 .isEmpty()) {
            Log.d("longlong"," 1")
            result = createResult(note.image1)
        } else if (!note.image2 .isEmpty()) {
            Log.d("longlong"," 2")
            result = createResult(note.image2)
        } else if (!note.image3 .isEmpty()) {
            Log.d("longlong"," 3")
            result = createResult(note.image3)
        } else if (!note.image4 .isEmpty()) {
            Log.d("longlong"," 4")
            result = createResult(note.image4)
        }
        if (result.isEmpty()) {
            Log.d("longlong"," \"assets://\" + \"firstpic.jpg\"")
            showOrHideImageLayout.set(false)
            return ""
        } else {
            Log.d("longlong"," result:"+result)
            showOrHideImageLayout.set(true)
            return result
        }
    }

    private fun createResult(image: String): String {
        if (image.contains("firstpic.jpg"))
            return ("file:///android_asset/" + "firstpic.jpg")
        else
            return ("file://" + image)
    }
    fun getAudioDuration(note:NoteItem):String{
        var duration = 0
        if(!note.voicepath.isEmpty()){
            media.reset()
            Log.e("longlong"," getAudioDuration:"+note.voicepath)
            media.setDataSource(note.voicepath)
            media.prepare()
            duration = media.duration
        }
        return (duration/ 1000).toString() +"'"
    }
    fun onAudioClicked(note:NoteItem){
        try {
        if(media.isPlaying){
            media.stop()
            playingVoicePath.set("")
        }else{
            if(!note.voicepath.isEmpty()) {
                val file = File(note.voicepath)
                Log.e("yanlonglong"," "+note.voicepath+" exsits "+file.exists())
                media.reset()
                media.setDataSource(note.voicepath)
                media.prepare()
                media.start()
                playingVoicePath.set(note.voicepath)
            }
        }
        }catch (ex:IllegalArgumentException){
            ex.printStackTrace()
        }
    }

    fun setIsAudioPlaying(note:NoteItem){
        if(playingVoicePath.get()==""){
            voiceStatusIcon.set(ContextCompat.getDrawable(mContext,R.drawable.voice_button_stop))
        }else {
            if (note.voicepath.equals(playingVoicePath.get()) && media.isPlaying) {
                Log.e("yanlonglong"," setIsAudioPlaying note.isPlaying.set(true) ")
                voiceStatusIcon.set(ContextCompat.getDrawable(mContext, R.drawable.voice_button_play))
            }
        }
    }

    fun onDeleteClicked(note:NoteItem){
        //sendbroadcast inform listfragment to handle this event
        Log.d("yanlonglong","NoteListItemPresenter onDeleteClicked click hashcode:"+this.hashCode())
        var intent = Intent(INTENT_SLIDE_DELETE_ITEM_ACTION)
        intent.`package` = mContext.packageName
        intent.putExtra("delete_id",note.id)
        mContext.sendBroadcast(intent)
        }

}