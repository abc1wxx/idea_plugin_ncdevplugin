package com.air.nc5dev.ui;

import cn.hutool.core.io.IoUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * ����� console ����
 *
 * @Author ���� Email: 209308343@qq.com
 * @Description
 * @Date 2020/11/6 18:22
 **/
public class PluginConsoleWindow implements ToolWindowFactory {
    //�����
    private JPanel mainPanel;
    ///����һ�� �й����������
    private JBScrollPane scrollPane;
    private JEditorPane jEditorPane;
    //�Ҽ��˵�
    private JPopupMenu popup;
    public static PluginConsoleWindow me;

    public PluginConsoleWindow() {
        me = this;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); //�õ���Ļ�ĳߴ�
        mainPanel = new JPanel();
        //���������Ĵ�С
        /*mainPanel.setPreferredSize(new Dimension((int) screenSize.getWidth() - 50
                , (int) screenSize.getHeight() / 3 * 2));*/

        //�����Ҽ��˵�
        popup = new JPopupMenu();
        popup.add(createPopupItem("����", "search", e -> {
            try {
                jEditorPane.setText(IoUtil.read(new FileInputStream("C:\\Users\\Administrator\\" +
                        ".IntelliJIdea2019.3\\system\\log\\threadDumps-freeze-20200829-185232-IU-193.6015.39" +
                        "-ComponentContainer.startComponents-16sec\\threadDump-20200829-185237.txt"), "utf-8"));
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        }));
        popup.addSeparator();
        popup.add(createPopupItem("���", "clear", e -> {
            jEditorPane.setText("");
        }));
        popup.addSeparator();
        popup.add(createPopupItem("����", "headLine", e -> {
            jEditorPane.setCaretPosition(0);
            jEditorPane.moveCaretPosition(0);
        }));
        popup.add(createPopupItem("β��", "tailLine", e -> {
            jEditorPane.setCaretPosition(jEditorPane.getText().length());
            jEditorPane.moveCaretPosition(jEditorPane.getText().length());
        }));

        //����������־�ı���
        jEditorPane = new JEditorPane();
        jEditorPane.setEditable(true);
        scrollPane = new JBScrollPane(jEditorPane);
        //���ù��������λ��
        /*scrollPane.setPreferredSize(new Dimension((int) screenSize.getWidth() - 50
                , (int) screenSize.getHeight() / 3 * 2 - 50));*/
        //��������������� �ɼ�
        scrollPane.setVisible(true);
        //���ù������Ĺ����ٶ�
        scrollPane.getVerticalScrollBar().setUnitIncrement(15);
        //�����˸����
        scrollPane.getVerticalScrollBar().setDoubleBuffered(true);

        jEditorPane.add(popup);
        jEditorPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getModifiers() == InputEvent.BUTTON1_MASK) {
                    //������
                    popup.setVisible(false);
                }
                if (e.getModifiers() == InputEvent.BUTTON2_MASK) {
                    //���ֵ��
                }
                if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
                    //�Ҽ�
                    popup.show(mainPanel, e.getX(), e.getY());
                }
            }
        });
        mainPanel.add(scrollPane);
    }

    /**
     * newһ���Ҽ��˵���
     *
     * @param name
     * @param action
     * @param actionListener
     * @return
     */
    private JMenuItem createPopupItem(String name, String action, ActionListener actionListener) {
        JMenuItem item = new JMenuItem(name);
        item.setActionCommand(action);
        item.addActionListener(actionListener);

        return item;
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(mainPanel, "", false);
        toolWindow.getContentManager().addContent(content);
    }

    //���ع��������
    public static int getColumnAtCaret(JTextComponent component)
    {
        int caretPosition = component.getCaretPosition();
        Element root = component.getDocument().getDefaultRootElement();
        int line = root.getElementIndex( caretPosition );
        int lineStart = root.getElement( line ).getStartOffset();

        return caretPosition - lineStart + 1;
    }

    //��ȡָ���еĵ�һ���ַ�λ��
    public static int getLineStart(JTextComponent component,int line)
    {
        int lineNumber = line - 1;
        Element root = component.getDocument().getDefaultRootElement();
        int lineStart = root.getElement( lineNumber ).getStartOffset();
        return lineStart;
    }

    //����ѡ�е��ַ���
    public static int getSelectedNumber(JTextComponent component)
    {
        if( component.getSelectedText() == null )
            return 0;
        else
            return component.getSelectedText().length();
    }

    //���ع��������
    public static int getLineAtCaret(JTextComponent component)
    {
        int caretPosition = component.getCaretPosition();
        Element root = component.getDocument().getDefaultRootElement();

        return root.getElementIndex( caretPosition ) + 1;
    }

    //�����ı�����
    public static int getLines(JTextComponent component)
    {
        Element root = component.getDocument().getDefaultRootElement();
        return root.getElementCount();
    }

    //�����ı�����ַ�����
    public static int getCharNumber(JTextComponent component)
    {
        Document doc = component.getDocument();
        return doc.getLength();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JBScrollPane getScrollPane() {
        return scrollPane;
    }

    public JEditorPane getjEditorPane() {
        return jEditorPane;
    }

    public JPopupMenu getPopup() {
        return popup;
    }

    public static PluginConsoleWindow getMe() {
        return me;
    }

}