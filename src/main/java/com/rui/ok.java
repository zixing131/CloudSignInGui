/*
 * Created by JFormDesigner on Sun Feb 16 15:07:54 CST 2020
 */

package com.rui;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 8
 */
public class ok extends JFrame {
    public static final Pattern TBS = Pattern.compile(".tbs.:\\s*\"\\S*");
    public static final String TIEBA_SEED_URL = "http://tieba.baidu.com/f/like/mylike?pn=1";
    public static final String AUTO_SAVE_TXT = "autoSave.txt";
    LinkedHashMap<String, String> headers = null;
    private String cookie;
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
        if (new File(AUTO_SAVE_TXT).exists()){
            //默认UTF-8编码，可以在构造中传入第二个参数做为编码
            FileReader fileReader = new FileReader(AUTO_SAVE_TXT);
            cookie= fileReader.readString();
            cookiesText.setText(cookie);
        }
//        设置可见
        setVisible(true);
        logTextArea.append("欢迎使用贴吧云签到GUI本地版.\n如果你需要源码,或者云签到版本,或者有什么建议,再或者单纯的想夸夸我,请前往\n  https://www.52pojie.cn/thread-1107576-1-1.html  \n如果你需要使用教程请前往\n   https://www.52pojie.cn/thread-1107576-1-1.html  \n");
        //设置关闭方式 如果不设置的话 关闭窗口之后不会退出程序
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
                cookie=cookiesText.getText().replace("Cookie: ", "").trim();
                siginEnum allBa = getAllBa(null);
                logTextArea.append(allBa.getMessage()+"\n");
                if (allBa.isStats()){
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
    public siginEnum getAllBa(String url) {
        if (Objects.isNull(cookie)) {
            logTextArea.append("用户的Cookie为空，无法继续执行\n");
            return siginEnum.SIGNIN_ERROR_COOKIE_IS_NULL;
        }
        if (save.isSelected()){
            File file = new File(AUTO_SAVE_TXT);
            if (!file.exists()){
                FileUtil.touch(file);
            }
            FileWriter writer = new FileWriter(file);
            writer.write(cookie);
        }
        if (Objects.isNull(url)) {
            url = TIEBA_SEED_URL;
        }
        headers = Signin.resolveHeadsToMap(
                "Host: tieba.baidu.com\n" +
                        "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:72.0) Gecko/20100101 Firefox/72.0\n" +
                        "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\n" +
                        "Accept-Language: zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2\n" +
                        "Accept-Encoding: gzip, deflate, br\n" +
                        "Connection: keep-alive\n" +
                        "Cookie: " + cookie + "\n" +
                        "Upgrade-Insecure-Requests: 1\n" +
                        "Cache-Control: max-age=0"
        );
        String s = Signin.get(url, headers);
        Document doc = null;
        if (s != null) {
            doc = Jsoup.parse(s);
        }else {
            logTextArea.append("错误！并未获取到任何贴吧\n");
        }
        Elements links = doc.select("a:not(.like_badge)[title]");
        if (links == null || links.isEmpty()) {
            logTextArea.append("错误！并未获取到任何贴吧\n");
            return siginEnum.SIGNIN_ERROR_TEIBA_IS_NULL;
        }
        for (Element link : links) {
            ThreadUtil.execute(() -> {
                baidutieba(link.text());
            });
        }
        Elements select = doc.select(".pagination>a");
        if (!Objects.equals(select.size(), 0) && Objects.equals(select.last().text(), "尾页")) {
            Set<String> set = new HashSet<>();
            select.forEach(e -> {
                String href = e.attr("href");
                set.add(href);
            });
            ThreadUtil.execute(() -> {
                set.forEach(e -> getAllBa("http://tieba.baidu.com/" + e));
            });
        }
        return siginEnum.SIGNIN_SUUCCESS;
    }

    public void baidutieba(String tablename) {
        Document parse = Jsoup.parse(Signin.get("https://tieba.baidu.com/f?kw=" + tablename, headers));
        Elements script = parse.getElementsByTag("script").eq(1);
        String text = script.toString();
        Matcher matcher = TBS.matcher(text);
        String TBid = null;
        if (matcher.find()) {
            String group = matcher.group();
            TBid = group.substring(group.indexOf("\"") + 1, group.lastIndexOf("\""));
        } else {
            logTextArea.append("发生错误");
        }
        LinkedHashMap<String, String> args = Signin.resolveArgsToMap(
                "ie=utf-8&kw=" +
                        tablename +
                        "&tbs=" +
                        TBid
        );
        String post = Signin.post("https://tieba.baidu.com/sign/add", headers, args);
        JSONObject jsonObject = JSONUtil.parseObj(post);
        Object no = jsonObject.get("no");
        if (Objects.equals(no, 1101)) {
            logTextArea.append(tablename + ",您今日已经签到\n");
        } else if (Objects.equals(no, 0)) {
            logTextArea.append(tablename + ",签到成功！\n");
        }
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
        save = new JCheckBox();

        //======== this ========
        setTitle("\u8d34\u5427\u5168\u7b7e\u5230-52pojie");
        setIconImage(new ImageIcon(getClass().getResource("/image/\u7b7e\u5230.png")).getImage());
        Container contentPane = getContentPane();
        contentPane.setLayout(null);

        //======== panel1 ========
        {
            panel1.setLayout(null);

            //---- start ----
            start.setText("\u5f00\u59cb\u7b7e\u5230");
            start.addActionListener(e -> startActionPerformed(e));
            panel1.add(start);
            start.setBounds(185, 130, 140, 40);

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

            //---- save ----
            save.setText("\u4fdd\u5b58Cookie");
            save.setSelected(true);
            panel1.add(save);
            save.setBounds(new Rectangle(new Point(340, 140), save.getPreferredSize()));

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
    private JCheckBox save;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
