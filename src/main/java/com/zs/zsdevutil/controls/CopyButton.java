package com.zs.zsdevutil.controls;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class CopyButton extends Button {
    public CopyButton() {
        super();
        // Set the ID
        setId("copyButton");

        // Set the icon
        FontIcon icon = new FontIcon(FontAwesomeSolid.COPY);
        setGraphic(icon);

        // Set the tooltip
        setTooltip(new Tooltip("Copy"));

        // Optional: Set a default style
        getStyleClass().add("copy-button");
    }
}
