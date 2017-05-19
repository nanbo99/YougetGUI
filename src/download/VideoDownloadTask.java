package download;

import com.getting.util.annotation.UiThread;
import com.getting.util.executor.ExecuteTask;
import javafx.beans.property.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.YougetPreference;

import javax.xml.bind.annotation.XmlElement;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class VideoDownloadTask extends ExecuteTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoDownloadTask.class);

    private final StringProperty videoProfile = new SimpleStringProperty();
    private final StringProperty url = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final DoubleProperty progress = new SimpleDoubleProperty();
    private final ObjectProperty<File> downloadDirectory = new SimpleObjectProperty<>();
    private final StringProperty title = new SimpleStringProperty();
    private YougetPreference yougetPreference = YougetPreference.getInstance();

    public VideoDownloadTask(String url, File downloadDirectory) {
        this.url.set(url);
        this.title.set(url);
        this.downloadDirectory.set(downloadDirectory);
    }

    // don't remove, add for JAXB
    public VideoDownloadTask() {
    }

    @NotNull
    @Override
    public List<String> buildParameters() {
        List<String> command = new ArrayList<>();
        command.add("-d");

        // add proxy
        if (yougetPreference.enableProxyProperty().get()) {
            command.add("-x");
            command.add(yougetPreference.getProxyHost() + ":" + yougetPreference.getProxyPort());
        }

        command.add("-o");
        command.add(downloadDirectory.get().getAbsolutePath());
        command.add(url.get());

        StringBuilder builder = new StringBuilder();
        for (String string : command) {
            builder.append(string);
            builder.append(" ");
        }
        LOGGER.info("buildParameters: " + builder.toString());

        return command;
    }

    @Override
    public File getOutputDirectory() {
        return downloadDirectory.get();
    }

    @XmlElement
    public String getVideoProfile() {
        return videoProfile.get();
    }

    @UiThread
    public void setVideoProfile(String videoProfile) {
        this.videoProfile.set(videoProfile);
    }

    public double getProgress() {
        return progress.get();
    }

    @UiThread
    public void setProgress(double progress) {
        this.progress.set(progress);
    }

    @NotNull
    public DoubleProperty progressProperty() {
        return progress;
    }

    @NotNull
    public StringProperty videoProfileProperty() {
        return videoProfile;
    }

    @XmlElement
    public File getDownloadDirectory() {
        return downloadDirectory.get();
    }

    public void setDownloadDirectory(File downloadDirectory) {
        this.downloadDirectory.set(downloadDirectory);
    }

    @XmlElement
    public String getTitle() {
        return title.get();
    }

    @UiThread
    public void setTitle(String title) {
        this.title.set(title);
    }

    @NotNull
    public StringProperty titleProperty() {
        return title;
    }

    @NotNull
    public ObjectProperty<File> downloadDirectoryProperty() {
        return downloadDirectory;
    }

    @XmlElement
    public String getUrl() {
        return url.get();
    }

    public void setUrl(String url) {
        this.url.set(url);
    }

    @NotNull
    public StringProperty urlProperty() {
        return url;
    }

    public String getStatus() {
        return status.get();
    }

    @UiThread
    public void setStatus(String status) {
        this.status.set(status);
    }

    @NotNull
    public StringProperty statusProperty() {
        return status;
    }

}
