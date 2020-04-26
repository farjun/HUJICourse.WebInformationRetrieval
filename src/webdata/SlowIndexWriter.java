package webdata;

import webdata.indexwriters.ProductsIndexWriter;
import webdata.indexwriters.ReviewsIndexWriter;
import webdata.indexwriters.WordsIndexWriter;
import webdata.iterators.ReviewsIterator;
import webdata.models.ProductReview;

import java.io.*;
import java.nio.file.Paths;

public class SlowIndexWriter {
    WordsIndexWriter wordsIndexWriter;
    ReviewsIndexWriter reviewsIndexWriter;
    ProductsIndexWriter productsIndexWriter;


    public void close(){
        this.wordsIndexWriter.close();
        this.productsIndexWriter.close();
        this.reviewsIndexWriter.close();

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

            this.wordsIndexWriter = new WordsIndexWriter( Paths.get(dirPath,"words.txt").toString());
            this.reviewsIndexWriter = new ReviewsIndexWriter( Paths.get(dirPath,"reviews.txt").toString());
            this.productsIndexWriter = new ProductsIndexWriter( Paths.get(dirPath,"products.txt").toString());

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
            while(iter.hasNext()) {
                ProductReview review = iter.next();
                this.wordsIndexWriter.process(review);
                this.reviewsIndexWriter.process(review);
                this.productsIndexWriter.process(review);
            }
            this.wordsIndexWriter.writeProcessed();
            this.reviewsIndexWriter.writeProcessed();
            this.productsIndexWriter.writeProcessed();

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
