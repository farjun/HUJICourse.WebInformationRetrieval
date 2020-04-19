/* 
 * Reference arithmetic coding
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/reference-arithmetic-coding
 * https://github.com/nayuki/Reference-arithmetic-coding
 */
package webdata.iostreams;

import java.io.IOException;
import java.io.OutputStream;

public final class BitOutputStream implements AutoCloseable, AppOutputStream {

	public static final int MAX_BUFFER_SIZE = 8;
	private final OutputStream output;
	private int buffer;
	private int bufferCurCapacity;
	
	/**
	 * Constructs a bit output stream based on the specified byte output stream.
	 * @param out the output stream
	 */
	public BitOutputStream(OutputStream out) {
		output = out;
		buffer = 0;
		bufferCurCapacity = 0;
	}

	public void write(int b) throws IOException {
		buffer = (buffer << 1) | b;
		bufferCurCapacity++;
		if (bufferCurCapacity == MAX_BUFFER_SIZE) {
			resetAndFlushBuffer();
		}
	}
	
	private void resetAndFlushBuffer() throws IOException{
		output.write(buffer);
		buffer = 0;
		bufferCurCapacity = 0;
	}


	public void close() throws IOException {
		while (bufferCurCapacity != 0)
			write(0);
		if(output != null) {
			output.close();
		}
	}

	@Override
	public void flush() throws IOException {
		while (bufferCurCapacity != 0)
			write(0);
		output.flush();
	}

}
