package webdata;

import webdata.indexes.IndexWriterImpl;
import webdata.iterators.ReviewsIterator;
import webdata.models.ProductReview;

import java.io.*;
import java.nio.file.Paths;

public class SlowIndexWriter {
    public static final int BATCH_SIZE = 1001;
    IndexWriterImpl indexWriter;

    public void close(){
        this.indexWriter.close();
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

            var wordsPath = Paths.get(dirPath,"words.txt").toString();
            var reviewsPath = Paths.get(dirPath,"reviews.txt").toString();
            var productsPath = Paths.get(dirPath,"products.txt").toString();
            this.indexWriter = new IndexWriterImpl(productsPath, reviewsPath, wordsPath);


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
                    this.indexWriter.writeProcessed();
                    curIteration = 1;
                    batchNumber++;
                    System.out.println("Batch number: " + String.valueOf(batchNumber) + " done");
                    System.out.println("total reviews processed: " + String.valueOf(batchNumber * BATCH_SIZE) );
                }
                ProductReview review = iter.next();
                this.indexWriter.process(review);
                curIteration++;
            }
            this.indexWriter.writeProcessed();
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

}
