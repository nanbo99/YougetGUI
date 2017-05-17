package util;

import com.getting.util.Preference;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class YougetPreference {

    private static final String DOWNLOAD_DIRECTORY_KEY = "download-directory";
    private static final String DEFAULT_DOWNLOAD_DIRECTORY = System.getProperty("java.io.tmpdir");
    private static final String ENABLE_PROXY_KEY = "enable-proxy";
    private static final String PROXY_HOST_KEY = "proxy-host";
    private static final String PROXY_PORT_KEY = "proxy-port";
    private static final String ENABLE_MONITOR_CLIPBOARD_KEY = "enable-monitor-clipboard";
    private static YougetPreference ourInstance = new YougetPreference();
    private final Preference preference = new Preference("youget-gui");
    private StringProperty downloadDirectory = new SimpleStringProperty();
    private BooleanProperty enableProxy = new SimpleBooleanProperty();
    private StringProperty proxyHost = new SimpleStringProperty();
    private StringProperty proxyPort = new SimpleStringProperty();
    private BooleanProperty enableMonitorClipboard = new SimpleBooleanProperty();

    private YougetPreference() {
        downloadDirectory.set(preference.get(DOWNLOAD_DIRECTORY_KEY, DEFAULT_DOWNLOAD_DIRECTORY));
        enableProxy.set(Boolean.parseBoolean(preference.get(ENABLE_PROXY_KEY, "false")));
        proxyHost.set(preference.get(PROXY_HOST_KEY, "127.0.0.1"));
        proxyPort.set(preference.get(PROXY_PORT_KEY, "8087"));
        enableMonitorClipboard.set(Boolean.parseBoolean(preference.get(ENABLE_MONITOR_CLIPBOARD_KEY, "false")));
    }

    public static YougetPreference getInstance() {
        return ourInstance;
    }

    public String getDownloadDirectory() {
        return downloadDirectory.get();
    }

    public void setDownloadDirectory(String downloadDirectory) {
        preference.save(DOWNLOAD_DIRECTORY_KEY, downloadDirectory);
        this.downloadDirectory.set(downloadDirectory);
    }

    public StringProperty downloadDirectoryProperty() {
        return downloadDirectory;
    }

    public boolean isEnableProxy() {
        return enableProxy.get();
    }

    public void setEnableProxy(boolean enableProxy) {
        preference.save(ENABLE_PROXY_KEY, "" + enableProxy);
        this.enableProxy.set(enableProxy);
    }

    public BooleanProperty enableProxyProperty() {
        return enableProxy;
    }

    public String getProxyHost() {
        return proxyHost.get();
    }

    public void setProxyHost(String proxyHost) {
        preference.save(PROXY_HOST_KEY, proxyHost);
        this.proxyHost.set(proxyHost);
    }

    public StringProperty proxyHostProperty() {
        return proxyHost;
    }

    public String getProxyPort() {
        return proxyPort.get();
    }

    public void setProxyPort(String proxyPort) {
        preference.save(PROXY_PORT_KEY, proxyPort);
        this.proxyPort.set(proxyPort);
    }

    public StringProperty proxyPortProperty() {
        return proxyPort;
    }

    public boolean isEnableMonitorClipboard() {
        return enableMonitorClipboard.get();
    }

    public void setEnableMonitorClipboard(boolean enableMonitorClipboard) {
        preference.save(ENABLE_MONITOR_CLIPBOARD_KEY, "" + enableMonitorClipboard);
        this.enableMonitorClipboard.set(enableMonitorClipboard);
    }

    public BooleanProperty enableMonitorClipboardProperty() {
        return enableMonitorClipboard;
    }

}
