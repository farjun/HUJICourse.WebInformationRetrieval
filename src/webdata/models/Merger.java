package webdata.models;

import webdata.iostreams.AppInputStream;

public class Merger {

    // external merge:
    // given k-1 sorted blocks merge them strict to the Kth block capacity and commit to disk
    // then replace the used block with new
    StringBuilder[] decodedBlocks;
    StringBuilder mergedBlock;
    static final int K = 5; // number of blocks to use for merge in RAM
    static final int blockLength = 3; // number of entries in each block


    public Merger(){
        decodedBlocks = new StringBuilder[K-1];
        mergedBlock = new StringBuilder();
    }

    public Merger(AppInputStream input){
        this();
    }

    public Merger(String input){
        this();
        String[] split = input.split(";");
        for(int i=0;i<decodedBlocks.length;i++) {
            for (int c = 0; c < blockLength; c++) {
//                decodedBlocks[i].append(split[])
            }
        }
    }

    public void cleanBlock(int blockIndex){
        if(blockIndex >= decodedBlocks.length || blockIndex<0) return;
        decodedBlocks[blockIndex].setLength(0);
    }


    public void cleanBlockAndFetchNew(int blockIndex){
        this.cleanBlock(blockIndex);
        // TODO: fetch new to decodedBlocks[blockIndex]
    }

    public void mergeIter(){
        int[] ptrs = new int[K-1]; // init to zeros by default
        int ptrInMin = 0;
        int blockMinIndex = 0;
        int tokenEndIndex = decodedBlocks[0].indexOf("|");
        String minToken = decodedBlocks[0].substring(ptrs[0], tokenEndIndex);
        for(int i=1;i<K-1;i++){
            tokenEndIndex = decodedBlocks[i].indexOf("|");
            String token = decodedBlocks[i].substring(ptrs[i], tokenEndIndex);
            if(minToken.compareTo(token) > 0){
                minToken = token;
                ptrInMin = ptrs[i];
                blockMinIndex = i;
            }
        }
        // append min token whole entry to merged
        int curr = ptrInMin;
        while(decodedBlocks[blockMinIndex].charAt(curr) != ';'){
            curr++;
            mergedBlock.append(decodedBlocks[blockMinIndex].charAt(curr));
        }
        if(curr >= decodedBlocks[blockMinIndex].length()) cleanBlockAndFetchNew(blockMinIndex);
        mergedBlock.append(';');

    }
}
