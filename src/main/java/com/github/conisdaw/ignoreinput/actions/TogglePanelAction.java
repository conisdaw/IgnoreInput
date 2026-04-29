package com.github.conisdaw.ignoreinput.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;

public class TogglePanelAction extends AnAction {

    public TogglePanelAction() {
        super("IgnoreInput: 切换面板", "显示或隐藏代码片段面板", null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        ToolWindowManager manager = ToolWindowManager.getInstance(project);
        ToolWindow tw = manager.getToolWindow("IgnoreInput");
        if (tw != null) {
            if (tw.isVisible()) {
                tw.hide(null);
            } else {
                tw.show(() -> {});
            }
        }
    }
}
