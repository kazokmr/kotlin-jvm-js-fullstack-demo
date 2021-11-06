import com.mongodb.ConnectionString
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo

// HostとPortを環境変数から取る。無ければデフォルトを設定する
val port = System.getenv("PORT")?.toInt() ?: 9090
val host = System.getenv("HOST")?.toString() ?: "127.0.0.1"

// MongoDBが外部サーバーの場合に環境変数から接続記述子を取得する。設定する際はretryWrites=falseにして接続エラー時に再接続しないようにする（理由は不明。ただの例かもしれない）
val connectionString: ConnectionString? = System.getenv("MONGODB_URI")?.let {
    ConnectionString("$it?retryWrites=false")
}

// 接続記述子が未設定なら標準接続とする
val client =
    if (connectionString != null) {
        KMongo.createClient(connectionString).coroutine
    } else {
        KMongo.createClient().coroutine
    }
val database = client.getDatabase(connectionString?.database ?: "shoppingList")
val collection = database.getCollection<ShoppingListItem>()

fun main() {
    embeddedServer(Netty, port = port, host = host) {
        install(ContentNegotiation) {
            json()
        }
        install(CORS) {
            method(HttpMethod.Get)
            method(HttpMethod.Post)
            method(HttpMethod.Delete)
            anyHost()
        }
        install(Compression) {
            gzip()
        }

        routing {
            get("/hello") {
                call.respondText("Hello, API!")
            }
            route(ShoppingListItem.path) {
                get {
                    call.respond(collection.find().toList())
                }
                post {
                    collection.insertOne(call.receive())
                    call.respond(HttpStatusCode.OK)
                }
                delete("/{id}") {
                    val id = call.parameters["id"]?.toInt() ?: error("Invalid delete request")
                    collection.deleteOne(ShoppingListItem::id eq id)
                    call.respond(HttpStatusCode.OK)
                }
            }
            get("/") {
                call.respondText(
                    this::class.java.classLoader.getResource("index.html")!!.readText(),
                    ContentType.Text.Html
                )
            }
            static("/") {
                resources("")
            }
        }
    }.start(wait = true)
}
