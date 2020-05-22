package webdata.indexes;

import webdata.models.CompressedArrayList;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;

public class ProductsIndex extends Index {
    private static final int NUM_OF_PRODUCTS_IN_BLOCK = 200;
    private final HashMap<String, CompressedArrayList> hashMap;

    public ProductsIndex(){
        this.hashMap = new HashMap<>();
    }

    public ProductsIndex(String serializedHashMap){
        this();
        this.loadData(serializedHashMap);
    }

    public void loadData(String rawIndex){
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

    public String[] toStringBlocks() {
        StringBuilder sb = new StringBuilder();
        String[] productsBlocks = new String[(this.hashMap.size() / NUM_OF_PRODUCTS_IN_BLOCK) + 1];
        int curNumOfProducts = 0;
        int curBlock = 0;
        for (String key: this.hashMap.keySet()) {
            sb.append(key).append(":").append(this.hashMap.get(key)).append("|");
            curNumOfProducts++;
            if( curNumOfProducts >= NUM_OF_PRODUCTS_IN_BLOCK){
                productsBlocks[curBlock] = sb.toString();
                sb = new StringBuilder();
                curNumOfProducts = 0;
                curBlock++;
            }
        }
        sb.deleteCharAt(sb.length()-1);
        productsBlocks[curBlock] = sb.toString();

        return productsBlocks;
    }

    public String toCompressedString() {
        return this.toString();
    }
}
