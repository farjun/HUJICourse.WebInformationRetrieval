package webdata.models;

import java.util.Arrays;
import java.util.Objects;

public class SortableNodeProducts extends SortableNode {

    private int[] array;
    private final String key;

    public SortableNodeProducts(int fromIter, String rawValue){
        super(fromIter, rawValue);
        String[] keysAndValue = rawValue.split(":");
        this.key = keysAndValue[0];
        this.array = valuesToIntArray(keysAndValue[1]);
    }

    private int[] valuesToIntArray(String values){
        return Arrays.stream(values.split(",")).mapToInt(Integer::parseInt).toArray();
    }

    private int[] mergeArrays(int[] array1, int[] array2){
        int sum = 0;
        int[] resArr = new int[array1.length + array2.length];
        for (int i = 0; i < array1.length; i++) {
            resArr[i] = array1[i];
            sum += array1[i];
        }
        resArr[array1.length] = array2[0] - sum;
        System.arraycopy(array2, 1, resArr, array1.length + 1, array2.length - 1);

        return resArr;
    }

    @Override
    public void merge(SortableNode other) {
        String[] keysAndValue = other.rawValue.split(":");
        int[] otherIntValues = valuesToIntArray(keysAndValue[1]);
        if(otherIntValues[0] < array[0]){
            this.array = mergeArrays(otherIntValues, this.array);
        }else{
            this.array = mergeArrays(this.array, otherIntValues);
        }
    }

    @Override
    public int compare(SortableNode o) {
        return this.key.compareTo(o.rawValue.split(":")[0]);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawValue);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.key);
        sb.append(':');
        for (int val: this.array) {
            sb.append(val).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append('|');
        return sb.toString();
    }

    @Override
    public String getKey(){
        return this.rawValue.substring(0,this.rawValue.indexOf(':'));
    }

}
