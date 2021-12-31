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
    private BytesFetcher byteFetcher;

    @Inject
    private ImageDrawService imageDrawService;

    public static void main(String[] args) {
        final App app;
        try {
            app = Guice.createInjector(new AudioModule()).getInstance(App.class);
            Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

            app.start();
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }


    }


    public App() {
        System.out.println("App");
    }

    public void start() {
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        List<String> commands = new ArrayList<>();
        String command;
        try {
            while ((command = console.readLine()) != null) {
                commands.add(0, command);
                if (byteFetcher.inProgress()) {
                    String word = commands.get(1);
                    byteFetcher.stop();
                    byte[] bytes = byteFetcher.getBytes();
                    int w = 240;
                    int h = 240;
                    log.info("Start draw");
                    imageDrawService.draw(word, w, h, bytes);
                    log.info("Finish draw");
                } else {
                    byteFetcher.start();
                }
            }
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    public void stop() {
        imageDrawService.clear();
    }

}
