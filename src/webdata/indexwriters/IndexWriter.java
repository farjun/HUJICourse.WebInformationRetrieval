package webdata.indexwriters;

import webdata.models.ProductReview;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public abstract class IndexWriter {
    private BufferedWriter outputFile;

    public IndexWriter(BufferedWriter outputFile) {
        this.outputFile = outputFile;
    }

    public IndexWriter(String filePath) {

        try {
            this.outputFile = new BufferedWriter(new FileWriter(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    abstract void write(ProductReview review) throws IOException;

    public void close(){
        try {
            this.outputFile.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

    }




}
