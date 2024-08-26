package com.zs.zsdevutil.viewmodel;

import com.zs.zsdevutil.etc.ErrorHandler;
import com.zs.zsdevutil.etc.ResourceManager;
import com.zs.zsdevutil.etc.StatePersistence;
import com.zs.zsdevutil.model.BaseController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainViewModel implements StatePersistingViewModel{

    private final ObservableList<String> tools = FXCollections.observableArrayList();
    private final ObjectProperty<Node> currentTool = new SimpleObjectProperty<>();
    private final StringProperty lastSelectedTool = new SimpleStringProperty("");
    private final StringProperty contentTitle = new SimpleStringProperty("Zs DevUtils");

    public ObservableValue<String> getSelectedTool() {
        return lastSelectedTool;
    }

    public ObservableValue<String> getContentTitle() {
        return contentTitle;
    }

    private static class ToolInfo {
        String viewFxmlPath;
        Object viewModel;  // Store the ViewModel instance

        ToolInfo(String viewFxmlPath, Object viewModel) {
            this.viewFxmlPath = viewFxmlPath;
            this.viewModel = viewModel;
        }
    }

    private final Map<String, ToolInfo> toolInfoMap = new HashMap<>();

    public MainViewModel() {
        initializeTools();
        loadPersistedState();
    }



    private void initializeTools() {
        toolInfoMap.put("Unix Time Converter", new ToolInfo("UnixTimeConverterView.fxml", new UnixTimeConverterViewModel()));
        toolInfoMap.put("JSON Format/Validate", new ToolInfo("JsonFormatValidateView.fxml", new JsonFormatValidateViewModel()));
        // Add more tools here as needed

        tools.addAll(toolInfoMap.keySet());
    }

    public void loadPersistedState() {
        try {
            JSONObject state = StatePersistence.loadState("MainView");
            lastSelectedTool.set(state.optString("lastSelectedTool", ""));
            if (!lastSelectedTool.get().isEmpty()) {
                showSelectedTool(lastSelectedTool.get());
            }
        } catch (IOException e) {
            ErrorHandler.logAndNotify(e);
        }
    }

    public void saveState() {
        try{
            JSONObject state = new JSONObject();
            state.put("lastSelectedTool", lastSelectedTool.get());
            StatePersistence.saveState("MainView", state);
        }catch (IOException e){
            ErrorHandler.logAndNotify(e);
        }
    }

    public void showSelectedTool(String toolName) {
        ToolInfo toolInfo = toolInfoMap.get(toolName);
        if (toolInfo != null) {
            try {
                FXMLLoader loader = new FXMLLoader(ResourceManager.getFXML(toolInfo.viewFxmlPath));
                Node view = loader.load();
                ((BaseController)loader.getController()).setViewModel(toolInfo.viewModel);
                currentTool.set(view);
                lastSelectedTool.set(toolName);
                contentTitle.set(toolName);
                saveState();
            } catch (Exception e) {
                ErrorHandler.logAndNotify(e);
            }
        }
    }

    public Object getToolViewModel(String toolName) {
        ToolInfo toolInfo = toolInfoMap.get(toolName);
        return toolInfo != null ? toolInfo.viewModel : null;
    }

    public void filterTools(String searchText) {
        tools.clear();
        toolInfoMap.keySet().stream()
                .filter(toolName -> toolName.toLowerCase().contains(searchText.toLowerCase()))
                .forEach(tools::add);
    }

    public ObservableList<String> getTools() {
        return tools;
    }

    public ObjectProperty<Node> currentToolProperty() {
        return currentTool;
    }
    
}