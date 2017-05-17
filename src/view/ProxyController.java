package view;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.YougetPreference;

import java.net.URL;
import java.util.ResourceBundle;

public class ProxyController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyController.class);

    public TextField proxyPortView;
    public TextField proxyHostView;
    public CheckBox enableProxyView;

    private YougetPreference yougetPreference = YougetPreference.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        enableProxyView.setSelected(yougetPreference.isEnableProxy());
        proxyHostView.setText(yougetPreference.getProxyHost());
        proxyPortView.setText(yougetPreference.getProxyPort());

        proxyHostView.disableProperty().bind(enableProxyView.selectedProperty().not());
        proxyPortView.disableProperty().bind(enableProxyView.selectedProperty().not());

        Platform.runLater(() -> proxyPortView.getScene().getWindow().setOnHidden(event -> {
            yougetPreference.setEnableProxy(enableProxyView.isSelected());
            yougetPreference.setProxyHost(proxyHostView.getText().trim());
            yougetPreference.setProxyPort(proxyPortView.getText().trim());
        }));
    }

}
