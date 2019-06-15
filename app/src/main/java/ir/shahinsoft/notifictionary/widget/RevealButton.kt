package ir.shahinsoft.languagenotifier.widget

import android.animation.Animator
import android.content.Context
import android.os.Build
import android.os.Handler
import androidx.appcompat.widget.AppCompatButton
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils


/**
 * Created by shayan4shayan on 2/3/18.
 */
class RevealButton : AppCompatButton, View.OnClickListener, Animator.AnimatorListener {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, int: Int) : super(context, attributeSet, int)

    override fun onAnimationEnd(animation: Animator?) {
        view?.visibility = View.VISIBLE
        clickListener?.betweenAnimations(this)
        Handler().postDelayed({
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val cx = view?.width ?: 0 / 2
                val cy = view?.height ?: 0 / 2

                val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()
                val reveal = ViewAnimationUtils.createCircularReveal(view, cx, cy, finalRadius, 0f)
                //view?.visibility = View.GONE
                reveal.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {

                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        view?.visibility = View.GONE
                        Log.d(javaClass.simpleName, "after called")
                        clickListener?.afterAnimation(this@RevealButton)
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                    }
                })
                reveal.start()
            }
        }, 1000)
    }

    override fun onAnimationStart(animation: Animator?) {
    }

    override fun onAnimationRepeat(animation: Animator?) {
    }

    override fun onAnimationCancel(animation: Animator?) {
    }

    override fun onClick(v: View?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            clickListener?.postAnimation(this)
            val cx = view?.width ?: 0 / 2
            val cy = view?.height ?: 0 / 2

            val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()
            val reveal = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, finalRadius)
            view?.visibility = View.VISIBLE
            reveal.addListener(this)
            reveal.start()
        } else {
            clickListener?.afterAnimation(this)
        }
    }

    var clickListener: OnRevealListener? = null
    var view: View? = null

    public fun setClickListenerWithReveal(clickListener: OnRevealListener) {
        this.clickListener = clickListener
        setOnClickListener(this)
    }

    public fun setRevealView(view: View) {
        this.view = view
    }

    public interface OnRevealListener {
        fun postAnimation(v: RevealButton)
        fun afterAnimation(v: RevealButton)
        fun betweenAnimations(v: RevealButton)
    }

}