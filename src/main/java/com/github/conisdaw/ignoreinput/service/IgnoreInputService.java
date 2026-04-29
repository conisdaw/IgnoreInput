package com.github.conisdaw.ignoreinput.service;

import com.github.conisdaw.ignoreinput.model.IgnoreInput;
import com.github.conisdaw.ignoreinput.typing.IgnoreInputTypedHandler;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.editor.actionSystem.TypedAction;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Service(Service.Level.APP)
@State(
    name = "IgnoreInput",
    storages = {@Storage("ignore-input.xml")}
)
public final class IgnoreInputService implements PersistentStateComponent<IgnoreInputService.State> {

    private State state = new State();
    private IgnoreInput activeInput;
    private int cursor;
    private boolean busy;
    private String activeProjectName;
    private TypedActionHandler originalHandler;

    public static IgnoreInputService getInstance() {
        return ApplicationManager.getApplication().getService(IgnoreInputService.class);
    }

    @Override
    public @Nullable State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }

    @NotNull
    public List<IgnoreInput> getInputs() {
        return state.inputs;
    }

    public void addInput(@NotNull IgnoreInput input) {
        state.inputs.add(input);
    }

    public void removeInput(@NotNull String id) {
        state.inputs.removeIf(s -> s.id.equals(id));
    }

    @Nullable
    public IgnoreInput getInput(@NotNull String id) {
        return state.inputs.stream().filter(s -> s.id.equals(id)).findFirst().orElse(null);
    }

    @Nullable
    public IgnoreInput getInputBySlot(int slot) {
        return state.inputs.stream().filter(s -> s.slot == slot).findFirst().orElse(null);
    }

    public void setInputSlot(@NotNull String id, int slot) {
        if (slot > 0) {
            state.inputs.stream()
                .filter(s -> s.slot == slot && !s.id.equals(id))
                .findFirst()
                .ifPresent(s -> s.slot = 0);
        }
        IgnoreInput target = getInput(id);
        if (target != null) {
            target.slot = slot;
        }
    }

    public boolean isActive() {
        return activeInput != null;
    }

    @Nullable
    public IgnoreInput getActiveInput() {
        return activeInput;
    }

    public int getCursor() {
        return cursor;
    }

    public int getTotalLength() {
        return activeInput != null ? activeInput.code.length() : 0;
    }

    @Nullable
    public String getActiveProjectName() {
        return activeProjectName;
    }

    public void engage(@NotNull IgnoreInput input, @NotNull Project project) {
        disengage();
        activeInput = input;
        cursor = 0;
        busy = false;
        activeProjectName = project.getName();

        TypedAction typedAction = TypedAction.getInstance();
        originalHandler = typedAction.getHandler();
        typedAction.setupHandler(new IgnoreInputTypedHandler(this, originalHandler));
    }

    public void typeNext(@NotNull Editor editor) {
        if (activeInput == null || busy) {
            return;
        }
        if (editor.isDisposed() || !editor.getDocument().isWritable()) {
            disengage();
            return;
        }

        String code = activeInput.code;
        if (cursor >= code.length()) {
            return;
        }

        busy = true;

        try {
            char ch = code.charAt(cursor);

            char closing = getClosingBracket(ch);
            if (closing != 0) {
                cursor++;
                ApplicationManager.getApplication().runWriteAction(() -> {
                    EditorModificationUtil.insertStringAtCaret(editor, String.valueOf(new char[]{ch, closing}), false, true);
                    editor.getCaretModel().moveToOffset(editor.getCaretModel().getOffset() - 1);
                });
            } else if (isClosingBracket(ch)) {
                cursor++;
                ApplicationManager.getApplication().runWriteAction(() -> {
                    int offset = editor.getCaretModel().getOffset();
                    if (offset < editor.getDocument().getTextLength()
                        && editor.getDocument().getCharsSequence().charAt(offset) == ch) {
                        editor.getCaretModel().moveToOffset(offset + 1);
                    } else {
                        EditorModificationUtil.insertStringAtCaret(editor, String.valueOf(ch), false, true);
                    }
                });
            } else {
                int n = 1;

                if (ch != '\n' && ch != '\r') {
                    double r = Math.random();
                    if (r > 0.7) n = 2;
                    if (r > 0.9) n = 3;
                }

                n = Math.min(n, code.length() - cursor);

                for (int i = 1; i < n; i++) {
                    char nc = code.charAt(cursor + i);
                    if (nc == '\n' || nc == '\r' || nc == '(' || nc == ')' || nc == '{' || nc == '}' || nc == '[' || nc == ']') {
                        n = i;
                        break;
                    }
                }

                String text = code.substring(cursor, cursor + n);
                cursor += n;

                ApplicationManager.getApplication().runWriteAction(() -> {
                    EditorModificationUtil.insertStringAtCaret(editor, text, false, true);
                });
            }
        } finally {
            busy = false;
        }
    }

    private static char getClosingBracket(char opening) {
        switch (opening) {
            case '(': return ')';
            case '[': return ']';
            default: return 0;
        }
    }

    private static boolean isClosingBracket(char ch) {
        return ch == ')' || ch == '}' || ch == ']';
    }

    public void disengage() {
        if (originalHandler != null) {
            TypedAction.getInstance().setupHandler(originalHandler);
        }
        activeInput = null;
        cursor = 0;
        busy = false;
        activeProjectName = null;
    }

    public static class State {
        public List<IgnoreInput> inputs = new ArrayList<>();
    }
}
