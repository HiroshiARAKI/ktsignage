package net.hirlab.ktsignage.model.data

data class Weather(
    val main: String,
    val description: String,
    val icon: String,
    val country: String,
    val city: String,
    val weatherId: Int,
    val temp: Float,
    val maxTemp: Float,
    val minTemp: Float,
    val humidity: Int,
    val time: Long,
) {
    companion object {
        const val INVALID_TEMP = Float.MIN_VALUE
    }
}