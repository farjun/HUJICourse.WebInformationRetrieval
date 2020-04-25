package webdata.models;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

class FreqMapIterator implements Iterator<Integer> {
    final private Iterator<Map.Entry<Integer, Integer>> iterator;
    private int count;
    private Map.Entry<Integer, Integer> nextKeyVal;

    public  FreqMapIterator(Iterator<Map.Entry<Integer, Integer>> iterator){
        this.iterator = iterator;
        this.count = 0;
    }

    @Override
    public boolean hasNext() {
        if(this.count % 2 == 1) return true;
        return this.iterator.hasNext();
    }

    @Override
    public Integer next() {
        if(this.count % 2 == 0){
            this.nextKeyVal = this.iterator.next();
            this.count += 1;
            return nextKeyVal.getKey();
        }
        this.count += 1;
        return nextKeyVal.getValue();
    }
}

public class TokenFreqEnumeration implements Enumeration<Integer> {


    private FreqMapIterator iterator;

    public TokenFreqEnumeration(FreqMapIterator iterator){
        this.iterator = iterator;
    }

    public TokenFreqEnumeration(TreeMap<Integer,Integer> map) {
        this.iterator = new FreqMapIterator(map.entrySet().iterator());
    }

    @Override
    public boolean hasMoreElements() {
        return iterator.hasNext();
    }

    @Override
    public Integer nextElement() {
        return (Integer)iterator.next();
    }

}
