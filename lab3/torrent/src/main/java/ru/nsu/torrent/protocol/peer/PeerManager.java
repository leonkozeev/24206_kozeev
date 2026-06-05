package ru.nsu.torrent.protocol.peer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import ru.nsu.torrent.domain.PeerAddress;
import ru.nsu.torrent.network.IoReactor;
import ru.nsu.torrent.network.PeerChannel;
import ru.nsu.torrent.protocol.message.Message;


public final class PeerManager {

    public static final int MAX_PEERS = 50;

    private final Set<String> knownIPs = ConcurrentHashMap.newKeySet();
    private final IoReactor reactor;
    private final PeerConnection.Builder connectionBuilder;

    public PeerManager(IoReactor reactor, PeerConnection.Builder connectionBuilder) {
        this.reactor = reactor;
        this.connectionBuilder = connectionBuilder;
    }

    public void connectToPeer(PeerAddress addr) {
        String key = addr.ip();
        if (knownIPs.contains(key)) return;

        reactor.schedule(() -> {
            if (knownIPs.contains(key)) return;
            if (activePeerCount() >= MAX_PEERS) return;

            try {
                SocketChannel sc = SocketChannel.open();
                sc.configureBlocking(false);
                sc.connect(new InetSocketAddress(addr.ip(), addr.port()));

                PeerChannel channel = new PeerChannel(sc, key);
                knownIPs.add(key);

                PeerConnection conn = connectionBuilder.channel(channel).build();
                reactor.register(conn, true);
            } catch (IOException e) {
                System.err.println("[peers] connect error " + key + ": " + e.getMessage());
            }
        });
    }


    public PeerConnection acceptPeer(PeerChannel channel) {
        if (
            activePeerCount() >= MAX_PEERS ||
            !knownIPs.add(channel.remoteIp())
        ) {
            closeQuietly(channel);
            return null;
        }

        try {
            channel.socketChannel().configureBlocking(false);
            return connectionBuilder.channel(channel).build();
        } catch (IOException e) {
            System.err.println("[peers] accept error: " + e.getMessage());
            knownIPs.remove(channel.remoteIp());
            closeQuietly(channel);
            return null;
        }
    }

    public void removePeer(PeerConnection conn) {
        knownIPs.remove(conn.channel().remoteIp());
        conn.close();
    }


    public void broadcast(Message msg) {
        for (SelectionKey key : reactor.selector().keys()) {
            if (
                key.isValid() &&
                key.attachment() instanceof PeerConnection conn &&
                conn.isHandshakeComplete()
            ) {
            conn.send(msg);
        }
        }
    }



    private int activePeerCount() {
        return (int) reactor.selector().keys().stream()
               .filter(k -> k.isValid() && k.attachment() instanceof PeerConnection)
               .count();
    }

    private static void closeQuietly(PeerChannel channel) {
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException ignored) { }
        }
    }
}
