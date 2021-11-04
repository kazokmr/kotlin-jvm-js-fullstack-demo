import kotlinx.browser.document
import kotlinx.browser.window
import react.dom.render

@ExperimentalJsExport
fun main() {
    window.onload = {
        render(document.getElementById("root")) {
            child(Welcome::class) {
                attrs {
                    name = "Kotlin/JS"
                }
            }
        }
    }
}
