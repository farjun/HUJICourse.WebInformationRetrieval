package webdata.indexes;

import webdata.encoders.ArithmeticDecoder;
import webdata.iostreams.BitRandomAccessInputStream;
import webdata.iterators.IndexValuesIterator;
import webdata.models.SortableNode;

import java.io.IOException;

public class Index {

    private int curBlockLoaded;
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
        curBlockLoaded = blockNum;
    }

    public IndexValuesIterator valuesIterator(BitRandomAccessInputStream inputStream, char seperator, int maxNumOfElementsInBuffer, int blobkNum) throws IOException  {
       return new IndexValuesIterator(this, inputStream, seperator,  maxNumOfElementsInBuffer, blobkNum);
    }

    public StringBuffer decodeBlock(BitRandomAccessInputStream inputStream, int blockNum) throws IOException {
        inputStream.setPointerToBlock(blockNum);
        ArithmeticDecoder dec = new ArithmeticDecoder(inputStream);
        StringBuffer sb = new StringBuffer();
        StringBuffer entry = new StringBuffer();

        int symbol = 0;
        while (true) {
            // Decode and write one byte
            try {
                symbol = dec.read();
                if(symbol == separator){
                    if(entry.length() > 5) {
                        sb.append(entry.toString());
                        sb.append((char) symbol);
                    }
                    entry = new StringBuffer();
                }else {
                    entry.append((char) symbol);
                }
            }catch (IOException e){
//                for (int i = 1; i <= 5; i++) {
//                    if( sb.charAt(sb.length() - 1) == separator)
//                        break;
//                    sb.deleteCharAt(sb.length() - 1);
//                }
                if(entry.length() > 5){
                    sb.append(entry.toString());
                    sb.append(separator);
                }
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

    public SortableNode createSortableNode(int fromIter, String removeFirst) {
        return new SortableNode(fromIter, removeFirst);
    }
}
