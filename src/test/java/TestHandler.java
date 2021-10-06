import java.util.Arrays;

public class TestHandler {
    public static void main(String[] args) {
        byte[] b = new byte[]{12,12};
        int r = byteToInt(b);
        double normalized = ((float) r) / 32767;
        int r2 = (int) (normalized * 127);
        byte[] b2 = int2BytesA(r, 1);
        System.out.println();
    }

    private static int byteToInt(byte[] highBits){
        int value  = 0;


        for (byte highBit : highBits) {
            value = value<<8;
            value = value | highBit;
        }
        return value;
    }
    private static byte[] int2BytesA(int n, int len) throws IllegalArgumentException
    {
        if (len <= 0) {
            throw new IllegalArgumentException("Illegal of length");
        }
        byte[] b = new byte[len];
        for (int i = len; i > 0; i--) {
            b[(i - 1)] = ((byte)(n >> 8 * (len - i) & 0xFF));
        }
        return b;
    }
}
