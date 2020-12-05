package ir.shahinsoft.notifictionary.widget

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.ImageView
import ir.shahinsoft.notifictionary.R
import ir.shahinsoft.notifictionary.databinding.DialogColorPickerBinding
import ir.shahinsoft.notifictionary.getColorCompat
import ir.shahinsoft.notifictionary.toast

class ColorPickerDialog(context: Context, val listener: (Int) -> Unit) : AlertDialog(context) {

    val images = ArrayList<ImageView>()

    var selectedIndex = -1

    lateinit var binding : DialogColorPickerBinding

    override fun show() {
        super.show()
        binding = DialogColorPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initImages()
        images.forEach { it.setOnClickListener { image -> select(image) } }
        binding.select.setOnClickListener { selectColor() }
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
            selectedIndex = images.indexOf(image)
        }
    }

    private fun initImages() {
        images.add(binding.imageRed)
        binding.imageRed.tag = context.getColorCompat(R.color.picker_red)
        images.add(binding.imagePink)
        binding.imagePink.tag = context.getColorCompat(R.color.picker_pink)
        images.add(binding.imagePurple)
        binding.imagePurple.tag = context.getColorCompat(R.color.picker_purple)
        images.add(binding.imagePurpleDeep)
        binding.imagePurpleDeep.tag = context.getColorCompat(R.color.picker_deep_purple)
        images.add(binding.imageBlueGray)
        binding.imageBlueGray.tag = context.getColorCompat(R.color.picker_blue_gray)
        images.add(binding.imageBlue)
        binding.imageBlue.tag = context.getColorCompat(R.color.picker_blue)
        images.add(binding.imageBlueLight)
        binding.imageBlueLight.tag = context.getColorCompat(R.color.picker_blue_light)
        images.add(binding.imageCyan)
        binding.imageCyan.tag = context.getColorCompat(R.color.picker_cyan)
        images.add(binding.imageTeal)
        binding.imageTeal.tag = context.getColorCompat(R.color.picker_teal)
        images.add(binding.imageLime)
        binding.imageLime.tag = context.getColorCompat(R.color.picker_lime)
        images.add(binding.imageIndigo)
        binding.imageIndigo.tag = context.getColorCompat(R.color.picker_indigo)
        images.add(binding.imageGreen)
        binding.imageGreen.tag = context.getColorCompat(R.color.picker_green)
        images.add(binding.imageGreenLight)
        binding.imageGreenLight.tag = context.getColorCompat(R.color.picker_green_light)
        images.add(binding.imageYellow)
        binding.imageYellow.tag = context.getColorCompat(R.color.picker_yellow)
        images.add(binding.imageAmber)
        binding.imageAmber.tag = context.getColorCompat(R.color.picker_amber)
        images.add(binding.imageOrange)
        binding.imageOrange.tag = context.getColorCompat(R.color.picker_orange)
        images.add(binding.imageOrangeDeep)
        binding.imageOrangeDeep.tag = context.getColorCompat(R.color.picker_orange_deep)
        images.add(binding.imageBrown)
        binding.imageBrown.tag = context.getColorCompat(R.color.picker_brown)
        images.add(binding.imageGray)
        binding.imageGray.tag = context.getColorCompat(R.color.picker_gray)
        images.add(binding.imageBlack)
        binding.imageBlack.tag = context.getColorCompat(R.color.picker_black)
    }

    interface ColorPickListener {
        fun onColorPicked(color: Int)
    }
}