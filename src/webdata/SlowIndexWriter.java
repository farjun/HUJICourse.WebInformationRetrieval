package webdata;

import org.junit.jupiter.api.Assertions;
import webdata.encoders.ArithmeticEncoder;
import webdata.indexes.*;
import webdata.iostreams.AppInputStream;
import webdata.iostreams.AppOutputStream;
import webdata.iostreams.BitOutputStream;
import webdata.iostreams.BitRandomAccessInputStream;
import webdata.iterators.IndexValuesIterator;
import webdata.iterators.ReviewsIterator;
import webdata.models.*;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;

public class SlowIndexWriter {
    public static final int BATCH_SIZE = 10000;
    public static final int NUM_OF_BLOCKS_IN_EACH_SORT = 3;
    private BlockSizesFile productsBlockSizesFile;
    protected AppOutputStream productsOutputStream;

    protected AppOutputStream reviewsOutputStream;
    private BlockSizesFile reviewsBlockSizesFile;

    protected AppOutputStream wordsOutputStream;
    private BlockSizesFile wordsBlockSizesFile;
    private WordsBlockSizesFile mergeWordsBlockSizesFile;

    private ProductsIndex productsIndex;
    private ReviewsIndex reviewsIndex;
    private WordsIndex wordsIndex;

    private String wordsPath;
    private String productsPath;
    private BitOutputStream wordsMergedOutputStream;
    private BlockSizesFile productsMergedBlockSizesFile;
    private BitOutputStream productsMergedOutputStream;

    private FileWriter additionalInfoWriter;

