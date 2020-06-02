package webdata.indexes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public class ReviewsIndex extends Index {
    private ArrayList<String> reviews;
    public static final int SCORE = 0;
    public static final int HELPFULLNESS_NUMERATOR = 1;
    public static final int HELPFULLNESS_DENUMERATOR = 2;
    public static final int LENGHT = 3;
    private static final int NUM_OF_REVIEWS_IN_BLOCK = 200;

    public ReviewsIndex(){
        this.reviews = new ArrayList<>();
    }

    /**
     * parses the reviews index - example: score,helpfullness,(numerator, denumenetor),lenght|
     * @param serializedReviewsIndex the serialized string form of the index
     */
    public ReviewsIndex(String serializedReviewsIndex){
        this();
        this.loadData(serializedReviewsIndex);
    }

    public void loadData(String rawIndex){
        this.reviews = new ArrayList<>();
        this.reviews.addAll(Arrays.asList(rawIndex.split("\\|")));
    }

    public void insert(String value){
        this.reviews.add(value);
    }

    public int[] getReviewNums(int reviewID){
        
        if(this.reviews.size() < reviewID || reviewID < 1){
            return new int[]{-1};
        }else{
            return Stream.of(this.reviews.get(reviewID-1).split(","))
                    .limit(4) // limit=4 because productId is not int
                    .mapToInt(Integer::parseInt)
                    .toArray();
        }
    }

    public String getProductId(int reviewID){
        int inBlockReviewId = (reviewID-1) % NUM_OF_REVIEWS_IN_BLOCK;
        if(this.reviews.size() <= inBlockReviewId || reviewID < 1){
            return null;
        } else {
            String entry = this.reviews.get(inBlockReviewId);
            int idx = entry.lastIndexOf(",") ;
            if(idx < 0){
                return null;
            }
            return entry.substring(idx + 1);
        }
    }

    public int getNumberOfReviews() {
        return this.reviews.size();
    }

    @Override
    public String toString() {
        return String.join("|", this.reviews);
    }

    public String[] toStringBlocks(boolean lastBatch) {
        StringBuilder sb = new StringBuilder();
        int curNumOfReviews = 0;
        int curBlock = 0;
        int numOfBlocks = (int)Math.floor(this.reviews.size() / NUM_OF_REVIEWS_IN_BLOCK);
        double blocksToReviewSizeRatio =  (double)this.reviews.size() / NUM_OF_REVIEWS_IN_BLOCK;

        if(lastBatch && numOfBlocks < blocksToReviewSizeRatio )
            numOfBlocks++;
        String[] reviewsInBlocks = new String[numOfBlocks];
        for (String key: this.reviews) {
            sb.append(key).append("|");
            curNumOfReviews++;
            if( curNumOfReviews >= NUM_OF_REVIEWS_IN_BLOCK){
                reviewsInBlocks[curBlock] = sb.toString();
                sb = new StringBuilder();
                curNumOfReviews = 0;
                curBlock++;
            }
        }
        if(lastBatch) {
            String lastBlock = sb.toString();
            if (!lastBlock.equals("")) {
                reviewsInBlocks[curBlock] = lastBlock;
            }
        }else{
            this.loadData(sb.toString());
        }
        return reviewsInBlocks;
    }

    public int getBlockNum(long reviewId){
        return (int) (reviewId -1) / NUM_OF_REVIEWS_IN_BLOCK;
    }
}
