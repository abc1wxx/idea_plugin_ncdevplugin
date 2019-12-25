package com.air.nc5dev.ui;

import javax.swing.*;

public class NC5HomeConfigDiaLogPanel extends JPanel {
    protected JTextField textField_home;
    protected JTextField textField_ip;
    protected JTextField textField_port;
    protected JTextField textField_oidmark;
    protected JTextField textField_sid;
    protected JTextField textField_user;
    protected JTextField textField_pass;
    protected JTextField textField_clientip;
    protected JTextField textField_cientport;
    protected JButton button_testdb;
    protected JButton button_choseDir;
    protected JButton button_adddesign;
    protected JButton button_yes;
    protected JButton button_canel;
    protected JLabel label_3;
    protected JComboBox comboBox_datasource;
    protected JComboBox comboBox_dbtype;
    protected  NC5HomeConfigDialogUIListner nc5HomeConfigDialogUIListner;

    /**
     * Create the dialog.
     */
    public NC5HomeConfigDiaLogPanel() {
        setBounds(0, 0, 941, 635);
        setLayout(null);

        JPanel contentPanel = new JPanel();
        contentPanel.setBounds(14, 14, 895, 544);
        add(contentPanel);
        contentPanel.setLayout(null);

        JLabel lblNc = new JLabel("NC 文件夹: ");
        lblNc.setBounds(14, 14, 107, 19);
        contentPanel.add(lblNc);

        textField_home = new JTextField();
        textField_home.setBounds(114, 11, 620, 32);
        contentPanel.add(textField_home);
        textField_home.setColumns(10);

        button_choseDir = new JButton("选择");
        button_choseDir.setBounds(737, -6, 101, 59);
        contentPanel.add(button_choseDir);

        JLabel label = new JLabel("数据库类型: ");
        label.setBounds(14, 142, 107, 19);
        contentPanel.add(label);

        comboBox_dbtype = new JComboBox(new String[] { "ORACLE11G", "ORACLE10G", "SQLSERVER2008", "DB297" });
        comboBox_dbtype.setBounds(114, 139, 170, 32);
        contentPanel.add(comboBox_dbtype);

        button_testdb = new JButton("测试链接");
        button_testdb.setBounds(559, 111, 120, 42);
        contentPanel.add(button_testdb);

        button_adddesign = new JButton("新增开发源");
        button_adddesign.setBounds(720, 112, 120, 42);
        contentPanel.add(button_adddesign);

        JLabel lblIp = new JLabel("IP:");
        lblIp.setBounds(14, 191, 46, 19);
        contentPanel.add(lblIp);

        textField_ip = new JTextField();
        textField_ip.setColumns(10);
        textField_ip.setBounds(114, 188, 433, 32);
        contentPanel.add(textField_ip);

        JLabel lblOid = new JLabel("Port:");
        lblOid.setBounds(14, 227, 80, 19);
        contentPanel.add(lblOid);

        textField_port = new JTextField();
        textField_port.setColumns(10);
        textField_port.setBounds(114, 224, 113, 32);
        contentPanel.add(textField_port);

        JLabel lblOidmark = new JLabel("OIDMark:");
        lblOidmark.setBounds(246, 230, 80, 19);
        contentPanel.add(lblOidmark);

        textField_oidmark = new JTextField();
        textField_oidmark.setColumns(10);
        textField_oidmark.setBounds(340, 224, 207, 32);
        contentPanel.add(textField_oidmark);

        JLabel lblDbnamesid = new JLabel("DBNAME(SID):");
        lblDbnamesid.setBounds(14, 263, 137, 19);
        contentPanel.add(lblDbnamesid);

        textField_sid = new JTextField();
        textField_sid.setColumns(10);
        textField_sid.setBounds(172, 260, 375, 32);
        contentPanel.add(textField_sid);

        JLabel lblUsername = new JLabel("用户名:");
        lblUsername.setBounds(14, 306, 96, 25);
        contentPanel.add(lblUsername);

        textField_user = new JTextField();
        textField_user.setColumns(10);
        textField_user.setBounds(114, 306, 708, 32);
        contentPanel.add(textField_user);

        JLabel lblPassword = new JLabel("密  码:");
        lblPassword.setBounds(14, 361, 96, 25);
        contentPanel.add(lblPassword);

        textField_pass = new JTextField();
        textField_pass.setColumns(10);
        textField_pass.setBounds(124, 358, 708, 32);
        contentPanel.add(textField_pass);

        JLabel label_1 = new JLabel("客户端地址:");
        label_1.setBounds(8, 451, 96, 25);
        contentPanel.add(label_1);

        textField_clientip = new JTextField();
        textField_clientip.setBounds(120, 444, 718, 32);
        contentPanel.add(textField_clientip);
        textField_clientip.setColumns(10);

        JLabel label_2 = new JLabel("客户端端口:");
        label_2.setBounds(14, 505, 96, 25);
        contentPanel.add(label_2);

        textField_cientport = new JTextField();
        textField_cientport.setBounds(120, 498, 718, 32);
        contentPanel.add(textField_cientport);
        textField_cientport.setColumns(10);

        label_3 = new JLabel("数据源: ");
        label_3.setBounds(14, 82, 107, 19);
        contentPanel.add(label_3);

        comboBox_datasource = new JComboBox();
        comboBox_datasource.setBounds(114, 79, 170, 32);
        contentPanel.add(comboBox_datasource);

        nc5HomeConfigDialogUIListner = NC5HomeConfigDialogUIListner.build(this);
    }
    protected JButton getButton_canel() {
        return button_canel;
    }
    public JButton getButton_adddesign() {
        return button_adddesign;
    }
    public JButton getButton_testdb() {
        return button_testdb;
    }
    public JButton getButton_yes() {
        return button_yes;
    }
    public JTextField getTextField_port() {
        return textField_port;
    }
    public JTextField getTextField_pass() {
        return textField_pass;
    }
    public JTextField getTextField_oidmark() {
        return textField_oidmark;
    }
    public JTextField getTextField_home() {
        return textField_home;
    }
    public JTextField getTextField_ip() {
        return textField_ip;
    }
    public JTextField getTextField_sid() {
        return textField_sid;
    }
    public JComboBox getComboBox_datasource() {
        return comboBox_datasource;
    }
    public JTextField getTextField_cientport() {
        return textField_cientport;
    }
    public JTextField getTextField_clientip() {
        return textField_clientip;
    }
    public JTextField getTextField_user() {
        return textField_user;
    }
    public JComboBox getComboBox_dbtype() {
        return comboBox_dbtype;
    }

