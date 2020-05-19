package webdata.iostreams;

import java.io.*;
import java.util.Objects;

public class BitRandomAccessInputStream {

    private final RandomAccessFile randomAccessFile;
    private final BufferedOutputStream bufferedOutputStream;
    private InputStream input;

    private int currentByte;

    private int numBitsRemaining;

    /**
     * Constructs a bit input stream based on the specified byte input stream.
     * @param in the byte input stream
     */
    public BitRandomAccessInputStream(File in) throws IOException  {
        randomAccessFile = new RandomAccessFile(in, "r");
        bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(randomAccessFile.getFD()));

        currentByte = 0;
        numBitsRemaining = 0;
    }

    /**
     * Reads a bit from this stream. Returns 0 or 1 if a bit is available, or -1 if
     * the end of stream is reached. The end of stream always occurs on a byte boundary.
     * @return the next bit of 0 or 1, or -1 for the end of stream
     */
    public int read() throws IOException {
        if (currentByte == -1)
            return -1;
        if (numBitsRemaining == 0) {
            currentByte = input.read();
            if (currentByte == -1)
                return -1;
            numBitsRemaining = 8;
        }
        numBitsRemaining--;
        return (currentByte >>> numBitsRemaining) & 1;
    }


    /**
     * Closes this stream and the underlying input stream.
     * @throws IOException if an I/O exception occurred
     */
    public void close() throws IOException {
        input.close();
        currentByte = -1;
        numBitsRemaining = 0;
    }

}
