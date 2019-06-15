package ir.shahinsoft.notifictionary.widget

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.ImageView
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.getColorCompat
import ir.shahinsoft.notifictionary.toast
import kotlinx.android.synthetic.main.dialog_color_picker.*

class ColorPickerDialog(context: Context, val listener: (Int) -> Unit) : AlertDialog(context) {

    val images = ArrayList<ImageView>()

    var selectedIndex = -1

    override fun show() {
        super.show()
        setContentView(R.layout.dialog_color_picker)
        initImages()
        images.forEach { it.setOnClickListener { image -> select(image) } }
        select.setOnClickListener { selectColor() }
    }

    private fun selectColor() {
        if (selectedIndex < 0) {
            context.toast(R.string.color_not_selected)
            return
        }
        val color = images[selectedIndex].tag as Int
        listener(color)
        dismiss()
    }

    private fun select(image: View?) {
        images.forEach { it.setImageDrawable(null); }
        if (image is ImageView) {
            image.setImageResource(R.drawable.ic_check_black_24dp)
            selectedIndex = images.indexOf(image)
        }
    }

    private fun initImages() {
        images.add(imageRed)
        imageRed.tag = context.getColorCompat(R.color.picker_red)
        images.add(imagePink)
        imagePink.tag = context.getColorCompat(R.color.picker_pink)
        images.add(imagePurple)
        imagePurple.tag = context.getColorCompat(R.color.picker_purple)
        images.add(imagePurpleDeep)
        imagePurpleDeep.tag = context.getColorCompat(R.color.picker_deep_purple)
        images.add(imageBlueGray)
        imageBlueGray.tag = context.getColorCompat(R.color.picker_blue_gray)
        images.add(imageBlue)
        imageBlue.tag = context.getColorCompat(R.color.picker_blue)
        images.add(imageBlueLight)
        imageBlueLight.tag = context.getColorCompat(R.color.picker_blue_light)
        images.add(imageCyan)
        imageCyan.tag = context.getColorCompat(R.color.picker_cyan)
        images.add(imageTeal)
        imageTeal.tag = context.getColorCompat(R.color.picker_teal)
        images.add(imageLime)
        imageLime.tag = context.getColorCompat(R.color.picker_lime)
        images.add(imageIndigo)
        imageIndigo.tag = context.getColorCompat(R.color.picker_indigo)
        images.add(imageGreen)
        imageGreen.tag = context.getColorCompat(R.color.picker_green)
        images.add(imageGreenLight)
        imageGreenLight.tag = context.getColorCompat(R.color.picker_green_light)
        images.add(imageYellow)
        imageYellow.tag = context.getColorCompat(R.color.picker_yellow)
        images.add(imageAmber)
        imageAmber.tag = context.getColorCompat(R.color.picker_amber)
        images.add(imageOrange)
        imageOrange.tag = context.getColorCompat(R.color.picker_orange)
        images.add(imageOrangeDeep)
        imageOrangeDeep.tag = context.getColorCompat(R.color.picker_orange_deep)
        images.add(imageBrown)
        imageBrown.tag = context.getColorCompat(R.color.picker_brown)
        images.add(imageGray)
        imageGray.tag = context.getColorCompat(R.color.picker_gray)
        images.add(imageBlack)
        imageBlack.tag = context.getColorCompat(R.color.picker_black)
    }

    interface ColorPickListener {
        fun onColorPicked(color: Int)
    }
}