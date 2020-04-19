package webdata.indexwriters;

import webdata.iostreams.AppOutputStream;
import webdata.iostreams.BitOutputStream;
import webdata.models.ProductReview;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;

public class ReviewsIndexWriter extends IndexWriter {
    ReviewsIndexWriter(AppOutputStream outputStream) {
        super(outputStream);
    }
    public ReviewsIndexWriter(String filePath)  throws IOException {
        super(new BitOutputStream(new FileOutputStream(filePath)));
    }

    @Override
    public void proccess(ProductReview review) {

    }

    @Override
    public void writeProccessed() throws IOException {

    }
}
