package webdata.encoders;

public class BitConstants {
    public static final int NUM_OF_BITS_IN_LONG = 32;
    public static final int BATCH_SEPERATOR = 256;
    public static final int NUM_OF_SYMBOLS = 257;

    /**
     * @return 100000000000000000000000
     */
    public static long getFullRange(){
        return 1L << NUM_OF_BITS_IN_LONG;
    }

    /**
     * @return 010000000000000000000000
     */
    public static long getHalfRange(){
        return getFullRange() >>> 1;
    }

    /**
     * @return 001000000000000000000000
     */
    public static long getQuarterRange(){
        return getHalfRange() >>> 1;
    }

    /**
     * @return 011111111111111111111111
     */
    public static long getAllOnes(){
        return getFullRange() - 1;
    }
}
