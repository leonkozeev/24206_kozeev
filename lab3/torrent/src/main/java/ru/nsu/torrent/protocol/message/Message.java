package ru.nsu.torrent.protocol.message;

import java.nio.ByteBuffer;

public interface Message {
    ByteBuffer encode();
}
