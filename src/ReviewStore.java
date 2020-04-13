import webdata.SlowIndexWriter;

//to manually test serialization
import webdata.models.ProductReview;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ReviewStore {

    public static void main(String[] args) {
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
