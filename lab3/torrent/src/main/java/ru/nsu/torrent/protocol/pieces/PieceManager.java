package ru.nsu.torrent.protocol.pieces;

import java.util.Arrays;

import ru.nsu.torrent.domain.TorrentMetadata;


public class PieceManager {
    private final TorrentMetadata metadata;
    private final PieceStatus[] pieceStatus;
    private final int[] peerCount;
    private final int[] blockCount;
    private final int[] freeBlockCount;
    private final boolean[][] blockReceived;
    private final boolean[][] blockRequested;
    private final Object lock = new Object();
    private int completedPieces = 0;

    public PieceManager(TorrentMetadata metadata) {
        this.metadata = metadata;
        int n = metadata.totalPieces();
        pieceStatus = new PieceStatus[n];
        peerCount = new int[n];
        blockCount = new int[n];
        freeBlockCount = new int[n];
        blockReceived = new boolean[n][];
        blockRequested = new boolean[n][];

        Arrays.fill(pieceStatus, PieceStatus.MISSING);

        for (int i = 0; i < n; i++) {
            blockCount[i] = metadata.getBlockCount(i);
            freeBlockCount[i] = blockCount[i];
            blockReceived[i] = new boolean[blockCount[i]];
            blockRequested[i] = new boolean[blockCount[i]];
        }
    }

    public void applyResumeBitfield(boolean[] verified) {
        synchronized (lock) {
            for (int i = 0; i < Math.min(verified.length, metadata.totalPieces()); i++) {
                if (verified[i] && pieceStatus[i] != PieceStatus.DONE) {
                    pieceStatus[i] = PieceStatus.DONE;
                    Arrays.fill(blockReceived[i], true);
                    Arrays.fill(blockRequested[i], true);
                    freeBlockCount[i] = 0;
                    completedPieces++;
                }
            }
        }
    }

    public void registerPeerBitfield(boolean[] peerBf, byte[] raw) {
        synchronized (lock) {
            boolean[] prev = peerBf.clone();
            for (int i = 0; i < metadata.totalPieces(); i++) {
                int byteIdx = i / 8;
                boolean has = byteIdx < raw.length && (raw[byteIdx] & (0x80 >>> (i % 8))) != 0;
                peerBf[i] = has;
                if (has  && !prev[i]) peerCount[i]++;
                if (!has && prev[i])  peerCount[i]--;
            }
        }
    }

    public void registerPeerHave(boolean[] peerBf, int pieceIndex) {
        synchronized (lock) {
            if (pieceIndex < peerBf.length && !peerBf[pieceIndex]) {
                peerBf[pieceIndex] = true;
                peerCount[pieceIndex]++;
            }
        }
    }

    public void removePeer(boolean[] peerBf) {
        synchronized (lock) {
            if (peerBf == null) return;
            for (int i = 0; i < Math.min(peerBf.length, metadata.totalPieces()); i++) {
                if (peerBf[i] && peerCount[i] > 0) peerCount[i]--;
            }
        }
    }

    public void requeueBlocks(Iterable<BlockRequest> inFlightRequests) {
        synchronized (lock) {
            for (BlockRequest req : inFlightRequests) {
                int pi = req.pieceIndex();
                int bi = req.blockOffset() / BlockRequest.BLOCK_SIZE;
                if (pi >= metadata.totalPieces()) continue;
                if (blockRequested[pi][bi] && !blockReceived[pi][bi]) {
                    blockRequested[pi][bi] = false;
                    freeBlockCount[pi]++;

                    if (
                        pieceStatus[pi] == PieceStatus.VERIFYING ||
                        pieceStatus[pi] == PieceStatus.MISSING
                    ) {
                        pieceStatus[pi] = PieceStatus.DOWNLOADING;
                    }
                }
            }
        }
    }

    public BlockRequest nextBlockFor(boolean[] peerBf) {
        synchronized (lock) {
            if (peerBf == null) return null;

            int best = findRarestPiece(peerBf);
            if (best == -1) return null;

            if (pieceStatus[best] == PieceStatus.MISSING) {
                pieceStatus[best] = PieceStatus.DOWNLOADING;
            }

            for (int b = 0; b < blockCount[best]; b++) {
                if (!blockReceived[best][b] && !blockRequested[best][b]) {
                    blockRequested[best][b] = true;
                    freeBlockCount[best]--;
                    int offset = b * BlockRequest.BLOCK_SIZE;
                    int len = metadata.getBlockLength(best, b);
                    return new BlockRequest(best, offset, len);
                }
            }
            return null;
        }
    }

    private int findRarestPiece(boolean[] peerBf) {
        int bestPiece = -1;
        int bestCount = Integer.MAX_VALUE;

        for (int i = 0; i < metadata.totalPieces(); i++) {
            if (i >= peerBf.length || !peerBf[i]) continue;
            PieceStatus s = pieceStatus[i];
            if (s == PieceStatus.DONE || s == PieceStatus.VERIFYING) continue;
            if (freeBlockCount[i] == 0) continue;
            if (peerCount[i] < bestCount) {
                bestCount = peerCount[i];
                bestPiece = i;
            }
        }
        return bestPiece;
    }

    public boolean receiveBlock(int pieceIndex, int blockOffset) {
        synchronized (lock) {
            if (pieceIndex >= metadata.totalPieces()) return false;

            PieceStatus s = pieceStatus[pieceIndex];
            if (s == PieceStatus.DONE || s == PieceStatus.VERIFYING) return false;

            int bi = blockOffset / BlockRequest.BLOCK_SIZE;
            if (blockReceived[pieceIndex][bi]) return false;

            blockReceived[pieceIndex][bi] = true;

            for (boolean received : blockReceived[pieceIndex]) {
                if (!received) return false;
            }
            pieceStatus[pieceIndex] = PieceStatus.VERIFYING;
            return true;
        }
    }

    public void markDone(int pieceIndex) {
        synchronized (lock) {
            if (pieceStatus[pieceIndex] != PieceStatus.DONE) {
                pieceStatus[pieceIndex] = PieceStatus.DONE;
                completedPieces++;
            }
        }
    }

    public void markFailed(int pieceIndex) {
        synchronized (lock) {
            pieceStatus[pieceIndex] = PieceStatus.MISSING;
            Arrays.fill(blockReceived[pieceIndex],  false);
            Arrays.fill(blockRequested[pieceIndex], false);
            freeBlockCount[pieceIndex] = blockCount[pieceIndex];
        }
    }

    public boolean weHave(int pieceIndex) {
        synchronized (lock) {
            return pieceIndex < metadata.totalPieces()
                && pieceStatus[pieceIndex] == PieceStatus.DONE;
        }
    }

    public boolean isComplete() {
        synchronized (lock) {
            return completedPieces >= metadata.totalPieces();
        }
    }

    public int completedCount() {
        synchronized (lock) {
            return completedPieces;
        }
    }

    public boolean peerHasAnythingWeNeed(boolean[] peerBf) {
        synchronized (lock) {
            if (peerBf == null) return false;
            for (int i = 0; i < metadata.totalPieces() && i < peerBf.length; i++) {
                if (peerBf[i] && pieceStatus[i] != PieceStatus.DONE) return true;
            }
            return false;
        }
    }

    public byte[] buildBitfield() {
        synchronized (lock) {
            byte[] out = new byte[(metadata.totalPieces() + 7) / 8];
            for (int i = 0; i < metadata.totalPieces(); i++) {
                if (pieceStatus[i] == PieceStatus.DONE) {
                    out[i / 8] |= (byte) (0x80 >>> (i % 8));
                }
            }
            return out;
        }
    }
}
