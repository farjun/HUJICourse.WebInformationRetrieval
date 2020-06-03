package webdata.iterators;

import webdata.encoders.ArithmeticDecoder;
import webdata.indexes.Index;
import webdata.iostreams.BitRandomAccessInputStream;
import webdata.iostreams.OutOfBitsException;
import webdata.iostreams.OutOfBlocksException;
import webdata.models.SortableNode;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Iterator;


public class IndexValuesIterator <T extends SortableNode> implements Iterator<T> {

    private final BitRandomAccessInputStream inputStream;
    private final char seperator;
    private final Index index;
    private int maxBufferSize;
    private int blockNum; // TODO for debugging purposes at least
    ArithmeticDecoder dec;
    private Deque<String> curNodesInBuffer;

    public IndexValuesIterator(Index index,BitRandomAccessInputStream inputStream, char seperator) throws IOException{
        this(index, inputStream, seperator, 30, 0);
    }

    public IndexValuesIterator(Index index,BitRandomAccessInputStream inputStream, char seperator, int maxBufferSize, int blockNum) throws IOException{
        inputStream.setPointerToBlock(blockNum);
        this.index = index;
        this.dec = new ArithmeticDecoder(inputStream);
        this.inputStream = inputStream;
        this.seperator = seperator;
        this.maxBufferSize = maxBufferSize;
        this.curNodesInBuffer = new LinkedList<>();
        this.blockNum = blockNum;
    }

    @Override
    public boolean hasNext() {
        return this.curNodesInBuffer.size() > 0 || inputStream.hasMoreInput();
    }

    private void fillBuffer() throws IOException {
        StringBuilder sb = new StringBuilder();
        while (curNodesInBuffer.size() < maxBufferSize) {
            // Decode and write one byte
            int symbol = dec.read();
            if(symbol != seperator){
                sb.append((char)symbol);
            }
            else{
                if((char)seperator == ';')
                    sb.append(';'); // in case of words index entries
                curNodesInBuffer.add(sb.toString());
                sb = new StringBuilder();
            }
        }
    }

    @Override
    public T next() {
        if(curNodesInBuffer.size() > 0){
            return (T)index.createSortableNode(curNodesInBuffer.removeFirst());
        }
        else{
            try {
                this.fillBuffer();
            }catch (OutOfBitsException exception){
                return null;
            }catch (IOException exception){
                System.err.println("Exception in iterator");
            }
        }
        if(curNodesInBuffer.size() <= 0) return null;
        return (T)index.createSortableNode(curNodesInBuffer.removeFirst());
    }
}

