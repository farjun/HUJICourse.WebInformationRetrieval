package webdata.iostreams;

import java.io.IOException;

public interface AppInputStream {
    int read() throws IOException;

    void close() throws IOException;
}
