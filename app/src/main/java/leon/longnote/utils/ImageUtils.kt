package leon.longnote.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import java.io.IOException
import android.content.Intent
import android.os.Build
import java.io.File


/**
 * Created by liyl9 on 2018/3/28.
 */
object ImageUtils {
    private var i = 800f

    /**
     * @param bitmap
     * @return 压缩后的bitmap
     */
    fun compressionBigBitmap(windowManager: WindowManager,bitmap: Bitmap?, isSysUp: Boolean): Bitmap? {
        var destBitmap: Bitmap? = null
        /* 图片宽度调整为100，大于这个比例的，按一定比例缩放到宽度为100 */
        if (bitmap != null) {
            if (bitmap.width > 80) {
                if (jugerWindow(windowManager) >= 1080) {
                    i = 1000f
                } else {
                    i = 700f
                }
                val scaleValue = (i / bitmap.width).toFloat()

                val matrix = Matrix()
                /* 针对系统拍照，旋转90° */
                if (isSysUp)
                    matrix.setRotate(90f)
                matrix.postScale(scaleValue, scaleValue)

                destBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width,
                        bitmap.height, matrix, true)
                val widthTemp = destBitmap!!.width
                val heightTemp = destBitmap.height
            } else {
                return destBitmap
            }
        }

        return destBitmap

    }

    fun jugerWindow(windowManager: WindowManager): Int {
        val mDisplayMetrics = DisplayMetrics()
        windowManager.getDefaultDisplay().getMetrics(mDisplayMetrics)
        val W = mDisplayMetrics.widthPixels
        val H = mDisplayMetrics.heightPixels
        Log.i("Main", "Width = " + W)
        Log.i("Main", "Height = " + H)
        return W
    }

    fun readPictureDegree(path: String): Int {
        var degree = 0
        try {
            val exifInterface = ExifInterface(path)
            val orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return degree
    }

    fun rotaingImageView(angle: Int, bitmap: Bitmap): Bitmap {
        // 旋转图片 动作
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        println("angle2=" + angle)
        // 创建新的图片
        return Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.width, bitmap.height, matrix, true)

    }

    fun getRealFilePath(context: Context, uri: Uri?): String? {
        if (null == uri) return null
        val scheme = uri!!.getScheme()
        var data: String? = null
        if (scheme == null)
            data = uri!!.getPath()
        else if (ContentResolver.SCHEME_FILE == scheme) {
            data = uri!!.getPath()
        } else if (ContentResolver.SCHEME_CONTENT == scheme) {
            val cursor = context.getContentResolver().query(uri, arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
            if (null != cursor) {
                if (cursor!!.moveToFirst()) {
                    val index = cursor!!.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    if (index > -1) {
                        data = cursor!!.getString(index)
                    }
                }
                cursor!!.close()
            }
        }
        return data
    }

    fun convertViewToBitmap(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height,
                Bitmap.Config.ARGB_8888)
        //利用bitmap生成画布
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        //把view中的内容绘制在画布上
        view.draw(canvas)

        return bitmap
    }

    fun shareMsg(context: Context, imgPath: String?) {
        val intent = Intent(Intent.ACTION_SEND)
        if (imgPath == null || imgPath == "") {
            intent.type = "text/plain" // 纯文本
        } else {
            val f = File(imgPath)
            if (f != null && f.exists() && f.isFile()) {
                intent.type = "image/png"
                val uri = LongNoteFileProvider.getUriForFile(context, f)
                intent.putExtra(Intent.EXTRA_STREAM, uri)
            }
        }
        if (Build.VERSION.SDK_INT >= 26) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(Intent.createChooser(intent, ""))
    }
}