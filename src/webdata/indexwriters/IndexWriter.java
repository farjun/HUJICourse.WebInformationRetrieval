package webdata.indexwriters;

import webdata.iostreams.AppOutputStream;
import webdata.models.ProductReview;

import java.io.IOException;

public abstract class IndexWriter {
    protected AppOutputStream outputStream;

    public IndexWriter(AppOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    abstract void process(ProductReview review) throws IOException;
    abstract void writeProcessed() throws IOException;

    public void close(){
        try {
            this.outputStream.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

    }




}
