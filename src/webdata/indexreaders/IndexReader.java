package webdata.indexreaders;

import webdata.iostreams.AppInputStream;
import webdata.iostreams.AppOutputStream;
import webdata.models.ProductReview;

import java.io.IOException;

public abstract class IndexReader {
    protected AppInputStream inputStream;

    public IndexReader(AppInputStream inputStream) {
        this.inputStream = inputStream;
    }

    abstract void loadIndex() throws IOException;

    public void close(){
        try {
            this.inputStream.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

    }




}
