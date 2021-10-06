import me.ironblock.oneblockpvp.oneblockpvpplugin.voicechat.ExternalAudioLoader;
import me.ironblock.oneblockpvp.oneblockpvpplugin.voicechat.TransformedAudioInputStream;
import me.ironblock.oneblockpvp.oneblockpvpplugin.voicechat.VoiceMixer;

import javax.sound.sampled.*;
import java.io.File;
import java.io.InputStream;

public class TestMusic {
    private static SourceDataLine sourceDataLine = null;
    private static AudioFormat audioFormat = new AudioFormat(8000, 8, 1, true, false);
    public static void main(String[] args) {
        try {
            TransformedAudioInputStream inputStream = ExternalAudioLoader.INSTANCE.loadAudio(new File("G:\\CloudMusic\\アニメ(ACG) - 白鹭归庭.wav"), audioFormat);
            TransformedAudioInputStream inputStream1 = ExternalAudioLoader.INSTANCE.loadAudio(new File("G:\\CloudMusic\\花澤香菜 - 恋愛サーキュレーション 8khz 16bit .wav"), audioFormat);

            DataLine.Info outputInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(outputInfo);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();
            byte[] bytes = new byte[16000];
            byte[] bytes1 = new byte[16000];
            while (inputStream.read(bytes)>0&&inputStream1.read(bytes1)>0){

                sourceDataLine.write(VoiceMixer.INSTANCE.mix(bytes,bytes1), 0, bytes.length);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
