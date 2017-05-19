package download;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class VideoDownloadTaskList {

    public VideoDownloadTaskList(List<VideoDownloadTask> videoDownloadTasks) {
        this.videoDownloadTasks = videoDownloadTasks;
    }

    // don't remove, add for JAXB
    public VideoDownloadTaskList() {
    }

    private List<VideoDownloadTask> videoDownloadTasks;

    @XmlElement
    public List<VideoDownloadTask> getVideoDownloadTasks() {
        return videoDownloadTasks;
    }

    public void setVideoDownloadTasks(List<VideoDownloadTask> videoDownloadTasks) {
        this.videoDownloadTasks = videoDownloadTasks;
    }

}
