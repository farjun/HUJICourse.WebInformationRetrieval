package webdata.indexreaders;

import webdata.indexes.WordsIndex;
import webdata.iostreams.AppInputStream;
import webdata.iostreams.BitInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;


public class WordsIndexReader extends IndexReader {

    private WordsIndex wordEntry;

    public WordsIndexReader(String filePath) throws IOException {
        this(new BitInputStream(new FileInputStream(filePath)));
    }

    public WordsIndexReader(AppInputStream inputStream) {
        super(inputStream);
    }

    public WordsIndex getWordEntry() {
        return wordEntry;
    }

    @Override
    public void loadIndex() throws IOException {
        var sb = this.decode(this.inputStream, this.DEFAULT_NUM_SYMBOLS);
        this.wordEntry = new WordsIndex(sb.toString());
    }

    public Enumeration<Integer> getReviewsWithToken(String token){
        return this.wordEntry.getReviewsWithToken(token);
    }

    public int getTokenCollectionFrequency(String token) {
        return this.wordEntry.tokenGlobalFreq.getOrDefault(token,0);
    }

    public int getTokenFrequency(String token) {
        return this.wordEntry.tokenFreq.size();
    }
}
