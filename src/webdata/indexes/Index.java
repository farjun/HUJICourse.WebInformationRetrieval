package webdata.indexes;

import webdata.encoders.ArithmeticDecoder;
import webdata.iostreams.BitRandomAccessInputStream;
import webdata.iterators.IndexValuesIterator;
import webdata.models.SortableNode;

import java.io.IOException;

public class Index {

    private final int curBlockLoaded;
    public final char separator;

    public Index(char separator){
        curBlockLoaded = -1;
        this.separator = separator;
    }

    public void loadBlock(BitRandomAccessInputStream inputStream, int blockNum) throws IOException  {
        if(curBlockLoaded == blockNum){
            return;
        }
        StringBuffer sb = this.decodeBlock(inputStream, blockNum);
        this.loadData(sb.toString());

    }

    public IndexValuesIterator valuesIterator(BitRandomAccessInputStream inputStream, char seperator, int maxNumOfElementsInBuffer, int blobkNum) throws IOException  {
       return new IndexValuesIterator(this, inputStream, seperator,  maxNumOfElementsInBuffer, blobkNum);
    }

    public StringBuffer decodeBlock(BitRandomAccessInputStream inputStream, int blockNum) throws IOException {
        inputStream.setPointerToBlock(blockNum);
        ArithmeticDecoder dec = new ArithmeticDecoder(inputStream);
        StringBuffer sb = new StringBuffer();
        int symbol = 0;
        while (true) {
            // Decode and write one byte
            try {
                symbol = dec.read();
                sb.append((char)symbol);
            }catch (IOException e){
                assert symbol == '$';
                sb.deleteCharAt(sb.length() - 1);
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

    public SortableNode createSortableNode(String removeFirst) {
        return new SortableNode(removeFirst);
    }
}
