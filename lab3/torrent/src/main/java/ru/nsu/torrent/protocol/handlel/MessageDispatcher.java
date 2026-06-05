package ru.nsu.torrent.protocol.handlel;

import java.util.HashMap;
import java.util.Map;

import ru.nsu.torrent.protocol.message.Message;
import ru.nsu.torrent.protocol.peer.PeerConnection;


public final class MessageDispatcher {
    private final Map<Class<? extends Message>, MessageHandler<?>> handlers;

    public MessageDispatcher(Map<Class<? extends Message>, MessageHandler<?>> handlers) {
        this.handlers = new HashMap<>(handlers);
    }

    public <M extends Message> void register(Class<M> clazz, MessageHandler<M> handler) {
        handlers.put(clazz, handler);
    }

    @SuppressWarnings("unchecked")
    public <M extends Message> void dispatch(M message, PeerConnection peer) {
        MessageHandler<M> handler = (MessageHandler<M>) handlers.get(message.getClass());
        if (handler != null) {
            handler.handle(message, peer);
        } else {
            System.err.println("[dispatcher] no handler for: " + message.getClass().getSimpleName());
        }
    }
}
