package webdata.models;

import webdata.iostreams.BitRandomAccessInputStream;
import webdata.iterators.IndexValuesIterator;

import java.io.IOException;

public class Merger {

    // external merge:
    // given k-1 sorted blocks merge them strict to the Kth block capacity and commit to disk
    // then replace the used block with new
    SortableNode[] decodedEntries;
    IndexValuesIterator[] iters;

    int ccc; //for the mocked data
    StringBuilder diskMock;

    public StringBuilder getMergedBlock() {
        return mergedBlock;
    }
    int entryCountInMergedBlock;
    StringBuilder mergedBlock;
    static final int K = 10; // number of blocks to use for merge in RAM
    static final int blockLength = 50; // number of entries in each block
    char separator;


    public Merger(char separator){
        this.separator = separator;
        diskMock = new StringBuilder();
        ccc = 0;


        decodedEntries = new SortableNode[K - 1];
        entryCountInMergedBlock = 0;
        mergedBlock = new StringBuilder();
    }

    public Merger(BitRandomAccessInputStream input, char separator) throws IOException {
        this(separator);
        iters = new IndexValuesIterator[K - 1];
        for(int i = 0; i<iters.length; i++) {
            iters[i] = new IndexValuesIterator(input, separator);
        }
        for(int i=0;i<decodedEntries.length;i++){
            this.cleanBlockAndFetchNew(i);
        }
    }


    public boolean cleanBlockAndFetchNew(int blockIndex){
        if(blockIndex >= decodedEntries.length || blockIndex<0) return false;
        if(!iters[blockIndex].hasNext()) return false;
        decodedEntries[blockIndex] = iters[blockIndex].next();
        return true;
    }

    private int countInStringBuilder(StringBuilder str, char chr){
        int count = 0;
        for (int i=0;i<str.length();i++) {
            if(str.charAt(i) == chr) count++;
        }
        return count;
    }
    
    public boolean mergeIter(){
         // init to zeros by default
        int blockMinIndex = 0;
        if(!this.cleanBlockAndFetchNew(0))
            return false;
        var minEntry = decodedEntries[0];
        for(int i=1;i<iters.length;i++){
            if(this.cleanBlockAndFetchNew(i)){
                var compRes = minEntry.compare(decodedEntries[i]);
                if(compRes < 0){
                    minEntry = decodedEntries[i];
                    blockMinIndex = i;
                }
                else if(compRes == 0){
                    decodedEntries[i].merge(minEntry);
                    minEntry = decodedEntries[i];
                }
            }
        }
        // append min token whole entry to merged
        mergedBlock.append(decodedEntries[blockMinIndex].toString());

        entryCountInMergedBlock++;
        if(entryCountInMergedBlock >= blockLength){
            writeToDisk();
            entryCountInMergedBlock = 0;
        }

        var toContinue = this.cleanBlockAndFetchNew(blockMinIndex);
//        mergedBlock.append(this.separator);
        return toContinue;
    }

    private void writeToDisk() {
        diskMock.append(mergedBlock);
        mergedBlock.setLength(0);

        System.out.println("DISK CONTENT:");
        System.out.println(diskMock);
    }

    public void externalMerge(){
        while (mergeIter());
    }
}
