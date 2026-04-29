package com.github.conisdaw.ignoreinput.typing;

import com.github.conisdaw.ignoreinput.service.IgnoreInputService;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import org.jetbrains.annotations.NotNull;

public class IgnoreInputTypedHandler implements TypedActionHandler {

    private final IgnoreInputService service;
    private final TypedActionHandler original;

    public IgnoreInputTypedHandler(@NotNull IgnoreInputService service, TypedActionHandler original) {
        this.service = service;
        this.original = original;
    }

    @Override
    public void execute(@NotNull Editor editor, char charTyped, @NotNull com.intellij.openapi.actionSystem.DataContext dataContext) {
        if (service.isActive()) {
            try {
                service.typeNext(editor);
            } catch (Throwable ex) {
                service.disengage();
            }
        } else if (original != null) {
            original.execute(editor, charTyped, dataContext);
        }
    }
}
