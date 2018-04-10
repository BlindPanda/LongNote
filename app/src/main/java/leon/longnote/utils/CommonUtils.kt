package leon.longnote.utils

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import leon.longnote.R

/**
 * Created by liyl9 on 2018/4/10.
 */
object CommonUtils {
    fun showSimpleDialog(context:Context, msgRes: Int, positiveListener: DialogInterface.OnClickListener, nagativeListener: DialogInterface.OnClickListener) {
        showDialog(context,msgRes, R.string.confirm, positiveListener, R.string.cancle, nagativeListener)
    }

    fun showDialog(context: Context, msgRes: Int, positiveButtonRes: Int, positiveListener: DialogInterface.OnClickListener, nagativeButtonRes: Int, nagativeListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(context).setMessage(msgRes).setPositiveButton(positiveButtonRes, positiveListener)
                .setNegativeButton(nagativeButtonRes, nagativeListener).show()
    }
}