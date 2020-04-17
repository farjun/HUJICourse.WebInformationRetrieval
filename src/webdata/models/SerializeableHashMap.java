package webdata.models;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SerializeableHashMap implements Serializable {
    private final HashMap<String, String> hashMap;

    public SerializeableHashMap(){
        this.hashMap = new HashMap<>();
    }

    public SerializeableHashMap(String serializedHashMap){
        this();
        String[] keysAndValues = serializedHashMap.split("\\|");
        for (int i = 0; i < keysAndValues.length; i++) {
            String[] keysAndValue = keysAndValues[i].split(":");
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
