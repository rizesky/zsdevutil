package com.zs.zsdevutil.etc;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonHighlighter {

    //    The value of each color number is on code-area.css
    private static final String[] COLORS = {
            "1", "2", "3", "4", "5", "6"
    };

    private static final Pattern JSON_PATTERN = Pattern.compile(
            "(?<BRACE>[{}\\[\\]])" +
                    "|(?<COLON>:)\\s*(?<VALUE>" +
                    "(?<STRING>\"(\\\\.|[^\\\\\"])*\")" +
                    "|(?<NUMBER>-?\\d+(\\.\\d+)?([eE][+-]?\\d+)?)" +
                    "|(?<BOOLEAN>true|false)" +
                    "|(?<NULL>null)" +
                    ")"
    );

    public JsonHighlighter() {
    }

    public void setupHighlighting(CodeArea codeArea) {
        codeArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
                .subscribe(change -> codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText())));
    }

    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = JSON_PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        Stack<Integer> bracketStack = new Stack<>();
        int colorIndex = 0;

        while (matcher.find()) {
            String styleClass = null;
            if (matcher.group("BRACE") != null) {
                String brace = matcher.group("BRACE");
                if ("{[".contains(brace)) {
                    bracketStack.push(colorIndex);
                    styleClass = "bracket-" + COLORS[colorIndex];
                    colorIndex = (colorIndex + 1) % COLORS.length;
                } else {
                    if (!bracketStack.isEmpty()) {
                        int openingColorIndex = bracketStack.pop();
                        styleClass = "bracket-" + COLORS[openingColorIndex];
                    } else {
                        styleClass = "bracket-default";
                    }
                }
            } else if (matcher.group("VALUE") != null) {
                if (matcher.group("STRING") != null) {
                    styleClass = "string";
                } else if (matcher.group("NUMBER") != null) {
                    styleClass = "number";
                } else if (matcher.group("BOOLEAN") != null) {
                    styleClass = "boolean";
                } else if (matcher.group("NULL") != null) {
                    styleClass = "null";
                }
            }

            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            if (styleClass != null) {
                spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            } else {
                spansBuilder.add(Collections.emptyList(), matcher.end() - matcher.start());
            }
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);

        return spansBuilder.create();
    }

}