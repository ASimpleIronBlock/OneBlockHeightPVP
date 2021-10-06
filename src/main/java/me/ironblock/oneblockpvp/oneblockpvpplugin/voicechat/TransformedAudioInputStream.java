package me.ironblock.oneblockpvp.oneblockpvpplugin.voicechat;

import org.jetbrains.annotations.NotNull;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class TransformedAudioInputStream extends InputStream {

    private final AudioInputStream audioInputStream;
    private final AudioFormat expectedAudioFormat;
    private final AudioFormat audioFormat;
    private final int sample;
    public TransformedAudioInputStream(AudioInputStream audioInputStream, AudioFormat expectedAudioFormat) {
        this.audioInputStream = audioInputStream;
        this.expectedAudioFormat = expectedAudioFormat;
        sample = (int) (audioInputStream.getFormat().getSampleRate()/expectedAudioFormat.getSampleRate());
        audioFormat = audioInputStream.getFormat();
    }

    @Override
    public int read() throws IOException {
        return 0;
    }
    /**
     *仅限8k->4k,16bit->8bit
     * @param b
     * @return
     * @throws IOException
     */
//    @Override
//    public int read(@NotNull byte[] b) throws IOException {
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        byte[] buffer = new byte[b.length];
//        audioInputStream.read(buffer);
//        ByteArrayOutputStream array = new ByteArrayOutputStream();
//
//        for (int i = 0; i < buffer.length; i++) {
//            if (i%2<1){
//                byte[] input = new byte[2];
//                input[0] = buffer[i];
//                input[1] = buffer[i + 1];
//                int value = bytes2Int(input);
//                byte[] newValue = int2BytesA(value,2);
//                array.write(newValue);
//            }
//        }
//        System.arraycopy(array.toByteArray(),0,b,0,array.toByteArray().length);
//        return array.toByteArray().length;
//    }
    /**
     *仅限8k->4k,16bit->8bit
     * @param b
     * @return
     * @throws IOException
     */
    @Override
    public int read(@NotNull byte[] b) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[b.length*2];
        int result = audioInputStream.read(buffer);
        if (result==-1){
            return result;
        }
        ByteArrayOutputStream array = new ByteArrayOutputStream();

        for (int i = 0; i < buffer.length; i++) {
            if (i%2<1){
                byte[] input = new byte[2];
                input[0] = buffer[i];
                input[1] = buffer[i + 1];
                int value = bytes2Int(input);
                int newValue = (int) (((double) value) / 32767 * 127);
                array.write(newValue);
            }
        }
        System.arraycopy(array.toByteArray(),0,b,0,array.toByteArray().length);
        return array.toByteArray().length;
    }


//    @Override
//    public int read(@NotNull byte[] b) throws IOException {
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        byte[] buffer = new byte[audioFormat.getSampleSizeInBits() / 8];
//        int read;
//        for (int i = 0; i < b.length; i++) {
//            read = audioInputStream.read(buffer, 0, buffer.length);
//            byte[] highBits = new byte[audioFormat.getSampleSizeInBits() / 8];
//            System.arraycopy(buffer,0,highBits,0,audioFormat.getSampleSizeInBits()/8);
//            int max = (int) (Math.pow(2.0,audioFormat.getSampleSizeInBits() - 1) - 1);
//            int value  = 0;
//
//
//            for (byte highBit : highBits) {
//                value = value<<8;
//                value = value | highBit;
//            }
//            double normalized = ((float) value) / max;
//            int expectedMax =  (int) (Math.pow(2.0, ((float) expectedAudioFormat.getSampleSizeInBits()) - 1) - 1);
//            int newValue = (int) (normalized * expectedMax);
////            System.out.println("max:"+max+",value:"+value+",normalized:"+normalized+",expectedMax:"+expectedMax+",newValue:"+newValue);
//            outputStream.write(int2BytesA(newValue,expectedAudioFormat.getSampleSizeInBits()/8));
//        }
//        byte[] array = outputStream.toByteArray();
//        System.arraycopy(array,0,b,0,array.length);
//        return array.length;
//    }

    //    @Override
//    public int read(@NotNull byte[] b) throws IOException {
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        byte[] buffer = new byte[sample * audioFormat.getSampleSizeInBits() / 8];
//        int read;
//        for (int i = 0; i < b.length; i++) {
//            read = audioInputStream.read(buffer, 0, buffer.length);
//            byte[] highBits = new byte[audioFormat.getSampleSizeInBits() / 8];
//            System.arraycopy(buffer,0,highBits,0,audioFormat.getSampleSizeInBits()/8);
//            int max = (int) (Math.pow(2.0,audioFormat.getSampleSizeInBits() - 1) - 1);
//            int value  = 0;
//
//
//            for (byte highBit : highBits) {
//                value = value<<8;
//                value = value | highBit;
//            }
//            double normalized = ((float) value) / max;
//            int expectedMax =  (int) (Math.pow(2.0, ((float) expectedAudioFormat.getSampleSizeInBits()) - 1) - 1);
//            int newValue = (int) (normalized * expectedMax);
////            System.out.println("max:"+max+",value:"+value+",normalized:"+normalized+",expectedMax:"+expectedMax+",newValue:"+newValue);
//            outputStream.write(int2BytesA(newValue,expectedAudioFormat.getSampleSizeInBits()/8));
//        }
//        byte[] array = outputStream.toByteArray();
//        System.arraycopy(array,0,b,0,array.length);
//        return array.length;
//    }
    private int bytes2Int(byte[] bytes){
        int res = 0;
          for(int i=0;i<bytes.length;i++){
              res += (bytes[i] & 0xff) << (i*8);
          }
          return res;
    }

    private byte[] int2BytesA(int n,int b) throws IllegalArgumentException
    {
        byte[] bytes = new byte[b];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (n >> i*8 & 0xff);
        }
        return bytes;

    }




}
