package com.zs.zsdevutil.viewmodel;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.JsonPathException;
import com.zs.zsdevutil.etc.ErrorHandler;
import com.zs.zsdevutil.etc.StatePersistence;
import javafx.beans.property.*;
import javafx.scene.input.Clipboard;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonFormatValidateViewModel implements StatePersistingViewModel {
    private static final Logger log = LoggerFactory.getLogger(JsonFormatValidateViewModel.class);
    private final StringProperty input = new SimpleStringProperty("");
    private final StringProperty output = new SimpleStringProperty("");
    private final StringProperty jsonPath = new SimpleStringProperty();
    private final IntegerProperty indentation = new SimpleIntegerProperty(2);
    private final BooleanProperty isValidJson = new SimpleBooleanProperty();

    public JsonFormatValidateViewModel(){
        loadPersistedState();

    }
    public void formatJson() {
        formatJson(input.get().trim());
    }

    private void formatJson(String jsonString) {
        try {
            Object json = jsonString.startsWith("[") ? new JSONArray(jsonString) : new JSONObject(jsonString);
            output.set(indentateAndToString(json, indentation.get()));
            log.info("Indent: " + indentation.get());
            isValidJson.set(true);
        } catch (JSONException e) {
            output.set("Invalid JSON: " + e.getMessage());
            isValidJson.set(false);
        } catch (Exception e) {
            ErrorHandler.logAndNotify(e);
        } finally {
            saveState();
        }
    }

    private String indentateAndToString(Object json, int indentSpaces) {
        if (json instanceof JSONObject) {
            return formatJSONObject((JSONObject) json, indentSpaces);
        } else if (json instanceof JSONArray) {
            return formatJSONArray((JSONArray) json,indentSpaces);
        }
        return json.toString();
    }

    private String formatJSONObject(JSONObject jsonObject, int indentLevel) {
        StringBuilder sb = new StringBuilder();
        String indentString = "  ".repeat(indentLevel);
        String innerIndentString = "  ".repeat(indentLevel + 1);

        sb.append("{\n");

        boolean first = true;
        for (String key : jsonObject.keySet()) {
            if (!first) {
                sb.append(",\n");
            }
            first = false;

            sb.append(innerIndentString).append("\"").append(key).append("\": ");

            Object value = jsonObject.get(key);
            if (value instanceof JSONObject) {
                sb.append(formatJSONObject((JSONObject) value, indentLevel + 1));
            } else if (value instanceof JSONArray) {
                sb.append(formatJSONArray((JSONArray) value, indentLevel + 1));
            } else {
                sb.append(JSONObject.valueToString(value));
            }
        }

        sb.append("\n").append(indentString).append("}");

        return sb.toString();
    }

    private String formatJSONArray(JSONArray jsonArray, int indentLevel) {
        StringBuilder sb = new StringBuilder();
        String indentString = "  ".repeat(indentLevel);
        String innerIndentString = "  ".repeat(indentLevel + 1);

        sb.append("[\n");

        for (int i = 0; i < jsonArray.length(); i++) {
            if (i > 0) {
                sb.append(",\n");
            }

            Object value = jsonArray.get(i);
            sb.append(innerIndentString);
            if (value instanceof JSONObject) {
                sb.append(formatJSONObject((JSONObject) value, indentLevel + 1));
            } else if (value instanceof JSONArray) {
                sb.append(formatJSONArray((JSONArray) value, indentLevel + 1));
            } else {
                sb.append(JSONObject.valueToString(value));
            }
        }

        sb.append("\n").append(indentString).append("]");

        return sb.toString();
    }

    public void applyJsonPath() {
        try {
            var document = com.jayway.jsonpath.Configuration.defaultConfiguration().jsonProvider().parse(input.get());
            var result = JsonPath.read(document, jsonPath.get());

            if(result==null){
                log.info("No result of json path");
                return;
            }
            formatJson(result.toString());
            isValidJson.set(true);
        } catch (JsonPathException e) {
            output.set("Invalid JSON Path: " + e.getMessage());
            isValidJson.set(false);
        } catch (Exception e) {
            output.set("Error applying JSON Path: " + e.getMessage());
            isValidJson.set(false);
        } finally {
            saveState();
        }
    }

    public void pasteFromClipboard() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard.hasString()) {
            input.set(clipboard.getString());
        }
    }

    public void loadSample() {
        input.set("""
                {
                  "store": {
                    "book": [
                      {
                        "category": "reference",
                        "sold": false,
                        "author": "Nigel Rees",
                        "title": "Sayings of the Century",
                        "price": 8.95
                      },
                      {
                        "category": "fiction",
                        "author": "Evelyn Waugh",
                        "title": "Sword of Honour",
                        "price": 12.99
                      },
                      {
                        "category": "fiction",
                        "author": "J. R. R. Tolkien",
                        "title": "The Lord of the Rings",
                        "act": null,
                        "isbn": "0-395-19395-8",
                        "price": 22.99
                      }
                    ],
                    "bicycle": {
                      "color": "red",
                      "price": 19.95
                    }
                  }
                }""");
    }

    public void clearInput() {
        input.set("");
    }

    // Getters for properties
    public StringProperty inputProperty() { return input; }
    public StringProperty outputProperty() { return output; }
    public StringProperty jsonPathProperty() { return jsonPath; }
    public IntegerProperty indentationProperty() { return indentation; }
    public BooleanProperty isValidJsonProperty() { return isValidJson; }

    @Override
    public void loadPersistedState() {
        try {
            JSONObject state = StatePersistence.loadState("JsonFormatValidate");
            input.set(state.optString("input", ""));
            jsonPath.set(state.optString("jsonPath", ""));
        } catch (Exception e){
            ErrorHandler.logAndNotify(e);
        }

    }

    @Override
    public void saveState() {
        try {
            JSONObject state = new JSONObject();
            state.put("input", input.get());
            state.put("jsonPath", jsonPath.get());
            StatePersistence.saveState("JsonFormatValidate", state);
        } catch (Exception e) {
            ErrorHandler.logAndNotify(e);
        }
    }
}