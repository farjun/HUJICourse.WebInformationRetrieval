//package webdata.indexreaders;
//
//import webdata.encoders.ArithmicDecoder;
//import webdata.iostreams.AppInputStream;
//import webdata.iostreams.BitInputStream;
//import webdata.indexes.ProductsIndex;
//import webdata.models.SymbolFreqTable;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.Collections;
//import java.util.Enumeration;
//
//public class ProductsIndexReader extends IndexReader {
//
//    private ProductsIndex productToReviewsMap;
//
//    public ProductsIndexReader(AppInputStream inputStream) {
//        super(inputStream);
//    }
//
//    public ProductsIndexReader(String filePath) throws IOException {
//        this(new BitInputStream(new FileInputStream(filePath)));
//    }
//
//    @Override
//    public void loadIndex() throws IOException {
//        var sb = this.decode(this.inputStream, this.DEFAULT_NUM_SYMBOLS);
//        this.productToReviewsMap = new ProductsIndex(sb.toString());
//    }
//
//    public Enumeration<Integer> getReviewsByProductId(String productId){
//        if (this.productToReviewsMap.contains(productId)) {
//            return this.productToReviewsMap.get(productId);
//        }
//        else{
//            return Collections.enumeration(Collections.emptyList());
//        }
//    }
//
//
//
//
//}
