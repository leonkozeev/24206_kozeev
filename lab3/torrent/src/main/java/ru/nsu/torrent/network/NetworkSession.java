package ru.nsu.torrent.network;

import java.io.IOException;
import java.nio.channels.SelectableChannel;

public interface NetworkSession {
    SelectableChannel sc();
    boolean hasPendingWrites();
    void flush() throws IOException;
    void close() throws IOException;
}