    public void close(){
        try {
            this.productsOutputStream.close();
            this.reviewsOutputStream.close();
            this.wordsOutputStream.close();
            this.additionalInfoWriter.close();
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

    public void writeEncoded(IndexBlock[] blocksToEncode, AppOutputStream out, BlockSizesFile blockSizesFile,
                             boolean lastBatch) throws IOException {
        ArithmeticEncoder enc = new ArithmeticEncoder(out);
        for (int i = 0; i < blocksToEncode.length; i++) {
            String curBlockToEncode = blocksToEncode[i].block;
            char[] symbols = curBlockToEncode.toCharArray();
            for (int symbol: symbols) {
                enc.writeSymbol(symbol);
            }
            enc.writeSymbol('$');
            // Flush remaining code bits
            if(lastBatch && i == blocksToEncode.length - 1){ // last block of last batch
                enc.finish();
            }
            int numOfBytesWritten = out.setCheckpoint();
            enc = new ArithmeticEncoder(out);

            blockSizesFile.addBlockDetails(numOfBytesWritten,blocksToEncode[i].key );
        }

        if(lastBatch) {
            blockSizesFile.flush();
        }
    }

    private void writeAdditionalInfo() {
        try {
            File addInfoFile = new File("./src/index/additional_info");
            if(!addInfoFile.exists())
                addInfoFile.createNewFile();
            this.additionalInfoWriter = new FileWriter(addInfoFile);

            this.additionalInfoWriter.write(""+this.wordsIndex.getGlobalFreqSum()+"\n");
            this.additionalInfoWriter.write(""+this.reviewsIndex.getNumberOfReviews()+"\n");

            this.additionalInfoWriter.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void writeProcessed( boolean lastBatch ) throws IOException {
        writeEncoded(this.productsIndex.toStringBlocks(lastBatch), this.productsOutputStream, this.productsBlockSizesFile, lastBatch);
        writeEncoded(this.reviewsIndex.toStringBlocks(lastBatch), this.reviewsOutputStream, this.reviewsBlockSizesFile,lastBatch );
        writeEncoded(this.wordsIndex.toStringBlocks(lastBatch), this.wordsOutputStream, this.wordsBlockSizesFile, lastBatch);
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

            wordsPath = Paths.get(dirPath,"words").toString();
            String reviewsPath = Paths.get(dirPath,"reviews").toString();
            productsPath = Paths.get(dirPath,"products").toString();

            this.productsOutputStream = new BitOutputStream(new FileOutputStream(productsPath));
            this.productsMergedOutputStream = new BitOutputStream(new FileOutputStream(productsPath.concat("_sorted")));
            this.productsBlockSizesFile = new BlockSizesFile(new FileWriter(productsPath.concat("block_sizes")));
            this.productsMergedBlockSizesFile = new BlockSizesFile(new FileWriter(productsPath.concat("block_sizes_merge")));
            this.productsIndex = new ProductsIndex();

            this.reviewsOutputStream = new BitOutputStream(new FileOutputStream(reviewsPath));
            this.reviewsBlockSizesFile = new BlockSizesFile(new FileWriter(reviewsPath.concat("block_sizes")));
            this.reviewsIndex = new ReviewsIndex();

            this.wordsOutputStream =  new BitOutputStream(new FileOutputStream(wordsPath));
            this.wordsMergedOutputStream =  new BitOutputStream(new FileOutputStream(wordsPath.concat("_sorted")));
            this.wordsBlockSizesFile = new BlockSizesFile(new FileWriter(wordsPath.concat("block_sizes")));
            this.mergeWordsBlockSizesFile = new WordsBlockSizesFile(new FileWriter(wordsPath.concat("block_sizes_merge")));
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
            this.writeAdditionalInfo();
            this.close();

            this.clearIndexesFromRAM();
            this.sort();
            this.clearRedundantFiles(); // AFTER sort

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void clearIndexesFromRAM(){
        this.wordsIndex = new WordsIndex(); // TODO can be optimized to not creating new indexes
        this.productsIndex = new ProductsIndex();
        this.reviewsIndex = new ReviewsIndex();
    }

    public void clearRedundantFiles(){
        File[] fs = new File[4];
        fs[0] = new File(wordsPath.concat("block_sizes"));
        fs[1] = new File(wordsPath);
        fs[2] = new File(productsPath.concat("block_sizes"));
        fs[3] = new File(productsPath);
        for(File f:fs){
            if(!f.exists()) continue;
            f.delete();
        }
    }

    public void sort(){
        try {
            writeSorted(wordsBlockSizesFile, wordsPath,  WordsIndex.NUM_OF_ENTRIES_IN_BLOCK/2, wordsIndex,
                    wordsMergedOutputStream, mergeWordsBlockSizesFile);
            writeSorted(productsBlockSizesFile, productsPath,  ProductsIndex.NUM_OF_PRODUCTS_IN_BLOCK/2, productsIndex,
                    productsMergedOutputStream, productsMergedBlockSizesFile);
        }
        catch (IOException e){
            e.printStackTrace();
            System.err.println(e);
        }
    }


    private void writeSorted(BlockSizesFile bsf, String inputPath,int blockSize, Index index, BitOutputStream mergedOutputStream, BlockSizesFile mergedBsf) throws IOException {
        ArrayList<Integer> blockSizes = bsf.getBlockSizes();
        IndexValuesIterator[] iterators = new IndexValuesIterator[blockSizes.size()]; //wordsInp, wordsBlockSizesFile,
        for(int i=0;i<iterators.length;i++){
            var wordsInp = new BitRandomAccessInputStream(new File(inputPath), blockSizes);
            iterators[i] = new IndexValuesIterator<>(index, wordsInp, index.separator, 3, i);
        }

        Merger merger = new Merger(iterators, index.separator, blockSize, blockSizes.size());
        int iterations = 0;
        while(merger.hasMoreInput()){
            try {
                IndexBlock[] blocks = merger.getSortedBlocks(NUM_OF_BLOCKS_IN_EACH_SORT);
                writeEncoded(blocks, mergedOutputStream, mergedBsf, !merger.hasMoreInput());
                iterations++;
                System.out.println(String.format("Sorted %s blocks out of %s blocks for input = %s index",
                        iterations*NUM_OF_BLOCKS_IN_EACH_SORT, blockSizes.size(), inputPath));
            }catch (Exception e){
                System.out.println("Sort error");
                System.out.println(e.toString());
                e.printStackTrace();
            }
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
            Integer elem = iterable.nextElement();
            if( elem == val){
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        String indexDir =  "./src/index";
        String reviewsFilePath = "./datasets/full/foods_partial.txt";
        SlowIndexWriter writer = new SlowIndexWriter();
        writer.slowWrite(reviewsFilePath, indexDir);
        writer.clearIndexesFromRAM();
//        writer.sort();
        IndexReader reader = new IndexReader(indexDir);
        for (int i = 1; i <= 100; i++) {
            String productId = reader.getProductId(i);
            Enumeration<Integer> reviewIds = reader.getProductReviews(productId);
            Assertions.assertTrue(enumerationContains(reviewIds, i), "checking review "+i+"");
        }

        var r = reader.getTokenCollectionFrequency("the");

    }
}
