package leon.longnote.utils

import android.content.Context
import android.os.Environment
import leon.longnote.R
import leon.longnote.convertor.DateConverter
import leon.longnote.db.NoteDb
import leon.longnote.model.NoteItem
import leon.longnote.model.Type
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * Created by liyl9 on 2018/3/20.
 */
class InitialDatabase {
    companion object {
        val saveDir = Environment.getExternalStorageDirectory()
                .path + "/LenovoCalendar/.NoteImage/"
        fun firstLaunch(context: Context): Boolean {
            return SharedPreference.getSharedPreference(context, "first_launch", true)
        }
    }
    lateinit var pathpic:String
    lateinit var pathvoice:String



    fun firstLaunchDataB(context: Context,db:NoteDb) {
        if (firstLaunch(context)) {
            //try {
                pathpic = assetsDataToSD(context,"firstpic.jpg")
                pathvoice = assetsDataToSD(context,"firstvoice.aac")
            /*} catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }*/
            val item1 = NoteItem(Type.X_TYPE.ordinal,context.getString(R.string.default2),"","","","", DateConverter.toDate(System.currentTimeMillis())!!,"",false)
            db.note_table().insert(item1)
            val item2 = NoteItem(Type.X_TYPE.ordinal,context.getString(R.string.default3),"","","","",DateConverter.toDate(System.currentTimeMillis())!!,"",false)
            db.note_table().insert(item2)
            val item3 = NoteItem(Type.Z_TYPE.ordinal,context.getString(R.string.default1),"",pathpic,"","",DateConverter.toDate(System.currentTimeMillis())!!,pathvoice,false)
            db.note_table().insert(item3)
            SharedPreference.setSharedPreference(context, "first_launch", false)
        }
    }

    @Throws(IOException::class)
    private fun assetsDataToSD(context: Context, fileName: String): String {
        val savePath = File(saveDir)
        if (!savePath.exists()) {
            savePath.mkdirs()
        }
        val PhotoFile = File(saveDir, fileName)
        PhotoFile.delete()
        if (!PhotoFile.exists()) {
            try {
                PhotoFile.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        val myInput: InputStream
        val myOutput = FileOutputStream(PhotoFile)
        myInput = context.getAssets().open(fileName)
        val buffer = ByteArray(1024)
        var length = myInput.read(buffer)
        while (length > 0) {
            myOutput.write(buffer, 0, length)
            length = myInput.read(buffer)
        }

        myOutput.flush()
        myInput.close()
        myOutput.close()
        return PhotoFile.getAbsolutePath()
    }
}