package webdata.models;

import java.util.Objects;

public class SortableNode{
    private String rawValue;

    public SortableNode(String rawValue){
        this.rawValue = rawValue;
    }

    public void merge(SortableNode other){
        rawValue = rawValue.concat(other.rawValue);
    }

    public int compare(SortableNode o) {
        return rawValue.compareTo(o.rawValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawValue);
    }

}