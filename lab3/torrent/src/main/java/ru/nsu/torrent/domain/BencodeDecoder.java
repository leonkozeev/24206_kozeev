package ru.nsu.torrent.domain;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class BencodeDecoder {

    private final byte[] data;
    private int pos;

    public BencodeDecoder(byte[] data) {
        this.data = data;
        this.pos  = 0;
    }

    public Object decode() {
        byte b = data[pos];
        if (b == 'i') return decodeInteger();
        if (b == 'l') return decodeList();
        if (b == 'd') return decodeDictionary(null);
        if (b >= '0' && b <= '9') return decodeByteString();
        throw new IllegalStateException("Invalid bencode at pos " + pos + ": 0x" + Integer.toHexString(b & 0xFF));
    }

    public Map<String, Object> decodeDictionary(Map<String, byte[]> rawCapture) {
        pos++;
        Map<String, Object> map = new LinkedHashMap<>();
        while (data[pos] != 'e') {
            byte[] keyBytes = decodeByteString();
            String key = new String(keyBytes, StandardCharsets.UTF_8);
            int valStart = pos;
            Object value = decode();
            if (rawCapture != null) {
                rawCapture.put(key, Arrays.copyOfRange(data, valStart, pos));
            }
            map.put(key, value);
        }
        pos++;
        return map;
    }

    private long decodeInteger() {
        pos++;
        boolean negative = data[pos] == '-';
        if (negative) pos++;
        long value = 0;
        while (data[pos] != 'e') {
            int digit = data[pos++] - '0';
            if (digit < 0 || digit > 9) {
                throw new IllegalStateException("Non-digit in integer at pos " + (pos - 1));
            }
            value = Math.addExact(Math.multiplyExact(value, 10), digit);
        }
        pos++;
        return negative ? -value : value;
    }

    private byte[] decodeByteString() {
        int colon = pos;
        while (data[colon] != ':') colon++;
        int length = Integer.parseInt(
            new String(data, pos, colon - pos, StandardCharsets.US_ASCII));
        pos = colon + 1;
        byte[] result = Arrays.copyOfRange(data, pos, pos + length);
        pos += length;
        return result;
    }

    private List<Object> decodeList() {
        pos++;
        List<Object> list = new ArrayList<>();
        while (data[pos] != 'e') {
            list.add(decode());
        }
        pos++;
        return list;
    }
}
