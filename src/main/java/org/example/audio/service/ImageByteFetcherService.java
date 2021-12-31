package org.example.audio.service;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;
import javax.inject.Singleton;
import javax.sound.sampled.TargetDataLine;

import org.example.services.BytesFetcher;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class ImageByteFetcherService implements BytesFetcher {

    private boolean busy = false;
    private DataBufferByte dataBufferByte;

    @Override
    public boolean inProgress() {
        return busy;
    }

    @Override
    public byte[] getBytes() {
        return dataBufferByte.getData();
    }

    @Override
    public void start() {
        busy = true;
        File imgPath = new File("file/img/face.jpg");
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(imgPath);

            // get DataBufferBytes from Raster
            WritableRaster raster = bufferedImage.getRaster();
            dataBufferByte = (DataBufferByte) raster.getDataBuffer();
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }
    }

    @Override
    public void stop() {

        busy = false;
        log.info("Stopped");
    }

    @Override
    public void drop() {

    }
}
