package me.ironblock.oneblockpvp.oneblockpvpplugin.voicechat

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.util.*
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import kotlin.math.pow


object ExternalAudioLoader {
    fun loadAudio(file: File, expectAudioFormat: AudioFormat): TransformedAudioInputStream {
        val audioInputStream = AudioSystem.getAudioInputStream(file);
       return TransformedAudioInputStream(audioInputStream,expectAudioFormat)

    }




}