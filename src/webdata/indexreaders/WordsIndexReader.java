package webdata.indexreaders;

import webdata.iostreams.AppInputStream;
import webdata.iostreams.AppOutputStream;
import webdata.iostreams.BitInputStream;
import webdata.iostreams.BitOutputStream;
import webdata.models.ProductReview;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class WordsIndexReader extends IndexReader {
    public WordsIndexReader(AppInputStream inputStream) {
        super(inputStream);
    }

    public WordsIndexReader(String filePath) throws IOException {
        this(new BitInputStream(new FileInputStream(filePath)));
    }

    @Override
    public void loadIndex() throws IOException {

    }
}
