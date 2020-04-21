package webdata.models;

import java.util.ArrayList;
import java.util.Enumeration;

public class CompressedArrayList  {
    private final ArrayList<String> array;
    private int consecutiveSum;
    private int iteratorCounter;

    public CompressedArrayList(){
        this.array = new ArrayList<>();
        this.iteratorCounter = 0;
        this.consecutiveSum = 0; 
    }

    public CompressedArrayList(String compressdArray){
        this();
        for (String element: compressdArray.split(",")) {
            this.addElement(element);
        }
    }
    private void addElement(String element){
        this.array.add(element);
    }

    public String get(int i){
        return this.array.get(i);
    }

    public int size(){
        return this.array.size();
    }

    public void add(int number){
        this.add(String.valueOf(number));
    }

    public void add(String number){
        String difference = String.valueOf(Math.abs(Integer.parseInt(number) - this.consecutiveSum));
        this.consecutiveSum += Integer.parseInt(difference);
        this.array.add(difference);

    }

    public Enumeration<Integer> iter(){
        return new CompressedArrayListIterator(this);
    }


    @Override
    public String toString() {
        return this.toCompressedString();
    }

    public String toCompressedString() {
        return String.join(",", this.array);
    }
}

class CompressedArrayListIterator  implements Enumeration<Integer>{

    private int iteratorCounter;
    private int consecutiveSum;
    private final CompressedArrayList array;

    public CompressedArrayListIterator(CompressedArrayList array){
        this.array = array;
        this.iteratorCounter = 0;
        this.consecutiveSum = 0;
    }

    @Override
    public boolean hasMoreElements() {
        return this.array.size() > this.iteratorCounter;
    }

    @Override
    public Integer nextElement() {
        String element = this.array.get(this.iteratorCounter);
        this.consecutiveSum += Integer.parseInt(element);
        this.iteratorCounter++;
        return this.consecutiveSum;
    }
}
