package ru.nsu.torrent.protocol.handlel;

import ru.nsu.torrent.protocol.message.RequestMessage;
import ru.nsu.torrent.protocol.peer.PeerConnection;
import ru.nsu.torrent.protocol.pieces.PieceManager;

public final class RequestHandler implements MessageHandler<RequestMessage> {

    private final PieceManager pieceManager;

    public RequestHandler(PieceManager pieceManager) {
        this.pieceManager = pieceManager;
    }

    @Override
    public void handle(RequestMessage msg, PeerConnection peer) {
        if (!peer.state().isPeerChoked() && pieceManager.weHave(msg.index())) {
            peer.listener().onServeRequest(peer, msg.index(), msg.begin(), msg.length());
        }
    }
}
