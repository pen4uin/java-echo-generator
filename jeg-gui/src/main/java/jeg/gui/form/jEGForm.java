package jeg.gui.form;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import jeg.core.config.jEGConfig;
import jeg.core.config.jEGConstants;
import jeg.core.jEGenerator;
import jeg.gui.util.ComponentUtil;
import jeg.gui.util.TextPaneUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

public class jEGForm {
    private static JFrame frame;
    private JComboBox serverBox;
    private JComboBox modelBox;
    private JComboBox gadgetBox;
    private JComboBox formatBox;
    private JTextField reqParamText;
    private JTextField reqHeaderNameText;
    private JTextField outputPathText;
    private JTextField classNameText;
    private JButton generateButton;
    private JPanel jEGPanel;
    private JPanel TopPanel;
    private JLabel serverLabel;
    private JLabel formatLabel;
    private JLabel modelLabel;
    private JLabel gadgetLabel;
    private JScrollPane textScrollPane;
    private JPanel MiddlePanel;
    private JLabel reqParamNameLabel;
    private JLabel headerNameLabel;
    private JLabel shellClsNameLabel;
    private JSeparator TopSep;
    private JSeparator MiddleSep;
    private JLabel authorLabel;
    private JLabel noticeLabel;
    private JPanel BottomPanel;
    private JPanel TipPanel;
    private JTextPane textPane;

    private jEGConfig config;


