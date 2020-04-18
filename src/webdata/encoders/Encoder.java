package webdata.encoders;

interface EncoderInterface { // not sure if redundant, keeping it for now
    public int encode(String text);
    public void decode(Object[] compressed);// keeping Object for now

}
// preparation to any compression method
public class Encoder implements EncoderInterface {

    @Override
    public int encode(String text) {
        /*
        reviewId (counter) | score | helpfullness (numerator, denumenetor) | length
        score - 2 byte
        reviewId - unlimited, <= 8 bytes (can be dynamic)
        helpfulnessNum - 4 byte
        helpfulnessDen - 4 byte
        length - 4 bytes ( we can assume fixed length ? )
        d-bytes | 2 byte | 1 byte | 1 byte | 4 byte (?)
        */

        return 0;
    }

    @Override
    public void decode(Object[] compressed) {

    }
}
