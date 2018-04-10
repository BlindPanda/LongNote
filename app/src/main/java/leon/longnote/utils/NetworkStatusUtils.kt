package leon.longnote.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.text.TextUtils

/**
 * Created by liyl9 on 2018/4/4.
 */
object NetworkStatusUtils {
    @SuppressLint("WrongConstant")
    fun isConnectNet(mContext: Context): Boolean {
        val systemServiceName = if (TextUtils.isEmpty(Context.CONNECTIVITY_SERVICE)) "connectivity" else Context.CONNECTIVITY_SERVICE
        val connectivityManager = mContext.getSystemService(systemServiceName) as ConnectivityManager
        return if (connectivityManager != null && connectivityManager.activeNetworkInfo != null) {
            connectivityManager.activeNetworkInfo.isAvailable
        } else false
    }

    @SuppressLint("WrongConstant")
    fun isWifiConnected(context: Context): Boolean {
        val systemServiceName = if (TextUtils.isEmpty(Context.CONNECTIVITY_SERVICE)) "connectivity" else Context.CONNECTIVITY_SERVICE
        val connectivityManager = context.getSystemService(systemServiceName) as ConnectivityManager ?: return false

        val wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI) ?: return false
        return wifiInfo.isConnected
    }
}