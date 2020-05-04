//package webdata.indexwriters;
//
//import webdata.encoders.ArithmicEncoder;
//import webdata.iostreams.AppOutputStream;
//import webdata.iostreams.BitOutputStream;
//import webdata.models.ProductReview;
//import webdata.indexes.ProductsIndex;
//
//import java.io.*;
//
//public class ProductsIndexWriter extends IndexWriter {
//
//    private ProductsIndex productToReviewsMap;
//
//    public ProductsIndexWriter(AppOutputStream outputStream) {
//        super(outputStream);
//        this.productToReviewsMap = new ProductsIndex();
//    }
//
//    public ProductsIndexWriter(String filePath) throws IOException {
//        this(new BitOutputStream(new FileOutputStream(filePath)));
//    }
//
//    @Override
//    public void process(ProductReview review) {
//        this.productToReviewsMap.insert(review.productId, review.getStringId());
//    }
//
//    @Override
//    public void writeProcessed() throws IOException {
//        ArithmicEncoder.writeEncoded(this.productToReviewsMap.toString(), this.outputStream);
//        this.productToReviewsMap = new ProductsIndex();
//    }
//
//
//}
