package webdata;

//to manually test serialization
import org.junit.jupiter.api.Assertions;
import webdata.encoders.ConcatEncoder;
import webdata.encoders.ConcatDecoder;
import webdata.models.Merger;
import webdata.models.ProductReview;
import webdata.models.WordIndexTrie;

        import java.io.*;
import java.util.Enumeration;

public class ReviewStore {

    public static void main(String[] args) throws Exception {
//        runMerger();
//        runTrie();
        run();
//        runConcatEncoder();
//        runArithmicCompressOnEntireFile();
    }

    public static void writeIndex(String reviewsFilePath){

    }

    private static void runMerger() {

//        var s = "a|2|{997:1,999:1};all|2|{997:1,999:1};bomb|2|{997:1,999:1};"+
//        "almost|2|{997:1,999:1};bottles|2|{997:1,999:1};different|2|{997:1,999:1};"+
//                "can|2|{997:1,999:1};cool|2|{997:1,999:1};mdu|6|{1:1};";
//        var mrgr = new Merger(s);
////        for(int i=0;i<12;i++){
////            mrgr.mergeIter();
////            System.out.println(
////                    mrgr.getMergedBlock().toString()
////            );
////        }
//        mrgr.externalMerge();
//        System.out.println(
//                    mrgr.getMergedBlock().toString()
//            );
        String indexDir =  "./src/index";
        String reviewsFilePath = "./datasets/1000.txt";
        SlowIndexWriter writer = new SlowIndexWriter();
        writer.slowWrite(reviewsFilePath, indexDir);
        IndexReader reader = new IndexReader(indexDir);
        for (int i = 1; i <= 100; i++) {
            String productId = reader.getProductId(i);
            Enumeration<Integer> reviewIds = reader.getProductReviews("B00067AD4U");
        }

    }
    private static void runTrie() {

        ProductReview rev1 = new ProductReview(
                997,
                "product/productId: B006F2NYI2",
                "review/userId: AF50D40Y85TV3", "review/profileName: Mike A.",
                "review/helpfulness: 1/1",
                "review/score: 5.0",
                "review/time: 1328140800",
                "review/summary: Great Hot Sauce and people who run it!",
                "review/text: Man what can i say, this salsa is the bomb!! i have all the different kinds." +
                        " i have it with almost every meal. the owner is a cool dude, He's dropped off free bottles" +
                        " to me in my mailbox. i stole the f");
        ProductReview rev2 = new ProductReview(
                999,
                "product/productId: B006F2NYI2",
                "review/userId: AF50D40Y85TV3", "review/profileName: Mike A.",
                "review/helpfulness: 1/1",
                "review/score: 5.0",
                "review/time: 1328140800",
                "review/summary: Great Hot Hot Hot  Hot'Hot Sauce and people who run it!",
                "review/text: Man what what what whatwhatv what what what can i say, this salsa is the bomb!! i have all the different kinds." +
                        " i have it with almost 123Tamer 22222222 every meal. the owner is a cool dude, He's dropped off free bottles" +
                        " to me in my mailbox. i stole the f");
        WordIndexTrie tree = new WordIndexTrie();
        tree.insert(rev1);
        tree.insert(rev2);
        tree.commit();
        System.out.println(tree.toString());

        var terminalNode = tree.getTerminalNode("Can".toLowerCase());
        System.out.println(terminalNode.getTokenGlobalFreq());

        terminalNode = tree.getTerminalNode("what".toLowerCase());
        System.out.println(terminalNode.getTokenFreq().size());
    }
    private static void run() {
//        String reviewsFilePath = "./src/datasets/full/foods.txt";
        String reviewsFilePath = "./datasets/1000.txt";
        String indexDir =  "./src/index";
        SlowIndexWriter writer = new SlowIndexWriter();
//        writer.slowWrite(reviewsFilePath,indexDir);
        IndexReader reader = new IndexReader(indexDir);
        Enumeration<Integer> enumeration = reader.getProductReviews("B0009XLVG0");

        System.out.println("getProductReviews:");
        while (enumeration.hasMoreElements()){
            System.out.print(enumeration.nextElement());
            System.out.print(",");
        }

        System.out.println("");
        System.out.println("getReviewsWithToken:");
        Enumeration<Integer> revWithTokenEnumeration = reader.getReviewsWithToken("the");
        while (revWithTokenEnumeration.hasMoreElements()){
            System.out.print(revWithTokenEnumeration.nextElement());
            System.out.print(",");
        }
        System.out.println("");

        for (int i = 950; i <= 1001; i++) {
            System.out.println("---------------"+Integer.toString(i)+"---------------");
            System.out.println(reader.getReviewHelpfulnessDenominator(i));
            System.out.println(reader.getReviewHelpfulnessNumerator(i));
            System.out.println(reader.getReviewLength(i));
            System.out.println(reader.getReviewScore(i));
            System.out.println(reader.getTokenCollectionFrequency("the"));
            System.out.println(reader.getTokenFrequency("the"));
            System.out.println(reader.getProductId(i));
        }
        System.out.print("getTokenSizeOfReviews:");
        System.out.println(reader.getTokenSizeOfReviews());
    }

    private static void runConcatEncoder() throws Exception {
        String[] words = new String[]{"the", "cat", "walk"};
        ConcatEncoder encodedObj = new ConcatEncoder(10, words);
        var buffer = encodedObj.getBuffer();
        var pointers = encodedObj.getPointers();
        ConcatDecoder decodedObj = new ConcatDecoder(buffer, pointers);
        System.out.println(decodedObj);
    }

    private static void run1() {
        String reviewsFilePath = "./src/datasets/1000.txt";
        SlowIndexWriter writer = new SlowIndexWriter();
        writer.slowWrite(reviewsFilePath, "./src/index");
        System.out.println("-----------------------------");
        try {
            // POC of serialization and deserialization with byte level control. didn't include all the data.
            // Note however that this process is relevant in index writer itself.
            ProductReview obj = new ProductReview(
                    997,
                    "product/productId: B006F2NYI2",
                    "review/userId: AF50D40Y85TV3", "review/profileName: Mike A.",
                    "review/helpfulness: 1/1",
                    "review/score: 5.0",
                    "review/time: 1328140800",
                    "review/summary: Great Hot Sauce and people who run it!",
                    "review/text: Man what can i say, this salsa is the bomb!! i have all the different kinds." +
                            " i have it with almost every meal. the owner is a cool dude, He's dropped off free bottles" +
                            " to me in my mailbox. i stole the f");
            FileOutputStream fos = new FileOutputStream("obj.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            // writeObject() method on Account class will
            // be automatically called by jvm
            System.out.println(obj);
            oos.writeObject(obj);
            FileInputStream fis = new FileInputStream("obj.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            ProductReview deobj = (ProductReview) ois.readObject();
            System.out.println(deobj);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
