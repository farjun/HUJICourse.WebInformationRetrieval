package webdata.encoders;

import webdata.iostreams.AppOutputStream;
import webdata.iostreams.BitOutputStream;
import webdata.models.SymbolFreqTable;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

public final class ArithmicEncoder extends ArithmicCoderBase {

    private AppOutputStream output;

    private int numUnderflow;

    public ArithmicEncoder(AppOutputStream out) {
        super();
        output = Objects.requireNonNull(out);
        numUnderflow = 0;
    }

    /**
     * Terminates the arithmetic coding by flushing any buffered bits, so that the output can be decoded properly.
     * It is important that this method must be called at the end of the each encoding process.
     * <p>Note that this method merely writes data to the underlying output stream but does not close it.</p>
     */
    public void finish(SymbolFreqTable freqs) throws IOException {
        writeSymbol(freqs, 256);
        output.write(1);
    }


    protected void shiftAndWrite() throws IOException {
        int bit = (int)(low >>> (numStateBits - 1));
        output.write(bit);

        // Write out the saved underflow bits
        for (; numUnderflow > 0; numUnderflow--)
            output.write(bit ^ 1);
    }


    protected void underflow() {
        numUnderflow++;
    }

    public static void writeEncoded(String toEncode, AppOutputStream out) throws IOException {
        SymbolFreqTable freqs = new SymbolFreqTable(257);
        ArithmicEncoder enc = new ArithmicEncoder(out);
        for (int symbol: toEncode.toCharArray()) {
            enc.writeSymbol(freqs, symbol);
            freqs.increment(symbol);
        }
        enc.finish(freqs);  // Flush remaining code bits
    }

}
