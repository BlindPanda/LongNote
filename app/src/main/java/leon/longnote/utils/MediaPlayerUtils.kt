package leon.longnote.utils

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.io.FileInputStream

/**
 * Created by liyl9 on 2018/4/3.
 */
object MediaPlayerUtils {
    @Throws(Exception::class)
    fun setMediaPlayerDataSource(context: Context,
                                 mp: MediaPlayer, fileInfo: String) {
        var fileInfo = fileInfo

        if (fileInfo.startsWith("content://")) {
            try {
                val uri = Uri.parse(fileInfo)
                fileInfo = getRingtonePathFromContentUri(context, uri)
            } catch (e: Exception) {
            }

        }

        try {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB)
                try {
                    setMediaPlayerDataSourcePreHoneyComb(context, mp, fileInfo)
                } catch (e: Exception) {
                    setMediaPlayerDataSourcePostHoneyComb(context, mp, fileInfo)
                }
            else
                setMediaPlayerDataSourcePostHoneyComb(context, mp, fileInfo)

        } catch (e: Exception) {
            try {
                setMediaPlayerDataSourceUsingFileDescriptor(context, mp,
                        fileInfo)
            } catch (ee: Exception) {
                val uri = getRingtoneUriFromPath(context, fileInfo)
                mp.reset()
                mp.setDataSource(uri)
            }

        }

    }

    @Throws(Exception::class)
    private fun setMediaPlayerDataSourcePreHoneyComb(context: Context,
                                                     mp: MediaPlayer, fileInfo: String) {
        mp.reset()
        mp.setDataSource(fileInfo)
    }

    @Throws(Exception::class)
    private fun setMediaPlayerDataSourcePostHoneyComb(context: Context,
                                                      mp: MediaPlayer, fileInfo: String) {
        mp.reset()
        mp.setDataSource(context, Uri.parse(Uri.encode(fileInfo)))
    }

    @Throws(Exception::class)
    private fun setMediaPlayerDataSourceUsingFileDescriptor(
            context: Context, mp: MediaPlayer, fileInfo: String) {
        val file = File(fileInfo)
        val inputStream = FileInputStream(file)
        mp.reset()
        mp.setDataSource(inputStream.getFD())
        inputStream.close()
    }

    private fun getRingtoneUriFromPath(context: Context, path: String): String {
        val ringtonesUri = MediaStore.Audio.Media.getContentUriForPath(path)
        val ringtoneCursor = context.getContentResolver().query(
                ringtonesUri, null,
                MediaStore.Audio.Media.DATA + "='" + path + "'", null, null)
        ringtoneCursor.moveToFirst()

        val id = ringtoneCursor.getLong(ringtoneCursor
                .getColumnIndex(MediaStore.Audio.Media._ID))
        ringtoneCursor.close()

        return if (!ringtonesUri.toString().endsWith(id.toString())) {
            ringtonesUri.toString()+"/" + id
        } else ringtonesUri.toString()
    }

    fun getRingtonePathFromContentUri(context: Context,
                                      contentUri: Uri): String {
        val proj = arrayOf<String>(MediaStore.Audio.Media.DATA)
        val ringtoneCursor = context.getContentResolver().query(contentUri,
                proj, null, null, null)
        ringtoneCursor.moveToFirst()

        val path = ringtoneCursor.getString(ringtoneCursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))

        ringtoneCursor.close()
        return path
    }
}