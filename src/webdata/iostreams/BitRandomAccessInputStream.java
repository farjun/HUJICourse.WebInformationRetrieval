package webdata.iostreams;

import java.io.*;
import java.util.ArrayList;

public class BitRandomAccessInputStream implements AppInputStream {

    private final RandomAccessFile randomAccessFile;
    private final int curBlockReading;
    private int curBlockNumOfBytes;
    private int numOfBytesRead;

    private int byteBuffer;

    private int numBitsRemaining;
    private final ArrayList<Integer> blockSizes;

    /**
     * Constructs a bit input stream based on the specified byte input stream.
     * @param in the byte input stream
     */
    public BitRandomAccessInputStream(File in, ArrayList<Integer> blockSizes) throws IOException  {
        randomAccessFile = new RandomAccessFile(in, "r");
        byteBuffer = 0;
        numBitsRemaining = 0;

        numOfBytesRead = 0;
        curBlockNumOfBytes = 0;
        curBlockReading = -1;
        this.blockSizes = blockSizes;
    }

    private boolean blockFinished(){
        return curBlockNumOfBytes <= numOfBytesRead && numBitsRemaining == 0;
    }

    public boolean hasMoreInput(){
        return !this.blockFinished();
    }

    public int getNumOfBlocks(){
        return this.blockSizes.size();
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
                byteBuffer = -1;
                return -1; // or 11111111111111
            }
        }
        numBitsRemaining--;
        return (byteBuffer >>> numBitsRemaining) & 1;
    }

    private void readByte() throws IOException {
        byteBuffer = randomAccessFile.read();
        numBitsRemaining = 8;
        numOfBytesRead++;
    }

    /**
     * Reads a bit from this stream. Returns 0 or 1 if a bit is available, or -1 if
     * the end of stream is reached. The end of stream always occurs on a byte boundary.
     * @return the next bit of 0 or 1, or -1 for the end of stream
     */
    public void setPointerToBlock(int blockNumber) throws IOException {
        if(blockNumber >= blockSizes.size()){
            throw new OutOfBlocksException();
        }

        randomAccessFile.seek(0);
        for (int i = 0; i < blockNumber; i++) {
            randomAccessFile.skipBytes(blockSizes.get(i));
        }
        curBlockNumOfBytes = blockSizes.get(blockNumber);
        byteBuffer = 0;
        numOfBytesRead = 0;
    }



    /**
     * Closes this stream and the underlying input stream.
     * @throws IOException if an I/O exception occurred
     */
    public void close() throws IOException {
        randomAccessFile.close();
        byteBuffer = -1;
        numBitsRemaining = 0;
    }

}
