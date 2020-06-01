package webdata.indexes;

import java.io.*;
import java.util.ArrayList;

public class BlockSizesFile {
    protected BufferedWriter out;
    protected BufferedReader in;
    protected final ArrayList<Integer> blockSizes;

    public BlockSizesFile(){blockSizes = new ArrayList<>();}

    public BlockSizesFile(FileWriter filename) throws IOException{
        this();
        out = new BufferedWriter(filename);
    }
    public BlockSizesFile(FileReader filename) throws IOException{
        this();
        in = new BufferedReader(filename);

        String line;
        while ((line = in.readLine()) != null){
            blockSizes.add(Integer.valueOf(line));
        }
    }

    public void addBlockSize(int batchSize) {
        blockSizes.add(batchSize);
    }

    public int getBlockSize(int blockNum){
        return this.blockSizes.get(blockNum);
    }

    public ArrayList<Integer> getBlockSizes(){
        return this.blockSizes;
    }

    public void flush() throws IOException{
        StringBuilder sb = new StringBuilder();
        for (int batchSize: blockSizes) {
            sb.append(batchSize).append("\n");
        }
        out.write(sb.toString());
        out.flush();
    }

    public int getBatchSize(int batchNumber){
        return this.blockSizes.get(batchNumber);
    }
}
