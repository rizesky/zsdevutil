package com.zs.zsdevutil.controls;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.fxmisc.richtext.GenericStyledArea;

import java.util.function.IntFunction;

public class TransparentBgLineNoFactory implements IntFunction<Node> {

    public static IntFunction<Node> get(GenericStyledArea<?, ?, ?> area) {
        return new TransparentBgLineNoFactory(area);
    }

    private final GenericStyledArea<?, ?, ?> area;

    private TransparentBgLineNoFactory(GenericStyledArea<?, ?, ?> area) {
        this.area = area;
    }

    @Override
    public Node apply(int idx) {
        Label lineNo = new Label(String.format("%d", idx + 1));
        lineNo.getStyleClass().add("custom-line-number");

        HBox wrapper = new HBox(lineNo);
        wrapper.getStyleClass().add("custom-line-number-wrapper");

        return wrapper;
    }
}