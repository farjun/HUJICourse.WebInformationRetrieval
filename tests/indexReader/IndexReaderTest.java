package indexReader;

import org.junit.jupiter.api.Assertions;
import webdata.IndexReader;
import webdata.IndexWriter;

import java.util.Enumeration;
import java.util.stream.IntStream;

class Constants100 {
    public static int[] ReviewScores = IntStream.of(5,1,4,2,5,4,5,5,5,5,5,5,1,4,5,5,2,5,5,5,5,5,5,5,5,5,1,4,5,5,5,5,4,4,5,4,5,5,4,5,5,5,5,5,5,3,5,3,4,3,1,5,4,3,4,5,5,5,5,5,3,5,1,5,5,5,5,2,3,5,5,5,5,1,2,1,5,5,3,5,5,4,5,3,3,5,5,5,5,5,5,5,5,5,5,5,5,5,5,1).toArray();
    public static int[] HelpfulnessNominator = IntStream.of(1,0,1,3,0,0,0,0,1,0,1,4,1,2,4,4,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,19,13,9,3,2,1,0,0,0,0,0,0,0,0,0,1,1,0,0,4,1,0,2,0,2,1,0,0,2,0,2,3,1,1,0,0,0,0,2,1,0,0,0,0,0,0,0,0,1,1,15,5,4,4,1,1,1,0,0,0,0,0,0,0,0,0,0,0).toArray();
    public static int[] HelpfulnessDenominator = IntStream.of(1,0,1,3,0,0,0,0,1,0,1,4,1,2,5,5,0,0,0,0,0,0,0,0,0,0,1,1,0,1,0,1,19,13,9,3,2,1,0,0,0,0,0,0,0,0,0,2,2,1,7,4,1,0,2,0,2,1,0,0,4,2,2,3,1,1,0,1,0,2,2,1,0,1,0,0,0,0,0,0,1,1,15,5,4,4,1,1,1,0,0,0,0,0,0,0,0,0,0,1).toArray();
    public static int[] ReviewLength = IntStream.of(48,32,93,41,27,73,51,24,27,25,154,67,80,15,21,26,42,25,132,29,48,57,29,19,61,25,17,37,99,154,94,19,206,93,99,81,45,50,100,61,122,233,34,36,48,42,48,21,52,37,20,71,233,34,69,61,35,32,26,23,31,30,17,178,57,33,143,94,44,18,74,125,75,314,15,19,97,60,24,34,38,26,447,80,64,50,83,37,130,49,38,22,37,186,25,64,169,58,78,37).toArray();

    public static String[] tokensToTest = new String[]{"the", "dog", "omerfarjun", "don't"};
    public static int[] tokensToTestCollectionFrequency = IntStream.of(288,33,0,0).toArray();
    public static int[] tokensToTestFrequency = IntStream.of(77,12,0,0).toArray();
    public static int[][] tokensToTestReview = {
            {
                    1, 2, 3, 4, 6, 7, 8, 9, 11, 12, 13, 15, 16, 19, 21, 22, 23, 25, 27, 28, 29, 30, 31, 33, 34, 35, 36,
                    37, 39, 41, 42, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 55, 56, 57, 59, 60, 61, 63, 64, 65, 66, 67,
                    68, 69, 71, 72, 73, 74, 77, 78, 79, 80, 82, 83, 84, 85, 87, 89, 90, 92, 93, 94, 95, 96, 97, 98, 99
            },
            {
                    1, 10, 84, 85, 87, 89, 92, 94, 96, 97, 98, 99
            },
            {},{}
    };


}
class IndexReaderTest {

    private static final String indexDir =  "./src/index";
    private static final String reviewsFilePath = "./datasets/100.txt";
    private static final String reviewsFilePath1000 = "./datasets/1000.txt";

