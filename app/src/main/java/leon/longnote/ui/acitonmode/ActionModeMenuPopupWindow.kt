package leon.longnote.ui.acitonmode

import leon.longnote.R
import android.app.Activity
import android.view.ActionMode
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView

/**
 * Created by liyl9 on 2018/4/8.
 */
class ActionModeMenuPopupWindow : View.OnClickListener {
    var mItemClickListener: View.OnClickListener? = null
    lateinit var mTitleView: TextView
    var mActivity: Activity
    var mActionMode: ActionMode
    var mPopupWindow: PopupWindow? = null
    var mLayout: View? = null
    var mSelectedAll: View? = null
    var mCancelSelectedAll: View? = null

    constructor(activity: Activity, mode: ActionMode) {
        mActivity = activity
        mActionMode = mode
        createActionModeTitle()
    }

    private fun createActionModeTitle() {
        mTitleView = mActivity.layoutInflater.inflate(
                R.layout.leac_action_mode_select_all_title,
                mActivity.window.decorView as ViewGroup, false) as TextView
        mTitleView.isClickable = true
        mTitleView.setOnClickListener { showSelectAllPopupWindow() }
        mTitleView.text = "select n item"

        val drawable = mActivity.resources.getDrawable(R.drawable.ic_arrow_drop_down_white_24dp)
        val width = drawable.intrinsicWidth
        val height = drawable.intrinsicHeight
        drawable.setBounds(0, 0, width, height)
        mTitleView.setCompoundDrawables(null, null, drawable, null)
        mActionMode.customView = mTitleView
    }

    private fun showSelectAllPopupWindow() {
        if (mLayout == null) {
            // 一个自定义的布局，作为显示的内容
            mLayout = mActivity.layoutInflater
                    .inflate(R.layout.leac_action_mode_select_all_item,
                            mActivity.window.decorView as ViewGroup,
                            false)
            mSelectedAll = mLayout!!.findViewById<View>(android.R.id.selectAll)
            mCancelSelectedAll = mLayout!!.findViewById<View>(android.R.id.empty)
            mSelectedAll!!.setOnClickListener(mItemClickListener)
            mCancelSelectedAll!!.setOnClickListener(mItemClickListener)
        }
        if (mPopupWindow == null) {
            mPopupWindow = PopupWindow(mActivity, null,
                    R.attr.actionOverflowMenuStyle)
        }
        mPopupWindow!!.contentView = mLayout
        mPopupWindow!!.width = ViewGroup.LayoutParams.WRAP_CONTENT
        mPopupWindow!!.height = ViewGroup.LayoutParams.WRAP_CONTENT
        mPopupWindow!!.isFocusable = true
        mPopupWindow!!.isTouchable = true

        mPopupWindow!!.setTouchInterceptor(View.OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_OUTSIDE) {
                v.performClick()
                mPopupWindow!!.dismiss()
                return@OnTouchListener true
            }
            false
            // 这里如果返回true的话，touch事件将被拦截
            // 拦截后 mPopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
        })

        // 设置好参数之后再show
        mPopupWindow!!.showAsDropDown(mTitleView)
    }

    fun setOnItemClickListener(itemClickListener: View.OnClickListener) {
        mItemClickListener = itemClickListener
    }

    fun setActionModeTitle(text: String) {
        mTitleView.text = text
    }

    fun dismiss() {
        mPopupWindow!!.dismiss()
    }

    fun setSelectedAllEnable(enable: Boolean) {
        if (mSelectedAll != null) {
            mSelectedAll!!.setEnabled(enable)
        }
    }

    fun setCancelSelectedAllEnable(enable: Boolean) {
        if (mCancelSelectedAll != null) {
            mCancelSelectedAll!!.setEnabled(enable)
        }
    }

    override fun onClick(arg0: View?) {
        if (mItemClickListener != null) {
            mItemClickListener!!.onClick(arg0)
        }
        when (arg0!!.getId()) {
            android.R.id.selectAll -> {
                mCancelSelectedAll!!.setEnabled(true)
                mSelectedAll!!.setEnabled(false)
            }
            android.R.id.empty -> {
                mCancelSelectedAll!!.setEnabled(false)
                mSelectedAll!!.setEnabled(true)
            }
        }
    }
}
