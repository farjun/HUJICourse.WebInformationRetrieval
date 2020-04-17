package webdata.indexreaders;

import webdata.encoders.ArithmicDecoder;
import webdata.encoders.ArithmicEncoder;
import webdata.iostreams.AppInputStream;
import webdata.iostreams.AppOutputStream;
import webdata.iostreams.BitInputStream;
import webdata.iostreams.BitOutputStream;
import webdata.models.ProductReview;
import webdata.models.SerializeableHashMap;
import webdata.models.SymbolFreqTable;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProductsIndexReader extends IndexReader {

    private SerializeableHashMap productToReviewsMap;

    public ProductsIndexReader(AppInputStream inputStream) {
        super(inputStream);
    }

    public ProductsIndexReader(String filePath) throws IOException {
        this(new BitInputStream(new FileInputStream(filePath)));
    }

    @Override
    public void loadIndex() throws IOException {
        SymbolFreqTable freqs = new SymbolFreqTable(257);
        ArithmicDecoder dec = new ArithmicDecoder(this.inputStream);
        StringBuffer sb = new StringBuffer();
        while (true) {
            // Decode and write one byte
            int symbol = dec.read(freqs);
            if (symbol == 256)  // EOF symbol
                break;

            //out.write(symbol);
            sb.append((char)symbol);
            freqs.increment(symbol);
        }
        System.out.println(sb.toString());
    }





}
