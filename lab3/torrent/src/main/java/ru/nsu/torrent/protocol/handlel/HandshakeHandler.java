package ru.nsu.torrent.protocol.handlel;

import java.util.Arrays;

import ru.nsu.torrent.protocol.message.BitfieldMessage;
import ru.nsu.torrent.protocol.message.HandshakeMessage;
import ru.nsu.torrent.protocol.peer.PeerConnection;
import ru.nsu.torrent.protocol.pieces.PieceManager;


public final class HandshakeHandler implements MessageHandler<HandshakeMessage> {

    private final PieceManager pieceManager;
    private final byte[] infoHash;
    private final byte[] localPeerId;

    public HandshakeHandler(PieceManager pieceManager, byte[] infoHash, byte[] localPeerId) {
        this.pieceManager = pieceManager;
        this.infoHash = infoHash;
        this.localPeerId = localPeerId;
    }

    @Override
    public void handle(HandshakeMessage msg, PeerConnection peer) {
        if (!Arrays.equals(msg.infoHash(), infoHash)) {
            peer.listener().onDisconnected(peer);
            return;
        }

        if (!peer.isHandshakeSent()) {
            peer.initiateHandshake(infoHash, localPeerId);
        }

        peer.completeHandshake();

        byte[] bf = pieceManager.buildBitfield();
        if (hasAnyBit(bf)) peer.send(new BitfieldMessage(bf));

        peer.listener().onHandshakeComplete(peer);
    }

    private static boolean hasAnyBit(byte[] bytes) {
        for (byte b : bytes) if (b != 0) return true;
        return false;
    }
}
