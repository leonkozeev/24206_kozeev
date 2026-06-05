package ru.nsu.torrent.protocol.handlel;

import ru.nsu.torrent.protocol.message.ChokeMessage;
import ru.nsu.torrent.protocol.peer.PeerConnection;
import ru.nsu.torrent.protocol.pieces.PieceManager;


public final class ChokeHandler implements MessageHandler<ChokeMessage> {

    private final PieceManager pieceManager;

    public ChokeHandler(PieceManager pieceManager) {
        this.pieceManager = pieceManager;
    }

    @Override
    public void handle(ChokeMessage msg, PeerConnection peer) {
        peer.state().onReceiveChoke();
        pieceManager.requeueBlocks(peer.pipeline().drainInflight());
    }
}
