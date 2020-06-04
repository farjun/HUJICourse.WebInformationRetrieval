package webdata;

import webdata.encoders.ArithmeticEncoder;
import webdata.indexes.*;
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
    public static final int BATCH_SIZE = 500;
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
    private String sortedWordsPath;
    private String productsPath;
    private String sortedProductsPath;

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
//
//    public void writeEncoded(String toEncode, AppOutputStream out, boolean lastBatch) throws IOException {
//        ArithmeticEncoder enc = new ArithmeticEncoder(out);
//        for (int symbol: toEncode.toCharArray()) {
//            enc.writeSymbol(symbol);
//        }
//        // Flush remaining code bits
//        if(lastBatch){
//            enc.finish();
//            out.flush();
//        }
//    }

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
            sortedWordsPath = wordsPath.concat("_sorted");
            var reviewsPath = Paths.get(dirPath,"reviews").toString();
            productsPath = Paths.get(dirPath,"products").toString();
            sortedProductsPath = productsPath.concat("_sorted");

            this.productsOutputStream = new BitOutputStream(new FileOutputStream(productsPath));
            this.productsIndex = new ProductsIndex();
            this.productsBlockSizesFile = new BlockSizesFile(new FileWriter(productsPath.concat("block_sizes")));

            this.reviewsOutputStream = new BitOutputStream(new FileOutputStream(reviewsPath));
            this.reviewsBlockSizesFile = new BlockSizesFile(new FileWriter(reviewsPath.concat("block_sizes")));
            this.reviewsIndex = new ReviewsIndex();

            this.wordsOutputStream =  new BitOutputStream(new FileOutputStream(wordsPath));
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
            this.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sort(){
        try {
//            var wordsOut = new BitOutputStream(new FileOutputStream(this.sortedWordsPath));
            ArrayList<Integer> wordsBlockSizes = wordsBlockSizesFile.getBlockSizes();
            IndexValuesIterator<SortableNodeWords>[] iterators = new IndexValuesIterator[wordsBlockSizes.size()]; //wordsInp, wordsBlockSizesFile,
            for(int i=0;i<iterators.length;i++){
                var wordsInp = new BitRandomAccessInputStream(new File(wordsPath), wordsBlockSizes);

                iterators[i] = new IndexValuesIterator<SortableNodeWords>(wordsIndex, wordsInp, wordsIndex.separator,
                        WordsIndex.NUM_OF_ENTRIES_IN_BLOCK, i);
            }
            Merger wordsMerger = new Merger(iterators, wordsIndex.separator, WordsIndex.NUM_OF_ENTRIES_IN_BLOCK,
                    wordsBlockSizes.size());
            IndexBlock[] res;
            while((res=wordsMerger.getSortedBlock())!=null){
                for(var e:res) System.out.print(e.block);
                System.out.println();

//                writeEncoded(res, this.wordsOutputStream, this.mergeWordsBlockSizesFile, false); //TODO check regarding last batch
            }

            System.out.println();
            System.out.println();
            System.out.println();

            ArrayList<Integer> productsBlockSizes = productsBlockSizesFile.getBlockSizes();
            IndexValuesIterator<SortableNodeProducts>[] productsIterators = new IndexValuesIterator[productsBlockSizes.size()]; //productsInp, wordsBlockSizesFile,
            for(int i=0;i<productsIterators.length;i++){
                var productsInp = new BitRandomAccessInputStream(new File(productsPath), productsBlockSizes);

                productsIterators[i] = new IndexValuesIterator<SortableNodeProducts>(productsIndex, productsInp, productsIndex.separator,
                        ProductsIndex.NUM_OF_PRODUCTS_IN_BLOCK, i);
            }
            Merger productMerger = new Merger(productsIterators, productsIndex.separator, ProductsIndex.NUM_OF_PRODUCTS_IN_BLOCK,
                    productsBlockSizes.size());



            IndexBlock[] productsRes;
            while((productsRes=productMerger.getSortedBlock())!=null){
                for(var e:productsRes) System.out.print(e.block);
                System.out.println();
            }
        }
        catch (IOException e){
            e.printStackTrace();
            System.err.println(e);
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
        writer.sort();
        IndexReader reader = new IndexReader(indexDir);
//        for (int i = 1; i <= 100; i++) {
//            String productId = reader.getProductId(i);
//            Enumeration<Integer> reviewIds = reader.getProductReviews("B00067AD4U");
//            Assertions.assertTrue(enumerationContains(reviewIds, i), "checking review "+i+"");
//        }

    }
}
