package ru.nsu.torrent.storage;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import ru.nsu.torrent.domain.TorrentMetadata;
import ru.nsu.torrent.protocol.TorrentStats;
import ru.nsu.torrent.protocol.pieces.PieceCompleteListener;
import ru.nsu.torrent.protocol.pieces.PieceManager;


public final class DiskService {

    private final FileStorage storage;
    private final PieceManager pieceManager;
    private final TorrentStats stats;
    private final PieceCompleteListener onPieceComplete;
    private final TorrentMetadata metadata;
    private final ExecutorService diskExecutor = Executors.newSingleThreadExecutor();

    public DiskService(
        FileStorage storage,
        PieceManager pieceManager,
        TorrentStats stats,
        PieceCompleteListener onPieceComplete,
        TorrentMetadata metadata
    ) {
        this.storage = storage;
        this.pieceManager = pieceManager;
        this.stats = stats;
        this.onPieceComplete = onPieceComplete;
        this.metadata = metadata;
    }

    public void scanExistingFile() {
        System.out.print("Scanning for resume...");
        boolean[] have = new boolean[metadata.totalPieces()];
        int found = 0;
        for (int i = 0; i < metadata.totalPieces(); i++) {
            try {
                if (storage.verifyPiece(i)) {
                    have[i] = true;
                    found++;
                }
            } catch (Exception ignored) { }
        }
        if (found > 0) {
            pieceManager.applyResumeBitfield(have);
            System.out.printf(" resumed %d/%d pieces.%n", found, metadata.totalPieces());
        } else {
            System.out.println(" fresh start.");
        }
    }

    public void asyncWriteBlock(int pieceIndex, int begin, byte[] block) {
        diskExecutor.submit(() -> {
            try {
                storage.writeBlock(pieceIndex, begin, block);

                stats.bytesDownloaded.addAndGet(block.length);
                boolean complete = pieceManager.receiveBlock(pieceIndex, begin);

                if (complete) {
                    verifyAndFinalisePiece(pieceIndex);
                }
            } catch (IOException e) {
                System.err.println("[disk] write error piece=" + pieceIndex + ": " + e.getMessage());
            }
        });
    }

    public void asyncReadBlock(int pieceIndex, int begin, int length, Consumer<byte[]> callback) {
        diskExecutor.submit(() -> {
            try {
                byte[] data = storage.readBlock(pieceIndex, begin, length);
                stats.bytesUploaded.addAndGet(data.length);
                callback.accept(data);
            } catch (IOException e) {
                System.err.println("[disk] read error piece=" + pieceIndex + ": " + e.getMessage());
            }
        });
    }

    public void shutdown() {
        diskExecutor.shutdown();
        try {
            if (!diskExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                diskExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            diskExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        try {
            storage.close();
        } catch (IOException ignored) { }
    }

    private void verifyAndFinalisePiece(int pieceIndex) {
        try {
            boolean valid = storage.verifyPiece(pieceIndex);
            if (valid) {
                pieceManager.markDone(pieceIndex);
                int done = pieceManager.completedCount();
                System.out.printf("  ✓ Piece %d  [%d/%d]%n", pieceIndex, done, metadata.totalPieces());

                if (!stats.downloadComplete.get() && pieceManager.isComplete()) {
                    stats.downloadComplete.set(true);
                    System.out.println("\nDownload complete! Now seeding (Ctrl+C to stop).");
                }

                onPieceComplete.onPieceComplete(pieceIndex);
            } else {
                System.err.println("  ✗ Piece " + pieceIndex + " hash mismatch – retrying");
                pieceManager.markFailed(pieceIndex);
            }
        } catch (Exception e) {
            System.err.println("[disk] verify error piece=" + pieceIndex + ": " + e.getMessage());
            pieceManager.markFailed(pieceIndex);
        }
    }
}
