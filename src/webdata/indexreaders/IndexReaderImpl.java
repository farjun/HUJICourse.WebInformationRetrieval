package webdata.indexreaders;

import webdata.encoders.ArithmicDecoder;
import webdata.indexes.ProductsIndex;
import webdata.indexes.ReviewsIndex;
import webdata.indexes.WordsIndex;
import webdata.iostreams.AppInputStream;
import webdata.iostreams.BitInputStream;
import webdata.iostreams.BitOutputStream;
import webdata.models.SymbolFreqTable;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;

public class IndexReaderImpl {
    protected AppInputStream productsInputStream;
    protected AppInputStream reviewsInputStream;
    protected AppInputStream wordsInputStream;
    protected final int DEFAULT_NUM_SYMBOLS = 257;
    private ProductsIndex productsIndex;
    private ReviewsIndex reviewsIndex;
    private WordsIndex wordsIndex;

    public IndexReaderImpl(AppInputStream productsInputStream, AppInputStream reviewsInputStream,
                       AppInputStream wordsInputStream) {
        this.productsInputStream = productsInputStream;
        this.reviewsInputStream = reviewsInputStream;
        this.wordsInputStream = wordsInputStream;
        this.reviewsIndex = new ReviewsIndex();
        this.wordsIndex = new WordsIndex();
        this.productsIndex = new ProductsIndex();
    }

    public IndexReaderImpl(String productFilePath, String reviewsFilePath, String wordsFilePath) throws IOException {
        this(   new BitInputStream(new FileInputStream(productFilePath)),
                new BitInputStream(new FileInputStream(reviewsFilePath)),
                new BitInputStream(new FileInputStream(wordsFilePath)));
    }

    public StringBuffer decode(AppInputStream inputStream, int numSymbols) throws IOException {
        SymbolFreqTable freqs = new SymbolFreqTable(numSymbols);
        ArithmicDecoder dec = new ArithmicDecoder(inputStream);
        StringBuffer sb = new StringBuffer();

        while (true) {
            // Decode and write one byte
            int symbol = dec.read(freqs);
            if (symbol == 256)  // EOF symbol
                break;

            sb.append((char)symbol);
            freqs.increment(symbol);
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


    public Enumeration<Integer> getReviewsByProductId(String productId){
        if (this.productsIndex.contains(productId)) {
            return this.productsIndex.get(productId);
        }
        else{
            return Collections.enumeration(Collections.emptyList());
        }
    }


    public void loadIndex() throws IOException {
        var sb = this.decode(this.reviewsInputStream, this.DEFAULT_NUM_SYMBOLS);
        this.reviewsIndex = new ReviewsIndex(sb.toString());
        sb = this.decode(this.productsInputStream, this.DEFAULT_NUM_SYMBOLS);
        this.productsIndex = new ProductsIndex(sb.toString());
        sb = this.decode(this.wordsInputStream, this.DEFAULT_NUM_SYMBOLS);
        this.wordsIndex = new WordsIndex(sb.toString());
    }

    public int getReviewScore(int reviewId) {
        int[] ret = this.reviewsIndex.get(reviewId);
        if(ret.length == 1){
            return -1;
        }
        return ret[ReviewsIndex.SCORE];
    }

    public int getReviewHelpfulnessNumerator(int reviewId) {
        int[] ret = this.reviewsIndex.get(reviewId);
        if(ret.length == 1){
            return -1;
        }
        return ret[ReviewsIndex.HELPFULLNESS_NUMERATOR];
    }


    public int getReviewHelpfulnessDenominator(int reviewId) {
        int[] ret = this.reviewsIndex.get(reviewId);
        if(ret.length == 1){
            return -1;
        }
        return ret[ReviewsIndex.HELPFULLNESS_DENUMERATOR];
    }


    public int getReviewLength(int reviewId) {
        int[] ret = this.reviewsIndex.get(reviewId);
        if(ret.length == 1){
            return -1;
        }
        return ret[ReviewsIndex.LENGHT];
    }

    public int getNumberOfReviews() {
        return this.reviewsIndex.getNumberOfReviews();
    }

    public Enumeration<Integer> getReviewsWithToken(String token){
        return this.wordsIndex.getReviewsWithToken(token);
    }

    public int getTokenCollectionFrequency(String token) {
        return this.wordsIndex.tokenGlobalFreq.getOrDefault(token,0);
    }

    public int getTokenFrequency(String token) {
        return this.wordsIndex.tokenFreq.size();
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