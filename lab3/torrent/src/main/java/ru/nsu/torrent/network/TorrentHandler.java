package ru.nsu.torrent.network;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import ru.nsu.torrent.protocol.handlel.MessageDispatcher;
import ru.nsu.torrent.protocol.peer.PeerConnection;
import ru.nsu.torrent.protocol.peer.PeerManager;
import ru.nsu.torrent.protocol.pieces.PieceManager;

public final class TorrentHandler implements ConnectionHandler {

    private final MessageDispatcher dispatcher;
    private final PeerManager peerManager;
    private final PieceManager pieceManager;
    private final IoReactor reactor;
    private final byte[] infoHash;
    private final byte[] peerId;

    public TorrentHandler(
        MessageDispatcher dispatcher,
        PeerManager peerManager,
        PieceManager pieceManager,
        IoReactor reactor,
        byte[] infoHash,
        byte[] peerId
    ) {
        this.dispatcher  = dispatcher;
        this.peerManager = peerManager;
        this.pieceManager = pieceManager;
        this.reactor = reactor;
        this.infoHash = infoHash;
        this.peerId = peerId;
    }

    @Override
    public void onAcceptable(SocketChannel sc) {
        try {
            String remoteAddr = sc.getRemoteAddress().toString();
            PeerChannel channel = new PeerChannel(sc, remoteAddr);
            PeerConnection conn = peerManager.acceptPeer(channel);
            if (conn != null) {
                reactor.register(conn, false);
            }
        } catch (IOException e) {
            System.err.println("[handler] accept error: " + e.getMessage());
            try {
                sc.close();
            } catch (IOException ignored) { }
        }
    }

    @Override
    public void onConnected(NetworkSession session) {
        PeerConnection conn = (PeerConnection) session;
        conn.initiateHandshake(infoHash, peerId);
    }

    @Override
    public void onData(NetworkSession session) {
        PeerConnection conn = (PeerConnection) session;
        try {
            if (conn.channel().read() == -1) {
                onDisconnected(session);
                return;
            }
            conn.channel().drain().forEach(m -> dispatcher.dispatch(m, conn));
        } catch (IOException e) {
            onDisconnected(session);
        }
    }

    @Override
    public void onWriteReady(NetworkSession session) {
    }

    @Override
    public void onDisconnected(NetworkSession session) {
        PeerConnection conn = (PeerConnection) session;

        SelectionKey key = conn.sc().keyFor(reactor.selector());
        if (key != null) key.cancel();

        pieceManager.requeueBlocks(conn.pipeline().drainInflight());
        pieceManager.removePeer(conn.bitfield());
        peerManager.removePeer(conn);
    }
}
