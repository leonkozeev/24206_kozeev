package ru.nsu.torrent.app;

import java.io.IOException;

import ru.nsu.torrent.network.IoReactor;
import ru.nsu.torrent.protocol.TorrentStats;
import ru.nsu.torrent.protocol.pieces.PieceManager;
import ru.nsu.torrent.storage.DiskService;
import ru.nsu.torrent.tracker.TrackerManager;

/**
 * Top-level application handle.  Owns the lifecycle of all major subsystems.
 */
public final class TorrentClient {

    private final IoReactor      reactor;
    private final DiskService    diskService;
    private final TrackerManager trackerManager;
    private final PieceManager   pieceManager;
    private final TorrentStats   stats;

    public TorrentClient(
        IoReactor reactor,
        DiskService diskService,
        TrackerManager trackerManager,
        PieceManager pieceManager,
        TorrentStats stats
    ) {
        this.reactor = reactor;
        this.diskService = diskService;
        this.trackerManager = trackerManager;
        this.pieceManager = pieceManager;
        this.stats = stats;
    }

    public void start() throws IOException {
        diskService.scanExistingFile();

        stats.downloadComplete.set(pieceManager.isComplete());
        if (stats.downloadComplete.get()) {
            System.out.println("Already complete — seeding... (Ctrl+C to stop)");
        }

        trackerManager.asyncAnnounce();
        reactor.run();
    }

    public void shutdown() {
        System.out.println("Shutting down...");
        reactor.stop();
        trackerManager.shutdown();
        diskService.shutdown();
        reactor.shutdown();
        System.out.println("Stopped.");
    }
}
