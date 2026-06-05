package ru.nsu.torrent.network;

import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.List;

import ru.nsu.torrent.domain.TorrentMetadata;
import ru.nsu.torrent.protocol.TorrentStats;
import ru.nsu.torrent.protocol.message.KeepAliveMessage;
import ru.nsu.torrent.protocol.peer.PeerConnection;
import ru.nsu.torrent.protocol.pieces.PieceManager;
import ru.nsu.torrent.tracker.TrackerManager;

public final class IntervalTicker implements TickListener {
    private static final long TRACKER_INTERVAL_MS = 30_000;
    private static final long KEEPALIVE_INTERVAL_MS = 110_000;
    private static final long CONNECT_TIMEOUT_MS = 20_000;
    private static final long PROGRESS_INTERVAL_MS = 5_000;

    private final IoReactor reactor;
    private final TrackerManager trackerManager;
    private final PieceManager pieceManager;
    private final TorrentMetadata metadata;
    private final TorrentStats stats;

    private long lastTrackerAnnounce;
    private long lastKeepalive;
    private long lastProgress;

    public IntervalTicker(
        IoReactor reactor,
        TrackerManager trackerManager,
        PieceManager pieceManager,
        TorrentMetadata metadata,
        TorrentStats stats,
        long startTime
    ) {
        this.reactor = reactor;
        this.trackerManager = trackerManager;
        this.pieceManager = pieceManager;
        this.metadata = metadata;
        this.stats = stats;
        this.lastTrackerAnnounce = startTime;
        this.lastKeepalive = startTime;
        this.lastProgress = startTime;
    }

    @Override
    public void onTick(long now) {
        if (now - lastTrackerAnnounce > TRACKER_INTERVAL_MS) {
            lastTrackerAnnounce = now;
            trackerManager.asyncAnnounce();
        }
        if (now - lastKeepalive > KEEPALIVE_INTERVAL_MS) {
            lastKeepalive = now;
            sendKeepalives();
        }
        if (now - lastProgress > PROGRESS_INTERVAL_MS) {
            lastProgress = now;
            printProgress();
        }
        evictStalled(now);
    }

    private void sendKeepalives() {
        for (SelectionKey key : reactor.selector().keys()) {
            if (key.isValid()
                    && key.attachment() instanceof PeerConnection conn
                    && conn.isHandshakeComplete()) {
                conn.send(KeepAliveMessage.INSTANCE);
            }
        }
    }

    private void printProgress() {
        int done  = pieceManager.completedCount();
        int total = metadata.totalPieces();
        int peers = (int) reactor.selector().keys().stream()
                .filter(k -> k.isValid() && k.attachment() instanceof PeerConnection)
                .count();
        System.out.printf(
            "Progress: %d/%d (%.1f%%) | peers=%d | dl=%s | ul=%s%n",
            done, total,
            total > 0 ? (double) done / total * 100 : 0.0,
            peers,
            fmt(stats.bytesDownloaded.get()),
            fmt(stats.bytesUploaded.get())
        );
    }

    private void evictStalled(long now) {
        List<PeerConnection> toEvict = new ArrayList<>();

        for (SelectionKey key : reactor.selector().keys()) {
            if (key.isValid() && key.attachment() instanceof PeerConnection conn) {
                boolean timedOut = !conn.isHandshakeComplete() && (now - conn.connectedAt) > CONNECT_TIMEOUT_MS;
                if (timedOut) toEvict.add(conn);
            }
        }

        for (PeerConnection conn : toEvict) {
            reactor.handler().onDisconnected(conn);
        }
    }

    private static String fmt(long bytes) {
        if (bytes < 1_024) return bytes + " B";
        if (bytes < 1_024 * 1_024) return String.format("%.1f KB", bytes / 1_024.0);
        if (bytes < 1_024L * 1_024 * 1_024) return String.format("%.1f MB", bytes / (1_024.0 * 1_024));
        return String.format("%.2f GB", bytes / (1_024.0 * 1_024 * 1_024));
    }
}
