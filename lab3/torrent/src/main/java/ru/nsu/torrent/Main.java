package ru.nsu.torrent;

import java.nio.file.Files;
import java.nio.file.Path;

import ru.nsu.torrent.app.TorrentClient;
import ru.nsu.torrent.app.TorrentClientBuilder;
import ru.nsu.torrent.domain.TorrentMetadata;
import ru.nsu.torrent.storage.FileStorage;

public final class Main {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: bittorrent-client <torrent-file> <output-directory>");
            System.exit(1);
        }

        Path torrentPath = Path.of(args[0]);
        Path outputDir   = Path.of(args[1]);

        try {
            Files.createDirectories(outputDir);

            TorrentMetadata metadata = TorrentMetadata.of(Files.readAllBytes(torrentPath));
            System.out.printf("File   : %s%n", metadata.fileName());
            System.out.printf("Size   : %,d bytes%n", metadata.fileLength());
            System.out.printf("Pieces : %d × %d KB%n",
                metadata.totalPieces(), metadata.pieceLength() / 1024);
            System.out.printf("Tracker: %s%n%n", metadata.announceUrl());

            FileStorage storage = new FileStorage(
                outputDir.resolve(metadata.fileName()), metadata);
            storage.initialize();

            TorrentClient client = new TorrentClientBuilder()
                .metadata(metadata)
                .storage(storage)
                .port(6881)
                .build();

            Runtime.getRuntime().addShutdownHook(new Thread(client::shutdown, "shutdown"));
            client.start();

        } catch (Exception e) {
            System.err.println("Fatal: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
