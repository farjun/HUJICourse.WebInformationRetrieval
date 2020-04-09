package webdata.indexwriters;

import webdata.models.ProductReview;

import java.io.BufferedWriter;
import java.io.IOException;

public class ProductsIndexWriter extends IndexWriter {

    public ProductsIndexWriter(BufferedWriter outputFile) {
        super(outputFile);
    }

    public ProductsIndexWriter(String filePath) {
        super(filePath);
    }

    @Override
    public void write(ProductReview review) throws IOException {
        System.out.println(review);
    }


}
