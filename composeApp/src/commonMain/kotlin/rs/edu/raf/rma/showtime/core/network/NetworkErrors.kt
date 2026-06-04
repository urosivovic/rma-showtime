package rs.edu.raf.rma.showtime.core.network

import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode

fun Throwable.isUnauthorizedResponse(): Boolean =
    (this as? ClientRequestException)?.response?.status == HttpStatusCode.Unauthorized ||
        message?.contains("401") == true

fun Throwable.toAuthMessage(): String {
    val status = (this as? ClientRequestException)?.response?.status

    return when (status) {
        HttpStatusCode.Unauthorized -> "Invalid username or password."
        HttpStatusCode.Conflict -> "Username is already taken."
        HttpStatusCode.BadRequest -> "Please check the entered data."
        else -> message ?: "Something went wrong. Please try again."
    }
}
