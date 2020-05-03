package webdata.indexreaders;

import webdata.indexes.ReviewsIndex;
import webdata.iostreams.AppInputStream;
import webdata.iostreams.BitInputStream;

import java.io.FileInputStream;
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

    public int getNumberOfReviews() {
        return this.reviewsIndex.getNumberOfReviews();
    }

}
