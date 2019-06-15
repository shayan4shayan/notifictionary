package ir.shahinsoft.notifictionary

import android.graphics.PorterDuff
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import android.widget.ImageView

fun ImageView.tint(@ColorRes id:Int){
    setColorFilter(ContextCompat.getColor(context,id), PorterDuff.Mode.SRC_IN)
}