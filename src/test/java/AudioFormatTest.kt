import java.io.File
import javax.sound.sampled.AudioSystem

fun main() {
    val file = File("G:\\CloudMusic\\アニメ(ACG) - 白鹭归庭.wav")
    val audioInputStream = AudioSystem.getAudioInputStream(file);
    val audioFormat = audioInputStream.format
    println("channels:${audioFormat.channels}")
    println("encoding:${audioFormat.encoding}")
    println("sampleRate:${audioFormat.sampleRate}")
    println("sampleSizeInBits:${audioFormat.sampleSizeInBits}")
    println("isBigEndian:${audioFormat.isBigEndian}")


}
class AudioFormatTest {
}