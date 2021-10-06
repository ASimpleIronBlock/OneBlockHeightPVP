import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

fun main() {
    val ran = Random((Math.random() * 4548654561).toInt())
    for (i in 1..100){
        val radian = ran.nextDouble() * Math.PI * 2
        println(radian)
    }
}

class TestRandom {
}