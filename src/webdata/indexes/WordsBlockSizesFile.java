package webdata.indexes;

import java.io.*;
import java.util.ArrayList;

public class WordsBlockSizesFile extends BlockSizesFile{

    private final ArrayList<String> blockKeyToken;

    public ArrayList<String> getBlockKeyToken() {
        return blockKeyToken;
    }

    public WordsBlockSizesFile(FileWriter filename) throws IOException{
        super(filename);
        blockKeyToken = new ArrayList<>();
    }

    public WordsBlockSizesFile(FileReader filename) throws IOException{
        super();
        this.in = new BufferedReader(filename);
        blockKeyToken = new ArrayList<>();

        String line;
        while ((line = this.in.readLine()) != null){
            String[] cols = line.split("|");
            blockSizes.add(Integer.valueOf(cols[0]));
            blockKeyToken.add(cols[1]);
        }
    }

    public void addBlockDetails(int batchSize, String token) {
        blockSizes.add(batchSize);
        blockKeyToken.add(token);
    }

    public String getBlockKeyToken(int blockNum){
        return this.blockKeyToken.get(blockNum);
    }

    @Override
    public void flush() throws IOException{
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<blockKeyToken.size();i++) {
            sb.append(this.blockSizes.get(i)).append("|").append(this.blockKeyToken.get(i)).append("\n");
        }
        out.write(sb.toString());
        out.flush();
    }

}
