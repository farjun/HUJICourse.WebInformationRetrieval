import webdata.SlowIndexWriter;

public class ReviewStore {




    public static void main(String[] args) {
        String reviewsFilePath = "./src/datasets/100.txt";
        SlowIndexWriter writer = new SlowIndexWriter();
        writer.slowWrite(reviewsFilePath, "./src/index");
    }
}
