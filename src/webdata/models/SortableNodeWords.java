package webdata.models;

import java.util.Objects;

public class SortableNodeWords extends SortableNode {
    // super class has "rawValue" String variable.
    public SortableNodeWords(String rawValue){
        super(rawValue);
    }

    public void merge(SortableNode other){
        // the|50|{1:45,3:2};
        // the|2|{7:10,9:2};
        // the|52|{1:45,3:2,7:10,9:2};
        String[] thisEntryDataStrs = this.rawValue.split("\\|");
        String[] otherEntryDataStrs = other.rawValue.split("\\|");

        int thisGlobFreq = Integer.parseInt(thisEntryDataStrs[1]);
        int otherGlobFreq = Integer.parseInt(otherEntryDataStrs[1]);

        String mergedFreqJSON = thisEntryDataStrs[2].
                concat(otherEntryDataStrs[2]).
                replace("}{",",");
        StringBuilder sb = new StringBuilder();

        this.rawValue = sb.
                append(thisEntryDataStrs[0]).
                append("|").
                append(thisGlobFreq+otherGlobFreq).
                append("|").
                append(mergedFreqJSON).
                toString();
    }

    @Override
    public int compare(SortableNode o) {
        try {
            int thisTokenEndIndex = this.rawValue.indexOf("|");
            String thisToken = this.rawValue.substring(0, thisTokenEndIndex);
            int otherTokenEndIndex = o.toString().indexOf("|");
            String otherToken = o.toString().substring(0, otherTokenEndIndex);
            return thisToken.compareTo(otherToken);
        }catch (Exception e){
            System.out.println("error in words sortable node compare");
            throw e;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawValue);
    }

    @Override
    public String toString(){
        if(rawValue.charAt(rawValue.length()-1) != ';')
            return rawValue.concat(";");
        else
            return rawValue;
    }

    @Override
    public String getKey(){
        return this.rawValue.substring(0,this.rawValue.indexOf('|'));
    }

}