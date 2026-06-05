package ru.nsu.torrent.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import ru.nsu.torrent.protocol.message.Message;
import ru.nsu.torrent.protocol.message.MessageDecoder;

public final class PeerChannel {
    private static final int READ_BUFFER_SIZE = 65_536;

    private final SocketChannel channel;
    private final MessageDecoder decoder = new MessageDecoder();
    private final ByteBuffer readBuffer = ByteBuffer.allocateDirect(READ_BUFFER_SIZE);
    private final Deque<ByteBuffer> writeQueue = new ArrayDeque<>();
    private final String remoteAddress;

    public PeerChannel(SocketChannel channel, String remoteAddress) {
        this.channel = channel;
        this.remoteAddress = remoteAddress;
    }

    public SocketChannel socketChannel() {
        return channel;
    }
    public String remoteAddress() {
        return remoteAddress;
    }

    public String remoteIp() {
        return remoteAddress
            .replaceFirst("^/", "")
            .replaceFirst(":\\d+$", "");
    }

    public int read() throws IOException {
        readBuffer.clear();
        int n = channel.read(readBuffer);
        if (n > 0) {
            readBuffer.flip();
            decoder.feed(readBuffer);
        }
        return n;
    }

    public List<Message> drain() {
        return decoder.drain();
    }

    public void enqueue(ByteBuffer buf) {
        writeQueue.addLast(buf);
    }

    public boolean flush() throws IOException {
        while (!writeQueue.isEmpty()) {
            ByteBuffer buf = writeQueue.peekFirst();
            channel.write(buf);
            if (buf.hasRemaining()) return false;
            writeQueue.pollFirst();
        }
        return true;
    }

    public boolean hasPendingWrites() {
        return !writeQueue.isEmpty();
    }

    public void close() throws IOException {
        channel.close();
    }
}
