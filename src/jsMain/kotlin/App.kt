import kotlinext.js.jsObject
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.html.js.onClickFunction
import react.Props
import react.dom.h1
import react.dom.li
import react.dom.ul
import react.fc
import react.useEffect
import react.useState

private val scope = MainScope()

val App = fc<Props> { _ ->
    var shoppingList by useState<List<ShoppingListItem>>(emptyList())

    useEffect {
        scope.launch {
            shoppingList = getShoppingList()
        }
    }

    h1 {
        +"Full-Stack Shopping List"
    }
    ul {
        shoppingList.sortedByDescending { it.priority }.forEach { item ->
            li {
                key = item.id.toString()
                +"[${item.priority}] ${item.desc}"
                attrs.onClickFunction = {
                    scope.launch {
                        deleteShoppingListItem(item)
                        shoppingList = getShoppingList()
                    }
                }
            }
        }
    }
    child(
        InputComponent,
        props = jsObject {
            onSubmit = { input ->
                val cartItem = ShoppingListItem(input.replace("!", ""), input.count { it == '!' })
                scope.launch {
                    addShoppingListItem(cartItem)
                    shoppingList = getShoppingList()
                }
            }
        }
    )
}