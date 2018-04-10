package leon.longnote.utils

import android.app.AlertDialog
import android.content.Context
import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import leon.longnote.R
import java.io.File
import java.io.IOException

/**
 * Created by liyl9 on 2018/4/3.
 */
class LongRecoder {
    private lateinit var recorder:MediaRecorder
    private val path: String
    private var context: Context
    private val SAMPLE_RATE_IN_HZ = 8000
    lateinit var recorderDialog: AlertDialog
    lateinit var cycleview: ImageView
    var appExecutors: AppExcutors
    private lateinit var voiceRecorderListener: VoiceRecorderListener
    lateinit var uniqueVoiceName:String

    companion object {
        private val MAX_TIME = 15 // 最长录制时间，单位秒，0为无时间限制
        private val MIX_TIME = 1 // 最短录制时间，单位秒，0为无时间限制，建议设为1

        private val RECORD_NO = 0 // 不在录音
        private val RECORD_ING = 1 // 正在录音
        private val RECODE_ED = 2 // 完成录音
        @Volatile
        private var RECODE_STATE = 0 // 录音的状态
        @Volatile
        private var recodeTime = 0.0f // 录音的时间
        @Volatile
        private var voiceValue = 0.0 // 麦克风获取的音量值

        private var sInstance: LongRecoder? = null

        fun getInstance(context: Context?, appExcutors: AppExcutors): LongRecoder {
            if (sInstance == null) {
                synchronized(LongRecoder::class.java) {
                    if (sInstance == null) {
                        sInstance = LongRecoder(context, appExcutors)
                    }
                }
            }
            return sInstance!!
        }
    }

    constructor(context: Context?, appExcutors: AppExcutors) {
        uniqueVoiceName = System.currentTimeMillis().toString()
        this.path = sanitizePath(uniqueVoiceName)
        this.context = context!!
        this.appExecutors = appExcutors
    }

    private fun sanitizePath(path: String): String {
        var path = path
        if (!path.startsWith("/")) {
            path = "/" + path
        }
        if (!path.contains(".")) {
            path += ".amr"
        }
        return (Environment.getExternalStorageDirectory().absolutePath
                + "/LenovoCalendar/myvoice" + path)
    }

    fun start() {
        if (RECODE_STATE == RECORD_ING) {
            return
        }

        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            return
        }
        val directory = File(path).parentFile
        if (!directory.exists() && !directory.mkdirs()) {
            return
        }
        if(File(path).exists()){
            File(path).delete()//remove old one
        }

