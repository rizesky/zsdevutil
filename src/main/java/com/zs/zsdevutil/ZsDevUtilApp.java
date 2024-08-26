package com.zs.zsdevutil;

import com.zs.zsdevutil.config.ConfigManager;
import com.zs.zsdevutil.config.LoggerConfig;
import com.zs.zsdevutil.etc.ResourceManager;
import com.zs.zsdevutil.view.MainView;
import com.zs.zsdevutil.viewmodel.MainViewModel;
import com.zs.zsdevutil.viewmodel.StatePersistingViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.prefs.Preferences;

import static com.zs.zsdevutil.config.ConfigVariables.APP_ENV;
import static com.zs.zsdevutil.config.ConfigVariables.APP_NAME_LONG;

public class ZsDevUtilApp extends Application {
    private static final Logger logger = LoggerFactory.getLogger(ZsDevUtilApp.class);


    public static void main(String[] args) {
        LoggerConfig.initializeLogger();
        // Set default uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> logger.error("Uncaught exception", throwable));

        logger.info("Application starting for {} environment", ConfigManager.getString(APP_ENV));
        launch();

    }
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(ResourceManager.getFXML("MainView.fxml"));
        Parent root = loader.load();

        root.getStyleClass().add(isDarkMode()?"dark":"light");

        MainView mainView = loader.getController();
        var mainViewModel = new MainViewModel();
        mainView.setViewModel(mainViewModel);

        Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        scene.getStylesheets().addAll(
                ResourceManager.getCSS("javafx-dark.css"),
                ResourceManager.getCSS("my-style.css"),
                ResourceManager.getCSS("code-area.css")
                );

        // Use the getApplicationTitle method to set the stage title
        stage.setTitle(getAppNameLong());
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(event -> {
            mainViewModel.saveState();
            // Save state for each tool's ViewModel
            for (String toolName : mainViewModel.getTools()) {
                Object viewModel = mainViewModel.getToolViewModel(toolName);
                if (viewModel instanceof StatePersistingViewModel) {
                    ((StatePersistingViewModel) viewModel).saveState();
                }
            }
        });
    }


    private String getAppNameLong() {
        String baseTitle = "ZsDevUtil - Developers Utility Tools";
        return ConfigManager.getString(APP_NAME_LONG)==null?baseTitle:ConfigManager.getString(APP_NAME_LONG);
    }

    private boolean isDarkMode() {
        var pre = Preferences.userNodeForPackage(MainView.class);
        return pre.getBoolean("darkMode",false);
    }




}