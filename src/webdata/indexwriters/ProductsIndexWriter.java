package webdata.indexwriters;

import webdata.encoders.ArithmicEncoder;
import webdata.iostreams.AppOutputStream;
import webdata.iostreams.BitOutputStream;
import webdata.models.ProductReview;
import webdata.models.SerializeableHashMapToArraylist;

import java.io.*;

public class ProductsIndexWriter extends IndexWriter {

    private SerializeableHashMapToArraylist productToReviewsMap;

    public ProductsIndexWriter(AppOutputStream outputStream) {
        super(outputStream);
        this.productToReviewsMap = new SerializeableHashMapToArraylist();
    }

    public ProductsIndexWriter(String filePath) throws IOException {
        this(new BitOutputStream(new FileOutputStream(filePath)));
    }

    @Override
    public void proccess(ProductReview review) {
        this.productToReviewsMap.addTo(review.productId, review.getStringId());
    }

    @Override
    public void writeProccessed() throws IOException {
        ArithmicEncoder.writeEncoded(this.productToReviewsMap.toString(), this.outputStream);
        this.outputStream.close();
        this.productToReviewsMap = new SerializeableHashMapToArraylist();

    }


}