    public void setTextField_home(JTextField textField_home) {
        this.textField_home = textField_home;
    }

    public void setTextField_ip(JTextField textField_ip) {
        this.textField_ip = textField_ip;
    }

    public void setTextField_port(JTextField textField_port) {
        this.textField_port = textField_port;
    }

    public void setTextField_oidmark(JTextField textField_oidmark) {
        this.textField_oidmark = textField_oidmark;
    }

    public void setTextField_sid(JTextField textField_sid) {
        this.textField_sid = textField_sid;
    }

    public void setTextField_user(JTextField textField_user) {
        this.textField_user = textField_user;
    }

    public void setTextField_pass(JTextField textField_pass) {
        this.textField_pass = textField_pass;
    }

    public void setTextField_clientip(JTextField textField_clientip) {
        this.textField_clientip = textField_clientip;
    }

    public void setTextField_cientport(JTextField textField_cientport) {
        this.textField_cientport = textField_cientport;
    }

    public void setButton_testdb(JButton button_testdb) {
        this.button_testdb = button_testdb;
    }

    public JButton getButton_choseDir() {
        return button_choseDir;
    }

    public void setButton_choseDir(JButton button_choseDir) {
        this.button_choseDir = button_choseDir;
    }

    public void setButton_adddesign(JButton button_adddesign) {
        this.button_adddesign = button_adddesign;
    }

    public void setButton_yes(JButton button_yes) {
        this.button_yes = button_yes;
    }

    public void setButton_canel(JButton button_canel) {
        this.button_canel = button_canel;
    }

    public JLabel getLabel_3() {
        return label_3;
    }

    public void setLabel_3(JLabel label_3) {
        this.label_3 = label_3;
    }

    public void setComboBox_datasource(JComboBox comboBox_datasource) {
        this.comboBox_datasource = comboBox_datasource;
    }

    public void setComboBox_dbtype(JComboBox comboBox_dbtype) {
        this.comboBox_dbtype = comboBox_dbtype;
    }

    public NC5HomeConfigDialogUIListner getNc5HomeConfigDialogUIListner() {
        return nc5HomeConfigDialogUIListner;
    }

    public void setNc5HomeConfigDialogUIListner(NC5HomeConfigDialogUIListner nc5HomeConfigDialogUIListner) {
        this.nc5HomeConfigDialogUIListner = nc5HomeConfigDialogUIListner;
    }
}
