package ru.nsu.torrent.domain;

public record PeerAddress(String ip, int port) {
    @Override
    public String toString() {
        return "/" + ip + ":" + port;
    }
}
