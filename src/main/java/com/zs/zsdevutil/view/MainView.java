package com.zs.zsdevutil.view;

import com.zs.zsdevutil.model.BaseController;
import com.zs.zsdevutil.service.NotificationService;
import com.zs.zsdevutil.viewmodel.MainViewModel;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainView implements BaseController<MainViewModel> {

    private static final Logger log = LoggerFactory.getLogger(MainView.class);
    @FXML private TextField searchField;
    @FXML private ListView<String> toolsList;
    @FXML private StackPane contentArea;
    @FXML private HBox notificationArea;
    @FXML private Label notificationLabel;

    private MainViewModel viewModel;

    @FXML private VBox sidebarContainer;

    @FXML private VBox sideNav;

    @FXML private Label contentTitle;


    public void setViewModel(MainViewModel viewModel) {
        this.viewModel = viewModel;
        bindViewModel();
    }



    @FXML
    private void initialize() {
        NotificationService.getInstance().addListener(this::handleNotification);
        notificationArea.setManaged(false);
        notificationArea.setVisible(false);
        sidebarContainer.setPrefWidth(300);

    }

    private void bindViewModel() {
        toolsList.setItems(viewModel.getTools());
        toolsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                viewModel.showSelectedTool(newVal);
            }
        });

        searchField.textProperty().addListener((obs, oldVal, newVal) -> viewModel.filterTools(newVal));

        viewModel.currentToolProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(newVal);
            }
        });

        contentTitle.textProperty().bind(viewModel.getContentTitle());
    }




    private void handleNotification(String message) {
        if (!javafx.application.Platform.isFxApplicationThread()) {
            javafx.application.Platform.runLater(() -> handleNotification(message));
            return;
        }

        notificationLabel.setText(message);
        showNotification();
    }

    private void showNotification() {
        if (!notificationArea.isVisible()) {
            notificationArea.setManaged(true);
            notificationArea.setVisible(true);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), notificationArea);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);

            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200),notificationArea);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event->{
                notificationArea.setVisible(false);
                notificationArea.setManaged(false);
            });


            new SequentialTransition(fadeIn,pause,fadeOut).play();
        }
    }

    @FXML
    private void closeNotification() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), notificationArea);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> {
            notificationArea.setManaged(false);
            notificationArea.setVisible(false);
        });
        fadeOut.play();
    }
}