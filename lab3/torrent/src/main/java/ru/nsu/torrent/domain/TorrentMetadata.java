package ru.nsu.torrent.domain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import ru.nsu.torrent.protocol.pieces.BlockRequest;

/**
 * Immutable metadata parsed from a {@code .torrent} file.
 *
 * <p>Fix: {@code pieceLength} is validated to fit in a positive {@code int}
 * (max ~2 GiB) rather than silently truncating on the cast.
 */
public record TorrentMetadata(
    String announceUrl,
    byte[] infoHash,
    int pieceLength,
    byte[][] pieceHashes,
    long fileLength,
    String fileName,
    int totalPieces,
    int lastPieceLength
) {

    private TorrentMetadata(
        String announceUrl,
        byte[] infoHash,
        int pieceLength,
        byte[][] pieceHashes,
        long fileLength,
        String fileName
    ) {
        this(
            announceUrl,
            infoHash,
            pieceLength,
            pieceHashes,
            fileLength,
            fileName,
            pieceHashes.length,
            (int) (fileLength - (long) (pieceHashes.length - 1) * pieceLength)
        );
    }

    @SuppressWarnings("unchecked")
    public static TorrentMetadata of(byte[] raw) throws Exception {
        Map<String, byte[]> rawValues = new HashMap<>();
        BencodeDecoder decoder = new BencodeDecoder(raw);
        Map<String, Object> root = decoder.decodeDictionary(rawValues);

        byte[] infoHash = MessageDigest.getInstance("SHA-1").digest(rawValues.get("info"));
        String announce = new String((byte[]) root.get("announce"), StandardCharsets.UTF_8).trim();

        Map<String, Object> info = (Map<String, Object>) root.get("info");
        String name = new String((byte[]) info.get("name"), StandardCharsets.UTF_8);
        long fileLength = (Long) info.get("length");
        long pieceLengthLong = (Long) info.get("piece length");

        if (pieceLengthLong <= 0 || pieceLengthLong > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                "Unsupported piece length: " + pieceLengthLong);
        }
        int pieceLength = (int) pieceLengthLong;

        byte[] piecesRaw = (byte[]) info.get("pieces");
        int numPieces = piecesRaw.length / 20;
        byte[][] pieceHashes = new byte[numPieces][20];
        for (int i = 0; i < numPieces; i++) {
            System.arraycopy(piecesRaw, i * 20, pieceHashes[i], 0, 20);
        }

        return new TorrentMetadata(
            announce, infoHash, pieceLength, pieceHashes, fileLength, name);
    }

    public int getPieceLength(int index) {
        return (index == totalPieces - 1) ? lastPieceLength : pieceLength;
    }

    public int getBlockCount(int pieceIndex) {
        return (getPieceLength(pieceIndex) + BlockRequest.BLOCK_SIZE - 1) / BlockRequest.BLOCK_SIZE;
    }

    public int getBlockLength(int pieceIndex, int blockIndex) {
        return Math.min(
            BlockRequest.BLOCK_SIZE,
            getPieceLength(pieceIndex) - blockIndex * BlockRequest.BLOCK_SIZE);
    }
}
