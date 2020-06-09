package webdata;

//to manually test serialization
import webdata.encoders.ConcatEncoder;
import webdata.encoders.ConcatDecoder;
import webdata.models.ProductReview;

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
        IndexWriter writer = new IndexWriter();
//        writer.slowWrite(reviewsFilePath, indexDir);
        IndexReader reader = new IndexReader(indexDir);
        for (int i = 1; i <= 100; i++) {
            String productId = reader.getProductId(i);
            Enumeration<Integer> reviewIds = reader.getProductReviews("B00067AD4U");
        }

    }

    private static void run() {
//        String reviewsFilePath = "./src/datasets/full/foods.txt";
        String reviewsFilePath = "./datasets/1000.txt";
        String indexDir =  "./src/index";
        IndexWriter writer = new IndexWriter();
        writer.write(reviewsFilePath,indexDir);
        IndexReader reader = new IndexReader(indexDir);
//        Enumeration<Integer> enumeration = reader.getProductReviews("B0009XLVG0");
//
//        System.out.println("getProductReviews:");
//        while (enumeration.hasMoreElements()){
//            System.out.print(enumeration.nextElement());
//            System.out.print(",");
//        }
//
//        System.out.println("");
//        System.out.println("getReviewsWithToken:");
//        Enumeration<Integer> revWithTokenEnumeration = reader.getReviewsWithToken("the");
//        while (revWithTokenEnumeration.hasMoreElements()){
//            System.out.print(revWithTokenEnumeration.nextElement());
//            System.out.print(",");
//        }
//        System.out.println("");

        for (int i = 199; i < 201; i++) {
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

}
