package ru.nsu.torrent.protocol.handlel;

import ru.nsu.torrent.protocol.message.InterestedMessage;
import ru.nsu.torrent.protocol.message.UnchokeMessage;
import ru.nsu.torrent.protocol.peer.PeerConnection;

public final class InterestedHandler implements MessageHandler<InterestedMessage> {
    @Override
    public void handle(InterestedMessage msg, PeerConnection peer) {
        peer.state().onReceiveInterested();
        if (peer.state().isPeerChoked()) {
            peer.state().onSendUnchoke();
            peer.send(UnchokeMessage.INSTANCE);
        }
    }
}
