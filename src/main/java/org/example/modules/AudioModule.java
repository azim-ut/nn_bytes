package org.example.modules;

import org.example.App;
import org.example.services.AudioBytesFetcher;
import org.example.services.BytesFetcher;
import org.example.services.ImageDrawService;

import com.google.inject.AbstractModule;

public class AudioModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(App.class);
        bind(ImageDrawService.class);
        bind(BytesFetcher.class).to(AudioBytesFetcher.class);
    }
}
