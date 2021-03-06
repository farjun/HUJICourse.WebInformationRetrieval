package webdata;

import webdata.indexes.IndexReaderImpl;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Enumeration;

public class IndexReader {

    private IndexReaderImpl indexReader;


    /**
     * Creates an IndexReader which will read from the given directory
     * @param dir
     */
    public IndexReader(String dir) {

        try {
            var wordsPath = Paths.get(dir,"words").toString();
            var reviewsPath = Paths.get(dir,"reviews").toString();
            var productsPath = Paths.get(dir,"products").toString();
            this.indexReader = new IndexReaderImpl(dir, productsPath, reviewsPath, wordsPath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * * @param reviewId
     * @return the product identifier for the given review
     * Returns null if there is no review with the given identifier
     */
    public String getProductId(int reviewId) {
        return this.indexReader.getProductId(reviewId);
    }

    /**
     * @param reviewId
     * @return the score for a given review -1 if there is no review with the given identifier
     */
    public int getReviewScore(int reviewId) {
        return this.indexReader.getReviewScore(reviewId);
    }
    /**
     * @param reviewId
     * @return the numerator for the helpfulness of a given review  -1 if there is no review with the given identifier
     */
    public int getReviewHelpfulnessNumerator(int reviewId) {
        return this.indexReader.getReviewHelpfulnessNumerator(reviewId);
    }

    /**
     * @param reviewId
     * @return the denominator for the helpfulness of a given review Returns -1 if there is no review with the given identifier
     */
    public int getReviewHelpfulnessDenominator(int reviewId) {
        return this.indexReader.getReviewHelpfulnessDenominator(reviewId);
    }

    /**
     *
     * @param reviewId
     * @return the number of tokens in a given review
     * Returns -1 if there is no review with the given identifier
     */
    public int getReviewLength(int reviewId) {
        return this.indexReader.getReviewLength(reviewId);
    }

    /**
     *
     * @param token
     * @return the number of reviews containing a given token (i.e., word)
     * Returns 0 if there are no reviews containing this token
     */
    public int getTokenFrequency(String token) {
        return this.indexReader.getTokenFrequency(token);
    }

    /**
     *
     * @param token
     * @return the number of times that a given token (i.e., word) appears in the reviews indexed
     * Returns 0 if there are no reviews containing this token
     */
    public int getTokenCollectionFrequency(String token) {
        return this.indexReader.getTokenCollectionFrequency(token);
    }

    /**
     * @param token
     * @return  a series of integers of the form id-1, freq-1, id-2, freq-2, ... such
     * that id-n is the n-th review containing the given token and freq-n is the
     * number of times that the token appears in review id-n
     * Note that the integers should be sorted by id
     *
     * Returns an empty Enumeration if there are no reviews containing this token
     */
     public Enumeration<Integer> getReviewsWithToken(String token) {
         return this.indexReader.getReviewsWithToken(token);
     }

    /**
     * @return  the number of product reviews available in the system
     */
    public int getNumberOfReviews() {
        return this.indexReader.getNumberOfReviews();
    }

    /**
     *
     * @return the number of number of tokens in the system
     * (Tokens should be counted as many times as they appear)
     */
    public int getTokenSizeOfReviews() {
        return this.indexReader.getTokenSizeOfReviews();
    }

    /**
     *
     * @param productId
     * @return the ids of the reviews for a given product identifier
     * Note that the integers returned should be sorted by id
     * Returns an empty Enumeration if there are no reviews for this product
     */
    public Enumeration<Integer> getProductReviews(String productId) {
        return this.indexReader.getProductReviews(productId);
    }

}
