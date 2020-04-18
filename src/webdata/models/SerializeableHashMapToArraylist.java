package webdata.models;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;

public class SerializeableHashMapToArraylist implements Serializable {
    private final HashMap<String, String> hashMap;

    public SerializeableHashMapToArraylist(){
        this.hashMap = new HashMap<>();
    }

    public SerializeableHashMapToArraylist(String serializedHashMap){
        this();
        String[] keysAndValues = serializedHashMap.split("\\|");
        for (String andValue : keysAndValues) {
            String[] keysAndValue = andValue.split(":");
            this.hashMap.put(keysAndValue[0], keysAndValues[1]);
        }
    }

    public String put(String key,  String value){
        return this.hashMap.put(key,value);
    }

    public String addTo(String key,  String value){
        if(this.hashMap.containsKey(key)){
            return this.hashMap.put(key, this.hashMap.get(key)+ "," + value);
        }
        return this.hashMap.put(key, value);
    }

    public Long[] get(String key){
        return Arrays.stream(this.hashMap.get(key).split(",")).map(Long::parseLong).toArray(Long[]::new);
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
}
