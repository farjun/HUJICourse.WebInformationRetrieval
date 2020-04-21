package webdata.indexwriters;

import webdata.encoders.ArithmicEncoder;
import webdata.indexes.ProductsIndex;
import webdata.indexes.ReviewsIndex;
import webdata.iostreams.AppOutputStream;
import webdata.iostreams.BitOutputStream;
import webdata.models.ProductReview;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;

public class ReviewsIndexWriter extends IndexWriter {
    private ReviewsIndex reviewsIndex;

    /**
     * writes the reviews index, example format: score,helpfullness,(numerator, denumenetor),lenght|
     * @param outputStream
     */
    ReviewsIndexWriter(AppOutputStream outputStream) {
        super(outputStream);
    }
    public ReviewsIndexWriter(String filePath)  throws IOException {
        super(new BitOutputStream(new FileOutputStream(filePath)));
        this.reviewsIndex = new ReviewsIndex();
    }



    @Override
    public void proccess(ProductReview review) {
        this.reviewsIndex.add(review.score + "," + review.helpfulnessNumerator + "," + review.helpfulnessDenominator + "," + review.length);
    }

    @Override
    public void writeProccessed() throws IOException {
        ArithmicEncoder.writeEncoded(this.reviewsIndex.toString(), this.outputStream);
        this.outputStream.close();
        this.reviewsIndex = new ReviewsIndex();
    }
}
