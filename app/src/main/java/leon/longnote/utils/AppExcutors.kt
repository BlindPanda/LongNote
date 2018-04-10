package leon.longnote.utils

import android.os.Looper
import android.util.Log
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Created by liyl9 on 2018/3/19.
 */
class AppExcutors(private var mDiskIO: Executor, private var mNetworkIO: Executor, private var mMainThread: Executor) {
    companion object {
        val TAG = "AppExcutors"
    }


    constructor() : this(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(3), MainThreadExecutor()) {
        Log.d(TAG, "constructor")
    }

    class MainThreadExecutor : Executor {
        private val mMainThreadHander = android.os.Handler(Looper.getMainLooper())
        override fun execute(command: Runnable?) {
            mMainThreadHander.post(command)
        }
    }

    fun diskIO(): Executor {
        return mDiskIO
    }

    fun networkIO(): Executor {
        return mNetworkIO
    }

    fun mainThread(): Executor {
        return mMainThread
    }
}
