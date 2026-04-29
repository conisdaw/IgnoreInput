package com.github.conisdaw.ignoreinput.statusbar;

import com.github.conisdaw.ignoreinput.model.IgnoreInput;
import com.github.conisdaw.ignoreinput.service.IgnoreInputService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.CustomStatusBarWidget;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class IgnoreInputStatusBarWidget implements CustomStatusBarWidget {

    private final Project project;
    private JLabel label;

    public IgnoreInputStatusBarWidget(@NotNull Project project) {
        this.project = project;
    }

    @NotNull
    @Override
    public String ID() {
        return "IgnoreInputStatusBar";
    }

    @Override
    public JComponent getComponent() {
        if (label == null) {
            label = new JLabel();
            label.setToolTipText("点击取消代码片段演示");
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    IgnoreInputService.getInstance().disengage();
                }
            });
        }
        return label;
    }

    @Override
    public void install(@NotNull StatusBar statusBar) {
        update();
    }

    @Override
    public void dispose() {
    }

    public void update() {
        if (label == null) return;
        IgnoreInputService service = IgnoreInputService.getInstance();
        if (!service.isActive()) {
            label.setText("");
            label.setVisible(false);
            return;
        }

        IgnoreInput active = service.getActiveInput();
        if (active == null) {
            label.setText("");
            label.setVisible(false);
            return;
        }

        int totalLines = active.code.split("\n", -1).length;
        int cursor = service.getCursor();
        String textBeforeCursor = active.code.substring(0, Math.min(cursor, active.code.length()));
        int currentLine = textBeforeCursor.split("\n", -1).length;
        label.setText(" Ln " + currentLine + "/" + totalLines + " ");
        label.setVisible(true);
    }
}
