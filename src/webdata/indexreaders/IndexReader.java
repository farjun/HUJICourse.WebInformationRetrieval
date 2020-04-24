package webdata.indexreaders;

import webdata.encoders.ArithmicDecoder;
import webdata.iostreams.AppInputStream;
import webdata.iostreams.AppOutputStream;
import webdata.models.ProductReview;
import webdata.models.SymbolFreqTable;

import java.io.IOException;
import webdata.models.SymbolFreqTable;
import webdata.encoders.ArithmicDecoder;

public abstract class IndexReader {
    protected AppInputStream inputStream;
    protected final int DEFAULT_NUM_SYMBOLS = 257;
    public IndexReader(AppInputStream inputStream) {
        this.inputStream = inputStream;
    }

    // we should consider putting the decoding code in loadIndex here and filling approperiat objects in inhertied classes
    abstract void loadIndex() throws IOException;

    public StringBuffer decode(AppInputStream inputStream, int numSymbols) throws IOException {
        SymbolFreqTable freqs = new SymbolFreqTable(numSymbols);
        ArithmicDecoder dec = new ArithmicDecoder(this.inputStream);
        StringBuffer sb = new StringBuffer();

        while (true) {
            // Decode and write one byte
            int symbol = dec.read(freqs);
            if (symbol == 256)  // EOF symbol
                break;

            sb.append((char)symbol);
            freqs.increment(symbol);
        }
        return sb;
    }

    public void close(){
        try {
            this.inputStream.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

    }




}
