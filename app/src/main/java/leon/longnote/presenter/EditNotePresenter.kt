package leon.longnote.presenter

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.ObservableField
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import leon.longnote.R
import leon.longnote.R.id.*
import leon.longnote.model.NoteItem
import leon.longnote.model.ResourceType
import leon.longnote.model.Type
import leon.longnote.ui.BasicApplication
import leon.longnote.ui.EditNoteFragment
import leon.longnote.ui.NoteListActivity
import leon.longnote.utils.*
import leon.longnote.utils.CommonUtils.showSimpleDialog
import leon.longnote.utils.ImageUtils.compressionBigBitmap
import leon.longnote.utils.ImageUtils.convertViewToBitmap
import leon.longnote.utils.ImageUtils.getRealFilePath
import leon.longnote.utils.ImageUtils.rotaingImageView
import leon.longnote.viewmodel.NoteItemViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


/**
 * Created by liyl9 on 2018/3/28.
 */
class EditNotePresenter(private val mEditNoteFragment: EditNoteFragment, private val windowManager: WindowManager, private var noteItemViewModel: NoteItemViewModel) {
    companion object {
        val REQUEST_CAMERA_PERMISSIONS = 2001
        val REQUEST_SELECT_PIC_PERMISSIONS = 2002
        val REQUEST_AUDIO_PERMISSIONS = 2003
        val REQUEST_CODE_CHOOSE = 101
        val CAMERA_RESULT = 100

    }

    lateinit var mPhotoFile: File
    lateinit var recorder: LongRecoder
    var media1: MediaPlayer
    var mProgressDialog:ProgressDialog?=null

    var voicePath = ObservableField<String>("")
    var imagepath1 = ObservableField<String>("")
    var imagepath2 = ObservableField<String>("")
    var imagepath3 = ObservableField<String>("")
    var imagepath4 = ObservableField<String>("")
    var textContent = ObservableField<String>("")
    var voiceDuration = ObservableField<String>("")


    var playingVoicePath1 = ObservableField<String>("")
    var voiceStatusIcon1 = ObservableField<Drawable>(ContextCompat.getDrawable(mEditNoteFragment.context, R.drawable.play_normal))
    var showOrHideVoiceLayout = ObservableField<Boolean>(false)
    var showOrHideImage1 = ObservableField<Boolean>(false)
    var showOrHideImage2 = ObservableField<Boolean>(false)
    var showOrHideImage3 = ObservableField<Boolean>(false)
    var showOrHideImage4 = ObservableField<Boolean>(false)
    var image1Byte = ObservableField<ByteArray>(null)
    var image2Byte = ObservableField<ByteArray>(null)
    var image3Byte = ObservableField<ByteArray>(null)
    var image4Byte = ObservableField<ByteArray>(null)
    var note = noteItemViewModel.item

    var showOrHideShareLabel = ObservableField<Boolean>(false)
    var showOrHideEditTextCursor = ObservableField<Boolean>(true)

    init {
        media1 = MediaPlayer()
        doInit(note.get())
    }

    fun doInit(noteEntitie: NoteItem?) {
        if (noteEntitie != null) {
            this.note.set(noteEntitie)
            voicePath.set(noteEntitie.voicepath)
            textContent.set(noteEntitie.textx)
            imagepath1.set(noteEntitie.image1)
            imagepath2.set(noteEntitie.image2)
            imagepath3.set(noteEntitie.image3)
            imagepath4.set(noteEntitie.image4)
            voiceDuration.set(getAudioDuration())
        }
        showOrHideVoiceLayout.set(if (note.get() == null) false else !note.get().voicepath.isNullOrEmpty())
        showOrHideImage1.set(if (note.get() == null) false else !note.get().image1.isNullOrEmpty())
        showOrHideImage2.set(if (note.get() == null) false else !note.get().image2.isNullOrEmpty())
        showOrHideImage3.set(if (note.get() == null) false else !note.get().image3.isNullOrEmpty())
        showOrHideImage4.set(if (note.get() == null) false else !note.get().image4.isNullOrEmpty())
        image1Byte.set(getBitmapBytes(if (note.get() == null) null else note.get().image1))
        image2Byte.set(getBitmapBytes(if (note.get() == null) null else note.get().image2))
        image3Byte.set(getBitmapBytes(if (note.get() == null) null else note.get().image3))
        image4Byte.set(getBitmapBytes(if (note.get() == null) null else note.get().image4))

    }


