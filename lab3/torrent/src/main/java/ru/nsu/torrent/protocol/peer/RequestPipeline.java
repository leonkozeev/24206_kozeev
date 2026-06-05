package ru.nsu.torrent.protocol.peer;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import ru.nsu.torrent.protocol.message.RequestMessage;
import ru.nsu.torrent.protocol.pieces.BlockRequest;
import ru.nsu.torrent.protocol.pieces.PieceManager;


public final class RequestPipeline {
    private static final int MAX_INFLIGHT = 50;

    private final Set<BlockRequest> inFlight = new LinkedHashSet<>();
    private final PeerConnection owner;
    private final PieceManager pieceManager;

    public RequestPipeline(PeerConnection owner, PieceManager pieceManager) {
        this.owner = owner;
        this.pieceManager = pieceManager;
    }

    public void fill() {
        while (inFlight.size() < MAX_INFLIGHT) {
            BlockRequest req = pieceManager.nextBlockFor(owner.bitfield());
            if (req == null) break;
            inFlight.add(req);
            owner.send(new RequestMessage(req.pieceIndex(), req.blockOffset(), req.blockLength()));
        }
    }

    public void acknowledge(int pieceIndex, int offset) {
        inFlight.removeIf(r -> r.pieceIndex() == pieceIndex && r.blockOffset() == offset);
    }

    public Set<BlockRequest> drainInflight() {
        Set<BlockRequest> snapshot = Collections.unmodifiableSet(new LinkedHashSet<>(inFlight));
        inFlight.clear();
        return snapshot;
    }

    public int size() {
        return inFlight.size();
    }
}
