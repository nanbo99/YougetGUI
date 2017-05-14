package download;

import com.getting.util.annotation.NotUiThread;
import com.getting.util.executor.Executor;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import org.jetbrains.annotations.NotNull;

public class VideoDownload extends Executor {

    private final StringProperty downloadSpeed = new SimpleStringProperty();

    public VideoDownload() {
        super(VideoDownload.class, "you-get-0.4.715-win32-full.exe");
    }

    public String getDownloadSpeed() {
        return downloadSpeed.get();
    }

    @NotNull
    public StringProperty downloadSpeedProperty() {
        return downloadSpeed;
    }

    @NotUiThread
    public void download(@NotNull VideoDownloadTask task) {
        Platform.runLater(() -> {
            task.setProgress(Double.NEGATIVE_INFINITY);
            task.setStatus("");
        });

        ChangeListener<String> listener = (observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }

            final String title = YougetUtil.getTitle(newValue);
            if (title != null) {
                Platform.runLater(() -> task.setTitle(title));
            }

            final String videoProfile = YougetUtil.getVideoProfile(newValue);
            if (videoProfile != null) {
                Platform.runLater(() -> task.setVideoProfile(videoProfile));
            }

            final YougetUtil.DownloadProgress downloadProgress = YougetUtil.getDownloadProgress(newValue);
            if (downloadProgress != null) {
                Platform.runLater(() -> {
                    task.setStatus(downloadProgress.description.trim());
                    task.setProgress(downloadProgress.downloaded / downloadProgress.total);
                });
            }

            Platform.runLater(() -> downloadSpeed.set(YougetUtil.getSpeed(newValue)));
        };

        executorOutputMessage.addListener(listener);

        execute(task, false);

        executorOutputMessage.removeListener(listener);

        Platform.runLater(() -> {
            downloadSpeed.set("");
            task.setProgress(1);
        });
    }

    @Override
    public void cancel() {
//        super.cancel();
        new Thread(this::forceCancel).start();
    }

}
