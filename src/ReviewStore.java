import webdata.IndexReader;
import webdata.iostreams.BitInputStream;
import webdata.SlowIndexWriter;

//to manually test serialization
import webdata.encoders.ArithmicDecoder;
import webdata.encoders.ArithmicEncoder;
import webdata.encoders.ConcatEncoder;
import webdata.encoders.ConcatDecoder;
import webdata.iostreams.BitOutputStream;
import webdata.models.ProductReview;

import java.io.*;

public class ReviewStore {

    public static void main(String[] args) throws Exception {
        run();
//        runConcatEncoder();
//        runArithmicCompressOnEntireFile();
    }

    private static void runArithmicCompressOnEntireFile() throws IOException {
        File inputFile = new File("src\\datasets\\1000.txt");
        File encodedFile = new File("src\\datasets\\1000-encoded.txt");
        File decodedFile = new File("src\\datasets\\1000-decoded.txt");
        

        // Perform file compression
        try (InputStream in = new BufferedInputStream(new FileInputStream(inputFile));
             BitOutputStream out = new BitOutputStream(new BufferedOutputStream(new FileOutputStream(encodedFile)))) {
            ArithmicEncoder.writeEncoded(in, out);
        }

        // Perform file decompression
        try (BitInputStream in = new BitInputStream(new BufferedInputStream(new FileInputStream(encodedFile)));
             OutputStream out = new BufferedOutputStream(new FileOutputStream(decodedFile))) {
            ArithmicDecoder.decompress(in, out);
        }
    }

    private static void run() {
        String reviewsFilePath = "./src/datasets/1000.txt";
        String indexDir =  "./src/index";
        SlowIndexWriter writer = new SlowIndexWriter();
        writer.slowWrite(reviewsFilePath,indexDir);

        IndexReader reader = new IndexReader(indexDir);


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