    public static void start() {
        Locale.setDefault(Locale.CHINA);
        frame = new JFrame(jEGConstants.JEG_NAME + " " + jEGConstants.JEG_VERSION);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        jEGForm jmgForm = new jEGForm();
        JPanel contentPanel = jmgForm.jEGPanel;
        contentPanel.setBorder(new EmptyBorder(8, 10, 8, 10));
        frame.setContentPane(contentPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = screenSize.width / 2;
        int centerY = screenSize.height / 2;
        Dimension jfSize = frame.getSize();
        int halfwidth = jfSize.width / 2;
        int halfHeight = jfSize.height / 2;
        frame.setLocation(centerX - halfwidth, centerY - halfHeight);
    }

    private String serverType = jEGConstants.SERVER_TOMCAT;
    private String modelType = jEGConstants.MODEL_CMD;
    private String gadgetType = jEGConstants.GADGET_NONE;
    private String formatType = jEGConstants.FORMAT_BASE64;
    private String outputDir;

    public jEGForm() {
        config = new jEGConfig();

        String[] serverBoxItems = {
                jEGConstants.SERVER_TOMCAT,
                jEGConstants.SERVER_SPRING_MVC,
                jEGConstants.SERVER_RESIN,
                jEGConstants.SERVER_WEBLOGIC,
                jEGConstants.SERVER_WEBSPHERE,
                jEGConstants.SERVER_JETTY,
                jEGConstants.SERVER_UNDERTOW,
                jEGConstants.SERVER_STRUTS2,
                jEGConstants.SERVER_UNKNOWN
        };
        String[] modelBoxItems = new String[]{
                jEGConstants.MODEL_CMD,
                jEGConstants.MODEL_CODE};
        String[] gadgetBoxItems = new String[]{
                jEGConstants.GADGET_NONE,
                jEGConstants.GADGET_JDK_TRANSLET,
                jEGConstants.GADGET_XALAN_TRANSLET};
        String[] formatBoxItems = new String[]{
                jEGConstants.FORMAT_BASE64,
                jEGConstants.FORMAT_BCEL,
                jEGConstants.FORMAT_BIGINTEGER,
                jEGConstants.FORMAT_CLASS,
                jEGConstants.FORMAT_JAR,
                jEGConstants.FORMAT_JS
        };

        serverBox.setModel(new DefaultComboBoxModel(serverBoxItems));
        modelBox.setModel(new DefaultComboBoxModel(modelBoxItems));
        gadgetBox.setModel(new DefaultComboBoxModel(gadgetBoxItems));
        formatBox.setModel(new DefaultComboBoxModel(formatBoxItems));
        serverBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                serverType = (String) serverBox.getSelectedItem();
            }
        });

        modelBox.addActionListener(e -> modelType = (String) modelBox.getSelectedItem());


        gadgetBox.addActionListener(e -> gadgetType = (String) gadgetBox.getSelectedItem());

        formatBox.addActionListener(e -> {
            formatType = (String) formatBox.getSelectedItem();
            assert formatType != null;
            if (formatType.equalsIgnoreCase(jEGConstants.FORMAT_CLASS) || formatType.equalsIgnoreCase(jEGConstants.FORMAT_JAR)) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showSaveDialog(jEGPanel);
                if (result == JFileChooser.APPROVE_OPTION) {
                    String selectedPath = fileChooser.getSelectedFile().getPath();
                    outputDir = selectedPath;
                }
            }
        });

        classNameText.getDocument().putProperty("owner", classNameText);
        classNameText.getDocument().addDocumentListener(ComponentUtil.createDocumentListener(classNameText, config::setClassName));

        reqHeaderNameText.getDocument().putProperty("owner", reqHeaderNameText);
        reqHeaderNameText.getDocument().addDocumentListener(ComponentUtil.createDocumentListener(reqHeaderNameText, config::setReqHeaderName));

        reqParamText.getDocument().putProperty("owner", reqParamText);
        reqParamText.getDocument().addDocumentListener(ComponentUtil.createDocumentListener(reqParamText, config::setReqParamName));


        generateButton.addActionListener(e -> {
            TextPaneUtil.initTextPane(textPane);
            TextPaneUtil.startPrintln(serverType + " " + modelType + " " + gadgetType + " " + formatType + "\n");
            try {
                jEGConfig config = new jEGConfig() {{
                    setServerType(serverType);
                    setModelType(modelType);
                    setGadgetType(gadgetType);
                    setFormatType(formatType);
                    setReqHeaderName(reqHeaderNameText.getText());
                    setReqParamName(reqParamText.getText());
                    setClassName(classNameText.getText());
                    setOutputDir(outputDir);
                    build();
                }};
                // 生成 payload
                jEGenerator generator = new jEGenerator(config);
                String result = generator.getPayload();
                TextPaneUtil.successPrintln("基础信息:");
                TextPaneUtil.rawPrintln("");
                if (config.getModelType().equals(jEGConstants.MODEL_CMD)) {
                    TextPaneUtil.rawPrintln("请求头: " + config.getReqHeaderName());
                } else {
                    TextPaneUtil.rawPrintln("请求参数: " + config.getReqParamName());
                }
                TextPaneUtil.rawPrintln("类名: " + config.getClassName());
                TextPaneUtil.rawPrintln("载荷长度: " + config.getClassBytesLength());
                TextPaneUtil.rawPrintln("");
                TextPaneUtil.successPrintln("结果输出:");
                TextPaneUtil.rawPrintln("");
                TextPaneUtil.rawPrintln(result);
                ComponentUtil.restoreScrollPosition(textScrollPane);
            } catch (Throwable ex) {
                TextPaneUtil.errorPrintln(ex.getMessage());
            }
        });
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        jEGPanel = new JPanel();
        jEGPanel.setLayout(new GridLayoutManager(7, 1, new Insets(0, 0, 0, 0), -1, -1));
        TopPanel = new JPanel();
        TopPanel.setLayout(new GridLayoutManager(1, 8, new Insets(0, 0, 0, 0), -1, -1));
        jEGPanel.add(TopPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        serverLabel = new JLabel();
        serverLabel.setText("中间件/框架");
        TopPanel.add(serverLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        formatLabel = new JLabel();
        formatLabel.setText("输出格式");
        TopPanel.add(formatLabel, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        modelLabel = new JLabel();
        modelLabel.setText("回显模式");
        TopPanel.add(modelLabel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        gadgetLabel = new JLabel();
        gadgetLabel.setText("利用类型");
        TopPanel.add(gadgetLabel, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        serverBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Tomcat");
        defaultComboBoxModel1.addElement("SpringMVC");
        defaultComboBoxModel1.addElement("Weblogic");
        defaultComboBoxModel1.addElement("Websphere");
        defaultComboBoxModel1.addElement("Resin");
        defaultComboBoxModel1.addElement("Undertow");
        defaultComboBoxModel1.addElement("Jetty");
        defaultComboBoxModel1.addElement("SpringWebFlux");
        serverBox.setModel(defaultComboBoxModel1);
        TopPanel.add(serverBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(139, -1), null, 0, false));
        modelBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("Command");
        defaultComboBoxModel2.addElement("Code");
        modelBox.setModel(defaultComboBoxModel2);
        TopPanel.add(modelBox, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(139, -1), null, 0, false));
        gadgetBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel3 = new DefaultComboBoxModel();
        defaultComboBoxModel3.addElement("NONE");
        defaultComboBoxModel3.addElement("JDK_AbstractTranslet");
        defaultComboBoxModel3.addElement("XALAN_AbstractTranslet");
        gadgetBox.setModel(defaultComboBoxModel3);
        TopPanel.add(gadgetBox, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        formatBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel4 = new DefaultComboBoxModel();
        defaultComboBoxModel4.addElement("BASE64");
        defaultComboBoxModel4.addElement("BIGINTEGER");
        defaultComboBoxModel4.addElement("BCEL");
        defaultComboBoxModel4.addElement("CLASS");
        defaultComboBoxModel4.addElement("JAR");
        defaultComboBoxModel4.addElement("JS");
        formatBox.setModel(defaultComboBoxModel4);
        TopPanel.add(formatBox, new GridConstraints(0, 7, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        textScrollPane = new JScrollPane();
        jEGPanel.add(textScrollPane, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 360), new Dimension(36, 207), null, 0, false));
        textPane = new JTextPane();
        textScrollPane.setViewportView(textPane);
        MiddlePanel = new JPanel();
        MiddlePanel.setLayout(new GridLayoutManager(2, 4, new Insets(0, 0, 0, 0), -1, -1));
        jEGPanel.add(MiddlePanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        reqParamNameLabel = new JLabel();
        reqParamNameLabel.setText("请求参数");
        MiddlePanel.add(reqParamNameLabel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        reqParamText = new JTextField();
        reqParamText.setToolTipText("可选，默认随机生成");
        MiddlePanel.add(reqParamText, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        headerNameLabel = new JLabel();
        headerNameLabel.setText("请求头键");
        MiddlePanel.add(headerNameLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        reqHeaderNameText = new JTextField();
        reqHeaderNameText.setText("");
        reqHeaderNameText.setToolTipText("可选，默认随机生成");
        MiddlePanel.add(reqHeaderNameText, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        shellClsNameLabel = new JLabel();
        shellClsNameLabel.setText("类名");
        MiddlePanel.add(shellClsNameLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        classNameText = new JTextField();
        classNameText.setToolTipText("可选，默认随机生成");
        MiddlePanel.add(classNameText, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        TopSep = new JSeparator();
        jEGPanel.add(TopSep, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        MiddleSep = new JSeparator();
        jEGPanel.add(MiddleSep, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        BottomPanel = new JPanel();
        BottomPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        jEGPanel.add(BottomPanel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        generateButton = new JButton();
        generateButton.setText("生成");
        BottomPanel.add(generateButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        TipPanel = new JPanel();
        TipPanel.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        jEGPanel.add(TipPanel, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        TipPanel.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        authorLabel = new JLabel();
        authorLabel.setText("请勿用于非法用途");
        TipPanel.add(authorLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        noticeLabel = new JLabel();
        noticeLabel.setText("by pen4uin");
        TipPanel.add(noticeLabel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return jEGPanel;
    }
}
