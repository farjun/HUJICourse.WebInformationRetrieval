package webdata.models;

import webdata.iterators.IndexValuesIterator;

import java.io.IOException;
import java.util.ArrayList;

public class Merger {

    public static final int NUM_OF_BLOCKS_TO_RETURN = 3;
    // external merge:
    // given k-1 sorted blocks merge them strict to the Kth block capacity and commit to disk
    // then replace the used block with new
    SortableNode[] decodedEntries;
    IndexValuesIterator[] iters;

    StringBuilder diskMock;

    public ArrayList<SortableNode> getMergedBlock() {
        return mergedBlock;
    }
    int entryCountInMergedBlock;
    ArrayList<SortableNode> mergedBlock;
    int numOfBlocks; // number of blocks to use for merge in RAM
    int blockLength; // number of entries in each block
    char separator;



    public Merger(char separator){
        this.separator = separator;
        this.mergedBlock = new ArrayList<>();
    }


    public Merger(IndexValuesIterator[] iters, char separator, int blockLength, int numOfBlocks) throws IOException {
        this(separator);
        this.blockLength = blockLength;
//        this.output = output;
//        this.outBlockSizesFile = outBlockSizesFile;
        this.numOfBlocks = numOfBlocks;
        this.decodedEntries = new SortableNode[numOfBlocks];
        this.iters = iters;
        for(int i=0;i<numOfBlocks;i++){
            this.cleanBlockAndFetchNew(i);
        }
    }


    public void cleanBlockAndFetchNew(int blockIndex){
        if(blockIndex >= decodedEntries.length || blockIndex<0) return;
        if(!iters[blockIndex].hasNext()){
            decodedEntries[blockIndex] = null;
            return;
        }
        decodedEntries[blockIndex] = iters[blockIndex].next();
    }

    private int countInStringBuilder(StringBuilder str, char chr){
        int count = 0;
        for (int i=0;i<str.length();i++) {
            if(str.charAt(i) == chr) count++;
        }
        return count;
    }
    public int getMinIndex(){
        int blockMinIndex = 0;
        while(blockMinIndex < decodedEntries.length && decodedEntries[blockMinIndex]==null )
            blockMinIndex++;
        if(blockMinIndex == decodedEntries.length)
            return -1;

        for(int i=blockMinIndex+1;i<iters.length;i++){
            if(decodedEntries[i] == null){
                continue;
            }
            int compRes = decodedEntries[blockMinIndex].compare(decodedEntries[i]);
            if(compRes > 0){
                blockMinIndex = i;
            }
        }
        return blockMinIndex;
    }
    public boolean hasMoreInput(){
        for (SortableNode decodedEntry : decodedEntries) {
            if (decodedEntry != null)
                return true;
        }
        return false;
    }


    public void mergeIter(){
        int blockMinIndex = getMinIndex();
        if(blockMinIndex<0) return;
        if(mergedBlock.size()>0 && mergedBlock.get(mergedBlock.size()-1).compare(decodedEntries[blockMinIndex]) == 0)
            mergedBlock.get(mergedBlock.size()-1).merge(decodedEntries[blockMinIndex]);
        else {
            mergedBlock.add(decodedEntries[blockMinIndex]);
        }

        this.cleanBlockAndFetchNew(blockMinIndex);
    }

    public IndexBlock getSortedBlock(){
        do {
            mergeIter();
        }
        while (this.mergedBlock.size() != this.blockLength && hasMoreInput()); // TODO optimize
        if(this.mergedBlock.size()==0){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String key = this.mergedBlock.get(0).getKey();
        for(SortableNode sn: this.mergedBlock){
            sb.append(sn.toString());
        }
        cleanMergingBlock();

        return new IndexBlock(sb.toString(), key);
    }

    public IndexBlock[] getSortedBlocks(int numOfBlocks){
        ArrayList<IndexBlock> res = new ArrayList<>();
        IndexBlock sortedBlock;
        while((sortedBlock = getSortedBlock()) != null){
            res.add(sortedBlock);
            if(res.size() == numOfBlocks){
                break;
            }
        }
        IndexBlock[] out = new IndexBlock[res.size()];
        res.toArray(out);
        return out;
    }

    public void cleanMergingBlock(){
        this.mergedBlock.clear();
    }

}
