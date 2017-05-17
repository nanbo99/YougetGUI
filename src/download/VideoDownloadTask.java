package download;

import com.getting.util.annotation.UiThread;
import com.getting.util.executor.ExecuteTask;
import javafx.beans.property.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.YougetPreference;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class VideoDownloadTask extends ExecuteTask implements Externalizable {

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

    // don't remove, needed by serialization
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

    public File getDownloadDirectory() {
        return downloadDirectory.get();
    }

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

    public String getUrl() {
        return url.get();
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

    @Override
    public void writeExternal(@NotNull ObjectOutput out) throws IOException {
        out.writeObject(videoProfile.get());
        out.writeObject(url.get());
        out.writeObject(title.get());
        out.writeObject(downloadDirectory.get());
    }

    @Override
    public void readExternal(@NotNull ObjectInput in) throws IOException, ClassNotFoundException {
        videoProfile.set((String) in.readObject());
        url.set((String) in.readObject());
        title.set((String) in.readObject());
        downloadDirectory.set((File) in.readObject());
    }

}
