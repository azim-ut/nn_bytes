package org.example.services;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImageDrawService {

    public void draw(String target, int width, int height, byte[] source) throws IOException {
        byte[][] matrix = fitMatrix(width, height, source);
        if (matrix.length == 0) {
            return;
        }
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bufferedImage.createGraphics();
        g.setColor(Color.BLACK);

        int x;
        int y;
        for (y = 0; y < height; y++) {
            for (x = 0; x < width; x++) {
                int b = matrix[x][y] + 127;
                if (b < 127) {
                    b++;
                }
                Color c = new Color(b, b, 255);
                g.setColor(c);
                g.drawRect(x, y, 1, 1);
            }
        }
        File file = new File("file/res/" + target + ".png");
        ImageIO.write(bufferedImage, "png", file);

    }

    private byte[][] fitMatrix(int width, int height, byte[] source) {
        if (source.length < 2) {
            log.error("Not enough data");
            return new byte[0][0];
        }

        int side = (int) Math.sqrt(source.length);

        byte[][] res = new byte[width][height];
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
                    res[x][y] = matrix[(int) (x / divW)][(int) (y / divH)];
                } catch (Exception e) {
                    log.error("fitMatrix exception: {}", e.getLocalizedMessage());
                }
            }
        }

        return res;

    }

    public void clear() {
        try {
            Files.list(Path.of("file/res/")).forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    log.error("clear file {} exception: {}", path, e.getLocalizedMessage());
                }
            });
        } catch (IOException e) {
            log.error("clear dir exception: {}", e.getLocalizedMessage());
        }
    }
}