    fun getBitmapBytes(path: String?): ByteArray? {
        if (path == null) {
            return null
        } else {
            if (path.isEmpty()) {
                return null
            }
            var bitmap = BitmapFactory.decodeFile(path)
            if (bitmap == null) {
                Log.d("yanlonglong", "1bitmap:null:" + path)
            }
            bitmap = ImageUtils.compressionBigBitmap(windowManager, bitmap, false)
            if (bitmap == null) {
                Log.d("yanlonglong", "2bitmap:null:" + path)
            }
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            return baos.toByteArray()
        }
    }

    fun getBitmapBytes(bitmap: Bitmap): ByteArray? {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        return baos.toByteArray()
    }

    fun getAudioDuration(): String {
        Log.e("yanlonglong","getAudioDuration")
        var duration = 0
        if (voicePath != null && !voicePath.get().isEmpty()) {
            media1 = MediaPlayer()
            media1.setDataSource(voicePath.get())
            media1.prepare()
            duration = media1.duration
            media1.stop()
        }
        return (duration / 1000).toString() + "'"
    }

    fun onAudioClicked() {
        Log.e("yanlonglong", "EditNotePresenter onAudioClicked")
        try {
            if (media1.isPlaying) {
                media1.stop()
                playingVoicePath1.set("")
                Log.e("yanlonglong", "EditNotePresenter onAudioClicked stop")
            } else {
                if (!voicePath.get().isEmpty()) {
                    Log.e("yanlonglong", "EditNotePresenter onAudioClicked paly")
                    media1 = MediaPlayer()
                    media1.setOnCompletionListener(object:MediaPlayer.OnCompletionListener{
                        override fun onCompletion(mp: MediaPlayer?) {
                            playingVoicePath1.set("")
                        }

                    })
                    media1.setDataSource(voicePath.get())
                    media1.prepare()
                    media1.start()
                    playingVoicePath1.set(voicePath.get())

                }
            }
            setIsAudioPlaying()//update icons
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }
    }

    fun onAudioLongClicked(): Boolean {
        Log.e("yanlonglong", "EditNotePresenter onAudioLongClicked paly")
        try {
            if (media1.isPlaying) {
                media1.stop()
                media1.release()
                playingVoicePath1.set("")
            }
            setIsAudioPlaying()//update icons
            //delete dialog
            showDeleteDialog(ResourceType.AUDIO.ordinal)
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }
        return true
    }


    fun onImageLongClicked(view: View): Boolean {
        // Log.e("yanlonglong", "note.get()textx " + note.get().textx)
        //delete dialog
        val typeOrdinal = when (view.id) {
            ll_img1 -> ResourceType.IMAGE1.ordinal
            ll_img2 -> ResourceType.IMAGE2.ordinal
            ll_img3 -> ResourceType.IMAGE3.ordinal
            ll_img4 -> ResourceType.IMAGE4.ordinal
            else -> -1
        }
        if (typeOrdinal != -1)
            showDeleteDialog(typeOrdinal)
        return true
    }


    private fun showDeleteDialog(audio: Int) {
        val msgResId = if (ResourceType.AUDIO.ordinal == audio) R.string.isdeletevoice else {
            if (ResourceType.SELF.ordinal == audio) R.string.isdeletenote else R.string.delpic
        }
        val positiveClickListener = DialogInterface.OnClickListener { dialog, which ->
            when (audio) {
                ResourceType.AUDIO.ordinal -> {
                    voicePath.set("")
                    showOrHideVoiceLayout.set(!voicePath.get().isNullOrEmpty())
                }
                ResourceType.IMAGE1.ordinal -> {
                    imagepath1.set("")
                    image1Byte.set(null)
                    showOrHideImage1.set(!imagepath1.get().isNullOrEmpty())
                }
                ResourceType.IMAGE2.ordinal -> {
                    imagepath2.set("")
                    image2Byte.set(null)
                    Log.e("yanlonglong", " " + imagepath2.get().isNullOrEmpty())
                    showOrHideImage2.set(!imagepath2.get().isNullOrEmpty())
                }
                ResourceType.IMAGE3.ordinal -> {
                    imagepath3.set("")
                    image3Byte.set(null)
                    showOrHideImage3.set(!imagepath3.get().isNullOrEmpty())
                }
                ResourceType.IMAGE4.ordinal -> {
                    imagepath4.set("")
                    image4Byte.set(null)
                    showOrHideImage4.set(!imagepath4.get().isNullOrEmpty())
                }
                ResourceType.SELF.ordinal -> {
                    //退出当前的fragment&删除本条note
                    noteItemViewModel.deleteNoteById(note.get().id!!)
                    (mEditNoteFragment.activity as NoteListActivity).backToListFragment()
                }
            }
        }
        val nagativeClickListener = DialogInterface.OnClickListener { dialog, which ->
            if (dialog != null) {
                dialog.dismiss()
            }
        }
        showSimpleDialog(mEditNoteFragment.context,msgResId, positiveClickListener, nagativeClickListener)
    }

