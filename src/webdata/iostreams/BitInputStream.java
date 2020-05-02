/* 
 * Reference arithmetic coding
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/reference-arithmetic-coding
 * https://github.com/nayuki/Reference-arithmetic-coding
 */
package webdata.iostreams;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.stream.Stream;


/**
 * A stream of bits that can be read. Because they come from an underlying byte stream,
 * the total number of bits is always a multiple of 8. The bits are read in big endian.
 * Mutable and not thread-safe.
 * @see BitOutputStream
 */
public final class BitInputStream implements AutoCloseable, AppInputStream {
	
	/*---- Fields ----*/
	
	// The underlying byte stream to read from (not null).
	private InputStream input;
	
	// Either in the range [0x00, 0xFF] if bits are available, or -1 if end of stream is reached.
	private int currentByte;
	
	// Number of remaining bits in the current byte, always between 0 and 7 (inclusive).
	private int numBitsRemaining;

	/**
	 * Constructs a bit input stream based on the specified byte input stream.
	 * @param in the byte input stream
	 */
	public BitInputStream(InputStream in) {
		input = Objects.requireNonNull(in);
		currentByte = 0;
		numBitsRemaining = 0;
	}

	/**
	 * Reads a bit from this stream. Returns 0 or 1 if a bit is available, or -1 if
	 * the end of stream is reached. The end of stream always occurs on a byte boundary.
	 * @return the next bit of 0 or 1, or -1 for the end of stream
	 */
	public int read() throws IOException {
		if (currentByte == -1)
			return -1;
		if (numBitsRemaining == 0) {
			currentByte = input.read();
			if (currentByte == -1)
				return -1;
			numBitsRemaining = 8;
		}
		numBitsRemaining--;
		return (currentByte >>> numBitsRemaining) & 1;
	}
	
	
	/**
	 * Closes this stream and the underlying input stream.
	 * @throws IOException if an I/O exception occurred
	 */
	public void close() throws IOException {
		input.close();
		currentByte = -1;
		numBitsRemaining = 0;
	}
	
}
