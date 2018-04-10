package leon.longnote.utils

/**
 * Created by liyl9 on 2018/4/3.
 */
interface VoiceRecorderListener {
    fun onSuccess(fullPath: String)
    fun onFail()
}