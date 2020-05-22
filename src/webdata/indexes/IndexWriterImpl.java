package webdata.indexes;

import webdata.encoders.ArithmeticEncoder;
import webdata.iostreams.AppOutputStream;
import webdata.iostreams.BitOutputStream;
import webdata.models.ProductReview;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class IndexWriterImpl {
    private final BlockSizesFile productsBlockSizesFile;
    protected AppOutputStream productsOutputStream;
    protected AppOutputStream reviewsOutputStream;
    protected AppOutputStream wordsOutputStream;
    private ProductsIndex productsIndex;
    private ReviewsIndex reviewsIndex;
    private WordsIndex wordsIndex;

    public IndexWriterImpl(String productFilePath, String reviewsFilePath, String wordsFilePath) throws IOException {
        this.productsOutputStream = new BitOutputStream(new FileOutputStream(productFilePath));
        this.productsIndex = new ProductsIndex();
        this.productsBlockSizesFile = new BlockSizesFile(new FileWriter(productFilePath.concat("block_sizes")));

        this.reviewsOutputStream = new BitOutputStream(new FileOutputStream(reviewsFilePath));
        this.reviewsIndex = new ReviewsIndex();

        this.wordsOutputStream =  new BitOutputStream(new FileOutputStream(wordsFilePath));
        this.wordsIndex = new WordsIndex();
    }


    public void close(){
        try {
            this.productsOutputStream.close();
            this.reviewsOutputStream.close();
            this.wordsOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void process(ProductReview review) {
        var reviewIndexInp = review.score + "," +
                review.helpfulnessNumerator + "," +
                review.helpfulnessDenominator + "," +
                review.length + "," +
                review.productId;
        this.reviewsIndex.insert(reviewIndexInp);
        this.wordsIndex.insert(review);
        this.productsIndex.insert(review.productId, review.getStringId());
    }

    public void writeEncoded(String toEncode, AppOutputStream out) throws IOException {
        ArithmeticEncoder enc = new ArithmeticEncoder(out);
        for (int symbol: toEncode.toCharArray()) {
            enc.writeSymbol(symbol);
        }
        enc.finish();  // Flush remaining code bits
    }

    public void writeEncoded(String[] toEncodeArr, AppOutputStream out, BlockSizesFile blockSizesFile) throws IOException {
        ArithmeticEncoder enc = new ArithmeticEncoder(out);
        for (String toEncode: toEncodeArr ) {
            for (int symbol: toEncode.toCharArray()) {
                enc.writeSymbol(symbol);
            }
            int numOfBytesWritten = out.setCheckpoint();
            enc = new ArithmeticEncoder(out);
            blockSizesFile.addBlockSize(numOfBytesWritten);
        }
        enc.finish();  // Flush remaining code bits
        blockSizesFile.flush();
    }

    public void writeProcessed() throws IOException {
        writeEncoded(this.productsIndex.toStringBlocks(), this.productsOutputStream, this.productsBlockSizesFile);
        writeEncoded(this.reviewsIndex.toString(), this.reviewsOutputStream);
        writeEncoded(this.wordsIndex.toString(), this.wordsOutputStream);

        this.reviewsIndex = new ReviewsIndex();
        this.productsIndex = new ProductsIndex();
        this.wordsIndex = new WordsIndex();
    }


}
