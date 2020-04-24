package webdata.indexreaders;

import webdata.encoders.ArithmicDecoder;
import webdata.indexes.ProductsIndex;
import webdata.indexes.ReviewsIndex;
import webdata.iostreams.AppInputStream;
import webdata.iostreams.AppOutputStream;
import webdata.iostreams.BitInputStream;
import webdata.iostreams.BitOutputStream;
import webdata.models.ProductReview;
import webdata.models.SymbolFreqTable;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ReviewsIndexReader extends IndexReader {
    private ReviewsIndex reviewsIndex;

    public ReviewsIndexReader(AppInputStream inputStream) {
        super(inputStream);
    }

    public ReviewsIndexReader(String filePath) throws IOException {
        this(new BitInputStream(new FileInputStream(filePath)));
        this.reviewsIndex = new ReviewsIndex();

    }

    @Override
    public void loadIndex() throws IOException {
        var sb = this.decode(this.inputStream, this.DEFAULT_NUM_SYMBOLS);
        this.reviewsIndex = new ReviewsIndex(sb.toString());
    }

    public int getReviewScore(int reviewId) {
        int[] ret = this.reviewsIndex.get(reviewId);
        if(ret.length == 1){
            return -1;
        }
        return ret[ReviewsIndex.SCORE];
    }

    public int getReviewHelpfulnessNumerator(int reviewId) {
        int[] ret = this.reviewsIndex.get(reviewId);
        if(ret.length == 1){
            return -1;
        }
        return ret[ReviewsIndex.HELPFULLNESS_NUMERATOR];
    }


    public int getReviewHelpfulnessDenominator(int reviewId) {
        int[] ret = this.reviewsIndex.get(reviewId);
        if(ret.length == 1){
            return -1;
        }
        return ret[ReviewsIndex.HELPFULLNESS_DENUMERATOR];
    }


    public int getReviewLength(int reviewId) {
        int[] ret = this.reviewsIndex.get(reviewId);
        if(ret.length == 1){
            return -1;
        }
        return ret[ReviewsIndex.LENGHT];
    }

}
