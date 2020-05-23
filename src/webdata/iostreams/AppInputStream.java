package webdata.iostreams;

import java.io.IOException;

public interface AppInputStream {
    int read() throws IOException;
    boolean hasMoreInput();
    void close() throws IOException;
}
