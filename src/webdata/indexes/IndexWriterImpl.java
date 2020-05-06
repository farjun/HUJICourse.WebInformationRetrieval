package webdata.indexes;

import webdata.encoders.ArithmicEncoder;
import webdata.iostreams.AppOutputStream;
import webdata.iostreams.BitOutputStream;
import webdata.models.ProductReview;

import java.io.FileOutputStream;
import java.io.IOException;

public class IndexWriterImpl {
    protected AppOutputStream productsOutputStream;
    protected AppOutputStream reviewsOutputStream;
    protected AppOutputStream wordsOutputStream;
    private ProductsIndex productsIndex;
    private ReviewsIndex reviewsIndex;
    private WordsIndex wordsIndex;

    public IndexWriterImpl(AppOutputStream productsOutputStream, AppOutputStream reviewsOutputStream,
                           AppOutputStream wordsOutputStream) {
        this.productsOutputStream = productsOutputStream;
        this.reviewsOutputStream = reviewsOutputStream;
        this.wordsOutputStream = wordsOutputStream;
        this.productsIndex = new ProductsIndex();
        this.reviewsIndex = new ReviewsIndex();
        this.wordsIndex = new WordsIndex();

    }

    public IndexWriterImpl(String productFilePath, String reviewsFilePath, String wordsFilePath) throws IOException {
        this(   new BitOutputStream(new FileOutputStream(productFilePath)),
                new BitOutputStream(new FileOutputStream(reviewsFilePath)),
                new BitOutputStream(new FileOutputStream(wordsFilePath)));
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

    public void writeProcessed() throws IOException {
        ArithmicEncoder.writeEncoded(this.productsIndex.toString(), this.productsOutputStream);
        ArithmicEncoder.writeEncoded(this.reviewsIndex.toString(), this.reviewsOutputStream);
        ArithmicEncoder.writeEncoded(this.wordsIndex.toString(), this.wordsOutputStream);

        this.reviewsIndex = new ReviewsIndex();
        this.productsIndex = new ProductsIndex();
        this.wordsIndex = new WordsIndex();
    }


}
