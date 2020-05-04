package webdata.indexes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public class ReviewsIndex {
    private final ArrayList<String> reviews;
    public static final int SCORE = 0;
    public static final int HELPFULLNESS_NUMERATOR = 1;
    public static final int HELPFULLNESS_DENUMERATOR = 2;
    public static final int LENGHT = 3;
    public static final int PRODUCT_ID = 4;

    public ReviewsIndex(){
        this.reviews = new ArrayList<>();
    }

    /**
     * parses the reviews index - example: score,helpfullness,(numerator, denumenetor),lenght|
     * @param serializedReviewsIndex the serialized string form of the index
     */
    public ReviewsIndex(String serializedReviewsIndex){
        this();
        this.reviews.addAll(Arrays.asList(serializedReviewsIndex.split("\\|")));
    }

    public void insert(String value){
        this.reviews.add(value);
    }

    public int[] getReviewNums(int reviewID){
        if(this.reviews.size() <= reviewID){
            return new int[]{-1};
        }else{
            return Stream.of(this.reviews.get(reviewID).split(","))
                    .limit(4) // limit=4 because productId is not int
                    .mapToInt(Integer::parseInt)
                    .toArray();
        }
    }

    public String getProductId(int reviewID){
        if(this.reviews.size() <= reviewID){
            return "";
        }else{
            var entry = this.reviews.get(reviewID);
            var idx = entry.lastIndexOf(",") ;
            if(idx < 0){
                return "";
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

    public String toCompressedString() {
        return this.toString();
    }
}
