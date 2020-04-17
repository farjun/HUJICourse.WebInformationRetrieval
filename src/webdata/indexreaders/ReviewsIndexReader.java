package webdata.indexreaders;

import webdata.iostreams.AppInputStream;
import webdata.iostreams.AppOutputStream;
import webdata.iostreams.BitInputStream;
import webdata.iostreams.BitOutputStream;
import webdata.models.ProductReview;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ReviewsIndexReader extends IndexReader {
    public ReviewsIndexReader(AppInputStream inputStream) {
        super(inputStream);
    }

    public ReviewsIndexReader(String filePath) throws IOException {
        this(new BitInputStream(new FileInputStream(filePath)));
    }

    @Override
    public void loadIndex() throws IOException {

    }
}
