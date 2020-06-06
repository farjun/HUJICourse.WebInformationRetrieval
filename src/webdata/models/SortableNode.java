package webdata.models;

import java.util.Objects;

public class SortableNode implements Comparable {
    public int fromIter;
    protected String rawValue;

    public SortableNode(int fromIter, String rawValue){
        this.fromIter = fromIter;
        this.rawValue = rawValue;
    }

    public void merge(SortableNode other){
        System.out.println("Not Calleble Method!!");
    }

    public int compare(SortableNode o) {
        return rawValue.compareTo(o.rawValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawValue);
    }

    @Override
    public String toString(){
        return rawValue;
    }

    public String getKey(){
        return "";
    }

    @Override
    public int compareTo(Object o) {
        return this.compare((SortableNode) o);
    }
}