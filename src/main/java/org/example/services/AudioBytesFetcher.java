package org.example.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import com.google.common.primitives.Bytes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AudioBytesFetcher implements BytesFetcher {

    private Thread listener;
    private TargetDataLine line;
    private AudioInputStream inputStream;
    private AudioFormat audioFormat;
    private final String tempFilePath = "file/voice/record.wav";
    private final byte noyceLevel = -120;

    @Override
    public void start() {
        log.info("{} Started", getClass().getName());
        audioFormat = new AudioFormat(16000, 8, 2, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);

        if (!AudioSystem.isLineSupported(info)) {
            log.error("Line not supported: {}", info);
            return;
        }

        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open();
        } catch (LineUnavailableException e) {
            log.error("Line not available: {}, Line: {}", e.getLocalizedMessage(), info);
            return;
        }

        listener = new Thread(() -> {
            line.start();
            inputStream = new AudioInputStream(line);
            File wavFile = new File(tempFilePath);
            try {
                AudioSystem.write(inputStream, AudioFileFormat.Type.WAVE, wavFile);
            } catch (IOException e) {
                log.error("Listener exception: {}", e.getLocalizedMessage());
            }
        });
        listener.start();
    }

    @Override
    public void stop() {
        if (listener != null) {
            listener.interrupt();
        }

        if (line != null) {
            line.stop();
            line.close();
        }

        log.info("{} Stop", getClass().getName());
    }

    @Override
    public boolean inProgress() {
        return listener != null && listener.isAlive() && !listener.isInterrupted();
    }

    @Override
    public byte[] getBytes() {
        List<Byte> res = new ArrayList<>();

        try {
            byte[] bytes = Files.readAllBytes(Path.of(tempFilePath));
            for (int i = 0; i < bytes.length; i++) {
                byte b = bytes[i];
                if (b < noyceLevel) {
                    b = -128;
                }
                res.add(b);
            }
        } catch (IOException e) {
            log.info("{} Bytes fetch exception: {}", getClass().getName(), e.getLocalizedMessage());
        }

        return Bytes.toArray(res);
    }

    @Override
    public void drop() {

    }
}
