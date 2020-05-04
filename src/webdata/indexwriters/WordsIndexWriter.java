//package webdata.indexwriters;
//
//import webdata.encoders.ArithmicEncoder;
//import webdata.indexes.ProductsIndex;
//import webdata.indexes.WordsIndex;
//import webdata.iostreams.AppOutputStream;
//import webdata.iostreams.BitOutputStream;
//import webdata.models.ProductReview;
//
//import java.io.BufferedWriter;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.HashMap;
//
//public class WordsIndexWriter extends IndexWriter {
//
//    private WordsIndex index;
//
//    public WordsIndexWriter(AppOutputStream outputStream) {
//        super(outputStream);
//        this.index = new WordsIndex();
//    }
//
//    public WordsIndexWriter(String filePath) throws IOException {
//        super(new BitOutputStream(new FileOutputStream(filePath)));
//        this.index = new WordsIndex();
//    }
//
//
//    @Override
//    public void process(ProductReview review) {
//        this.index.insert(review);
//    }
//
//    @Override
//    public void writeProcessed() throws IOException {
//        ArithmicEncoder.writeEncoded(this.index.toString(), this.outputStream);
//        this.index = new WordsIndex();
//    }
//}
