package webdata.indexreaders;

import webdata.encoders.ArithmicDecoder;
import webdata.indexes.ProductsIndex;
import webdata.indexes.WordsIndex;
import webdata.iostreams.AppInputStream;
import webdata.iostreams.AppOutputStream;
import webdata.iostreams.BitInputStream;
import webdata.iostreams.BitOutputStream;
import webdata.models.ProductReview;
import webdata.models.SymbolFreqTable;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;

public class WordsIndexReader extends IndexReader {

    public WordsIndexReader(String filePath) throws IOException {
        this(new BitInputStream(new FileInputStream(filePath)));
    }

    public WordsIndexReader(AppInputStream inputStream) {
        super(inputStream);
    }

    public WordsIndex getWordEntry() {
        return wordEntry;
    }

    private WordsIndex wordEntry;

    @Override
    public void loadIndex() throws IOException {
        var sb = this.decode(this.inputStream, this.DEFAULT_NUM_SYMBOLS);
        this.wordEntry = new WordsIndex(sb.toString());
    }

}
