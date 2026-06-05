package ru.nsu.torrent.protocol.peer;

import java.io.IOException;
import java.nio.channels.SelectableChannel;

import ru.nsu.torrent.network.NetworkSession;
import ru.nsu.torrent.network.PeerChannel;
import ru.nsu.torrent.protocol.message.HandshakeMessage;
import ru.nsu.torrent.protocol.message.Message;
import ru.nsu.torrent.protocol.pieces.PieceManager;


public final class PeerConnection implements NetworkSession {
    private final PeerChannel channel;
    private final PeerState state = new PeerState();
    private final RequestPipeline pipeline;
    private final PeerEventListener listener;
    private final boolean[] bitfield;

    private boolean handshakeReceived = false;
    private boolean handshakeSent = false;

    public final long connectedAt = System.currentTimeMillis();


    public PeerConnection(
        PeerChannel channel,
        PieceManager pieceManager,
        PeerEventListener listener,
        int totalPieces
    ) {
        this.channel = channel;
        this.listener = listener;
        this.pipeline = new RequestPipeline(this, pieceManager);
        this.bitfield = new boolean[totalPieces];
    }


    public static final class Builder {
        private PeerChannel channel;
        private PieceManager pieceManager;
        private PeerEventListener listener;
        private int totalPieces;

        public Builder channel(PeerChannel channel) {
            this.channel = channel;
            return this;
        }

        public Builder pieceManager(PieceManager pieceManager) {
            this.pieceManager = pieceManager;
            return this;
        }

        public Builder listener(PeerEventListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder totalPieces(int totalPieces) {
            this.totalPieces = totalPieces;
            return this;
        }

        public PeerConnection build() {
            if (channel == null) throw new IllegalStateException("channel required");
            if (pieceManager == null) throw new IllegalStateException("pieceManager required");
            if (listener == null) throw new IllegalStateException("listener required");
            return new PeerConnection(channel, pieceManager, listener, totalPieces);
        }
    }

    public void initiateHandshake(byte[] infoHash, byte[] localPeerId) {
        send(new HandshakeMessage(infoHash, localPeerId));
        handshakeSent = true;
    }

    public void completeHandshake() {
        handshakeReceived = true;
    }

    public boolean isHandshakeComplete() {
        return handshakeReceived;
    }
    public boolean isHandshakeSent() {
        return handshakeSent;
    }

    public void request() {
        if (state.canRequest()) {
            pipeline.fill();
        }
    }

    public void send(Message message) {
        channel.enqueue(message.encode());
        listener.onWritePending(this);
    }

    public boolean[] bitfield() { return bitfield; }
    public PeerEventListener listener() { return listener; }
    public PeerState state() { return state; }
    public RequestPipeline pipeline() { return pipeline; }
    public PeerChannel channel() { return channel; }


    @Override public SelectableChannel sc() { return channel.socketChannel(); }
    @Override public boolean hasPendingWrites() { return channel.hasPendingWrites(); }
    @Override public void flush() throws IOException { channel.flush(); }

    @Override
    public void close() {
        try {
            channel.close();
        } catch (IOException ignored) { }
    }
}
