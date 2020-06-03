package webdata.models;

public class IndexBlock {
    public String key;
    public String block;


    public IndexBlock(String block, String key) {
        this.key = key;
        this.block = block;
    }

    public IndexBlock(String block) {
        this.key = null;
        this.block = block;
    }
}
