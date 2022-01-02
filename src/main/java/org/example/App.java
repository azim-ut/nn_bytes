package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.example.modules.AudioModule;
import org.example.services.BytesFetcher;
import org.example.services.ImageDrawService;

import com.google.inject.Guice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {
    @Inject
    private BytesFetcher bytesFetcher;

    @Inject
    private ImageDrawService imageDrawService;

    public static void main(String[] args) {
        App app = Guice.createInjector(new AudioModule()).getInstance(App.class);
        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
        app.start();
    }

    private void start() {
        try (BufferedReader console = new BufferedReader(new InputStreamReader(System.in));) {
            List<String> commands = new ArrayList<>();
            String command;
            while ((command = console.readLine()) != null) {
                commands.add(0, command);
                if (bytesFetcher.inProgress()) {
                    String target = commands.get(1);
                    bytesFetcher.stop();
                    byte[] bytes = bytesFetcher.getBytes();
                    int w = 240;
                    int h = 240;
                    log.info("Draw started");
                    imageDrawService.draw(target, w, h, bytes);
                    log.info("Draw finished");
                    commands = new ArrayList<>();
                } else {
                    bytesFetcher.start();
                }
            }
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        }
    }

    private void stop() {
        imageDrawService.clear();
    }
}
