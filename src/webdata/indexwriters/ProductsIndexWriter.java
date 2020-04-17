package webdata.indexwriters;

import webdata.encoders.ArithmicEncoder;
import webdata.iostreams.AppOutputStream;
import webdata.iostreams.BitOutputStream;
import webdata.models.ProductReview;
import webdata.models.SerializeableHashMap;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ProductsIndexWriter extends IndexWriter {

    private final ArithmicEncoder encoder;
    private SerializeableHashMap productToReviewsMap;

    public ProductsIndexWriter(AppOutputStream outputStream) {
        super(outputStream);
        this.encoder = new ArithmicEncoder(outputStream);
        this.productToReviewsMap = new SerializeableHashMap();
    }

    public ProductsIndexWriter(String filePath) throws IOException {
        this(new BitOutputStream(new FileOutputStream(filePath)));
    }

    @Override
    public void proccess(ProductReview review) throws IOException {
        this.productToReviewsMap.addTo(review.productId, review.getStringId());
    }

    @Override
    public void writeProccessed() throws IOException {
        ArithmicEncoder.writeEncoded(this.productToReviewsMap.toString(), this.outputStream);
        this.productToReviewsMap = new SerializeableHashMap();
    }


}
