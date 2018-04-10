package leon.longnote.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import android.support.v4.content.FileProvider
import java.io.File

/**
 * Created by liyl9 on 2018/4/2.
 */
object LongNoteFileProvider {
    fun getUriForFile(mContext: Context, file: File): Uri? {
        var fileUri: Uri? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri = getUriForFile24(mContext, file)
        } else {
            fileUri = Uri.fromFile(file)
        }
        return fileUri
    }

    private fun getUriForFile24(mContext: Context, file: File): Uri {
        return FileProvider.getUriForFile(mContext, "leon.longnote.fileprovider", file)
    }
}