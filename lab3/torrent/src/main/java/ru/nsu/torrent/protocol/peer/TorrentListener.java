package ru.nsu.torrent.protocol.peer;

import ru.nsu.torrent.network.IoReactor;
import ru.nsu.torrent.protocol.message.PieceMessage;
import ru.nsu.torrent.storage.DiskService;


public final class TorrentListener implements PeerEventListener {

    private final IoReactor reactor;
    private final DiskService diskService;

    public TorrentListener(IoReactor reactor, DiskService diskService) {
        this.reactor = reactor;
        this.diskService = diskService;
    }

    @Override
    public void onBlockReceived(PeerConnection peer, int pieceIndex, int begin, byte[] block) {
        diskService.asyncWriteBlock(pieceIndex, begin, block);
    }

    @Override
    public void onServeRequest(PeerConnection peer, int pieceIndex, int begin, int length) {
        diskService.asyncReadBlock(
            pieceIndex,
            begin,
            length,
            data -> reactor.schedule(() -> peer.send(new PieceMessage(pieceIndex, begin, data)))
        );
    }

    @Override
    public void onDisconnected(PeerConnection peer) {
        peer.close();
    }

    @Override
    public void onWritePending(PeerConnection peer) {
        reactor.signalWrite(peer);
    }
}
