package webdata.indexwriters;

interface EncoderInterface { // not sure if redundant, keeping it for now
    public int encode(String text);
    public void decode(Object[] compressed);// keeping Object for now

}

public class Encoder implements EncoderInterface {

    @Override
    public int encode(String text) {
        /*
        review - id (counter) | score | helpfullness (numerator, denumenetor) | length
        score - 1 byte
        reviewId - unlimited, <= 8 bytes (can be dynamic)
        helpfulnessNum - 1 byte
        helpfulnessDen - 1 byte
        length - 4 bytes ( we can assume fixed length ? )
        */
        return 0;
    }

    @Override
    public void decode(Object[] compressed) {

    }
}
