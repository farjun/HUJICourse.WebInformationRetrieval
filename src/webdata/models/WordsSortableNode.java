package webdata.models;

import java.util.Objects;

public class WordsSortableNode extends SortableNode {
    // super class has "rawValue" String variable.
    public WordsSortableNode(String rawValue){
        super(rawValue);
    }
    public void merge(WordsSortableNode other){
        // the|50|{1:45,3:2};
        // the|2|{7:10,9:2};
        // the|52|{1:45,3:2,7:10,9:2};
        var thisEntryDataStrs = this.rawValue.split("|");
        var otherEntryDataStrs = other.toString().split("|");

        int thisGlobFreq = Integer.parseInt(thisEntryDataStrs[1]);
        int otherGlobFreq = Integer.parseInt(otherEntryDataStrs[1]);

        String mergedFreqJSON = thisEntryDataStrs[2].
                concat(otherEntryDataStrs[2]).
                replace("};{",",");
        StringBuilder sb = new StringBuilder();
        this.rawValue = sb.
                append(thisEntryDataStrs[0]).
                append(thisGlobFreq+otherGlobFreq).
                append(mergedFreqJSON).
                toString();
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
        return rawValue.toString();
    }

}