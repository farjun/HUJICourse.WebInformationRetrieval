package webdata.indexes;

import java.io.*;
import java.util.ArrayList;

public class BlockSizesFile {
    private final ArrayList<String> blockKeyToken;
    protected BufferedWriter out;
    protected BufferedReader in;
    protected final ArrayList<Integer> blockSizes;

    public BlockSizesFile(){
        blockSizes = new ArrayList<>();
        blockKeyToken = new ArrayList<>();

    }


    public int searchByToken(String token){
        // assumes the list is ordered.
        int left = 0, right = blockKeyToken.size()-1;
        while(left<right){
            int mid = (left+right)/2;
            String midToken = blockKeyToken.get(mid);
            int compRes = token.compareTo(midToken);
            if(compRes==0){
                return mid;
            }
            else if(compRes<0){
                right = mid-1;
            } else {
                left = mid+1;
            }
        }
        return -1;
    }

    public BlockSizesFile(FileWriter filename) throws IOException{
        this();
        out = new BufferedWriter(filename);
    }
    public BlockSizesFile(FileReader filename) throws IOException{
        this();
        in = new BufferedReader(filename);

        String line;
        while ((line = in.readLine()) != null){
            String[] splittedLine = line.split("\\|");
            if( splittedLine.length == 1){
                blockSizes.add(Integer.valueOf(line));
            }else{
                blockSizes.add(Integer.valueOf(splittedLine[0]));
                blockKeyToken.add(splittedLine[1]);
            }

        }
    }

    public void addBlockSize(int batchSize) {
        blockSizes.add(batchSize);
    }

    public void addBlockDetails(int batchSize, String token) {
        blockSizes.add(batchSize);
        if(token != null)
            blockKeyToken.add(token);
    }

    public int getBlockSize(int blockNum){
        return this.blockSizes.get(blockNum);
    }

    public ArrayList<Integer> getBlockSizes(){
        return this.blockSizes;
    }

    public void flush() throws IOException{
        StringBuilder sb = new StringBuilder();
        if(this.blockKeyToken.size() == 0) {
            for (Integer blockSize : this.blockSizes) {
                sb.append(blockSize).append("\n");
            }
        }else{
            for (int i = 0; i < this.blockSizes.size(); i++){
                sb.append(this.blockSizes.get(i)).append('|').append(this.blockKeyToken.get(i)).append("\n");
            }
        }

        out.write(sb.toString());
        out.flush();
    }

    public int getBatchSize(int batchNumber){
        return this.blockSizes.get(batchNumber);
    }

}
