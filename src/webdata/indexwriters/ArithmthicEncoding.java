package webdata.indexwriters;

import webdata.models.ProductReview;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;

public class ArithmthicEncoding extends IndexWriter {

    HashMap<String, String> symbols;

    public ArithmthicEncoding(BufferedWriter outputFile) {
        super(outputFile);
        this.symbols = new HashMap<>();
    }

    public ArithmthicEncoding(String filePath) {
        super(filePath);
    }



    @Override
    void write(ProductReview review) throws IOException {

    }
}
