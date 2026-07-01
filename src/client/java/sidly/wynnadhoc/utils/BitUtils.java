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
}