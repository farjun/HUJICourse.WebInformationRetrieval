package webdata.iostreams;

import java.io.IOException;

public interface AppOutputStream {
    void write(int b) throws IOException;
    void close() throws IOException;
    void flush()  throws IOException;
}
