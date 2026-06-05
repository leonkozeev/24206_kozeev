package ru.nsu.torrent.app;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import ru.nsu.torrent.domain.TorrentMetadata;
import ru.nsu.torrent.network.ConnectionHandler;
import ru.nsu.torrent.network.IntervalTicker;
import ru.nsu.torrent.network.IoReactor;
import ru.nsu.torrent.network.NetworkSession;
import ru.nsu.torrent.network.TickListener;
import ru.nsu.torrent.network.TorrentHandler;
import ru.nsu.torrent.protocol.TorrentStats;
import ru.nsu.torrent.protocol.handlel.BitfieldHandler;
import ru.nsu.torrent.protocol.handlel.ChokeHandler;
import ru.nsu.torrent.protocol.handlel.HandshakeHandler;
import ru.nsu.torrent.protocol.handlel.HaveHandler;
import ru.nsu.torrent.protocol.handlel.InterestedHandler;
import ru.nsu.torrent.protocol.handlel.KeepAliveHandler;
import ru.nsu.torrent.protocol.handlel.MessageDispatcher;
import ru.nsu.torrent.protocol.handlel.NotInterestedHandler;
import ru.nsu.torrent.protocol.handlel.PieceHandler;
import ru.nsu.torrent.protocol.handlel.RequestHandler;
import ru.nsu.torrent.protocol.handlel.UnchokeHandler;
import ru.nsu.torrent.protocol.message.BitfieldMessage;
import ru.nsu.torrent.protocol.message.ChokeMessage;
import ru.nsu.torrent.protocol.message.HandshakeMessage;
import ru.nsu.torrent.protocol.message.HaveMessage;
import ru.nsu.torrent.protocol.message.InterestedMessage;
import ru.nsu.torrent.protocol.message.KeepAliveMessage;
import ru.nsu.torrent.protocol.message.NotInterestedMessage;
import ru.nsu.torrent.protocol.message.PieceMessage;
import ru.nsu.torrent.protocol.message.RequestMessage;
import ru.nsu.torrent.protocol.message.UnchokeMessage;
import ru.nsu.torrent.protocol.peer.PeerConnection;
import ru.nsu.torrent.protocol.peer.PeerManager;
import ru.nsu.torrent.protocol.peer.TorrentListener;
import ru.nsu.torrent.protocol.pieces.PieceCompleteListener;
import ru.nsu.torrent.protocol.pieces.PieceManager;
import ru.nsu.torrent.storage.DiskService;
import ru.nsu.torrent.storage.FileStorage;
import ru.nsu.torrent.tracker.TrackerClient;
import ru.nsu.torrent.tracker.TrackerManager;

public final class TorrentClientBuilder {

    private TorrentMetadata metadata;
    private FileStorage storage;
    private int port = 6881;

    public TorrentClientBuilder metadata(TorrentMetadata metadata) {
        this.metadata = metadata;
        return this;
    }

    public TorrentClientBuilder storage(FileStorage storage) {
        this.storage = storage;
        return this;
    }

    public TorrentClientBuilder port(int port) {
        this.port = port;
        return this;
    }

    public TorrentClient build() throws IOException {
        Objects.requireNonNull(metadata, "metadata must be provided");
        Objects.requireNonNull(storage, "storage must be provided");

        byte[] peerId = generatePeerId();
        TorrentStats stats = new TorrentStats();
        PieceManager pieceManager = new PieceManager(metadata);
        TrackerClient trackerClient = new TrackerClient(metadata, peerId, port);

        MessageDispatcher dispatcher = new MessageDispatcher(Map.of(
            HandshakeMessage.class, new HandshakeHandler(pieceManager, metadata.infoHash(), peerId),
            KeepAliveMessage.class, new KeepAliveHandler(),
            BitfieldMessage.class, new BitfieldHandler(pieceManager),
            InterestedMessage.class, new InterestedHandler(),
            NotInterestedMessage.class, new NotInterestedHandler(),
            HaveMessage.class, new HaveHandler(pieceManager),
            RequestMessage.class, new RequestHandler(pieceManager),
            PieceMessage.class, new PieceHandler(),
            ChokeMessage.class, new ChokeHandler(pieceManager),
            UnchokeMessage.class, new UnchokeHandler()
        ));

        DelegatingHandler delegatingHandler = new DelegatingHandler();
        DelegatingTicker delegatingTicker = new DelegatingTicker();

        IoReactor reactor = new IoReactor.Builder()
            .handler(delegatingHandler)
            .tickListener(delegatingTicker)
            .port(port)
            .build();

        PeerConnection.Builder connBuilder = new PeerConnection.Builder()
            .pieceManager(pieceManager)
            .totalPieces(metadata.totalPieces());

        PeerManager peerManager = new PeerManager(reactor, connBuilder);

        TorrentHandler handler = new TorrentHandler(
            dispatcher,
            peerManager,
            pieceManager,
            reactor,
            metadata.infoHash(),
            peerId
        );

        delegatingHandler.setDelegate(handler);

        PieceCompleteListener onPieceComplete =
            pieceIndex -> reactor.schedule(
                () -> peerManager.broadcast(new HaveMessage(pieceIndex)
            )
        );

        DiskService diskService = new DiskService(
            storage, pieceManager, stats, onPieceComplete, metadata);

        TorrentListener peerListener = new TorrentListener(reactor, diskService);

        connBuilder.listener(peerListener);

        TrackerManager trackerManager = new TrackerManager(
            trackerClient,
            stats,
            reactor,
            peerManager
        );

        TickListener ticker = new IntervalTicker(
            reactor,
            trackerManager,
            pieceManager,
            metadata,
            stats,
            System.currentTimeMillis()
        );

        delegatingTicker.setDelegate(ticker);

        return new TorrentClient(
            reactor,
            diskService,
            trackerManager,
            pieceManager,
            stats
        );
    }

    private static final class DelegatingHandler implements ConnectionHandler {
        private volatile ConnectionHandler delegate;
        void setDelegate(ConnectionHandler delegate) {
            this.delegate = delegate;
        }
        @Override public void onAcceptable(SocketChannel sc) {
            delegate.onAcceptable(sc);
        }
        @Override public void onConnected(NetworkSession s) {
            delegate.onConnected(s);
        }
        @Override public void onData(NetworkSession s) {
            delegate.onData(s);
        }
        @Override public void onWriteReady(NetworkSession s) {
            delegate.onWriteReady(s);
        }
        @Override public void onDisconnected(NetworkSession s) {
            delegate.onDisconnected(s);
        }
    }

    private static final class DelegatingTicker implements TickListener {
        private volatile TickListener delegate;
        void setDelegate(TickListener delegate) { this.delegate = delegate; }
        @Override public void onTick(long now) {
            if (delegate != null) delegate.onTick(now);
        }
    }

    private static byte[] generatePeerId() {
        byte[] id = new byte[20];
        byte[] prefix = "-JT0001-".getBytes();
        System.arraycopy(prefix, 0, id, 0, prefix.length);
        Random rng = new Random();
        for (int i = prefix.length; i < 20; i++) {
            id[i] = (byte) ('a' + rng.nextInt(26));
        }
        return id;
    }
}
