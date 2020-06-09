package webdata.indexes;

import webdata.iostreams.BitRandomAccessInputStream;
import webdata.iostreams.OutOfBlocksException;

import java.io.*;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;

public class IndexReaderImpl {
    private final BlockSizesFile wordsBsf;
    private final BlockSizesFile reviewsBsf;
    private String indexDirPath;
    private final BlockSizesFile productsBsf;
    protected BitRandomAccessInputStream productsInputStream;
    protected BitRandomAccessInputStream reviewsInputStream;
    protected BitRandomAccessInputStream wordsInputStream;
    private ProductsIndex productsIndex;
    private ReviewsIndex reviewsIndex;
    private WordsIndex wordsIndex;
    private FileReader additionalInfoReader;

    public IndexReaderImpl(String indexDirPath, String productFilePath, String reviewsFilePath, String wordsFilePath) throws IOException {
        this.indexDirPath = indexDirPath;

        productsBsf = new BlockSizesFile(new FileReader(productFilePath.concat("block_sizes_merge")));
        this.productsInputStream = new BitRandomAccessInputStream(new File(productFilePath.concat("_sorted")), productsBsf.getBlockSizes());

        reviewsBsf = new BlockSizesFile(new FileReader(reviewsFilePath.concat("block_sizes")));
        this.reviewsInputStream = new BitRandomAccessInputStream(new File(reviewsFilePath), reviewsBsf.getBlockSizes());

        wordsBsf = new BlockSizesFile(new FileReader(wordsFilePath.concat("block_sizes_merge")));
        this.wordsInputStream = new BitRandomAccessInputStream(new File(wordsFilePath.concat("_sorted")), wordsBsf.getBlockSizes());


        this.additionalInfoReader = new FileReader(Paths.get(this.indexDirPath,"additional_info").toString());

        this.reviewsIndex = new ReviewsIndex();
        this.wordsIndex = new WordsIndex();
        this.productsIndex = new ProductsIndex();

        this.readAdditionalInfo();
    }

    private void readAdditionalInfo() {
        try {
            File addInfoFile = new File(Paths.get(this.indexDirPath,"additional_info").toString());
            if(!addInfoFile.exists()) {
                wordsIndex.setGlobalFreqSum(0);
                reviewsIndex.setNumOfReviews(0);
                return;
            }
            additionalInfoReader = new FileReader(addInfoFile);
            int ch;
            StringBuilder sb = new StringBuilder();
            while((ch=additionalInfoReader.read())!=-1)
                sb.append((char)ch);
            String[] lines = sb.toString().split("\n");
            wordsIndex.setGlobalFreqSum(Integer.parseInt(lines[0]));
            reviewsIndex.setNumOfReviews(Integer.parseInt(lines[1]));
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /*API Section*/
    public Enumeration<Integer> getProductReviews(String productId){
        try{
            this.productsIndex.loadBlock(this.productsInputStream, productsBsf.searchByToken(productId));
            if (this.productsIndex.contains(productId)) {
                return this.productsIndex.get(productId);
            }
            else{
                return Collections.enumeration(Collections.emptyList());
            }
        }catch (OutOfBlocksException e){
            return null;
        }catch (IOException e){
            System.err.println("Exception was raised in getProductReviews");
            return null;
        }
    }

    public int getReviewScore(int reviewId) {
        try{
            this.reviewsIndex.loadBlock(this.reviewsInputStream, this.reviewsIndex.getBlockNum(reviewId));
        }catch (OutOfBlocksException e){
            return -1;
        }catch (IOException e){
            System.err.println("Exception was raised in getProductReviews");
        }

        int[] ret = this.reviewsIndex.getReviewNums(reviewId);
        if(ret.length == 1){
            return -1;
        }
        return ret[ReviewsIndex.SCORE];
    }

    public int getReviewHelpfulnessNumerator(int reviewId) {
        try {
            this.reviewsIndex.loadBlock(this.reviewsInputStream, this.reviewsIndex.getBlockNum(reviewId));
        }catch (OutOfBlocksException e){
            return -1;
        }catch (IOException e){
            System.err.println("Exception was raised in getProductReviews");
        }

        int[] ret = this.reviewsIndex.getReviewNums(reviewId);
        if(ret.length == 1){
            return -1;
        }
        return ret[ReviewsIndex.HELPFULLNESS_NUMERATOR];
    }


    public int getReviewHelpfulnessDenominator(int reviewId) {
        try {
            this.reviewsIndex.loadBlock(this.reviewsInputStream, this.reviewsIndex.getBlockNum(reviewId));
        } catch (OutOfBlocksException e){
            return -1;
        } catch (IOException e){
            System.err.println("Exception was raised in getProductReviews");
        }

        int[] ret = this.reviewsIndex.getReviewNums(reviewId);
        if(ret.length == 1){
            return -1;
        }
        return ret[ReviewsIndex.HELPFULLNESS_DENUMERATOR];
    }

    public String getProductId(int reviewId) {
        try {
            this.reviewsIndex.loadBlock(this.reviewsInputStream, this.reviewsIndex.getBlockNum(reviewId));
        }catch (OutOfBlocksException e){
            return null;
        }catch (IOException e){
            System.err.println("Exception was raised in getProductReviews");
        }
        return this.reviewsIndex.getProductId(reviewId);
    }

    public int getReviewLength(int reviewId) {
        try {
            this.reviewsIndex.loadBlock(this.reviewsInputStream, this.reviewsIndex.getBlockNum(reviewId));
        } catch (OutOfBlocksException e){
            return -1;
        } catch (IOException e){
            System.err.println("Exception was raised in getProductReviews");
        }
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
        try {
            this.wordsIndex.loadBlock(this.wordsInputStream, this.wordsBsf.searchByToken(token.toLowerCase()));
        }catch (OutOfBlocksException e){
            return Collections.enumeration(Collections.emptyList());
        }catch (IOException e){
            System.err.println("Exception was raised in getReviewsWithToken");
        }
        return this.wordsIndex.getReviewsWithToken(token.toLowerCase());
    }

    public int getTokenCollectionFrequency(String token) {
        try {
            this.wordsIndex.loadBlock(this.wordsInputStream, this.wordsBsf.searchByToken(token.toLowerCase()));
        }catch (OutOfBlocksException e){
            return -1;
        }catch (IOException e){
            System.err.println("Exception was raised in getTokenCollectionFrequency");
        }
        return this.wordsIndex.tokenGlobalFreq.getOrDefault(token.toLowerCase(),0);
    }

    public int getTokenFrequency(String token) {
        try {
            this.wordsIndex.loadBlock(this.wordsInputStream, this.wordsBsf.searchByToken(token.toLowerCase()));
        }catch (OutOfBlocksException e){
            return -1;
        }catch (IOException e){
            System.err.println("Exception was raised in getTokenFrequency");
        }
        if(!this.wordsIndex.tokenFreq.containsKey(token.toLowerCase())) return 0;
        return this.wordsIndex.tokenFreq.get(token.toLowerCase()).size();
    }

    public int getTokenSizeOfReviews() {
        return this.wordsIndex.getGlobalFreqSum();
//        if(this.wordsIndex.tokenGlobalFreq.isEmpty()) return 0;
//        return this.wordsIndex.tokenGlobalFreq
//                .values()
//                .stream()
//                .mapToInt(Integer::valueOf)
//                .sum();

    }

}
