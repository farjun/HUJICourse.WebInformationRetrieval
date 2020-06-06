package webdata.models;

import java.util.Objects;

public class SortableNode{
    protected String rawValue;

    public SortableNode(String rawValue){
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

}