package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import java.io.IOException;

public class ProxySetupDialog extends Dialog<Void> {

    public ProxySetupDialog() {
        setTitle("设置代理服务器");
        try {
            Parent root = FXMLLoader.load(ProxySetupDialog.class.getResource("proxy.fxml"));
            getDialogPane().setContent(root);
        } catch (IOException e) {
            e.printStackTrace();
        }

        getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        setResultConverter(param -> null);
    }

}