    fun setIsAudioPlaying() {
        if (playingVoicePath1.get() == "") {
            voiceStatusIcon1.set(ContextCompat.getDrawable(mEditNoteFragment.context, R.drawable.play_normal))
        } else {
            if (voicePath.get().equals(playingVoicePath1.get()) && media1.isPlaying) {
                Log.e("yanlonglong", " setIsAudioPlaying note.isPlaying.set(true) ")
                voiceStatusIcon1.set(ContextCompat.getDrawable(mEditNoteFragment.context, R.drawable.stop_normal))
            }
        }
    }

    fun onDeleteClick() {
        Log.e("yanlonglong", "EditNotePresenter onDeleteClicked")
        showDeleteDialog(ResourceType.SELF.ordinal)
    }

    fun onSaveClicked() {
        if (noteItemViewModel.isNew.get()) {
            updateItemDate()
            noteItemViewModel.insertNoteItem(note.get())
        } else {
            if (hasChanged()) {
                updateItemDate()
                noteItemViewModel.updateNoteById(note.get())
            }
        }
        (mEditNoteFragment.activity as NoteListActivity).backToListFragment()
    }

    private fun updateItemDate() {
        if (note.get() == null) {
            note.set(NoteItem())
        }
        if (note.get().voicepath != voicePath.get()) {
            note.get().voicepath = voicePath.get()
        }
        if (note.get().image1 != imagepath1.get()) {
            note.get().image1 = imagepath1.get()
        }
        if (note.get().image2 != imagepath2.get()) {
            note.get().image2 = imagepath2.get()
        }
        if (note.get().image3 != imagepath3.get()) {
            note.get().image3 = imagepath3.get()
        }
        if (note.get().image4 != imagepath4.get()) {
            note.get().image4 = imagepath4.get()
        }
        if (note.get().textx != textContent.get()) {
            note.get().textx = textContent.get()
        }
        checkAndSetType()
    }

    private fun checkAndSetType() {
        if (note.get().voicepath.isNullOrEmpty() && note.get().image1.isNullOrEmpty() && note.get().image2.isNullOrEmpty() && note.get().image3.isNullOrEmpty()
                && note.get().image4.isNullOrEmpty()) {
            note.get().type = Type.X_TYPE.ordinal
        } else {
            note.get().type = Type.Z_TYPE.ordinal
        }
    }

