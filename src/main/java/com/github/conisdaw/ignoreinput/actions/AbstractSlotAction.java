package com.github.conisdaw.ignoreinput.actions;

import com.github.conisdaw.ignoreinput.model.IgnoreInput;
import com.github.conisdaw.ignoreinput.service.IgnoreInputService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractSlotAction extends AnAction {

    protected final int slot;

    protected AbstractSlotAction(String text, int slot) {
        super(text, "启动快捷键 " + slot + " 对应的代码片段", null);
        this.slot = slot;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        IgnoreInputService service = IgnoreInputService.getInstance();
        IgnoreInput input = service.getInputBySlot(slot);
        if (input != null) {
            service.engage(input, project);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(IgnoreInputService.getInstance().getInputBySlot(slot) != null);
    }

    public static class Slot1 extends AbstractSlotAction {
        public Slot1() { super("IgnoreInput: 快捷键 1", 1); }
    }

    public static class Slot2 extends AbstractSlotAction {
        public Slot2() { super("IgnoreInput: 快捷键 2", 2); }
    }

    public static class Slot3 extends AbstractSlotAction {
        public Slot3() { super("IgnoreInput: 快捷键 3", 3); }
    }

    public static class Slot4 extends AbstractSlotAction {
        public Slot4() { super("IgnoreInput: 快捷键 4", 4); }
    }

    public static class Slot5 extends AbstractSlotAction {
        public Slot5() { super("IgnoreInput: 快捷键 5", 5); }
    }
}
