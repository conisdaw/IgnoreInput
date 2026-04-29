package com.github.conisdaw.ignoreinput.actions;

import com.github.conisdaw.ignoreinput.service.IgnoreInputService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class StopIgnoreInputAction extends AnAction {

    public StopIgnoreInputAction() {
        super("IgnoreInput: 取消演示", "停止当前代码片段演示", null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        IgnoreInputService.getInstance().disengage();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(IgnoreInputService.getInstance().isActive());
    }
}
