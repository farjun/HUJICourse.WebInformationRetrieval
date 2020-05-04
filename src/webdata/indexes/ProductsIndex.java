package webdata.indexes;

import webdata.models.CompressedArrayList;

import java.util.Enumeration;
import java.util.HashMap;

public class ProductsIndex {
    private final HashMap<String, CompressedArrayList> hashMap;

    public ProductsIndex(){
        this.hashMap = new HashMap<>();
    }

    public ProductsIndex(String serializedHashMap){
        this();
        String[] keysAndValues = serializedHashMap.split("\\|");
        for (String andValue : keysAndValues) {

            String[] keysAndValue = andValue.split(":");
            this.hashMap.put(keysAndValue[0], new CompressedArrayList(keysAndValue[1]));
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

    public String toCompressedString() {
        return this.toString();
    }
}
