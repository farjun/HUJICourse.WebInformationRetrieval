package webdata.models;

import webdata.encoders.ArithmeticEncoder;
import webdata.indexes.BlockSizesFile;
import webdata.indexes.WordsBlockSizesFile;
import webdata.iostreams.AppOutputStream;
import webdata.iostreams.BitRandomAccessInputStream;
import webdata.iterators.IndexValuesIterator;

import java.io.IOException;

public class Merger {

    // external merge:
    // given k-1 sorted blocks merge them strict to the Kth block capacity and commit to disk
    // then replace the used block with new
    SortableNode[] decodedEntries;
    IndexValuesIterator[] iters;

    StringBuilder diskMock;

    public StringBuilder getMergedBlock() {
        return mergedBlock;
    }
    int entryCountInMergedBlock;
    StringBuilder mergedBlock;
    static final int K = 10; // number of blocks to use for merge in RAM
    static final int blockLength = 50; // number of entries in each block
    char separator;
    AppOutputStream output;
    BlockSizesFile blockSizesFile;
    boolean isWordsMerger;

    public Merger(char separator, boolean isWordsMerger){
        this.diskMock = new StringBuilder();

        this.separator = separator;
        this.decodedEntries = new SortableNode[K - 1];
        this.entryCountInMergedBlock = 0;
        this.mergedBlock = new StringBuilder();
        this.isWordsMerger = isWordsMerger;
    }

    public Merger(BitRandomAccessInputStream input, AppOutputStream output,
                  BlockSizesFile blockSizesFile, char separator, boolean isWordsMerger) throws IOException {
        this(separator, isWordsMerger);
        iters = new IndexValuesIterator[K - 1];
        for(int i = 0; i<iters.length; i++) {
            iters[i] = new IndexValuesIterator(input, separator); // TODO:integrate Omer addition
        }
        for(int i=0;i<decodedEntries.length;i++){
            this.cleanBlockAndFetchNew(i);
        }
        this.output = output;
        this.blockSizesFile = blockSizesFile;
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
            if(isWordsMerger)
                writeToDiskWords();
            else
                writeToDisk();
            entryCountInMergedBlock = 0;
        }

        var shouldContinue = this.cleanBlockAndFetchNew(blockMinIndex);
//        mergedBlock.append(this.separator);
        return shouldContinue;
    }
    public void writeEncodedWords(String blockToEncode,
                             boolean lastBatch) throws IOException {
        ArithmeticEncoder enc = new ArithmeticEncoder(this.output);
        for (int symbol: blockToEncode.toCharArray()) {
            enc.writeSymbol(symbol);
        }
        int numOfBytesWritten = this.output.setCheckpoint();
        enc = new ArithmeticEncoder(this.output);
        var firstTokenEnd = blockToEncode.indexOf("|");
        var blockSizesFile = (WordsBlockSizesFile)this.blockSizesFile; // down cast to WordsBlockSizesFile
        blockSizesFile.addBlockDetails(numOfBytesWritten, blockToEncode.substring(0,firstTokenEnd));

        // Flush remaining code bits
        if(lastBatch){
            enc.finish();
            blockSizesFile.flush();
            this.output.flush();
        }

    }
    public void writeEncoded(String blockToEncode,
                                  boolean lastBatch) throws IOException {
        ArithmeticEncoder enc = new ArithmeticEncoder(this.output);
        for (int symbol: blockToEncode.toCharArray()) {
            enc.writeSymbol(symbol);
        }
        int numOfBytesWritten = this.output.setCheckpoint();
        enc = new ArithmeticEncoder(this.output);
        this.blockSizesFile.addBlockSize(numOfBytesWritten);

        // Flush remaining code bits
        if(lastBatch){
            enc.finish();
            this.blockSizesFile.flush();
            this.output.flush();
        }

    }
    private void writeToDisk() {
        diskMock.append(mergedBlock);

        System.out.println("DISK CONTENT:");
        System.out.println(diskMock); // TODO:

        try{
            writeEncoded(mergedBlock.toString(), false); //TODO: check last
        } catch(IOException e){
            System.err.println(e.toString());
        }
        mergedBlock.setLength(0);
    }
    private void writeToDiskWords() {
        diskMock.append(mergedBlock);

        System.out.println("DISK CONTENT:");
        System.out.println(diskMock); // TODO:

        try{
            writeEncodedWords(mergedBlock.toString(), false); //TODO: check last
        } catch(IOException e){
            System.err.println(e.toString());
        }
        mergedBlock.setLength(0);
    }

    public void externalMerge(){
        while (mergeIter());
    }
}
