package ir.shahinsoft.notifictionary.model

data class ShopCategory(val id: Int, val name: String) {
    var picUrl: String = ""

    override fun toString(): String {
        return name
    }

    fun toUrl() = picUrl
}