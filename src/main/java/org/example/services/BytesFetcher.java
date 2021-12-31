package org.example.services;

public interface BytesFetcher {
    void start();
    void stop();
    boolean inProgress();
    byte[] getBytes();
    void drop();
}
