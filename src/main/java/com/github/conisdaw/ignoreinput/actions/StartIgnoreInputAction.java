package com.github.conisdaw.ignoreinput.actions;

import com.github.conisdaw.ignoreinput.model.IgnoreInput;
import com.github.conisdaw.ignoreinput.service.IgnoreInputService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StartIgnoreInputAction extends AnAction {

    public StartIgnoreInputAction() {
        super("IgnoreInput: 应用代码片段", "开始输入已保存的代码片段", null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        IgnoreInputService service = IgnoreInputService.getInstance();
        List<IgnoreInput> inputs = service.getInputs();

        if (inputs.isEmpty()) {
            Messages.showInfoMessage(project, "暂无已保存的代码片段。", "IgnoreInput");
            return;
        }

        BaseListPopupStep<IgnoreInput> step = new BaseListPopupStep<>("选择代码片段", inputs) {
            @Override
            public PopupStep<?> onChosen(IgnoreInput selected, boolean finalChoice) {
                service.engage(selected, project);
                return PopupStep.FINAL_CHOICE;
            }

            @Override
            public String getTextFor(IgnoreInput s) {
                return s.slot > 0 ? "[" + s.slot + "] " + s.name : s.name;
            }
        };

        JBPopupFactory.getInstance()
            .createListPopup(step)
            .showInBestPositionFor(e.getDataContext());
    }
}
