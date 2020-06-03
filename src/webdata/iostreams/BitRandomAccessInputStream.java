package webdata.iostreams;

import webdata.encoders.BitUtils;

import java.io.*;
import java.util.ArrayList;

public class BitRandomAccessInputStream implements AppInputStream {

    public static final int NUM_OF_BYTES_IN_DECODER_BUFFER = 3;
    private final RandomAccessFile randomAccessFile;
    private int curBlockReading;
    private File in;
    private int curBlockNumOfBytes;
    private int numOfBytesRead;

    private int byteBuffer;

    private File input;

    private int numBitsRemaining;
    private ArrayList<Integer> blockSizes;

    private int numOfBytsInDecoderBuffer;

    /**
     * Constructs a bit input stream based on the specified byte input stream.
     * @param input the byte input stream
     */
    public BitRandomAccessInputStream(File input, ArrayList<Integer> blockSizes) throws IOException  {
        this.input = input;
        randomAccessFile = new RandomAccessFile(input, "r");
        byteBuffer = 0;
        numBitsRemaining = 0;
        numOfBytesRead = 0;
        curBlockNumOfBytes = 0;
        curBlockReading = -1;
        numOfBytsInDecoderBuffer = NUM_OF_BYTES_IN_DECODER_BUFFER ;
        this.blockSizes = blockSizes;
    }
    // copy constructor

    public BitRandomAccessInputStream(BitRandomAccessInputStream bitRandomAccessInputStream) throws IOException  {
        this(new File(bitRandomAccessInputStream.getInputPath()), bitRandomAccessInputStream.blockSizes);
    }
    public String getInputPath() {
        return input.getAbsolutePath();
    }

    public boolean blockFinished(){
//        if(curBlockNumOfBytes < numOfBytesRead + 1 ){
//            System.out.println("omer");
//        }
        return curBlockNumOfBytes == numOfBytesRead && numBitsRemaining == 0 || curBlockNumOfBytes < numOfBytesRead;
    }

    public boolean hasMoreInput(){
        return !this.blockFinished() || numOfBytsInDecoderBuffer >= 0;
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
        if (numBitsRemaining == 0) {
            readByte();
        }
        numBitsRemaining--;
        return (byteBuffer >>> numBitsRemaining) & 1;
    }

    private void readByte() throws IOException {
        if (blockFinished()) {
            byteBuffer =  -1;
            numOfBytsInDecoderBuffer--;
        }else {
            byteBuffer = randomAccessFile.read();
            numOfBytesRead++;
        }
        numBitsRemaining = 8;

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
        numOfBytsInDecoderBuffer = NUM_OF_BYTES_IN_DECODER_BUFFER;

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
