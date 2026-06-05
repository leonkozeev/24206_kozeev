package ru.nsu.torrent.tracker;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.nsu.torrent.domain.BencodeDecoder;
import ru.nsu.torrent.domain.PeerAddress;
import ru.nsu.torrent.domain.TorrentMetadata;


public final class TrackerClient {

    private final TorrentMetadata metadata;
    private final byte[] peerId;
    private final int port;

    public TrackerClient(TorrentMetadata metadata, byte[] peerId, int port) {
        this.metadata = metadata;
        this.peerId = peerId;
        this.port = port;
    }

    public List<PeerAddress> announce(long downloaded, long uploaded) throws Exception {
        long left = Math.max(0, metadata.fileLength() - downloaded);

        String url = metadata.announceUrl()
            + "?info_hash=" + percentEncode(metadata.infoHash())
            + "&peer_id="   + percentEncode(peerId)
            + "&port="      + port
            + "&uploaded="  + uploaded
            + "&downloaded="+ downloaded
            + "&left="      + left
            + "&compact=1";

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setConnectTimeout(15_000);
        conn.setReadTimeout(15_000);
        conn.setRequestProperty("User-Agent", "JavaTorrent/1.0");

        byte[] body;
        try (InputStream is = conn.getInputStream()) {
            body = is.readAllBytes();
        }
        return extractPeers(body);
    }

    @SuppressWarnings("unchecked")
    private List<PeerAddress> extractPeers(byte[] body) {
        BencodeDecoder  decoder  = new BencodeDecoder(body);
        Map<String,Object> response = decoder.decodeDictionary(null);
        List<PeerAddress> peers = new ArrayList<>();

        Object peersObj = response.get("peers");
        if (peersObj instanceof byte[] compact) {
            for (int i = 0; i + 5 < compact.length; i += 6) {
                String ip = (compact[i] & 0xFF) + "."
                          + (compact[i + 1] & 0xFF) + "."
                          + (compact[i + 2] & 0xFF) + "."
                          + (compact[i + 3] & 0xFF);
                int p = ((compact[i + 4] & 0xFF) << 8) | (compact[i + 5] & 0xFF);
                peers.add(new PeerAddress(ip, p));
            }
        }
        return peers;
    }

    private static String percentEncode(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 3);
        for (byte b : bytes) {
            int v = b & 0xFF;
            if (isUnreserved(v)) {
                sb.append((char) v);
            } else {
                sb.append(String.format("%%%02X", v));
            }
        }
        return sb.toString();
    }

    private static boolean isUnreserved(int v) {
        return (v >= 'A' && v <= 'Z')
            || (v >= 'a' && v <= 'z')
            || (v >= '0' && v <= '9')
            || v == '-' || v == '_' || v == '.' || v == '~';
    }
}
