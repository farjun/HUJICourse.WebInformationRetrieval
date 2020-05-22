package webdata.iostreams;

import java.io.*;
import java.util.ArrayList;

import static webdata.encoders.BitUtils.END_OF_FILE;

public class BitRandomAccessInputStream implements AppInputStream {

    private final RandomAccessFile randomAccessFile;
    private int curBlockNumOfBytes;
    private int numOfBytesRead;

    private int currentByte;

    private int numBitsRemaining;
    private final ArrayList<Integer> blockSizes;

    /**
     * Constructs a bit input stream based on the specified byte input stream.
     * @param in the byte input stream
     */
    public BitRandomAccessInputStream(File in, ArrayList<Integer> blockSizes) throws IOException  {
        randomAccessFile = new RandomAccessFile(in, "r");
        currentByte = 0;
        numBitsRemaining = 0;

        numOfBytesRead = 0;
        curBlockNumOfBytes = 0;
        this.blockSizes = blockSizes;
    }

    private boolean blockFinished(){
        return curBlockNumOfBytes <= numOfBytesRead && numBitsRemaining == 0;
    }

    /**
     * Reads a bit from this stream. Returns 0 or 1 if a bit is available, or -1 if
     * the end of stream is reached. The end of stream always occurs on a byte boundary.
     * @return the next bit of 0 or 1, or -1 for the end of stream
     */
    public int read() throws IOException {
        if (blockFinished())
            return -1;

        if (numBitsRemaining == 0) {
            readByte();
            // if we reach the end of this block we will stop reading and return end of stream
            if(curBlockNumOfBytes <= numOfBytesRead) {
                currentByte = -1;
                return -1; // or 11111111111111
            }
        }
        numBitsRemaining--;
        return (currentByte >>> numBitsRemaining) & 1;
    }

    private void readByte() throws IOException {
        currentByte = randomAccessFile.read();
        numBitsRemaining = 8;
        numOfBytesRead++;
    }

    /**
     * Reads a bit from this stream. Returns 0 or 1 if a bit is available, or -1 if
     * the end of stream is reached. The end of stream always occurs on a byte boundary.
     * @return the next bit of 0 or 1, or -1 for the end of stream
     */
    public void setPointerToBlock(int blockNumber) throws IOException {
        randomAccessFile.seek(0);
        for (int i = 0; i < blockNumber; i++) {
            randomAccessFile.skipBytes(blockSizes.get(i));
        }
        curBlockNumOfBytes = blockSizes.get(blockNumber);
        currentByte = 0;
        numOfBytesRead = 0;
    }



    /**
     * Closes this stream and the underlying input stream.
     * @throws IOException if an I/O exception occurred
     */
    public void close() throws IOException {
        randomAccessFile.close();
        currentByte = -1;
        numBitsRemaining = 0;
    }

}
