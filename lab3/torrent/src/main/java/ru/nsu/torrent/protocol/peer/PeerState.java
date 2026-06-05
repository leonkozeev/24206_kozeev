package ru.nsu.torrent.protocol.peer;

/**
 * Tracks the four-way choke/interest state for one peer connection.
 * Not thread-safe — must only be accessed from the reactor thread.
 */
public final class PeerState {

    private boolean amChoked = true;
    private boolean amInterested = false;
    private boolean peerChoked = true;
    private boolean peerInterested = false;

    public void onReceiveChoke()        { amChoked = true;  }
    public void onReceiveUnchoke()      { amChoked = false; }
    public void onReceiveInterested()   { peerInterested = true; }
    public void onReceiveNotInterested(){ peerInterested = false; }
    public void onSendInterested()      { amInterested = true; }
    public void onSendUnchoke()         { peerChoked = false; }

    public boolean canRequest()   { return !amChoked && amInterested; }
    public boolean isPeerChoked() { return peerChoked; }
    public boolean amInterested() { return amInterested; }
    public boolean amChoked()     { return amChoked; }
}
