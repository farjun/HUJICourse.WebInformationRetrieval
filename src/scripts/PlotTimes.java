package scripts;

import org.junit.jupiter.api.Assertions;
import webdata.IndexReader;
import webdata.IndexWriter;

import java.io.File;
import java.util.Enumeration;
import java.util.Objects;

public class PlotTimes {
    static String[] words = new String[]{"dribble","clash","order", "pity", "satisfaction", "ballot","economic", "college",
            "applaud", "job", "expect", "patrol", "chauvinist", "student", "carbon", "agony", "future", "owl", "map",
            "hip", "match", "entitlement", "swipe", "white", "button", "drag", "criticism", "eat", "decrease", "dish",
            "density", "unpleasant", "visible", "crevice", "calendar", "coverage", "pen", "snake", "athlete", "physics",
            "innocent", "enhance", "award", "chimpanzee", "platform","strain", "fund", "date", "check", "hurl",
            "shout","set","dynamic",
            "proportion","slump","fax","nature","innocent","note","arch","sheet","leaf","sex","legislature","veil",
            "junior","verdict","tradition","sun","outline","football","tight","comedy","parade","ideal","bow","ensure",
            "liability","available","circulate","answer","rhythm","rumor","bold","folklore","vegetable","agile",
            "appeal","headquarters","bay","pray","possibility","halt","discovery","provincial","dance","spare","ruin", "estimate",
            "killer"};

    public static void printInSec(long nanosec){
        System.out.println((float)nanosec/1000000000);
    }

    public static long folderSize(File directory) {
        long length = 0;
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isFile())
                length += file.length();
            else
                length += folderSize(file);
        }
        return length;
    }

    public static void writeIndex(String reviewsFilePath, String indexDir){
        IndexWriter writer = new IndexWriter();
        writer.write(reviewsFilePath, indexDir);
        System.out.print("Index size:");
        System.out.println(folderSize(new File(indexDir)));
    }

    public static void timeReader(String indexDir) {
        long startTime = System.nanoTime();
        IndexReader reader = new IndexReader(indexDir);
        for (String word : words) {
            reader.getReviewsWithToken(word);
        }
        long stopTime = System.nanoTime();
        System.out.print("100 getReviewsWithToken took:");
        printInSec(stopTime - startTime);

        startTime = System.nanoTime();
        for (String word : words) {
            reader.getTokenFrequency(word);
        }
        stopTime = System.nanoTime();
        System.out.print("100 getTokenFrequency took:");
        printInSec(stopTime - startTime);
    }

    public static void runIndex(String reviewsFilePath) {
        String indexDir =  "./src/index";
        long startTime = System.nanoTime();
        System.out.println("Starting Writing index");
        writeIndex(reviewsFilePath, indexDir);
        long stopTime = System.nanoTime();
        System.out.print("Done Writing index:");
        printInSec(stopTime - startTime);

        System.out.println("Starting Reading operations");
        timeReader(indexDir);
        System.out.println("Done Reading operations");
    }

    public static void main(String[] args) {
        String reviewsFilePathBase = "./datasets/counted/";
        int[] sizes = new int[]{100,1000,10000,50000,100000,200000,300000,500000};
//        int[] sizes = new int[]{50000};
        for (int size: sizes) {
            String reviewsFilePath = reviewsFilePathBase + size + ".txt";
            System.out.print("Starting Index run on index of size: ");
            System.out.println(size);
            runIndex(reviewsFilePath);
        }
        System.out.print("Starting Index run on Movies database:");
        String reviewsFilePath = "./datasets/full/Movies_&_TV.txt";
        runIndex(reviewsFilePath);


    }
}
