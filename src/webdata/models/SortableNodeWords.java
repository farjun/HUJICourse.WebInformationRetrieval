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
        String[] otherEntryDataStrs = other.toString().split("\\|");

        int thisGlobFreq = Integer.parseInt(thisEntryDataStrs[1]);
        thisEntryDataStrs[1] = null; // dropping pointer
        int otherGlobFreq = Integer.parseInt(otherEntryDataStrs[1]);
        otherEntryDataStrs[1] = null; // dropping pointer

        otherEntryDataStrs[2] = otherEntryDataStrs[2].substring(1); // deleting "{"
        StringBuilder mergedFreqJSON = new StringBuilder();
        mergedFreqJSON.append(thisEntryDataStrs[2]).
                replace(mergedFreqJSON.length()-2, mergedFreqJSON.length(),",");
        thisEntryDataStrs[2] = null; // dropping pointer
        mergedFreqJSON.append(otherEntryDataStrs[2]);
        otherEntryDataStrs[2] = null; // dropping pointer
        StringBuilder sb = new StringBuilder();
        sb.append(thisEntryDataStrs[0]).
                append("|").
                append(thisGlobFreq+otherGlobFreq).
                append("|").
                append(mergedFreqJSON);
        mergedFreqJSON = null; // dropping pointer
        this.rawValue = sb.toString();
    }

    @Override
    public int compare(SortableNode o) {
        int thisTokenEndIndex = this.rawValue.indexOf("|");
        String thisToken = this.rawValue.substring(0, thisTokenEndIndex);
        int otherTokenEndIndex = o.toString().indexOf("|");
        String otherToken = o.toString().substring(0, otherTokenEndIndex);
        return thisToken.compareTo(otherToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawValue);
    }

    @Override
    public String toString(){
        return rawValue;
    }

    @Override
    public String getKey(){
        return this.rawValue.substring(0,this.rawValue.indexOf('|'));
    }

}