package webdata.encoders;
import webdata.iostreams.AppInputStream;
import webdata.iostreams.BitInputStream;
import webdata.models.SymbolFreqTable;

import java.io.*;

/**
 * Reads from an arithmetic-coded bit stream and decodes symbols. Not thread-safe.
 * @see ArithmicCoderBase
 */
public final class ArithmicDecoder extends ArithmicCoderBase {

    private AppInputStream input;

    // The current raw code bits being buffered, which is always in the range [low, high].
    private long code;

    public ArithmicDecoder(AppInputStream in) throws IOException {
        super();
        input = in;
        code = 0;
        for (int i = 0; i < numStateBits; i++)
            code = code << 1 | readCodeBit();
    }

    public int read(SymbolFreqTable freqs) throws IOException {
        // Translate from coding range scale to frequency table scale
        long total = freqs.getTotal();
        long range = high - low + 1;
        long offset = code - low;
        long value = ((offset + 1) * total - 1) / range;

        // A kind of binary search. Find highest symbol such that freqs.getLow(symbol) <= value.
        int start = 0;
        int end = freqs.getSymbolLimit();
        while (end - start > 1) {
            int middle = (start + end) >>> 1;
            if (freqs.getLow(middle) > value)
                end = middle;
            else
                start = middle;
        }

        int symbol = start;

        writeSymbol(freqs, symbol);
        return symbol;
    }


    protected void shift() throws IOException {
        code = ((code << 1) & stateMask) | readCodeBit();
    }


    protected void underflow() throws IOException {
        code = (code & halfRange) | ((code << 1) & (stateMask >>> 1)) | readCodeBit();
    }


    // Returns the next bit (0 or 1) from the input stream. The end
    // of stream is treated as an infinite number of trailing zeros.
    private int readCodeBit() throws IOException {
        int temp = input.read();
        if (temp == -1)
            temp = 0;
        return temp;
    }



    // To allow unit testing, this method is package-private instead of private.
    public static void decompress(AppInputStream in, OutputStream out) throws IOException {
        SymbolFreqTable freqs = new SymbolFreqTable(257);
        ArithmicDecoder dec = new ArithmicDecoder(in);
        while (true) {
            // Decode and write one byte
            int symbol = dec.read(freqs);
            if (symbol == 256)  // EOF symbol
                break;
            out.write(symbol);
            freqs.increment(symbol);
        }
    }

    public static void main(String[] args) throws IOException{
        // Perform file decompression
        try (BitInputStream in = new BitInputStream(new BufferedInputStream(new FileInputStream("src\\index\\products.txt")));
             OutputStream out = new BufferedOutputStream(new FileOutputStream("src\\index\\products-decoded.txt"))) {
            ArithmicDecoder.decompress(in, out);
        }
    }

}

