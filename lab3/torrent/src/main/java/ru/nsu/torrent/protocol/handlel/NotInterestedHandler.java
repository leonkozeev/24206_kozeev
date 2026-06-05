package ru.nsu.torrent.protocol.handlel;

import ru.nsu.torrent.protocol.message.NotInterestedMessage;
import ru.nsu.torrent.protocol.peer.PeerConnection;

public final class NotInterestedHandler implements MessageHandler<NotInterestedMessage> {
    @Override
    public void handle(NotInterestedMessage msg, PeerConnection peer) {
        peer.state().onReceiveNotInterested();
    }
}
