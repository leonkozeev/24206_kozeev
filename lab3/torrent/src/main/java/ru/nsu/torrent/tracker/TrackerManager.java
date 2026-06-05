package ru.nsu.torrent.tracker;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ru.nsu.torrent.domain.PeerAddress;
import ru.nsu.torrent.network.IoReactor;
import ru.nsu.torrent.protocol.TorrentStats;
import ru.nsu.torrent.protocol.peer.PeerManager;


public final class TrackerManager {

    private final TrackerClient trackerClient;
    private final TorrentStats stats;
    private final IoReactor reactor;
    private final PeerManager peerManager;

    private final ExecutorService trackerExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "tracker");
        t.setDaemon(true);
        return t;
    });

    public TrackerManager(
        TrackerClient trackerClient,
        TorrentStats stats,
        IoReactor reactor,
        PeerManager peerManager
    ) {
        this.trackerClient = trackerClient;
        this.stats = stats;
        this.reactor = reactor;
        this.peerManager = peerManager;
    }

    public void asyncAnnounce() {
        trackerExecutor.submit(this::announce);
    }

    public void shutdown() {
        trackerExecutor.shutdown();
        try {
            if (!trackerExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                trackerExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            trackerExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }


    private void announce() {
        try {
            List<PeerAddress> peers = trackerClient.announce(
                stats.bytesDownloaded.get(),
                stats.bytesUploaded.get()
            );
            System.out.println("Tracker: " + peers.size() + " peers");
            reactor.schedule(() -> peers.forEach(peerManager::connectToPeer));
        } catch (Exception e) {
            System.err.println("[tracker] announce error: " + e.getMessage());
        }
    }
}
