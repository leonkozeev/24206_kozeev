package ru.nsu.torrent.protocol;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;


public final class TorrentStats {
    public final AtomicLong bytesDownloaded = new AtomicLong(0);
    public final AtomicLong bytesUploaded = new AtomicLong(0);
    public final AtomicBoolean downloadComplete = new AtomicBoolean(false);
}