        //show ui
        showRecorderDialog()
        RECODE_STATE = RECORD_ING
        try {
            recorder = MediaRecorder()
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            recorder.setAudioSamplingRate(SAMPLE_RATE_IN_HZ)
            recorder.setOutputFile(path)
            recorder.prepare()
            recorder.start()
        } catch (e: IllegalStateException) {
            Log.e("yanlonglong","setAudioSource error 1")
            e.printStackTrace()
            RECODE_STATE = RECORD_NO
        } catch (e: IOException) {
            Log.e("yanlonglong","setAudioSource error 2")
            e.printStackTrace()
            RECODE_STATE = RECORD_NO
        } catch (e: RuntimeException) {
            Log.e("yanlonglong","setAudioSource error 3")
            e.printStackTrace()
            RECODE_STATE = RECORD_NO
        }
        if(RECODE_STATE== RECORD_ING) {//means normal
            runTimerThread()
        }

    }

    private fun runTimerThread() {
        appExecutors.networkIO().execute {
            recodeTime = 0.0f
            while (RECODE_STATE == RECORD_ING) {
                if (recodeTime >= MAX_TIME && MAX_TIME != 0) {
                    //handler.sendEmptyMessage(0x10)
                    //录音超过最长时间设定15s，自动终止
                    handlerMaxTime()
                } else {
                    try {
                        Thread.sleep(200)
                        recodeTime += 0.2f
                        if (RECODE_STATE == RECORD_ING) {
                            //handler.sendEmptyMessage(0x11)
                            handlerRecording()
                        }
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                }
            }
        }
    }

    private fun handlerRecording() {
        appExecutors.mainThread().execute {
            try{
                voiceValue = getAmplitude()
            }catch (ex:IllegalStateException){
                Log.e("yanlonglong","getAmplitude error")
            }

            if (voiceValue < 200.0) {
                cycleview.setImageResource(R.drawable.cyclebg)
            } else if (voiceValue > 200.0 && voiceValue < 600) {
                cycleview.setImageResource(R.drawable.cyclebg1)
            } else if (voiceValue > 600.0 && voiceValue < 1400) {
                cycleview.setImageResource(R.drawable.cyclebg2)
            } else if (voiceValue > 1400.0 && voiceValue < 3200) {
                cycleview.setImageResource(R.drawable.cyclebg3)
            } else if (voiceValue > 3200.0 && voiceValue < 5000.0) {
                cycleview.setImageResource(R.drawable.cyclebg4)
            } else if (voiceValue > 5000.0 && voiceValue < 7000.0) {
                cycleview.setImageResource(R.drawable.cyclebg5)
            } else if (voiceValue > 7000.0 && voiceValue < 9000.0) {
                cycleview.setImageResource(R.drawable.cyclebg6)
            } else if (voiceValue > 9000.0 && voiceValue < 11000.0) {
                cycleview.setImageResource(R.drawable.cyclebg7)
            } else if (voiceValue > 11000.0) {
                cycleview.setImageResource(R.drawable.cyclebg8)
            }
        }
    }

    private fun handlerMaxTime() {
        appExecutors.mainThread().execute {
            // 录音超过15秒自动停止,录音状态设为语音完成
            if (RECODE_STATE == RECORD_ING) {
                RECODE_STATE = RECODE_ED
                // 如果录音图标正在显示的话,关闭显示图标
                if (recorderDialog.isShowing()) {
                    recorderDialog.dismiss()
                }

                // 停止录音
                stop()
                voiceValue = 0.0
                voiceRecorderListener.onSuccess(path)
                // 如果录音时长小于1秒，显示录音失败的图标
                if (recodeTime < 1.0) {
                    // showWarnToast();
                    Toast.makeText(context,
                            context.getString(R.string.timelittle), Toast.LENGTH_SHORT).show()
                    // timeText.setText("");
                    // recordBt.setText("按住录音");
                    RECODE_STATE = RECORD_NO
                } else {
                    // recordBt.setText("按住录音");
                    // timeText.setText("录音时间:" + ((int)
                    // recodeTime));
                }
            }
        }
    }

    private fun stop() {
        try {
            Log.e("yanlonglong","normal stop")
            recorder.stop()
            recorder.reset()
            recorder.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun getAmplitude(): Double {
        return recorder?.maxAmplitude?.toDouble() ?: 0.0
    }

    private fun showRecorderDialog() {
        recorderDialog = AlertDialog.Builder(context).create()
        recorderDialog.show()
        recorderDialog.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        recorderDialog.window.setWindowAnimations(R.style.dialogWindowAnim)
        recorderDialog.setContentView(R.layout.talk_layout)
        cycleview = recorderDialog.findViewById<ImageView>(R.id.cycleview)
        recorderDialog.show()
    }

    fun safeStop() {
        Log.e("yanlonglong"," safeStop")
        // 如果是正在录音
        if (RECODE_STATE == RECORD_ING) {
            Log.e("yanlonglong"," safeStop1")
            RECODE_STATE = RECODE_ED
            // 如果录音图标正在显示,关闭
            if (recorderDialog.isShowing) {
                Log.e("yanlonglong"," safeStop3")
                recorderDialog.dismiss()
            }

            // 停止录音
            stop()
            voiceValue = 0.0

            if (recodeTime < MIX_TIME) {
                // showWarnToast();
                Toast.makeText(context,
                        context.getString(R.string.timelittle), Toast.LENGTH_SHORT).show()
                // recordBt.setText("按住录音");
                RECODE_STATE = RECORD_NO
            }else{
                voiceRecorderListener.onSuccess(path)
            }
        }else{
            Log.e("yanlonglong"," safeStop2")
        }
    }

    fun setVoiceRecorderListener(listener: VoiceRecorderListener) {
        this.voiceRecorderListener = listener
    }
}