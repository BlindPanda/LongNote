package leon.longnote.utils

/**
 * Created by liyl9 on 2018/3/23.
 */
import android.databinding.BindingAdapter
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import com.bumptech.glide.Glide
import android.view.MotionEvent
import android.widget.EditText
import leon.longnote.presenter.EditNotePresenter


/**
 * 使用ImageLoader显示图片
 * @param imageView
 * @param url
 */
@BindingAdapter("imageUrl")
fun ImageView.setImageUrl(url: String?) {
    Log.d("longlong","url:"+url)
    Glide.with(context).load(url).into(this)
}
/**
 * 使用ImageLoader显示图片
 * @param imageView
 * @param url
 */
@BindingAdapter("imageBitmap")
fun ImageView.setImageBitmap(bytes: ByteArray?) {
    if(bytes!=null) {
        Glide.with(context).load(bytes).asBitmap().into(this)
    }
}


@BindingAdapter("visibleOrGone")
fun View.setVisibleOrGone(show: Boolean) {
    visibility = if (show) VISIBLE else GONE
}
@BindingAdapter("cursorVisibleOrGone")
fun EditText.setCursorVisibleOrGone(show: Boolean) {
    isCursorVisible = show
}

@BindingAdapter("touchListener")
fun setTouchListener(self: View, presenter: EditNotePresenter) {
    self.setOnTouchListener { view, event ->
        // Check if the button is PRESSED
        if (event.action == MotionEvent.ACTION_DOWN) {
            //do some thing
            presenter.onStartVoiceRecordeClick()
        }// Check if the button is RELEASED
        else if (event.action == MotionEvent.ACTION_UP) {
            //do some thing
            presenter.onStopVoiceRecordeClick()
        }
        false
    }
}