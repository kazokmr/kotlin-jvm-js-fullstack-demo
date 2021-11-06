import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onSubmitFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import react.Props
import react.dom.form
import react.dom.input
import react.fc
import react.useState

external interface InputProps : Props {
    var onSubmit: (String) -> Unit
}

val InputComponent = fc<InputProps> { props ->
    var text by useState<String>("")

    val submitHandler: (Event) -> Unit = {
        it.preventDefault()
        text = ""
        props.onSubmit(text)
    }

    val changeHandler: (Event) -> Unit = {
        val value = (it.target as HTMLInputElement).value
        text = value
    }

    form {
        attrs.onSubmitFunction = submitHandler
        input(InputType.text) {
            attrs.onChangeFunction = changeHandler
            attrs.value = text
        }
    }
}