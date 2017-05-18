package util;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.Clipboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class ClipboardMonitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClipboardMonitor.class);
    private Clipboard clipboard = Clipboard.getSystemClipboard();
    private ObservableList<String> clipboardStrings = FXCollections.observableArrayList();

    private boolean continueMonitor = true;
    private boolean pauseMonitor = true;

    public ClipboardMonitor() {
        Thread monitorThread = new Thread(() -> {
            LOGGER.info("start monitor");
            try {
                while (continueMonitor) {
                    synchronized (this) {
                        while (pauseMonitor) {
                            LOGGER.info("pause monitor");
                            wait();
                            LOGGER.info("resume monitor");
                        }
                    }

                    Platform.runLater(() -> {
                        if (clipboard.hasString() && clipboard.getString() != null) {
                            List<String> newClipboardString = Arrays.asList(clipboard.getString().split("\n"));
                            for (String string : newClipboardString) {
                                if (!clipboardStrings.contains(string)) {
                                    LOGGER.info("new string: " + string);
                                    clipboardStrings.add(string);
                                }
                            }
                        }
                    });

                    Thread.sleep(50);
                }
            } catch (InterruptedException e) {
                LOGGER.error("", e);
            }

            LOGGER.info("stop monitor");
        });
        monitorThread.setDaemon(true);
        monitorThread.start();
    }

    public synchronized void pause() {
        pauseMonitor = true;
        notifyAll();
    }

    public synchronized void start() {
        pauseMonitor = false;
        notifyAll();
    }

    public synchronized void quit() {
        continueMonitor = false;
        notifyAll();
    }

    public ObservableList<String> getClipboardStrings() {
        return clipboardStrings;
    }

}
