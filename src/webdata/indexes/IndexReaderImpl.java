package webdata.indexes;

import webdata.encoders.ArithmeticDecoder;
import webdata.iostreams.AppInputStream;
import webdata.iostreams.BitInputStream;
import webdata.iostreams.BitRandomAccessInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;

import static webdata.encoders.BitUtils.END_OF_FILE;

public class IndexReaderImpl {
    protected BitRandomAccessInputStream productsInputStream;
    protected AppInputStream reviewsInputStream;
    protected AppInputStream wordsInputStream;
    private ProductsIndex productsIndex;
    private ReviewsIndex reviewsIndex;
    private WordsIndex wordsIndex;

    public IndexReaderImpl(String productFilePath, String reviewsFilePath, String wordsFilePath) throws IOException {
        BlockSizesFile bsf = new BlockSizesFile(new FileReader(productFilePath.concat("block_sizes")));
        this.productsInputStream = new BitRandomAccessInputStream(new File(productFilePath), bsf.getBlockSizes());
        this.reviewsInputStream = new BitInputStream(new FileInputStream(reviewsFilePath));
        this.wordsInputStream = new BitInputStream(new FileInputStream(wordsFilePath));
        this.reviewsIndex = new ReviewsIndex();
        this.wordsIndex = new WordsIndex();
        this.productsIndex = new ProductsIndex();
    }

    public StringBuffer decode(AppInputStream inputStream) throws IOException {
        ArithmeticDecoder dec = new ArithmeticDecoder(inputStream);
        StringBuffer sb = new StringBuffer();

        while (true) {
            // Decode and write one byte
            int symbol = dec.read();
            if (symbol == 256)  // EOF symbol
                break;

            sb.append((char)symbol);
        }
        return sb;
    }

    public StringBuffer decodeBlock(BitRandomAccessInputStream inputStream, int blockNum) throws IOException {
        inputStream.setPointerToBlock(blockNum);
        ArithmeticDecoder dec = new ArithmeticDecoder(inputStream);
        StringBuffer sb = new StringBuffer();

        while (true) {
            // Decode and write one byte
            int symbol = dec.read();
            if (symbol == END_OF_FILE)  // EOF symbol
                break;

            sb.append((char)symbol);
        }
        return sb;
    }

    public void close(){
        try {
            this.productsInputStream.close();
            this.wordsInputStream.close();
            this.reviewsInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void loadIndex() throws IOException {
//        var sb = this.decode(this.reviewsInputStream);
//        this.reviewsIndex = new ReviewsIndex(sb.toString());
//        sb = this.decode(this.productsInputStream);
//        this.productsIndex = new ProductsIndex(sb.toString());
//        sb = this.decode(this.wordsInputStream);
//        this.wordsIndex = new WordsIndex(sb.toString());
//    }

    public void loadBlock(BitRandomAccessInputStream inputStream, Index index, int blockNum) {
        try{
            StringBuffer sb = this.decodeBlock(inputStream, blockNum);
            index.loadData(sb.toString());

        }catch (IOException e){
            System.err.println("could not load block!");
            e.printStackTrace();
        }
    }

    /*API Section*/

    public Enumeration<Integer> getProductReviews(String productId){

        this.loadBlock(this.productsInputStream, this.productsIndex, 1);
        if (this.productsIndex.contains(productId)) {
            return this.productsIndex.get(productId);
        }
        else{
            return Collections.enumeration(Collections.emptyList());
        }
    }

    public int getReviewScore(int reviewId) {
        int[] ret = this.reviewsIndex.getReviewNums(reviewId);
        if(ret.length == 1){
            return -1;
        }
        return ret[ReviewsIndex.SCORE];
    }

    public int getReviewHelpfulnessNumerator(int reviewId) {
        int[] ret = this.reviewsIndex.getReviewNums(reviewId);
        if(ret.length == 1){
            return -1;
        }
        return ret[ReviewsIndex.HELPFULLNESS_NUMERATOR];
    }


    public int getReviewHelpfulnessDenominator(int reviewId) {
        int[] ret = this.reviewsIndex.getReviewNums(reviewId);
        if(ret.length == 1){
            return -1;
        }
        return ret[ReviewsIndex.HELPFULLNESS_DENUMERATOR];
    }

    public String getProductId(int reviewId) {
        return this.reviewsIndex.getProductId(reviewId);
    }

    public int getReviewLength(int reviewId) {
        int[] ret = this.reviewsIndex.getReviewNums(reviewId);
        if(ret.length == 1){
            return -1;
        }
        return ret[ReviewsIndex.LENGHT];
    }

    public int getNumberOfReviews() {
        return this.reviewsIndex.getNumberOfReviews();
    }

    public Enumeration<Integer> getReviewsWithToken(String token){
        return this.wordsIndex.getReviewsWithToken(token.toLowerCase());
    }

    public int getTokenCollectionFrequency(String token) {
        return this.wordsIndex.tokenGlobalFreq.getOrDefault(token.toLowerCase(),0);
    }

    public int getTokenFrequency(String token) {
        if(!this.wordsIndex.tokenFreq.containsKey(token.toLowerCase())) return 0;
        return this.wordsIndex.tokenFreq.get(token.toLowerCase()).size();
    }

    public int getTokenSizeOfReviews() {
        if(this.wordsIndex.tokenGlobalFreq.isEmpty()) return 0;
        return this.wordsIndex.tokenGlobalFreq
                .values()
                .stream()
                .mapToInt(Integer::valueOf)
                .sum();
    }

}
