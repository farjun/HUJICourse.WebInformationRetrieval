package webdata;

import org.junit.jupiter.api.Assertions;
import webdata.encoders.ArithmeticEncoder;
import webdata.indexes.BlockSizesFile;
import webdata.indexes.ProductsIndex;
import webdata.indexes.ReviewsIndex;
import webdata.indexes.WordsIndex;
import webdata.iostreams.AppOutputStream;
import webdata.iostreams.BitOutputStream;
import webdata.iterators.ReviewsIterator;
import webdata.models.ProductReview;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Enumeration;

public class SlowIndexWriter {
    public static final int BATCH_SIZE = 500;
    private BlockSizesFile productsBlockSizesFile;
    protected AppOutputStream productsOutputStream;

    protected AppOutputStream reviewsOutputStream;
    private BlockSizesFile reviewsBlockSizesFile;

    protected AppOutputStream wordsOutputStream;
    private ProductsIndex productsIndex;
    private ReviewsIndex reviewsIndex;
    private WordsIndex wordsIndex;

    public void close(){
        try {
            this.productsOutputStream.close();
            this.reviewsOutputStream.close();
            this.wordsOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void process(ProductReview review) {
        var reviewIndexInp = review.score + "," +
                review.helpfulnessNumerator + "," +
                review.helpfulnessDenominator + "," +
                review.length + "," +
                review.productId;
        this.reviewsIndex.insert(reviewIndexInp);
        this.wordsIndex.insert(review);
        this.productsIndex.insert(review.productId, review.getStringId());
    }

    public void writeEncoded(String toEncode, AppOutputStream out) throws IOException {
        ArithmeticEncoder enc = new ArithmeticEncoder(out);
        for (int symbol: toEncode.toCharArray()) {
            enc.writeSymbol(symbol);
        }
        enc.finish();  // Flush remaining code bits
    }

    public void writeEncoded(String[] blocksToEncode, AppOutputStream out, BlockSizesFile blockSizesFile) throws IOException {
        ArithmeticEncoder enc = new ArithmeticEncoder(out);
        for (String curBlockToEncode: blocksToEncode ) {
            for (int symbol: curBlockToEncode.toCharArray()) {
                enc.writeSymbol(symbol);
            }
            int numOfBytesWritten = out.setCheckpoint();
            enc = new ArithmeticEncoder(out);
            blockSizesFile.addBlockSize(numOfBytesWritten);
        }
        // Flush remaining code bits
        enc.finish();
        blockSizesFile.flush();
        out.flush();
    }

    public void writeProcessed( boolean lastBatch ) throws IOException {
        writeEncoded(this.productsIndex.toStringBlocks(lastBatch), this.productsOutputStream, this.productsBlockSizesFile);
        writeEncoded(this.reviewsIndex.toStringBlocks(lastBatch), this.reviewsOutputStream, this.reviewsBlockSizesFile );
        writeEncoded(this.wordsIndex.toString(), this.wordsOutputStream);

    }


    /**
     * creats the dir and files for the index
     * @param dirPath the dir to create the index files in
     */
    private void setWriters(String dirPath){
        File directory = new File(dirPath);
        try {
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    throw new IOException("directories were not created");
                }
            }

            var wordsPath = Paths.get(dirPath,"words").toString();
            var reviewsPath = Paths.get(dirPath,"reviews").toString();
            var productsPath = Paths.get(dirPath,"products").toString();

            this.productsOutputStream = new BitOutputStream(new FileOutputStream(productsPath));
            this.productsIndex = new ProductsIndex();
            this.productsBlockSizesFile = new BlockSizesFile(new FileWriter(productsPath.concat("block_sizes")));

            this.reviewsOutputStream = new BitOutputStream(new FileOutputStream(reviewsPath));
            this.reviewsBlockSizesFile = new BlockSizesFile(new FileWriter(reviewsPath.concat("block_sizes")));
            this.reviewsIndex = new ReviewsIndex();

            this.wordsOutputStream =  new BitOutputStream(new FileOutputStream(wordsPath));
            this.wordsIndex = new WordsIndex();

        } catch (IOException e) {
            this.close();
            e.printStackTrace();
            System.err.println("IO Exception in Slow index writer");
        }
    }


    /**
     * Given product review data, creates an on disk index
     * inputFile is the path to the file containing the review data
     * dir is the directory in which all index files will be created
     * if the directory does not exist, it should be created
     */
    public void slowWrite(String inputFile, String dir) {
        this.setWriters(dir);
        ReviewsIterator iter = new ReviewsIterator(inputFile);
        try {
            int curIteration = 1;
            long batchNumber = 0;
            while(iter.hasNext()) {
                if(curIteration >= BATCH_SIZE){
                    this.writeProcessed(false);
                    curIteration = 1;
                    batchNumber++;
                    System.out.println("Batch number: " + String.valueOf(batchNumber) + " done");
                    System.out.println("total reviews processed: " + String.valueOf(batchNumber * BATCH_SIZE) );
                }
                ProductReview review = iter.next();
                this.process(review);
                curIteration++;
            }
            this.writeProcessed(true);
            this.close();

        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    /**
     * Delete all index files by removing the given directory
     */
    public void removeIndex(String dir) {
        File directory = new File(dir);
        if(!directory.exists()) return;
        var files = directory.listFiles();
        if(files.length == 0){
            directory.delete();
        }
        for(var f: files){
            f.delete();
        }
        directory.delete();
    }

    private static boolean enumerationContains(Enumeration<Integer> iterable, int val){
        while(iterable.hasMoreElements()){
            if( iterable.nextElement() == val){
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        String indexDir =  "./src/index";
        String reviewsFilePath = "./datasets/1000.txt";
        SlowIndexWriter writer = new SlowIndexWriter();
        writer.slowWrite(reviewsFilePath, indexDir);
        IndexReader reader = new IndexReader(indexDir);
        for (int i = 1; i <= 100; i++) {
            String productId = reader.getProductId(i);
            Enumeration<Integer> reviewIds = reader.getProductReviews("B00067AD4U");
            Assertions.assertTrue(enumerationContains(reviewIds, i), "checking review "+i+"");
        }

    }
}
