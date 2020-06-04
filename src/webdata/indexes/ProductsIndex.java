package webdata.indexes;

import webdata.models.*;

import java.util.*;

public class ProductsIndex extends Index {
    public static final int NUM_OF_PRODUCTS_IN_BLOCK = 100;
    private HashMap<String, CompressedArrayList> hashMap;

    public ProductsIndex(){
        super('|');
        this.hashMap = new HashMap<>();
    }

    public ProductsIndex(String serializedHashMap){
        this();
        this.loadData(serializedHashMap);
    }

    public void loadData(String rawIndex){
        this.hashMap = new HashMap<>();
        String[] keysAndValues = rawIndex.split("\\|");
        for (String andValue : keysAndValues) {
            try {
                String[] keysAndValue = andValue.split(":");
                this.hashMap.put(keysAndValue[0], new CompressedArrayList(keysAndValue[1]));
            }catch (Exception e){
                System.out.println("error");
            }
        }
    }

    public void put(String key,  CompressedArrayList value){
        this.hashMap.put(key, value);
    }

    public boolean contains(String key){
        return this.hashMap.containsKey(key);
    }

    public void insert(String key, String value){
        if(this.hashMap.containsKey(key)){
            this.hashMap.get(key).add(value);
        }
        else{
            CompressedArrayList cal = new CompressedArrayList();
            cal.add(value);
            this.hashMap.put(key, cal);
        }
    }

    public Enumeration<Integer> get(String key){
        return this.hashMap.get(key).iter();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String key: this.hashMap.keySet()) {
            sb.append(key).append(":").append(this.hashMap.get(key)).append("|");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    public IndexBlock[] toStringBlocks(boolean lastBatch) {
        StringBuilder sb = new StringBuilder();
        int numOfBlocks = (this.hashMap.size() / NUM_OF_PRODUCTS_IN_BLOCK);
        if(lastBatch && Math.floor((double)this.hashMap.size() / NUM_OF_PRODUCTS_IN_BLOCK) < (double)this.hashMap.size() / NUM_OF_PRODUCTS_IN_BLOCK )
            numOfBlocks++;

        IndexBlock[] productsBlocks = new IndexBlock[numOfBlocks];
        int curNumOfProducts = 0;
        int curBlock = 0;

        SortedSet<String> keys = new TreeSet<>(this.hashMap.keySet());

        for (String key: keys) {
            sb.append(key).append(":").append(this.hashMap.get(key)).append("|");
            curNumOfProducts++;
            if( curNumOfProducts >= NUM_OF_PRODUCTS_IN_BLOCK){
                productsBlocks[curBlock] = new IndexBlock(sb.toString(), keys.first());
                sb = new StringBuilder();
                curNumOfProducts = 0;
                curBlock++;
            }
        }
        if(lastBatch) {
            String lastBlock = sb.toString();
            if (!lastBlock.equals("")) {
                sb.deleteCharAt(sb.length() - 1);
                productsBlocks[curBlock] = new IndexBlock(lastBlock, keys.first());;
            }
        }else{
            this.loadData(sb.toString());
        }

        return productsBlocks;
    }


    public SortableNode createSortableNode(String removeFirst) {
        return new SortableNodeProducts(removeFirst);
    }
}
