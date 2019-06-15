package ir.shahinsoft.languagenotifier.model

import androidx.annotation.StringRes

class Help(@StringRes val title: Int,
           @StringRes val content: Int){
    var isSelected = false
}