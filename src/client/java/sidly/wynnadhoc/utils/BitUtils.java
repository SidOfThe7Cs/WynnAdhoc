package sidly.wynnadhoc.utils;

public class BitUtils {
    public static char setBits(char value, int startIndex, int length, int valueToSet) {
        int mask = ((1 << length) - 1) << startIndex;
        int intValue = value & 0xFFFF;
        intValue &= ~mask;
        intValue |= (valueToSet << startIndex) & mask;
        return (char) intValue; // char is always unsigned
    }

    public static int getBits(char value, int startIndex, int length) {
        int intValue = value & 0xFFFF;
        int mask = ((1 << length) - 1) << startIndex;
        return (intValue & mask) >> startIndex;
    }

    public static byte[] combineByteArrays(byte[] a, byte[] b) {
        if (a == null && b == null) {
            return new byte[0];
        }
        if (a == null || a.length == 0) return b;
        if (b == null || b.length == 0) return a;

        byte[] combined = new byte[a.length + b.length];
        System.arraycopy(a, 0, combined, 0, a.length);
        System.arraycopy(b, 0, combined, a.length, b.length);
        return combined;
    }
}