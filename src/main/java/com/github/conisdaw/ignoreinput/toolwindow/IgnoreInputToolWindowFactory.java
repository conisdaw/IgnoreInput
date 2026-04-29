package com.github.conisdaw.ignoreinput.toolwindow;

import com.github.conisdaw.ignoreinput.model.IgnoreInput;
import com.github.conisdaw.ignoreinput.service.IgnoreInputService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.*;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class IgnoreInputToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        IgnoreInputPanel panel = new IgnoreInputPanel(project);
        Content content = ContentFactory.getInstance().createContent(panel, "", false);
        toolWindow.getContentManager().addContent(content);
    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return true;
    }

    private static class IgnoreInputPanel extends JBPanel<IgnoreInputPanel> {
        private final Project project;
        private final IgnoreInputService service;

        private final JBTextField nameField;
        private final JComboBox<String> slotCombo;
        private final JBTextArea codeArea;
        private final JBPanel<?> listPanel;
        private final JScrollPane scrollPane;

        private static final String[] SLOT_OPTIONS = {"无快捷键", "Ctrl+1", "Ctrl+2", "Ctrl+3", "Ctrl+4", "Ctrl+5"};

        IgnoreInputPanel(Project project) {
            this.project = project;
            this.service = IgnoreInputService.getInstance();

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

            JLabel newLabel = new JLabel("新建代码片段");
            newLabel.setFont(newLabel.getFont().deriveFont(Font.BOLD, 11f));
            newLabel.setAlignmentX(LEFT_ALIGNMENT);
            newLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
            add(newLabel);

            JPanel nameRow = new JPanel(new BorderLayout(4, 0));
            nameRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
            nameRow.setAlignmentX(LEFT_ALIGNMENT);

            nameField = new JBTextField();
            nameField.getEmptyText().setText("名称");
            nameRow.add(nameField, BorderLayout.CENTER);

            slotCombo = new JComboBox<>(SLOT_OPTIONS);
            slotCombo.setPreferredSize(new Dimension(80, 26));
            nameRow.add(slotCombo, BorderLayout.EAST);

            add(nameRow);
            add(Box.createVerticalStrut(4));

            codeArea = new JBTextArea();
            codeArea.setRows(6);
            codeArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            codeArea.getEmptyText().setText("在此粘贴代码...");
            codeArea.setLineWrap(false);

            JScrollPane codeScroll = new JBScrollPane(codeArea);
            codeScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
            codeScroll.setAlignmentX(LEFT_ALIGNMENT);
            add(codeScroll);
            add(Box.createVerticalStrut(4));

            JButton saveBtn = new JButton("保存");
            saveBtn.setAlignmentX(LEFT_ALIGNMENT);
            saveBtn.putClientProperty("JButton.buttonType", "default");
            saveBtn.addActionListener(e -> saveInput());
            add(saveBtn);

            add(Box.createVerticalStrut(12));

            JLabel savedLabel = new JLabel("已保存的代码片段");
            savedLabel.setFont(savedLabel.getFont().deriveFont(Font.BOLD, 11f));
            savedLabel.setAlignmentX(LEFT_ALIGNMENT);
            savedLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
            add(savedLabel);

            listPanel = new JBPanel<>();
            listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

            scrollPane = new JBScrollPane(listPanel);
            scrollPane.setAlignmentX(LEFT_ALIGNMENT);
            scrollPane.setPreferredSize(new Dimension(Integer.MAX_VALUE, 300));
            scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
            add(scrollPane);

            refreshList();

            add(Box.createVerticalStrut(8));
            JLabel hint = new JLabel("<html><small style='color:#999'>" +
                "Ctrl+1~5&nbsp;&nbsp;使用快捷键<br>" +
                "Ctrl+0&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;停止演示<br>" +
                "Ctrl+H&nbsp;&nbsp;&nbsp;&nbsp;切换面板</small></html>");
            hint.setAlignmentX(LEFT_ALIGNMENT);
            add(hint);
        }

        private void saveInput() {
            String name = nameField.getText().trim();
            String code = codeArea.getText();
            int slot = slotCombo.getSelectedIndex();

            if (name.isEmpty() || code.isEmpty()) return;

            IgnoreInput input = new IgnoreInput(
                String.valueOf(System.currentTimeMillis()),
                name,
                code,
                slot,
                System.currentTimeMillis()
            );
            service.addInput(input);

            nameField.setText("");
            codeArea.setText("");
            slotCombo.setSelectedIndex(0);
            refreshList();
        }

        private void refreshList() {
            listPanel.removeAll();
            List<IgnoreInput> inputs = service.getInputs();

            if (inputs.isEmpty()) {
                JLabel empty = new JLabel("暂无代码片段");
                empty.setForeground(JBColor.GRAY);
                empty.setBorder(BorderFactory.createEmptyBorder(12, 4, 12, 4));
                listPanel.add(empty);
            } else {
                for (IgnoreInput s : inputs) {
                    listPanel.add(createInputCard(s));
                    listPanel.add(Box.createVerticalStrut(6));
                }
            }

            listPanel.revalidate();
            listPanel.repaint();
        }

        private void showPreviewDialog(IgnoreInput s) {
            JBTextArea previewArea = new JBTextArea(s.code);
            previewArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            previewArea.setEditable(false);
            previewArea.setLineWrap(false);

            JBScrollPane previewScroll = new JBScrollPane(previewArea);
            previewScroll.setPreferredSize(new Dimension(560, 360));

            JButton closeBtn = new JButton("关闭");
            closeBtn.putClientProperty("JButton.buttonType", "default");
            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bottomPanel.add(closeBtn);

            JPanel container = new JBPanel<>(new BorderLayout());
            container.add(previewScroll, BorderLayout.CENTER);
            container.add(bottomPanel, BorderLayout.SOUTH);

            JBPopup popup = JBPopupFactory.getInstance()
                .createComponentPopupBuilder(container, previewArea)
                .setTitle("预览: " + s.name)
                .setMovable(true)
                .setResizable(true)
                .setRequestFocus(true)
                .setCancelOnClickOutside(true)
                .createPopup();
            closeBtn.addActionListener(e -> popup.closeOk(null));
            popup.showCenteredInCurrentWindow(project);
        }

        private JPanel createInputCard(IgnoreInput s) {
            JPanel card = new JBPanel<>();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(JBColor.border(), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
            ));

            JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            titleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

            if (s.slot > 0) {
                JLabel slotTag = new JLabel("Ctrl+" + s.slot);
                slotTag.setFont(slotTag.getFont().deriveFont(9f));
                slotTag.setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 5));
                slotTag.setOpaque(true);
                slotTag.setBackground(JBColor.border());
                titleRow.add(slotTag);
            }

            JLabel nameLabel = new JLabel(s.name);
            nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 12f));
            titleRow.add(nameLabel);

            card.add(titleRow);

            JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            actionRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

            JButton useBtn = new JButton("使用");
            useBtn.setFont(useBtn.getFont().deriveFont(11f));
            useBtn.putClientProperty("JButton.buttonType", "default");
            useBtn.addActionListener(e -> service.engage(s, project));
            actionRow.add(useBtn);

            JButton previewBtn = new JButton("预览");
            previewBtn.setFont(previewBtn.getFont().deriveFont(11f));
            previewBtn.putClientProperty("JButton.buttonType", "default");
            previewBtn.addActionListener(e -> showPreviewDialog(s));
            actionRow.add(previewBtn);

            JButton delBtn = new JButton("删除");
            delBtn.setFont(delBtn.getFont().deriveFont(11f));
            delBtn.putClientProperty("JButton.buttonType", "default");
            delBtn.addActionListener(e -> {
                int result = Messages.showYesNoDialog(
                    project,
                    "确定要删除代码片段 \"" + s.name + "\" 吗？",
                    "删除确认",
                    Messages.getQuestionIcon()
                );
                if (result == Messages.YES) {
                    service.removeInput(s.id);
                    refreshList();
                }
            });
            actionRow.add(delBtn);

            JComboBox<String> slotSelector = new JComboBox<>(SLOT_OPTIONS);
            slotSelector.setSelectedIndex(s.slot);
            slotSelector.setFont(slotSelector.getFont().deriveFont(10f));
            slotSelector.setPreferredSize(new Dimension(70, 22));
            slotSelector.addActionListener(e -> {
                service.setInputSlot(s.id, slotSelector.getSelectedIndex());
                refreshList();
            });
            actionRow.add(slotSelector);

            card.add(actionRow);

            return card;
        }
    }
}
