package webdata;

public class IndexReader {

    /**
     * Creates an IndexReader which will read from the given directory
     * @param dir
     */
    public IndexReader(String dir) {
    }


    /**
     * Returns the product identifier for the given review
     * Returns null if there is no review with the given identifier     * @param reviewId
     * @return
     */
    public String getProductId(int reviewId) {
        return null;
    }

    /**
     * @param reviewId
     * @return the score for a given review -1 if there is no review with the given identifier
     */
    public int getReviewScore(int reviewId) {
        return 0;
    }
    /**
     * @param reviewId
     * @return Returns the numerator for the helpfulness of a given review  -1 if there is no review with the given identifier
     */
    public int getReviewHelpfulnessNumerator(int reviewId) {
        return 0;
    }

    /**
     * @param reviewId
     * @return   * * Returns the denominator for the helpfulness of a given review Returns -1 if there is no review with the given identifier
     */
    public int getReviewHelpfulnessDenominator(int reviewId) {
        return 0;
    }

    /**
     *
     * @param reviewId
     * @return Returns the number of tokens in a given review
     * Returns -1 if there is no review with the given identifier
     */
    public int getReviewLength(int reviewId) {
        return 0;
    }

    /**
     *
     * @param token
     * @return Return the number of reviews containing a given token (i.e., word)
     * Returns 0 if there are no reviews containing this token
     */
    public int getTokenFrequency(String token) {
        return 0;
    }

    /**
     *
     * @param token
     * @return Return the number of times that a given token (i.e., word) appears in the reviews indexed
     * Returns 0 if there are no reviews containing this token
     */
    public int getTokenCollectionFrequency(String token) {
        return 0;
    }

}
