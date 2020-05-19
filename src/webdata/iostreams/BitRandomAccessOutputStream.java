package webdata.iostreams;

import java.io.*;

public class BitRandomAccessOutputStream {
    public static final int MAX_BUFFER_SIZE = 8;

    private final RandomAccessFile randomAccessFile;
    private final BufferedOutputStream bufferedOutputStream;

    private int buffer;
    private int bufferCurCapacity;

    /**
     * Constructs a bit output stream based on the specified byte file .
     * @param out the byte file
     */
    public BitRandomAccessOutputStream(File out) throws IOException {
        randomAccessFile = new RandomAccessFile(out, "rw");
        bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(randomAccessFile.getFD()));

        buffer = 0;
        bufferCurCapacity = 0;
    }



    public void write(int b) throws IOException {
        buffer = (buffer << 1) | b;
        bufferCurCapacity++;
        if (bufferCurCapacity == MAX_BUFFER_SIZE) {
            bufferedOutputStream.write(buffer);
            buffer = 0;
            bufferCurCapacity = 0;
        }
    }

    public void close() throws IOException {
        while (bufferCurCapacity != 0)
            write(0);

        randomAccessFile.close();
    }

    public void flush() throws IOException {
        while (bufferCurCapacity != 0)
            write(0);
    }

}
