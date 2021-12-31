package org.example.services;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.inject.Singleton;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class ImageDrawService {

    public void clear() {
        try {
            Files.list(Path.of("file/pics/")).forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    log.error("{}: Path: {}, {}", getClass().getName(), path, e.getLocalizedMessage());
                }
            });
        } catch (IOException e) {
            log.error("{}: {}", getClass().getName(), e.getLocalizedMessage());
        }
    }

    private byte[][] fitMatrix(int width, int height, byte[] source) {
        if (source.length < 2) {
            log.error("Not enough data");
            return new byte[0][0];
        }
        byte[][] res = new byte[width][height];
        int side = (int) Math.sqrt(source.length);

        byte[][] matrix = new byte[side][side];
        int t = 0;
        for (int j = 0; j < side; j++) {
            for (int i = 0; i < side; i++) {
                matrix[i][j] = source[t];
                t++;
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double divW = (double) width / (double) side;
                double divH = (double) height / (double) side;
                try {
                    res[y][x] = matrix[(int) (x / divW)][(int) (y / divH)];
                } catch (Exception e) {
                    log.error(e.getLocalizedMessage());
                }
            }
        }

        return res;
    }

    public void draw(String word, int width, int height, byte[] source) throws IOException {

        byte[][] matrix = fitMatrix(width, height, source);
        if (matrix.length == 0) {
            log.error("no data");
            return;
        }

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

        Graphics2D g = (Graphics2D) bufferedImage.getGraphics();

        g.setRenderingHints(new HashMap<RenderingHints.Key, Object>() {{
            put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
            put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        }});

        g.setColor(Color.BLACK);
        int x;
        int y;
        int ind = 0;
        for (y = 0; y < height; y++) {
            for (x = 0; x < width; x++) {
                int b = matrix[x][y] + 127;
                if (b < 127) {
                    b += 1;
                }
                Color c = new Color(b, b, 255);
                g.setColor(c);
                g.drawRect(x, y, 1, 1);
                ind++;

                if (source.length <= ind) {
                    ind--;
                }
            }
        }

        File file = new File("file/pics/" + word + ".png");
        ImageIO.write(bufferedImage, "png", file);
    }

}
