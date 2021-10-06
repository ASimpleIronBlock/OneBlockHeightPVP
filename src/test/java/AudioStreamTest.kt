import me.ironblock.oneblockpvp.oneblockpvpplugin.OneBlockPvPPlugin
import me.ironblock.oneblockpvp.oneblockpvpplugin.voicechat.ExternalAudioLoader
import me.ironblock.oneblockpvp.oneblockpvpplugin.voicechat.VoiceChatModule
import org.bukkit.Bukkit
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.File

fun main() {
    while (true){
        try {
            val input = ExternalAudioLoader.loadAudio(
                File("G:\\CloudMusic\\アニメ(ACG) - 白鹭归庭 高损.wav"),
                VoiceChatModule.audioFormat
            )
            var read = 0
            val buffer = ByteArray(4000)
            while (run {
                    read = input.read(buffer)
                    read
                } !=-1){
                println(buffer.contentToString())

                Thread.sleep(10)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
class AudioStreamTest {
}