import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import di.initKoin

fun main() = run {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "KMMComposeLearning",
        ) {
            App(BatteryManager())
        }
    }
}