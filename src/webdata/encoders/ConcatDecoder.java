package webdata.encoders;

import java.nio.CharBuffer;
import java.util.ArrayList;

public class ConcatDecoder {

    private String[] words;
    private CharBuffer buffer;

    public String[] getWords() {
        return words;
    }


    public CharBuffer getBuffer() {
        return buffer;
    }

    public ConcatDecoder(CharBuffer buffer, ArrayList<Integer> pointers){
        this.buffer = buffer;
        int curr = 0;
        this.buffer.rewind();
        String concatStr = buffer.toString();
        words = new String[pointers.size()];
        //TODO read from buffer
    }

}
