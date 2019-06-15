package ir.shahinsoft.languagenotifier.model

data class License(val name: String, val url: String) {
    override fun toString(): String {

        return name
    }

    val isUrl get() = url.startsWith("http://") || url.startsWith("https://")

    val isEmail get() = url.contains("@")
}