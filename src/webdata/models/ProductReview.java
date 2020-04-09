package webdata.models;

public class ProductReview {
    public final String productId;
    public final String userId;
    public final String profileName;
    public final String helpfulness;
    public final String score;
    public final String time;
    public final String summary;
    public final String text;

    public ProductReview(String productId, String userId, String profileName, String helpfulness, String score, String time, String summary, String text ){

        this.productId = productId.substring("product/productId: ".length());
        this.userId = userId.substring("review/userId: ".length());;
        this.profileName = profileName.substring("review/profileName: ".length());;
        this.helpfulness = helpfulness.substring("review/helpfulness: ".length());;
        this.score = score.substring("review/score: ".length());;
        this.time = time.substring("review/time: ".length());;
        this.summary = summary.substring("review/summary: ".length());;
        this.text = text.substring("review/text: ".length());;
    }

    @Override
    public String toString() {
        return "ProductReview{" +
                "productId='" + productId + '\'' +
                ", userId='" + userId + '\'' +
                ", profileName='" + profileName + '\'' +
                ", helpfulness='" + helpfulness + '\'' +
                ", score='" + score + '\'' +
                ", time='" + time + '\'' +
                ", summary='" + summary + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