    @org.junit.jupiter.api.BeforeAll
    static void setUp() {
        IndexWriter writer = new IndexWriter();
        writer.write(reviewsFilePath, indexDir);
        writer.clearIndexesFromRAM();
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {

    }

    @org.junit.jupiter.api.Test
    void getProductId() {
        IndexReader reader = new IndexReader(indexDir);
        for (int i = 1; i <= reader.getNumberOfReviews(); i++) {
            Assertions.assertNotNull(reader.getProductId(i), "checking review "+i+" and prodId"+reader.getProductId(i));
        }
    }

    @org.junit.jupiter.api.Test
    void getReviewScore() {
        IndexReader reader = new IndexReader(indexDir);
        for (int i = 1; i <= reader.getNumberOfReviews(); i++) {
            Assertions.assertEquals(reader.getReviewScore(i), Constants100.ReviewScores[i-1], "checking review "+i+"");
        }
        Assertions.assertNull(reader.getProductId(reader.getNumberOfReviews()+1));
    }

    @org.junit.jupiter.api.Test
    void getReviewHelpfulnessNumerator() {
        IndexReader reader = new IndexReader(indexDir);
        for (int i = 1; i <= reader.getNumberOfReviews(); i++) {
            Assertions.assertEquals(reader.getReviewHelpfulnessNumerator(i), Constants100.HelpfulnessNominator[i-1]);
        }
        Assertions.assertNull(reader.getProductId(reader.getNumberOfReviews()+1));
    }

    @org.junit.jupiter.api.Test
    void getReviewHelpfulnessDenominator() {
        IndexReader reader = new IndexReader(indexDir);
        for (int i = 1; i <= reader.getNumberOfReviews(); i++) {
            Assertions.assertEquals(reader.getReviewHelpfulnessDenominator(i), Constants100.HelpfulnessDenominator[i-1], "checking review "+i+"");
        }
        Assertions.assertNull(reader.getProductId(reader.getNumberOfReviews()+1));
    }

    @org.junit.jupiter.api.Test
    void getReviewLength() {
        IndexReader reader = new IndexReader(indexDir);
        for (int i = 1; i <= reader.getNumberOfReviews(); i++) {
            Assertions.assertEquals(Constants100.ReviewLength[i-1], reader.getReviewLength(i), "checking review "+i+"");
        }
        Assertions.assertNull(reader.getProductId(reader.getNumberOfReviews()+1));
    }

    @org.junit.jupiter.api.Test
    void getTokenFrequency() {
        IndexReader reader = new IndexReader(indexDir);
        int counter = 0;
        for (String token: Constants100.tokensToTest) {
            Assertions.assertEquals(reader.getTokenFrequency(token), Constants100.tokensToTestFrequency[counter]);
            counter++;
        }
    }

    @org.junit.jupiter.api.Test
    void getTokenCollectionFrequency() {
        IndexReader reader = new IndexReader(indexDir);
        int counter = 0;
        for (String token: Constants100.tokensToTest) {
            Assertions.assertEquals(reader.getTokenCollectionFrequency(token),
                    Constants100.tokensToTestCollectionFrequency[counter]);
            counter++;
        }
    }

    @org.junit.jupiter.api.Test
    void getReviewsWithToken() {
        IndexReader reader = new IndexReader(indexDir);
        int counter = 0;
        for (String token: Constants100.tokensToTest) {
            var enumeration = reader.getReviewsWithToken(token);
            while(enumeration.hasMoreElements()) {
                var id = enumeration.nextElement();
                var revs = IntStream.of(Constants100.tokensToTestReview[counter]);
                Assertions.assertTrue(revs.anyMatch(x -> x == id));
                var freq = enumeration.nextElement();
                // TODO
            }
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
        // works with 1000.txt
//        IndexReader reader = new IndexReader(indexDir);
//        var our = reader.getTokenSizeOfReviews();
//        var actual = 75447;
//        Assertions.assertEquals(our, actual);
    }

    private boolean enumerationContains(Enumeration<Integer> iterable, int val){
        while(iterable.hasMoreElements()){
            if( iterable.nextElement() == val){
                return true;
            }
        }
        return false;
    }

    @org.junit.jupiter.api.Test
    void getProductReviews() {
        IndexReader reader = new IndexReader(indexDir);
        for (int i = 1; i <= 100; i++) {
            String productId = reader.getProductId(i);
            Enumeration<Integer> reviewIds = reader.getProductReviews(productId);
            Assertions.assertTrue(this.enumerationContains(reviewIds, i), "checking review "+i+"");
        }
    }
}