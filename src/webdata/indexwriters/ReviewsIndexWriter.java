package webdata.indexwriters;

import webdata.models.ProductReview;

import java.io.BufferedWriter;

public class ReviewsIndexWriter extends IndexWriter {
    ReviewsIndexWriter(BufferedWriter outputFile) {
        super(outputFile);
    }
    public ReviewsIndexWriter(String filePath) {
        super(filePath);
    }

    @Override
    public void write(ProductReview review) {

    }
}
