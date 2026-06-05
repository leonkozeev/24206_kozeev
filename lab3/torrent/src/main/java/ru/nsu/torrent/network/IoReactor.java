package ru.nsu.torrent.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class IoReactor {
    private static final int SELECT_TIMEOUT_MS = 500;

    private final Selector selector;
    private final ServerSocketChannel serverChannel;
    private final ConnectionHandler handler;
    private final TickListener tickListener;

    private final Queue<Runnable> pendingTasks = new ConcurrentLinkedQueue<>();
    private volatile boolean running = true;


    public static final class Builder {
        private ConnectionHandler handler;
        private TickListener tickListener;
        private int port = 6881;

        public Builder handler(ConnectionHandler handler) {
            this.handler = handler;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder tickListener(TickListener tickListener) {
            this.tickListener = tickListener;
            return this;
        }

        public IoReactor build() throws IOException {
            Objects.requireNonNull(handler, "ConnectionHandler is required");
            Selector selector = Selector.open();
            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            IoReactor reactor = new IoReactor(selector, serverChannel, handler, tickListener);
            reactor.bind(port);
            return reactor;
        }
    }

    public IoReactor(
        Selector selector,
        ServerSocketChannel serverChannel,
        ConnectionHandler handler,
        TickListener tickListener
    ) {
        this.selector = selector;
        this.serverChannel = serverChannel;
        this.handler = handler;
        this.tickListener = tickListener;
    }

    private void bind(int port) throws IOException {
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(port));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void register(NetworkSession session, boolean outbound) {
        schedule(() -> {
            try {
                int ops = outbound ? SelectionKey.OP_CONNECT : SelectionKey.OP_READ;
                session.sc().register(selector, ops, session);
            } catch (IOException e) {
                System.err.println("[reactor] registration failed: " + e.getMessage());
                handler.onDisconnected(session);
            }
        });
    }

    public void signalWrite(NetworkSession session) {
        schedule(() -> {
            SelectionKey key = session.sc().keyFor(selector);
            if (key != null && key.isValid()) {
                updateInterestOps(key, session);
            }
        });
    }

    public void schedule(Runnable task) {
        pendingTasks.add(task);
        selector.wakeup();
    }

    public Selector selector() {
        return selector;
    }
    public ConnectionHandler handler()  {
        return handler;
    }

    public void run() throws IOException {
        while (running) {
            selector.select(SELECT_TIMEOUT_MS);
            if (!running) break;
            drainTasks();
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();
                dispatch(key);
            }
            if (tickListener != null) tickListener.onTick(System.currentTimeMillis());
        }
    }

    public void stop() {
        running = false;
        selector.wakeup();
    }

    public void shutdown() {
        assert running == false : "stop before shutdown";
        System.out.println("[reactor] shutting down");
        for (SelectionKey key : selector.keys()) {
            try {
                key.channel().close();
            } catch (IOException ignored) { }
        }
        try {
            selector.close();
        } catch (IOException ignored) { }
    }

    private void dispatch(SelectionKey key) {
        if (!key.isValid()) return;

        if (key.isAcceptable()) {
            handleAccept();
            return;
        }

        NetworkSession session = (NetworkSession) key.attachment();
        if (session == null) {
            return;
        }

        try {
            if (key.isConnectable()) {
                handleConnect(key, session);
            }
            if (key.isValid() && key.isWritable()) {
                session.flush();
                handler.onWriteReady(session);
                updateInterestOps(key, session);
            }
            if (key.isValid() && key.isReadable()) {
                handler.onData(session);
            }
        } catch (IOException e) {
            cancelAndDisconnect(key, session);
        }
    }

    private void handleConnect(SelectionKey key, NetworkSession session) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        if (sc.finishConnect()) {
            handler.onConnected(session);
            updateInterestOps(key, session);
        }
    }

    private void handleAccept() {
        try {
            SocketChannel sc = serverChannel.accept();
            if (sc != null) {
                sc.configureBlocking(false);
                handler.onAcceptable(sc);
            }
        } catch (IOException e) {
            System.err.println("[reactor] accept error: " + e.getMessage());
        }
    }

    private void cancelAndDisconnect(SelectionKey key, NetworkSession session) {
        key.cancel();
        handler.onDisconnected(session);
        try { session.close(); } catch (IOException ignored) { }
    }

    private void drainTasks() {
        Runnable task;
        while ((task = pendingTasks.poll()) != null) {
            try {
                task.run();
            } catch (Exception e) {
                System.err.println("[reactor] task error: " + e.getMessage());
            }
        }
    }

    private void updateInterestOps(SelectionKey key, NetworkSession session) {
        if (!key.isValid()) return;
        int ops = SelectionKey.OP_READ;
        if (session.hasPendingWrites()) ops |= SelectionKey.OP_WRITE;
        key.interestOps(ops);
    }
}
