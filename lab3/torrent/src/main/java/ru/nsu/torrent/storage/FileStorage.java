package ru.nsu.torrent.storage;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Arrays;

import ru.nsu.torrent.domain.TorrentMetadata;

public final class FileStorage {

    private final Path filePath;
    private final TorrentMetadata metadata;
    private RandomAccessFile raf;
    private FileChannel channel;

    public FileStorage(Path filePath, TorrentMetadata metadata) {
        this.filePath = filePath;
        this.metadata = metadata;
    }

    public void initialize() throws IOException {
        raf = new RandomAccessFile(filePath.toFile(), "rw");
        channel = raf.getChannel();
        if (raf.length() < metadata.fileLength()) {
            raf.setLength(metadata.fileLength());
        }
    }

    public void writeBlock(int pieceIndex, int blockOffset, byte[] data) throws IOException {
        long pos = (long) pieceIndex * metadata.pieceLength() + blockOffset;
        channel.write(ByteBuffer.wrap(data), pos);
    }

    public byte[] readPiece(int pieceIndex) throws IOException {
        long pos = (long) pieceIndex * metadata.pieceLength();
        int length = metadata.getPieceLength(pieceIndex);
        return readFully(pos, length);
    }

    public byte[] readBlock(int pieceIndex, int blockOffset, int length) throws IOException {
        long pos = (long) pieceIndex * metadata.pieceLength() + blockOffset;
        return readFully(pos, length);
    }

    public boolean verifyPiece(int pieceIndex) throws Exception {
        byte[] actual = MessageDigest.getInstance("SHA-1").digest(readPiece(pieceIndex));
        return Arrays.equals(actual, metadata.pieceHashes()[pieceIndex]);
    }

    public void close() throws IOException {
        if (channel != null && channel.isOpen()) {
            channel.force(true);
            channel.close();
        }
        if (raf != null) {
            raf.close();
        }
    }

    private byte[] readFully(long offset, int length) throws IOException {
        ByteBuffer buf  = ByteBuffer.allocate(length);
        int read = 0;
        while (read < length) {
            int n = channel.read(buf, offset + read);
            if (n == -1) break;
            read += n;
        }
        return buf.array();
    }
}
