package main;

import com.getting.util.AsyncTask;
import com.getting.util.Looper;
import com.getting.util.Task;
import com.getting.util.annotation.UiThread;
import download.VideoDownload;
import download.VideoDownloadTask;
import download.VideoDownloadTaskList;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.stage.DirectoryChooser;
import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.action.Action;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ClipboardMonitor;
import util.DownloadHistoryUtil;
import util.YougetPreference;
import view.ProxySetupDialog;
import view.VideoUrlInputDialog;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);

    private static final File DOWNLOAD_HISTORY_FILE = new File(System.getProperty("java.io.tmpdir"), "youget-gui-history.xml");

    private final VideoDownload videoDownload = new VideoDownload();
    private final Looper downloadLooper = new Looper("download");
    private final Looper downloadHistoryLooper = new Looper("download history");

    @FXML
    private CheckMenuItem enableMonitorClipboardView;

    private YougetPreference yougetPreference = YougetPreference.getInstance();

    @FXML
    private NotificationPane notification;
    @FXML
    private Label downloadSpeedView;
    @FXML
    private Label downloadDirectoryView;
    @FXML
    private TableColumn<VideoDownloadTask, String> videoTitleColumn;
    @FXML
    private TableColumn<VideoDownloadTask, String> videoProfileColumn;
    @FXML
    private TableColumn<VideoDownloadTask, File> downloadDirectoryColumn;
    @FXML
    private TableView<VideoDownloadTask> downloadList;
    @FXML
    private TableColumn<VideoDownloadTask, String> downloadStatusColumn;
    @FXML
    private TableColumn<VideoDownloadTask, Double> downloadProgressColumn;

    private ClipboardMonitor clipboardMonitor = new ClipboardMonitor();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeDownloadList();

        downloadDirectoryView.textProperty().bind(yougetPreference.downloadDirectoryProperty());
        downloadSpeedView.textProperty().bind(videoDownload.downloadSpeedProperty());

        enableMonitorClipboardView.setSelected(yougetPreference.isEnableMonitorClipboard());
        yougetPreference.enableMonitorClipboardProperty().bind(enableMonitorClipboardView.selectedProperty());

        downloadLooper.postTask(new ReadDownloadHistoryTask());

        initClipboardMonitor();

        Platform.runLater(this::addExitListener);
    }

    private void initClipboardMonitor() {
        clipboardMonitor.getClipboardStrings().addListener((ListChangeListener<String>) c -> {
            while (c.next()) {
                for (String string : c.getAddedSubList()) {
                    if (string.contains(".com")) {
                        addDownloadTask(new String[]{string});
                    }
                }
            }
        });
        enableMonitorClipboardView.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                clipboardMonitor.start();
            } else {
                clipboardMonitor.pause();
            }
        });
        if (enableMonitorClipboardView.isSelected()) {
            clipboardMonitor.start();
        }
    }

    private void initializeDownloadList() {
        videoProfileColumn.setCellValueFactory(new PropertyValueFactory<>("videoProfile"));
        downloadStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        downloadProgressColumn.setCellValueFactory(new PropertyValueFactory<>("progress"));
        downloadProgressColumn.setCellFactory(ProgressBarTableCell.forTableColumn());
        downloadDirectoryColumn.setCellValueFactory(new PropertyValueFactory<>("downloadDirectory"));
        videoTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        downloadList.getItems().addListener((ListChangeListener<VideoDownloadTask>) c -> {
            downloadHistoryLooper.removeTask(SAVE_DOWNLOAD_HISTORY_ID);
            downloadHistoryLooper.postTask(new SaveDownloadHistoryTask());
        });
    }

    @UiThread
    private void addDownloadTask(@NotNull String[] urls) {
        ArrayList<VideoDownloadTask> params = new ArrayList<>();
        for (String url : urls) {
            url = url.trim();
            if (url.isEmpty()) {
                continue;
            }

            params.add(new VideoDownloadTask(url, new File(yougetPreference.getDownloadDirectory())));
        }

        addDownloadTask(params);
    }

    @UiThread
    private void addDownloadTask(@NotNull List<VideoDownloadTask> params) {
        for (VideoDownloadTask param : params) {
            downloadList.getItems().add(param);
            downloadLooper.postTask(new DownloadTask(param));
        }

        downloadList.requestFocus();
        downloadList.getSelectionModel().selectLast();
    }

    private void exit() {
        downloadLooper.removeAllTasks();
        downloadHistoryLooper.quit();
        downloadLooper.quit();
        clipboardMonitor.quit();
    }

    private void addExitListener() {
        downloadList.getScene().getWindow().setOnCloseRequest(event -> {
            downloadHistoryLooper.removeTask(SAVE_DOWNLOAD_HISTORY_ID);
            downloadHistoryLooper.postTask(new SaveDownloadHistoryTask());

            if (downloadLooper.isAllDone()) {
                exit();
                return;
            }

            event.consume();

            notification.getActions().clear();
            notification.getActions().add(new Action("退出", actionEvent -> {
                exit();
                Platform.exit();
            }));
            notification.show("还有视频在下载，确认退出？");
        });
    }

    @FXML
    private void onAddUrlClick() {
        VideoUrlInputDialog videoUrlInputDialog = new VideoUrlInputDialog();
        videoUrlInputDialog.setTitle("新建下载");
        videoUrlInputDialog.initOwner(downloadList.getScene().getWindow());
        videoUrlInputDialog.showAndWait().ifPresent(s -> addDownloadTask(s.split("\n")));
    }

    @FXML
    private void onOpenDownloadDirectoryClick() {
        if (downloadList.getSelectionModel().isEmpty()) {
            return;
        }

        try {
            java.awt.Desktop.getDesktop().open(downloadList.getSelectionModel().getSelectedItem().getDownloadDirectory());
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

    @FXML
    private void onSetDownloadDirectoryClick() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File downloadDirectory = new File(yougetPreference.getDownloadDirectory());
        if (downloadDirectory.isDirectory()) {
            directoryChooser.setInitialDirectory(downloadDirectory);
        }

        File directory = directoryChooser.showDialog(downloadList.getScene().getWindow());
        if (directory == null) {
            return;
        }

        yougetPreference.setDownloadDirectory(directory.toString());
        downloadDirectoryView.setText(directory.toString());
    }

    @FXML
    private void onClear() {
        downloadList.getItems().clear();
        downloadLooper.removeAllTasks();
    }

    @FXML
    private void onAddUrlFromClipboardClick() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        LOGGER.info("onAddUrlFromClipboardClick");
        if (clipboard.hasString() && clipboard.getString() != null) {
            LOGGER.info(clipboard.getString());
            addDownloadTask(clipboard.getString().split("\n"));
        }
    }

    @FXML
    private void onRemoveClick() {
        if (downloadList.getSelectionModel().isEmpty()) {
            return;
        }

        VideoDownloadTask taskNeedRemove = downloadList.getSelectionModel().getSelectedItem();
        downloadLooper.removeTask(taskNeedRemove);
        downloadList.getItems().remove(taskNeedRemove);
    }

    @FXML
    private void onSetupProxy() {
        ProxySetupDialog proxySetupDialog = new ProxySetupDialog();
        proxySetupDialog.initOwner(downloadList.getScene().getWindow());
        proxySetupDialog.showAndWait();
    }

    private class ReadDownloadHistoryTask extends AsyncTask<List<VideoDownloadTask>> {

        public ReadDownloadHistoryTask() {
            super(null, 0);
        }

        @Override
        public List<VideoDownloadTask> runTask() {
            if (!DOWNLOAD_HISTORY_FILE.exists()) {
                return new ArrayList<>();
            }

            try {
                VideoDownloadTaskList videoDownloadTaskList = (VideoDownloadTaskList) DownloadHistoryUtil.xmlToBean(DOWNLOAD_HISTORY_FILE, VideoDownloadTaskList.class);
                if (videoDownloadTaskList.getVideoDownloadTasks() == null) {
                    return new ArrayList<>();
                } else {
                    return videoDownloadTaskList.getVideoDownloadTasks();
                }
            } catch (Exception e) {
                LOGGER.error("ReadDownloadHistoryTask", e);
            }

            return new ArrayList<>();
        }

        @Override
        public void postTaskOnUi(List<VideoDownloadTask> result) {
            addDownloadTask(result);
        }

    }

    private static final Object SAVE_DOWNLOAD_HISTORY_ID = new Object();

    private class SaveDownloadHistoryTask extends Task {

        public SaveDownloadHistoryTask() {
            super(SAVE_DOWNLOAD_HISTORY_ID, 0);
        }

        @Override
        public void run() {
            try {
                DownloadHistoryUtil.beanToXml(DOWNLOAD_HISTORY_FILE, new VideoDownloadTaskList(new ArrayList<>(downloadList.getItems())), VideoDownloadTaskList.class);
            } catch (Exception e) {
                LOGGER.error("SaveDownloadHistoryTask", e);
            }
        }

    }

    private class DownloadTask extends AsyncTask<Void> {

        private final VideoDownloadTask videoDownloadTask;

        public DownloadTask(VideoDownloadTask videoDownloadTask) {
            super(videoDownloadTask, 0);
            this.videoDownloadTask = videoDownloadTask;
        }

        @Override
        public Void runTask() {
            videoDownload.download(videoDownloadTask);
            return null;
        }

        @Override
        public void preTaskOnUi() {
            downloadList.scrollTo(videoDownloadTask);
        }

        @Override
        public void cancel() {
            videoDownload.cancel();
        }

    }

}
