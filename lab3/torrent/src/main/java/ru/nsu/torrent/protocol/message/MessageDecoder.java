package ru.nsu.torrent.protocol.message;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


public final class MessageDecoder {
    private static final int HANDSHAKE_LEN = 68;
    private static final int BUFFER_CAPACITY = 2 << 20;

    private final byte[] buf = new byte[BUFFER_CAPACITY];
    private int writePos = 0;
    private int readPos = 0;
    private boolean handshakeDone = false;

    public void feed(ByteBuffer incoming) {
        int n = incoming.remaining();
        if (writePos - readPos + n > BUFFER_CAPACITY) {
            compact();
        }

        incoming.get(buf, writePos, n);
        writePos += n;
    }

    public List<Message> drain() {
        List<Message> out = new ArrayList<>();
        while (true) {
            if (!handshakeDone) {
                if (available() < HANDSHAKE_LEN) break;
                int saved = readPos;
                Message hs = decodeHandshake();
                if (hs != null) {
                    out.add(hs);
                    handshakeDone = true;
                } else {
                    readPos = saved + HANDSHAKE_LEN;
                }
            } else {
                if (available() < 4) break;
                int msgLen = readInt(readPos);
                if (msgLen == 0) {
                    readPos += 4;
                    out.add(KeepAliveMessage.INSTANCE);
                    continue;
                }
                if (available() < 4 + msgLen) break;
                readPos += 4;
                Message msg = decodeMessage(msgLen);
                if (msg != null) out.add(msg);
            }
        }
        compact();
        return out;
    }

    private Message decodeHandshake() {
        if ((buf[readPos] & 0xFF) != 19) return null;
        readPos += 1 + 19 + 8;
        byte[] infoHash = readBytes(20);
        byte[] peerId   = readBytes(20);
        return new HandshakeMessage(infoHash, peerId);
    }

    private Message decodeMessage(int msgLen) {
        int id = buf[readPos++] & 0xFF;
        int payloadLen = msgLen - 1;
        return switch (id) {
            case 0 -> ChokeMessage.INSTANCE;
            case 1 -> UnchokeMessage.INSTANCE;
            case 2 -> InterestedMessage.INSTANCE;
            case 3 -> NotInterestedMessage.INSTANCE;
            case 4 -> {
                int idx = readInt(readPos); readPos += 4;
                yield new HaveMessage(idx);
            }
            case 5 -> new BitfieldMessage(readBytes(payloadLen));
            case 6 -> {
                int idx = readInt(readPos); readPos += 4;
                int beg = readInt(readPos); readPos += 4;
                int len = readInt(readPos); readPos += 4;
                yield new RequestMessage(idx, beg, len);
            }
            case 7 -> {
                int idx = readInt(readPos); readPos += 4;
                int beg = readInt(readPos); readPos += 4;
                byte[] blk = readBytes(payloadLen - 8);
                yield new PieceMessage(idx, beg, blk);
            }
            default -> {
                readPos += payloadLen;
                yield null;
            }
        };
    }

    private int available() {
        return writePos - readPos;
    }

    private int readInt(int pos) {
        return ((buf[pos] & 0xFF) << 24) | 
               ((buf[pos + 1] & 0xFF) << 16) |
               ((buf[pos + 2] & 0xFF) <<  8) | 
               (buf[pos + 3] & 0xFF);
    }

    private byte[] readBytes(int len) {
        byte[] out = new byte[len];
        System.arraycopy(buf, readPos, out, 0, len);
        readPos += len;
        return out;
    }

    private void compact() {
        if (readPos == 0) return;
        int rem = writePos - readPos;
        System.arraycopy(buf, readPos, buf, 0, rem);
        writePos = rem;
        readPos  = 0;
    }
}
