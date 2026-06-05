package ru.nsu.torrent.protocol.handlel;

import ru.nsu.torrent.protocol.message.UnchokeMessage;
import ru.nsu.torrent.protocol.peer.PeerConnection;

public final class UnchokeHandler implements MessageHandler<UnchokeMessage> {
    @Override
    public void handle(UnchokeMessage msg, PeerConnection peer) {
        peer.state().onReceiveUnchoke();
        peer.request();
    }
}
