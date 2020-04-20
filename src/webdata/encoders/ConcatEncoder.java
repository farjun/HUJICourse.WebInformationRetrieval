package webdata.encoders;
import java.nio.CharBuffer;
import java.util.ArrayList;

// preparation to any compression method
public class ConcatEncoder {

    private int bufferSize;
    private String[] words;
    private ArrayList<Integer> pointers; // TODO: convert to deltas
    private CharBuffer buffer;

    public long getBufferSize() {
        return bufferSize;
    }

    public String[] getWords() {
        return words;
    }

    public ArrayList<Integer> getPointers() {
        return pointers;
    }

    public CharBuffer getBuffer() {
        return buffer;
    }

    public ConcatEncoder(int globalNumOfChars, String[] words){
        this.pointers = new ArrayList<Integer>();
//        this.bufferSize = globalNumOfChars * Character.BYTES;
        this.words = words;
        this.buffer = CharBuffer.allocate(globalNumOfChars);
        int curr = 0;
        for(var word: words){
            this.pointers.add(curr);
            curr += word.length();
            buffer.put(word);
        }
    }

}
