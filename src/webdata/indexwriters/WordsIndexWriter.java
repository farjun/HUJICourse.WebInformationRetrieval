package webdata.indexwriters;

import webdata.models.ProductReview;

import java.io.BufferedWriter;

public class WordsIndexWriter extends IndexWriter {
    public WordsIndexWriter(BufferedWriter outputFile) {
        super(outputFile);
    }

    public WordsIndexWriter(String filePath) {
        super(filePath);
    }

    @Override
    public void write(ProductReview review) {

    }
}
