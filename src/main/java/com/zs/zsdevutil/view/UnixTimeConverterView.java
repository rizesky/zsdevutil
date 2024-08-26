package com.zs.zsdevutil.view;

import com.zs.zsdevutil.model.BaseController;
import com.zs.zsdevutil.model.TimeConversionMode;
import com.zs.zsdevutil.viewmodel.UnixTimeConverterViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnixTimeConverterView implements BaseController<UnixTimeConverterViewModel> {
    private static final Logger log = LoggerFactory.getLogger(UnixTimeConverterView.class);
    @FXML private TextField inputField;
    @FXML private Label errorMessageLabel;
    @FXML private ComboBox<TimeConversionMode> conversionModeComboBox;
    @FXML private ListView<String> supportedFormatListView;
    @FXML private TitledPane supportedDateFormatPane;
    @FXML private TextField localDateField;
    @FXML private TextField dayOfYearField;
    @FXML private TextField utcField;
    @FXML private TextField weekOfYearField;
    @FXML private TextField relativeField;
    @FXML private TextField isLeapYearField;
    @FXML private TextField unixTimeField;
    @FXML private TextField otherFormat1;
    @FXML private TextField otherFormat2;
    @FXML private TextField otherFormat3;
    @FXML private TextField otherFormat4;
    @FXML private TextField otherFormat5;
    @FXML private TextField otherFormat6;
    @FXML private TextField otherFormat7;
    @FXML private TextField otherFormat8;

    private UnixTimeConverterViewModel viewModel;

    @Override
    public void setViewModel(UnixTimeConverterViewModel viewModel) {
        this.viewModel = viewModel;
        bindViewModel();
    }

    @FXML
    private void initialize() {
        errorMessageLabel.setVisible(false);
        errorMessageLabel.setManaged(false);

        conversionModeComboBox.getItems().addAll(TimeConversionMode.values());
        conversionModeComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(TimeConversionMode mode) {
                return mode.getDisplayName();
            }

            @Override
            public TimeConversionMode fromString(String string) {
                return TimeConversionMode.valueOf(string);
            }
        });

        ObservableList<String> formats = FXCollections.observableArrayList(
                "MM/dd/yyyy (e.g., 12/03/2011)",
                "yyyy-MM-dd (e.g., 2011-12-03)",
                "dd-MM-yyyy HH:mm (e.g., 03-12-2011 13:45)",
                "MMM d, yyyy (e.g., Dec 3, 2011)",
                "ISO Local Date (e.g., 2011-12-03)",
                "ISO Local Date Time (e.g., 2011-12-03T10:15:30)"
        );
        supportedFormatListView.setItems(formats);


    }

    private void bindViewModel() {
        inputField.textProperty().bindBidirectional(viewModel.inputProperty());
        conversionModeComboBox.valueProperty().bindBidirectional(viewModel.conversionModeProperty());

        errorMessageLabel.textProperty().bind(viewModel.errorMessageProperty());
        errorMessageLabel.visibleProperty().bind(viewModel.hasErrorProperty());
        errorMessageLabel.managedProperty().bind(viewModel.hasErrorProperty());

        viewModel.hasErrorProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                inputField.getStyleClass().add("error");
            } else {
                inputField.getStyleClass().remove("error");
            }
        });

        localDateField.textProperty().bind(viewModel.localDateProperty());
        dayOfYearField.textProperty().bind(viewModel.dayOfYearProperty());
        utcField.textProperty().bind(viewModel.utcProperty());
        weekOfYearField.textProperty().bind(viewModel.weekOfYearProperty());
        relativeField.textProperty().bind(viewModel.relativeProperty());
        isLeapYearField.textProperty().bind(viewModel.isLeapYearProperty());
        unixTimeField.textProperty().bind(viewModel.unixTimeProperty());
        otherFormat1.textProperty().bind(viewModel.otherFormat1Property());
        otherFormat2.textProperty().bind(viewModel.otherFormat2Property());
        otherFormat3.textProperty().bind(viewModel.otherFormat3Property());
        otherFormat4.textProperty().bind(viewModel.otherFormat4Property());
        otherFormat5.textProperty().bind(viewModel.otherFormat5Property());
        otherFormat6.textProperty().bind(viewModel.otherFormat6Property());
        otherFormat7.textProperty().bind(viewModel.otherFormat7Property());
        otherFormat8.textProperty().bind(viewModel.otherFormat8Property());

        inputField.textProperty().addListener((obs, oldVal, newVal) -> viewModel.convert());
        conversionModeComboBox.valueProperty().addListener((obs, oldMode, mode) ->{
            log.info("mode changed: {},{}", obs,mode);
            switch (mode){
                case UNIX_TIME -> {
                    supportedDateFormatPane.setExpanded(false);
                    supportedDateFormatPane.setVisible(false);
                }
                case HUMAN_READABLE -> {
                    supportedDateFormatPane.setVisible(true);
                    supportedDateFormatPane.setExpanded(true);
                }
            }
            viewModel.convert();
        });
    }

    @FXML
    private void setNow() {
        viewModel.setNow();
    }

    @FXML
    private void pasteFromClipboard() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard.hasString()) {
            inputField.setText(clipboard.getString());
        }
    }

    @FXML
    private void clearInput() {
        inputField.clear();
    }

    @FXML
    private void copyLocal() {
        copyToClipboard(localDateField.getText());
    }

    @FXML
    private void copyDayOfYear() {
        copyToClipboard(dayOfYearField.getText());
    }

    @FXML
    private void copyUTC() {
        copyToClipboard(utcField.getText());
    }

    @FXML
    private void copyWeekOfYear() {
        copyToClipboard(weekOfYearField.getText());
    }

    @FXML
    private void copyRelative() {
        copyToClipboard(relativeField.getText());
    }

    @FXML
    private void copyIsLeapYear() {
        copyToClipboard(isLeapYearField.getText());
    }

    @FXML
    private void copyUnixTime() {
        copyToClipboard(unixTimeField.getText());
    }

    @FXML
    private void copyOtherFormat1() {
        copyToClipboard(otherFormat1.getText());
    }

    @FXML
    private void copyOtherFormat2() {
        copyToClipboard(otherFormat2.getText());
    }

    @FXML
    private void copyOtherFormat3() {
        copyToClipboard(otherFormat3.getText());
    }

    @FXML
    private void copyOtherFormat4() {
        copyToClipboard(otherFormat4.getText());
    }
    @FXML private void copyOtherFormat5() { copyToClipboard(otherFormat5.getText()); }
    @FXML private void copyOtherFormat6() { copyToClipboard(otherFormat6.getText()); }
    @FXML private void copyOtherFormat7() { copyToClipboard(otherFormat7.getText()); }
    @FXML private void copyOtherFormat8() { copyToClipboard(otherFormat8.getText()); }


    private void copyToClipboard(String text) {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
    }
}