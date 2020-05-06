package webdata;

import org.junit.jupiter.api.Assertions;

import java.util.Enumeration;
import java.util.stream.IntStream;

class Constants100 {
    public static int[] ReviewScores = IntStream.of(5,1,4,2,5,4,5,5,5,5,5,5,1,4,5,5,2,5,5,5,5,5,5,5,5,5,1,4,5,5,5,5,4,4,5,4,5,5,4,5,5,5,5,5,5,3,5,3,4,3,1,5,4,3,4,5,5,5,5,5,3,5,1,5,5,5,5,2,3,5,5,5,5,1,2,1,5,5,3,5,5,4,5,3,3,5,5,5,5,5,5,5,5,5,5,5,5,5,5,1).toArray();
    public static int[] HelpfulnessNominator = IntStream.of(1,0,1,3,0,0,0,0,1,0,1,4,1,2,4,4,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,19,13,9,3,2,1,0,0,0,0,0,0,0,0,0,1,1,0,0,4,1,0,2,0,2,1,0,0,2,0,2,3,1,1,0,0,0,0,2,1,0,0,0,0,0,0,0,0,1,1,15,5,4,4,1,1,1,0,0,0,0,0,0,0,0,0,0,0).toArray();
    public static int[] HelpfulnessDenominator = IntStream.of(1,0,1,3,0,0,0,0,1,0,1,4,1,2,5,5,0,0,0,0,0,0,0,0,0,0,1,1,0,1,0,1,19,13,9,3,2,1,0,0,0,0,0,0,0,0,0,2,2,1,7,4,1,0,2,0,2,1,0,0,4,2,2,3,1,1,0,1,0,2,2,1,0,1,0,0,0,0,0,0,1,1,15,5,4,4,1,1,1,0,0,0,0,0,0,0,0,0,0,1).toArray();
    public static int[] ReviewLength = IntStream.of(93,32,93,41,27,73,50,24,26,25,150,66,79,15,21,25,42,25,132,29,44,57,27,19,60,25,17,37,94,150,93,19,199,90,95,76,44,49,100,58,118,226,33,35,46,39,47,21,50,37,19,71,226,33,68,60,34,32,26,23,31,30,17,175,57,32,140,87,42,18,72,124,74,308,15,19,95,60,24,34,37,26,442,80,63,50,82,36,127,48,38,22,37,184,24,62,164,58,77,35).toArray();

    public static String[] tokensToTest = new String[]{"the", "dog", "omerfarjun", "don't"};
    public static int[] tokensToTestFrequency = IntStream.of(288,33,0,0).toArray();

}
class IndexReaderTest {

    private static final String indexDir =  "./src/index";
    private static final String reviewsFilePath = "./src/datasets/100.txt";

    @org.junit.jupiter.api.BeforeAll
    static void setUp() {
        SlowIndexWriter writer = new SlowIndexWriter();
        writer.slowWrite(reviewsFilePath,indexDir);

    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {

    }

    @org.junit.jupiter.api.Test
    void getProductId() {
        IndexReader reader = new IndexReader(indexDir);
        for (int i = 1; i <= reader.getNumberOfReviews(); i++) {
            System.out.println(i);
            System.out.println(reader.getProductId(i));
            Assertions.assertNotNull(reader.getProductId(i));
        }
        Assertions.assertNull(reader.getProductId(reader.getNumberOfReviews()));
    }

    @org.junit.jupiter.api.Test
    void getReviewScore() {
        IndexReader reader = new IndexReader(indexDir);
        for (int i = 1; i < reader.getNumberOfReviews(); i++) {
            Assertions.assertEquals(reader.getReviewScore(i), Constants100.ReviewScores[i]);
        }
        Assertions.assertNull(reader.getProductId(reader.getNumberOfReviews()));
    }

    @org.junit.jupiter.api.Test
    void getReviewHelpfulnessNumerator() {
        IndexReader reader = new IndexReader(indexDir);
        for (int i = 1; i < reader.getNumberOfReviews(); i++) {
            Assertions.assertEquals(reader.getReviewHelpfulnessNumerator(i), Constants100.HelpfulnessNominator[i]);
        }
        Assertions.assertNull(reader.getProductId(reader.getNumberOfReviews()));
    }

    @org.junit.jupiter.api.Test
    void getReviewHelpfulnessDenominator() {
        IndexReader reader = new IndexReader(indexDir);
        for (int i = 1; i < reader.getNumberOfReviews(); i++) {
            Assertions.assertEquals(reader.getReviewHelpfulnessDenominator(i), Constants100.HelpfulnessDenominator[i]);
        }
        Assertions.assertNull(reader.getProductId(reader.getNumberOfReviews()));
    }

    @org.junit.jupiter.api.Test
    void getReviewLength() {
        IndexReader reader = new IndexReader(indexDir);
        for (int i = 1; i < reader.getNumberOfReviews(); i++) {
            Assertions.assertEquals(reader.getReviewLength(i), Constants100.ReviewLength[i]);
        }
        Assertions.assertNull(reader.getProductId(reader.getNumberOfReviews()));
    }

    @org.junit.jupiter.api.Test
    void getTokenFrequency() {
        //todo seems to not work
//        IndexReader reader = new IndexReader(indexDir);
//        int counter = 0;
//        for (String token: Constants100.tokensToTest) {
//            Assertions.assertTrue(reader.getTokenFrequency(token) > 0);
//            Assertions.assertEquals(reader.getTokenFrequency(token), Constants100.ReviewLenght[counter]);
//            counter++;
//        }
    }

    @org.junit.jupiter.api.Test
    void getTokenCollectionFrequency() {
        //todo seems to not work
        IndexReader reader = new IndexReader(indexDir);
        int counter = 0;
        for (String token: Constants100.tokensToTest) {
            Assertions.assertEquals(reader.getTokenCollectionFrequency(token), Constants100.tokensToTestFrequency[counter]);
            counter++;
        }
    }

    @org.junit.jupiter.api.Test
    void getReviewsWithToken() {
        IndexReader reader = new IndexReader(indexDir);
        int counter = 0;
        for (String token: Constants100.tokensToTest) {
            System.out.print(reader.getReviewsWithToken(token));
            System.out.print(",");

//            Assertions.assertEquals(reader.getTokenFrequency(token), Constants100.ReviewLenght[counter]);
            counter++;
        }
    }

    @org.junit.jupiter.api.Test
    void getNumberOfReviews() {
        IndexReader reader = new IndexReader(indexDir);
        Assertions.assertTrue(reader.getNumberOfReviews() > 0);

    }

    @org.junit.jupiter.api.Test
    void getTokenSizeOfReviews() {
        Assertions.assertTrue(false);
//        IndexReader reader = new IndexReader(indexDir);
//        Assertions.assertEqual(reader.getTokenSizeOfReviews(), );
    }

    private boolean enumerationnContains(Enumeration<Integer> iterable, int val){
        while(iterable.hasMoreElements()){
            if( iterable.nextElement() == val){
                return true;
            }
        }
        return false;
    }

    @org.junit.jupiter.api.Test
    void getProductReviews() {
        //todo first to fix product id, then test should be working
//        IndexReader reader = new IndexReader(indexDir);
//        for (int i = 1; i < reader.getNumberOfReviews(); i++) {
//            String productId = reader.getProductId(i);
//            Enumeration<Integer> reviewIds = reader.getProductReviews(productId);
//            Assertions.assertTrue(this.enumerationnContains(reviewIds,i));
//        }
    }
}