    fun onCancelClicked() {
        if (noteItemViewModel.isNew.get() || hasChanged()) {
            val msgResId = R.string.nosave
            val positiveClickListener = object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    (mEditNoteFragment.activity as NoteListActivity).backToListFragment()
                }
            }
            val nagativeClickListener = object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    if (dialog != null) {
                        dialog.dismiss()
                    }
                }
            }
            showSimpleDialog(mEditNoteFragment.context,msgResId, positiveClickListener, nagativeClickListener)
        }else{
            //not new  or no modify just quit
            (mEditNoteFragment.activity as NoteListActivity).backToListFragment()
        }
    }

    fun hasChanged(): Boolean {
        if (note.get() == null) return false
        Log.e("yanlonglong", "hasChanged updatetext before:" + textContent.get())
        Log.e("yanlonglong", "hasChanged updatetextafter:" + note.get().textx)
        return (note.get().voicepath != voicePath.get() || note.get().image1 != imagepath1.get()
                || note.get().image2 != imagepath2.get() || note.get().image3 != imagepath3.get()
                || note.get().image4 != imagepath4.get() || note.get().textx != textContent.get())
    }

    fun onStartVoiceRecordeClick() {
        val permissionsNeeded = ArrayList<String>()
        if (!addPermission(android.Manifest.permission.RECORD_AUDIO))
            permissionsNeeded.add(android.Manifest.permission.RECORD_AUDIO)
        if (permissionsNeeded.size > 0) {
            requestPermissions(
                    mEditNoteFragment.activity!!,
                    permissionsNeeded.toTypedArray(),
                    REQUEST_AUDIO_PERMISSIONS)
        } else {
            startRecord()
        }
    }

    private fun startRecord() {
        Log.e("yanlonglong","presenter startRecord")
        recorder = LongRecoder.getInstance(mEditNoteFragment.context,(mEditNoteFragment.activity.application as BasicApplication).getAppExecutors())
        recorder.setVoiceRecorderListener(object :VoiceRecorderListener{
            override fun onSuccess(fullPath: String) {
                Log.e("yanlonglong","presenter onSuccess:"+fullPath)
                val oldFilePath = voicePath.get()
                //update voice layout
                voicePath.set(fullPath)
                voiceDuration.set(getAudioDuration())
                showOrHideVoiceLayout.set(true)
                /*if(!oldFilePath.isNullOrEmpty()){
                    var oldVoiceFile = File(oldFilePath)
                    oldVoiceFile.delete()
                }*/
            }

            override fun onFail() {
                //do nothing
            }

        })
        recorder.start()
    }


    fun onStopVoiceRecordeClick() {
        Log.e("yanlonglong","presenter onStopVoiceRecordeClick")
        if (recorder != null) {
            recorder.safeStop()
        }

    }

    fun onPictureSelectClick() {
        val permissionsNeeded = ArrayList<String>()
        if (!addPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permissionsNeeded.size > 0) {
            requestPermissions(
                    mEditNoteFragment.activity!!,
                    permissionsNeeded.toTypedArray(),
                    REQUEST_SELECT_PIC_PERMISSIONS)
        } else {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(mEditNoteFragment.activity!!, intent, REQUEST_CODE_CHOOSE, null)
        }
    }

    fun onCameraClick() {
        val savePath = File(InitialDatabase.saveDir)
        if (!savePath.exists()) {
            savePath.mkdirs()
        }
        val permissionsNeeded = ArrayList<String>()
        if (!addPermission(android.Manifest.permission.CAMERA))
            permissionsNeeded.add(android.Manifest.permission.CAMERA)
        if (!addPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permissionsNeeded.size > 0) {
            requestPermissions(
                    mEditNoteFragment.activity!!,
                    permissionsNeeded.toTypedArray(),
                    REQUEST_CAMERA_PERMISSIONS)
        } else {
            cameraPhoto()
        }

    }

    /**
     * 调用相机拍照
     */
    private fun cameraPhoto() {
        val state = Environment.getExternalStorageState()
        if (state == Environment.MEDIA_MOUNTED) {
            val picname = System.currentTimeMillis().toString() + ""
            mPhotoFile = File(InitialDatabase.saveDir, picname + ".jpg")
            mPhotoFile.delete()
            if (!mPhotoFile.exists()) {
                try {
                    mPhotoFile.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                    return
                }

            }
            val intent = Intent("android.media.action.IMAGE_CAPTURE")
            val uri = LongNoteFileProvider.getUriForFile(mEditNoteFragment.context!!, mPhotoFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            if (Build.VERSION.SDK_INT >= 26) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivityForResult(mEditNoteFragment.activity!!, intent, CAMERA_RESULT, null)
        }

    }

    private fun addPermission(permission: String): Boolean {
        var result = false
        if (ContextCompat.checkSelfPermission(mEditNoteFragment.context!!, permission) == PackageManager.PERMISSION_GRANTED) {
            result = true
        }
        return result
    }

    fun getCameraTakedPhoto(): File {
        return mPhotoFile
    }

    fun handleCameraTakendPhoto() {
        Log.e("yanlonglong", "handleCameraTakendPhoto")
        val bitmapOptions = BitmapFactory.Options()
        bitmapOptions.inSampleSize = 3
        val degree = ImageUtils.readPictureDegree(mPhotoFile.absolutePath)
        var bitmapcamera = BitmapFactory.decodeFile(mPhotoFile.path,
                bitmapOptions)
        bitmapcamera = rotaingImageView(degree, bitmapcamera)

        bitmapcamera = compressionBigBitmap(windowManager, bitmapcamera, false)

        val index = getImageShowIndex()

        showImage(bitmapcamera, index, mPhotoFile.path)
        Log.e("yanlonglong", "index:" + index)

    }

    fun showImage(bitmapcamera: Bitmap, index: Int, path: String) {
        if (index != -1) {
            when (index) {
                1 -> {
                    imagepath1.set(path)
                    Log.e("yanlonglong", "setddddd:")
                    image1Byte.set(getBitmapBytes(bitmapcamera))
                    showOrHideImage1.set(true)
                }
                2 -> {
                    imagepath2.set(path)
                    image2Byte.set(getBitmapBytes(bitmapcamera))
                    showOrHideImage2.set(true)
                }
                3 -> {
                    imagepath3.set(path)
                    image3Byte.set(getBitmapBytes(bitmapcamera))
                    showOrHideImage3.set(true)
                }
                4 -> {
                    imagepath4.set(path)
                    image4Byte.set(getBitmapBytes(bitmapcamera))
                    showOrHideImage4.set(true)
                }
            }
        }
    }

    private fun getImageShowIndex(): Int {
        if (imagepath1.get().isNullOrEmpty()) {
            image1Byte.set(null)//clean
            return 1
        }
        if (imagepath2.get().isNullOrEmpty()) {
            image2Byte.set(null)//clean
            return 2
        }
        if (imagepath3.get().isNullOrEmpty()) {
            image3Byte.set(null)//clean
            return 3
        }
        if (imagepath4.get().isNullOrEmpty()) {
            image4Byte.set(null)//clean
            return 4
        }
        Toast.makeText(mEditNoteFragment.context, R.string.atleast5, Toast.LENGTH_SHORT).show()
        return -1
    }

    fun handleGalleryChoosePhoto(data: Intent?) {
        if (data != null) {
            // 得到图片的全路径
            val uri = data.getData()
            val bitmapOptions = BitmapFactory.Options()
            bitmapOptions.inSampleSize = 3
            Log.e("yanlonglong", "uri:" + uri + " path:" + uri.path)
            val path = getRealFilePath(mEditNoteFragment.context!!, uri)
            var bitmapcamera = BitmapFactory.decodeFile(path,
                    bitmapOptions)
            // var bitmapcamera = MediaStore.Images.Media.getBitmap(mEditNoteFragment.context.contentResolver,uri)

            bitmapcamera = compressionBigBitmap(windowManager, bitmapcamera, false)
            val index = getImageShowIndex()
            if (path != null) {
                showImage(bitmapcamera, index, path)
            }
        }
    }

    fun onDestory() {
        if((media1!!)!=null&&media1!!.isPlaying){
            media1.stop()
            media1.release()
        }
        if(mProgressDialog!=null){
            mProgressDialog!!.dismiss()
            mProgressDialog=null
        }
    }

    fun onBackPressed() {
        onCancelClicked()//do cancel thing
    }
    fun onShareClick(){
        if(NetworkStatusUtils.isConnectNet(mEditNoteFragment.context)){
            val oldValue1 = showOrHideEditTextCursor.get()
            val oldValue2 = showOrHideVoiceLayout.get()
            val oldValue3 = showOrHideShareLabel.get()
            showOrHideEditTextCursor.set(false)
            showOrHideVoiceLayout.set(false)
            showOrHideShareLabel.set(true)
            mEditNoteFragment.view!!.invalidate()
            mProgressDialog = ProgressDialog(mEditNoteFragment.context)
            mProgressDialog!!.setMessage(mEditNoteFragment.context.getString(R.string.festival_share_task))
            mProgressDialog!!.show()
            (mEditNoteFragment.activity.application as BasicApplication).getAppExecutors().networkIO().execute {
                createSharePicture()
                (mEditNoteFragment.activity.application as BasicApplication).getAppExecutors().mainThread().execute{
                    showOrHideEditTextCursor.set(oldValue1)
                    showOrHideVoiceLayout.set(oldValue2)
                    showOrHideShareLabel.set(oldValue3)
                    mProgressDialog!!.dismiss()
                }
            }
        }else{
            Toast.makeText(mEditNoteFragment.context, mEditNoteFragment.context.getString(R.string.share_wrong), Toast.LENGTH_SHORT).show()
        }
    }

    private fun createSharePicture() {
        val bgbitmap = convertViewToBitmap(mEditNoteFragment.view!!.findViewById(scrolllayout)!!)

        var fos: FileOutputStream? = null
        var mfile:File?=null
        try {
            val savePath = File(InitialDatabase.saveDir)
            if (!savePath.exists()) {
                savePath.mkdirs()
            }
            val pathshare = InitialDatabase.saveDir + "viewshot.png"
            mfile = File(pathshare)

            fos = FileOutputStream(mfile)
            if (fos != null) {
                bgbitmap.compress(Bitmap.CompressFormat.PNG, 75, fos)
                fos.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        if (mfile != null && mfile.exists() && mfile.isFile()) {
            ImageUtils.shareMsg(mEditNoteFragment.context,mfile.absolutePath)
        }
    }
}
