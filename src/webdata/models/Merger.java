package webdata.models;

import webdata.encoders.ArithmeticEncoder;
import webdata.indexes.BlockSizesFile;
import webdata.indexes.WordsBlockSizesFile;
import webdata.iostreams.AppOutputStream;
import webdata.iostreams.BitRandomAccessInputStream;
import webdata.iterators.IndexValuesIterator;

import java.io.IOException;
import java.util.ArrayList;

public class Merger {

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
    BlockSizesFile inBlockSizesFile;


    public Merger(char separator, boolean isWordsMerger){

        this.separator = separator;
        this.mergedBlock = new ArrayList<>();

    }

    public Merger(BitRandomAccessInputStream input, BlockSizesFile inBlockSizesFile,
            char separator, boolean isWordsMerger, int blockLength) throws IOException {
        this(separator, isWordsMerger);
        this.blockLength = blockLength;
//        this.output = output;
//        this.outBlockSizesFile = outBlockSizesFile;
        this.inBlockSizesFile = inBlockSizesFile;
        ArrayList<Integer> inBlockSizes = this.inBlockSizesFile.getBlockSizes();
        this.numOfBlocks = inBlockSizes.size();
        this.decodedEntries = new SortableNode[numOfBlocks];
        this.iters = new IndexValuesIterator[numOfBlocks];
        for(int i = 0; i<iters.length; i++) {
            iters[i] = new IndexValuesIterator(new BitRandomAccessInputStream(input), separator,30, i); // TODO: check with Omer
        }
        for(int i=0;i<numOfBlocks;i++){
            this.cleanBlockAndFetchNew(i);
        }
    }


    public void cleanBlockAndFetchNew(int blockIndex){
        if(blockIndex >= decodedEntries.length || blockIndex<0) return;
        if(!iters[blockIndex].hasNext()) return;
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
        for(int i=1;i<iters.length;i++){
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

        if(mergedBlock.size()>0 && mergedBlock.get(mergedBlock.size()-1).compare(decodedEntries[blockMinIndex]) == 0)
            mergedBlock.get(mergedBlock.size()-1).merge(decodedEntries[blockMinIndex]);
        else {
            mergedBlock.add(decodedEntries[blockMinIndex]);
        }

        this.cleanBlockAndFetchNew(blockMinIndex);
    }
//

    public String[] getSortedBlock(){
        return getSortedBlock(this.blockLength);
    }

    public String[] getSortedBlock(int blockLength_){
        do {
            mergeIter();
        }
        while (this.mergedBlock.size() <= blockLength_ && hasMoreInput()); // TODO optimize
        String[] res = new String[this.mergedBlock.size()];
        int i=0;
        for(SortableNode sn: this.mergedBlock)
            res[i++] = sn.toString();
        return res;
    }

    public void cleanMergingBlock(){
        this.mergedBlock.clear();
    }

//    public void writeEncodedWords(String blockToEncode,
//                             boolean lastBatch) throws IOException {
//        ArithmeticEncoder enc = new ArithmeticEncoder(this.output);
//        for (int symbol: blockToEncode.toCharArray()) {
//            enc.writeSymbol(symbol);
//        }
//        int numOfBytesWritten = this.output.setCheckpoint();
//        enc = new ArithmeticEncoder(this.output);
//        var firstTokenEnd = blockToEncode.indexOf("|");
//        WordsBlockSizesFile outBlockSizesFile = (WordsBlockSizesFile)this.outBlockSizesFile; // down cast to WordsBlockSizesFile
//        outBlockSizesFile.addBlockDetails(numOfBytesWritten, blockToEncode.substring(0,firstTokenEnd));
//
//        // Flush remaining code bits
//        if(lastBatch){
//            enc.finish();
//            outBlockSizesFile.flush();
//            this.output.flush();
//        }
//
//    }


    //    public void writeEncoded(String blockToEncode,
//                                  boolean lastBatch) throws IOException {
//        ArithmeticEncoder enc = new ArithmeticEncoder(this.output);
//        for (int symbol: blockToEncode.toCharArray()) {
//            enc.writeSymbol(symbol);
//        }
//        int numOfBytesWritten = this.output.setCheckpoint();
//        enc = new ArithmeticEncoder(this.output);
//        this.outBlockSizesFile.addBlockSize(numOfBytesWritten);
//
//        // Flush remaining code bits
//        if(lastBatch){
//            enc.finish();
//            this.outBlockSizesFile.flush();
//            this.output.flush();
//        }
//
//    }
//    private void writeToDisk() {
//        diskMock.append(mergedBlock);
//
//        System.out.println("DISK CONTENT:");
//        System.out.println(diskMock); // TODO:
//
//        try{
//            writeEncoded(mergedBlock.toString(), false); //TODO: check last
//        } catch(IOException e){
//            System.err.println(e.toString());
//        }
//        mergedBlock = new ArrayList<>();
//    }
//    private void writeToDiskWords() {
//        diskMock.append(mergedBlock);
//
//        System.out.println("DISK CONTENT:");
//        System.out.println(diskMock); // TODO:
//
//        try{
//            for(int i=0;i<mergedBlock.size()-1;i++){
//                writeEncodedWords(mergedBlock.get(i).toString(), false); //TODO: check last
//            }
//            if(mergedBlock.size()>0){
//                var tmp = mergedBlock.get(mergedBlock.size()-1);
//                mergedBlock.clear();
//                mergedBlock.add(tmp);
//            }
//        } catch(IOException e){
//            System.err.println(e.toString());
//        }
//        mergedBlock = new ArrayList<>();
//    }

}
