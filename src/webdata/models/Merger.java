package webdata.models;

import webdata.iostreams.AppInputStream;

public class Merger {

    // external merge:
    // given k-1 sorted blocks merge them strict to the Kth block capacity and commit to disk
    // then replace the used block with new
    StringBuilder[] decodedBlocks;
    int[] ptrs;

    int ccc;

    public StringBuilder getMergedBlock() {
        return mergedBlock;
    }

    StringBuilder mergedBlock;
    static final int K = 5; // number of blocks to use for merge in RAM
    static final int blockLength = 3; // number of entries in each block TODO: discuss whether it needs to be byte wise.


    public Merger(){
        ccc = 0;
        ptrs = new int[K-1];
        decodedBlocks = new StringBuilder[K - 1];
        for (int i = 0; i<decodedBlocks.length; i++) {
            decodedBlocks[i] = new StringBuilder();
        }

        mergedBlock = new StringBuilder();
    }

    public Merger(AppInputStream input){
        this();
    }

    public Merger(String input){
        this();
        int c = 0;
        String[] split = input.split(";");
        if(split.length==0) return;
        for(int i=0;i<decodedBlocks.length;i++) {
            for(int j=0;j<blockLength;j++){
                if(c >= split.length) break;
                decodedBlocks[i].append(split[c]);
                decodedBlocks[i].append(';');
                c++;
            }
        }
    }

    public void cleanBlock(int blockIndex){
        if(blockIndex >= decodedBlocks.length || blockIndex<0) return;
        decodedBlocks[blockIndex].setLength(0);
    }


    public boolean cleanBlockAndFetchNew(int blockIndex){
        this.cleanBlock(blockIndex);
        // TODO: fetch new decoded block and insert into decodedBlocks[blockIndex]
        // MOKCKING for now
        decodedBlocks[blockIndex].append("instant"+ccc+"|1|{2:30};");
        decodedBlocks[blockIndex].append("zzzzz"+ccc+"|1|{2:30};");
        ccc++;
        if(ccc>3) return false; //
        return true;
    }

    public boolean mergeIter(){
         // init to zeros by default
        int ptrInMin = ptrs[0];
        int blockMinIndex = 0;
        int tokenEndIndex = decodedBlocks[0].substring(ptrs[0]).indexOf("|");
        String minToken = decodedBlocks[0].substring(ptrs[0], ptrs[0]+tokenEndIndex);
        for(int i=1;i<decodedBlocks.length;i++){
            tokenEndIndex = decodedBlocks[i].substring(ptrs[i]).indexOf("|");
            if(tokenEndIndex<0) continue;
            String token = decodedBlocks[i].substring(ptrs[i], ptrs[i]+tokenEndIndex);
            if(minToken.compareTo(token) > 0){
                minToken = token;
                ptrInMin = ptrs[i];
                blockMinIndex = i;
            }
        }
        // append min token whole entry to merged
        int curr = ptrInMin;
        while(curr<decodedBlocks[blockMinIndex].length()
                &&
                decodedBlocks[blockMinIndex].charAt(curr) != ';'){
            mergedBlock.append(decodedBlocks[blockMinIndex].charAt(curr));
            curr++;
        }
        ptrs[blockMinIndex] = ++curr;
        var toContinue = true;
        if(curr >= decodedBlocks[blockMinIndex].length()){
            ptrs[blockMinIndex] = 0;
            toContinue = cleanBlockAndFetchNew(blockMinIndex);
        }
        mergedBlock.append(';');
        return toContinue;
    }

    public void externalMerge(){
        while (mergeIter());
    }
}
