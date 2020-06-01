package webdata.indexes;

import webdata.encoders.ArithmeticDecoder;
import webdata.iostreams.BitRandomAccessInputStream;
import webdata.iterators.IndexValuesIterator;

import java.io.IOException;

public class Index {

    private final int curBlockLoaded;

    public Index(){
        curBlockLoaded = -1;
    }

    public void loadBlock(BitRandomAccessInputStream inputStream, int blockNum) throws IOException  {
        if(curBlockLoaded == blockNum){
            return;
        }
        StringBuffer sb = this.decodeBlock(inputStream, blockNum);
        this.loadData(sb.toString());

    }

    public IndexValuesIterator valuesIterator(BitRandomAccessInputStream inputStream, char seperator, int maxNumOfElementsInBuffer, int blobkNum) throws IOException  {
       return new IndexValuesIterator(inputStream, seperator,  maxNumOfElementsInBuffer, blobkNum);
    }

    public StringBuffer decodeBlock(BitRandomAccessInputStream inputStream, int blockNum) throws IOException {
        inputStream.setPointerToBlock(blockNum);
        ArithmeticDecoder dec = new ArithmeticDecoder(inputStream);
        StringBuffer sb = new StringBuffer();

        while (true) {
            // Decode and write one byte
            try {
                int symbol = dec.read();
                sb.append((char)symbol);
            }catch (IOException e){
                break;
            }
        }
        return sb;
    }

    public void loadData(String rawIndex){

    }

    public int getBlockNum(Object key){
        return 0;
    }

}
