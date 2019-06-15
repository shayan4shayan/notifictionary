package ir.shahinsoft.notifictionary.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class History {
    fun toTranslate() = Translate().apply { name = word;this.translate = this@History.translate }

    @PrimaryKey
    var id = 0

    @ColumnInfo(name = "word")
    var word = ""
    @ColumnInfo(name = "translate")
    var translate = ""
}