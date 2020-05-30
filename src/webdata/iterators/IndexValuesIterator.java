package webdata.iterators;

import webdata.models.SortableNode;

import javax.swing.text.html.HTMLDocument;
import java.util.Iterator;


public class IndexValuesIterator implements Iterator<SortableNode> {

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public SortableNode next() {
        return null;
    }
}

