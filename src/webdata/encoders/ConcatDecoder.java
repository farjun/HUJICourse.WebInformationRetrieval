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

    public ConcatDecoder(CharBuffer buffer, ArrayList<Integer> pointers) throws Exception {
        this.buffer = buffer;
        int curr = 0;
        this.buffer.rewind();
        String concatStr = buffer.toString();
        words = new String[pointers.size()];
        //TODO read from buffer
        if(pointers.size() > 0){
            for(var i=0; i<pointers.size(); ++i){
                var ptrFirst = pointers.get(i);
                if(i == pointers.size() - 1 ){
                    words[i] = concatStr.substring(ptrFirst);
                }
                else{
                    var ptrSec = pointers.get(i+1);
                    words[i] = concatStr.substring(ptrFirst, ptrSec);
                }

            }
        }
        else {
            throw new Exception("pointers list is empty");
        }
    }

}
