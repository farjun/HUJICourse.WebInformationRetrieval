package webdata.models;

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.nio.ByteBuffer;
import java.util.stream.Collectors;

public class ProductReview implements Serializable {
    private int id;

    public final String productId;
    public final String userId;
    public final String profileName;
    public int helpfulnessNumerator;
    public int helpfulnessDenominator;
    public short score;
    public final String time;
    public final String summary;
    public final String text;
    public int length;
    private HashMap<String, Integer> tokenStats;

    private ArrayList<String> tokens;

    // Sizes. fixed for now
    private final int REVIEW_ID = Integer.BYTES;
    private final int SCORE = Short.BYTES;
    private final int HELPFUL_NUM = Integer.BYTES;
    private final int HELPFUL_DEN = Integer.BYTES;
    private final int LENGTH = Integer.BYTES;
    private final int TOTAL_SIZE = REVIEW_ID + SCORE + HELPFUL_NUM + HELPFUL_DEN + LENGTH;
    public ProductReview(int id, String productId, String userId, String profileName, String helpfulness, String score,
                         String time, String summary, String text ){
        this.id = id;
        this.productId = productId.substring("product/productId: ".length());
        this.userId = userId.substring("review/userId: ".length());
        this.profileName = profileName.substring("review/profileName: ".length());
        String[] helpfulnessSplit = helpfulness.substring("review/helpfulness: ".length()).split("/");
        this.helpfulnessNumerator = Integer.parseInt(helpfulnessSplit[0]);
        this.helpfulnessDenominator = Integer.parseInt(helpfulnessSplit[1]);

        this.score = Short.parseShort(score.substring("review/score: ".length()).split(".0")[0]); // based on ex1, score is int 1-5
        this.time = time.substring("review/time: ".length());
        this.summary = summary.substring("review/summary: ".length());
        this.text = text.substring("review/text:".length());

        this.tokenStats = new HashMap<String, Integer>();
        var tokensList = this.text.toLowerCase().split("[^a-zA-Z0-9]+");
        this.tokens = new ArrayList<String>(Arrays.asList(tokensList).stream().filter(str -> !str.isEmpty()).
                collect(Collectors.toList()));
        this.length = this.tokens.size();
        for(var token: this.tokens){
            if(token.length()==0){
                continue;
            }
            var count = this.tokenStats.getOrDefault(token, 0);
            this.tokenStats.put(token, count+1);
        }
    }

    public HashMap<String, Integer> getTokenStats() {
        return tokenStats;
    }

    public int getId() {
        return id;
    }

    public String getStringId() {
        return String.valueOf(id);
    }

    @Override
    public String toString() {
        return "ProductReview{" +
                "id='" + id + '\'' +
                "productId='" + productId + '\'' +
                ", userId='" + userId + '\'' +
                ", profileName='" + profileName + '\'' +
                ", helpfulness='" + helpfulnessNumerator+"/"+helpfulnessDenominator+ '\'' +
                ", score='" + score + '\'' +
                ", time='" + time + '\'' +
                ", summary='" + summary + '\'' +
                ", text='" + text + '\'' +
                ", length='" + length + '\'' +
                '}';
    }


    private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException
    {
        byte[] entryBytesArr = aInputStream.readAllBytes();
        ByteBuffer entryBuffer = ByteBuffer.wrap(entryBytesArr);
        entryBuffer.rewind();
        this.id = entryBuffer.getInt();
        this.score = entryBuffer.getShort();
        this.helpfulnessNumerator = entryBuffer.getInt();
        this.helpfulnessDenominator = entryBuffer.getInt();
        this.length = entryBuffer.getInt();
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws IOException
    {
        // TODO: use for encoder performance comparison
        // fixed len for now. it will then use a compression method
        ByteBuffer entryBuffer = ByteBuffer.allocate(this.TOTAL_SIZE);
        entryBuffer.putInt(this.id);
        entryBuffer.putShort(this.score);
        entryBuffer.putInt(this.helpfulnessNumerator);
        entryBuffer.putInt(this.helpfulnessDenominator);
        entryBuffer.putInt(this.length);
        aOutputStream.write(entryBuffer.array());
    }
}
