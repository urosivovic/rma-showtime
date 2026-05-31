package rs.edu.raf.rma

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import rs.edu.raf.rma.showtime.ShowtimeApp

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Showtime",
    ) {
        ShowtimeApp()
    }
}
