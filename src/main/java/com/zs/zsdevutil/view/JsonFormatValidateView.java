package com.zs.zsdevutil.view;

import com.zs.zsdevutil.controls.TransparentBgLineNoFactory;
import com.zs.zsdevutil.etc.ErrorHandler;
import com.zs.zsdevutil.etc.JsonHighlighter;
import com.zs.zsdevutil.model.BaseController;
import com.zs.zsdevutil.viewmodel.JsonFormatValidateViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JsonFormatValidateView implements BaseController<JsonFormatValidateViewModel> {
    private static final Logger log = LoggerFactory.getLogger(JsonFormatValidateView.class);
    @FXML private VBox outputContainer;
    @FXML
    private TextField jsonPathField;
    @FXML
    private ComboBox<Integer> indentationComboBox;
    @FXML
    private Label statusLabel;
    @FXML
    private CodeArea inputArea;
    @FXML
    private CodeArea outputArea;
    private JsonFormatValidateViewModel viewModel;


    @FXML
    private void initialize() {
        indentationComboBox.getItems().addAll(2, 4, 8);
        indentationComboBox.getSelectionModel().select(0);

        indentationComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Integer object) {
                return object != null ? object + " spaces" : "";
            }

            @Override
            public Integer fromString(String string) {
                try {
                    return Integer.parseInt(string.replace(" spaces", ""));
                } catch (NumberFormatException e) {
                    return 2; // Default value
                }
            }
        });


        inputArea.setParagraphGraphicFactory(TransparentBgLineNoFactory.get(inputArea));
        //To allow user format when pressing Ctrl+Enter
        inputArea.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.ENTER) {
                event.consume(); // Prevents the event from being processed further
                formatJson();
            }
        });
        setupDarkModeCaretListener(inputArea);

        jsonPathField.addEventFilter(KeyEvent.KEY_PRESSED,event->{
            if (event.isControlDown() && event.getCode() == KeyCode.ENTER) {
                event.consume();
                applyJsonPath();
            }
        });

        outputArea.setEditable(false);
        outputArea.setParagraphGraphicFactory(TransparentBgLineNoFactory.get(outputArea));
        setupDarkModeCaretListener(outputArea);
        new JsonHighlighter().setupHighlighting(outputArea);
    }


    @Override
    public void setViewModel(JsonFormatValidateViewModel viewModel) {
        this.viewModel = viewModel;
        bindViewModel();
    }

    private void bindViewModel() {
        jsonPathField.textProperty().bindBidirectional(viewModel.jsonPathProperty());

        indentationComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                viewModel.indentationProperty().set(newVal);
            }
        });
        viewModel.indentationProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                indentationComboBox.getSelectionModel().select(newVal.intValue());
            }
        });

        viewModel.isValidJsonProperty().addListener((obs, oldVal, newVal) -> {
            statusLabel.setText(newVal ? "Valid JSON" : "Invalid JSON");
            statusLabel.setStyle(newVal ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
        });


        // Bidirectional binding for inputArea
        // Set up new listeners for bidirectional    updates
        inputArea.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.equals(viewModel.inputProperty().get())) {
                viewModel.inputProperty().set(newText);
            }
        });

        viewModel.inputProperty().addListener((obs, oldText, newText) -> {
            if (!newText.equals(inputArea.getText())) {
                Platform.runLater(() -> inputArea.replaceText(newText));
            }
        });

        // Ensure initial synchronization
        if (!viewModel.inputProperty().get().equals(inputArea.getText())) {
            Platform.runLater(() -> inputArea.replaceText(viewModel.inputProperty().get()));
        }

        // Update outputArea when ViewModel changes
        viewModel.outputProperty().addListener((obs, oldText, newText) -> outputArea.replaceText(newText));
    }

    @FXML
    private void formatJson() {
        viewModel.formatJson();
    }

    @FXML
    private void applyJsonPath() {
        viewModel.applyJsonPath();
    }

    @FXML
    private void pasteFromClipboard() {
        viewModel.pasteFromClipboard();
    }

    @FXML
    private void loadSample() {
        viewModel.loadSample();
    }

    @FXML
    private void clearInput() {
        viewModel.clearInput();
    }

    @FXML
    private void copyOutput() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(outputArea.getText());
        clipboard.setContent(content);
    }

    @FXML
    private void openJsonPathTutorial(ActionEvent actionEvent) {
        String url = "https://datatracker.ietf.org/doc/rfc9535"; // URL for a JSON Path tutorial
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException e) {
            ErrorHandler.logAndNotify(e);
        }
    }

    private void setupDarkModeCaretListener(CodeArea codeArea){
        codeArea.caretPositionProperty().addListener((obs, oldPos, newPos) -> Platform.runLater(() -> {
            codeArea.setStyleSpans(0, codeArea.getStyleSpans(0, codeArea.getLength()));
            int paragraph = codeArea.getCurrentParagraph();
            int lineStart = codeArea.getAbsolutePosition(paragraph, 0);
            int lineEnd = lineStart + codeArea.getParagraphLength(paragraph);

            StyleSpans<Collection<String>> styles = codeArea.getStyleSpans(lineStart, lineEnd);
            StyleSpans<Collection<String>> newStyles = styles.mapStyles(styleSet -> {
                List<String> newStyleSet = new ArrayList<>(styleSet);
                newStyleSet.add("current-line");
                return newStyleSet;
            });

            codeArea.setStyleSpans(lineStart, newStyles);
        }));
    }
}