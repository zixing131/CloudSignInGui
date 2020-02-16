/*
 * Created by JFormDesigner on Sun Feb 16 15:07:54 CST 2020
 */

package com.rui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 * @author 8
 */
public class ok extends JFrame {
    public ok() {
        Font f = new Font("宋体",Font.PLAIN,12);
        String[] names ={ "Label", "CheckBox", "PopupMenu","MenuItem", "CheckBoxMenuItem",
                "JRadioButtonMenuItem","ComboBox", "Button", "Tree", "ScrollPane",
                "TabbedPane", "EditorPane", "TitledBorder", "Menu", "TextArea",
                "OptionPane", "MenuBar", "ToolBar", "ToggleButton", "ToolTip",
                "ProgressBar", "TableHeader", "Panel", "List", "ColorChooser",
                "PasswordField","TextField", "Table", "Label", "Viewport",
                "RadioButtonMenuItem","RadioButton", "DesktopPane", "InternalFrame"
        };
        for (String item : names) {
            UIManager.put(item+ ".font",f);
        }
        initComponents();
//        设置可见
        setVisible(true);
        logTextArea.append("欢迎使用贴吧云签到GUI本地版.\n如果你需要源码或者云签到版本请前往\nhttps://github.com/Rui2450\n如果你需要使用帮助请前往");
        //设置关闭方式 如果不设置的话 关闭窗口之后不会退出程序
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void startActionPerformed(ActionEvent e) {
        start.setText("正在签到,请稍等");
        start.setEnabled(false);
        SwingWorker<String, Object> task = new SwingWorker<String, Object>() {
            /**
             *计算结果，如果无法计算则抛出异常。
             *
             * <p>
             *请注意，此方法仅执行一次。
             *
             * <p>
             *注意：此方法在后台线程中执行。
             *
             * @返回计算结果
             * @throws异常，如果无法计算结果
             */
            @Override
            protected String doInBackground() {
                Signin signin = new Signin(cookiesText.getText());
                siginEnum allBa = signin.getAllBa(null);
                logTextArea.append(allBa.getMessage()+"\n");
                if (allBa.isStats()){
                    ArrayList<String> backToList = signin.backToList;
                    backToList.forEach(a->{
                        logTextArea.append(a+"\n");
                    });
                    start.setText("签到完成!");
                }else {
                    logTextArea.append(allBa.getMessage()+"\n");
                    start.setText("签到失败!");
                }
                return null;
            }
        };
        task.execute();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        start = new JButton();
        scrollPane1 = new JScrollPane();
        cookiesText = new JTextArea();
        label1 = new JLabel();
        scrollPane2 = new JScrollPane();
        logTextArea = new JTextArea();

        //======== this ========
        setTitle("\u8d34\u5427\u5168\u7b7e\u5230-52pojie");
        Container contentPane = getContentPane();
        contentPane.setLayout(null);

        //======== panel1 ========
        {
            panel1.setLayout(null);

            //---- start ----
            start.setText("\u5f00\u59cb\u7b7e\u5230");
            start.addActionListener(e -> startActionPerformed(e));
            panel1.add(start);
            start.setBounds(195, 130, 140, 40);

            //======== scrollPane1 ========
            {

                //---- cookiesText ----
                cookiesText.setLineWrap(true);
                cookiesText.setWrapStyleWord(true);
                scrollPane1.setViewportView(cookiesText);
            }
            panel1.add(scrollPane1);
            scrollPane1.setBounds(120, 20, 310, 95);

            //---- label1 ----
            label1.setText("Cookies:");
            panel1.add(label1);
            label1.setBounds(25, 20, 65, 95);

            //======== scrollPane2 ========
            {

                //---- logTextArea ----
                logTextArea.setLineWrap(true);
                logTextArea.setWrapStyleWord(true);
                logTextArea.setEditable(false);
                scrollPane2.setViewportView(logTextArea);
            }
            panel1.add(scrollPane2);
            scrollPane2.setBounds(0, 190, 535, 170);

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < panel1.getComponentCount(); i++) {
                    Rectangle bounds = panel1.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = panel1.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                panel1.setMinimumSize(preferredSize);
                panel1.setPreferredSize(preferredSize);
            }
        }
        contentPane.add(panel1);
        panel1.setBounds(0, -5, 535, 360);

        {
            // compute preferred size
            Dimension preferredSize = new Dimension();
            for(int i = 0; i < contentPane.getComponentCount(); i++) {
                Rectangle bounds = contentPane.getComponent(i).getBounds();
                preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
            }
            Insets insets = contentPane.getInsets();
            preferredSize.width += insets.right;
            preferredSize.height += insets.bottom;
            contentPane.setMinimumSize(preferredSize);
            contentPane.setPreferredSize(preferredSize);
        }
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JButton start;
    private JScrollPane scrollPane1;
    private JTextArea cookiesText;
    private JLabel label1;
    private JScrollPane scrollPane2;
    private JTextArea logTextArea;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
