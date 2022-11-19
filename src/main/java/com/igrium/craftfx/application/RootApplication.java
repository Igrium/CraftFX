package com.igrium.craftfx.application;

import java.util.concurrent.CountDownLatch;

import com.igrium.craftfx.CraftFX;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * The global JavaFX application that allows for multiple "applications" to
 * co-exist in one runtime.
 */
public final class RootApplication extends Application {

    private static RootApplication instance;
    private static final CountDownLatch initLatch = new CountDownLatch(1);

    /**
     * Get the active application manager instance.
     * @return The application instance, or <code>null</code> if JavaFX is not running.
     */
    public static RootApplication getInstance() {
        return instance;
    }

    /**
     * Launch JavaFX and wait for it to initialize.
     * @return The root JavaFX application.
     * @throws RuntimeException If the thread is interrupted while waiting for JavaFX to init.
     */
    public static RootApplication launchJFX() {
        if (instance == null) {
            CraftFX.LOGGER.info("Launching JavaFX...");
            // Launch the application in a new thread so JavaFX doesn't hang the entire client. 
            new Thread(() -> {
                try {
                    Application.launch(RootApplication.class, new String[0]);
                } catch (Exception e) {
                    CraftFX.LOGGER.error("Error starting JavaFX!", e);
                }
            }, "JavaFX Bootstrapper").start();
        }
        RootApplication inst;
        try {
            inst = waitForInit();
        } catch (InterruptedException e) {
            throw new RuntimeException("Thread interrupted while waiting for JavaFX to init!", e);
        }
        return inst;
    }

    /**
     * Wait for the UI to finish initializing.
     * @return The root application.
     * @throws InterruptedException If the current thread is interrupted while waiting.
     */
    public static RootApplication waitForInit() throws InterruptedException {
        initLatch.await();
        return instance;
    }

    private Stage primaryStage;

    public RootApplication() {
        instance = this;
        initLatch.countDown();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Platform.setImplicitExit(false);
        this.primaryStage = primaryStage;
    }

    protected Stage getPrimaryStage() {
        return primaryStage;
    }

}
