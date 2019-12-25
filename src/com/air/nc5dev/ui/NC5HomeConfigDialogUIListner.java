package com.air.nc5dev.ui;

import com.air.nc5dev.acion.NC5HomePathConfigAction;
import com.air.nc5dev.bean.NCDataSourceVO;
import com.air.nc5dev.util.NCPropXmlUtil;
import com.air.nc5dev.util.ProjectNCConfigUtil;
import com.air.nc5dev.util.idea.ProjectUtil;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import nc.vo.framework.rsa.Encode;

import javax.annotation.Nullable;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

/**
 * NC5HomeConfigDialogUI 的监听器
 */
public class NC5HomeConfigDialogUIListner {
    private NC5HomeConfigDiaLogPanel ui;

    /**
     * 当点击了  测试数据库链接
     *
     * @param e
     */
    private void OnTestDb(ActionEvent e) {
        if (NCPropXmlUtil.isDataSourceEmpty()) {
            return;
        }
        int selectedIndex = ui.comboBox_datasource.getSelectedIndex();
        testConnectDb(NCPropXmlUtil.get(selectedIndex));
    }

    /**
     * 测试数据源链接
     *
     * @param ds
     */
    private void testConnectDb(NCDataSourceVO ds) {
        //final String ora11 = "ORACLE11G", ora10 = "ORACLE10G", sqlserver = "SQLSERVER2008", db297 = "DB297";
        Connection con;
        String user = ui.textField_user.getText();
        String password = new Encode().decode(ui.textField_pass.getText());
        String className = ds.getDriverClassName();
        String url = ui.textField_ip.getText();
        try {
            Class.forName(className);
            con = DriverManager.getConnection(url, user, password);
            con.close();
        } catch (Exception e) {
            Messages.showErrorDialog(ProjectUtil.getDefaultProject(), e.toString(), "连接失败");
            return ;
        }

        Messages.showMessageDialog("连接成功", "提示", null);
    }

    /**
     * 当点击了  选择HOME文件夹
     *
     * @param e
     */
    private void OnChoseHomeDir(ActionEvent e) {
        VirtualFile virtualFile = FileChooser.chooseFile(new FileChooserDescriptor(false, true
                        , false, false, false, false), null
                , null);
        String path = virtualFile.getPath();
        File home = new File(path);
        if (home.exists() && home.isDirectory()) {
            ui.textField_home.setText(home.getPath());
            readNCConfig();
        } else {
            Messages.showMessageDialog("请选择正确的文件夹！", "错误", null);
        }
    }

    /**
     * 读取 NC的各种基本配置信息
     */
    private void readNCConfig() {
        ProjectNCConfigUtil.setNCHomePath(ui.textField_home.getText());
        loadConf();
    }

    /**
     * 当点击了  新增开发数据源
     *
     * @param e
     */
    private void OnAddDesgin(ActionEvent e) {
        if (NCPropXmlUtil.isDataSourceEmpty()) {
            return;
        }
        final String designName = "design";
        boolean hasDesign = NCPropXmlUtil.stream().anyMatch(ncDataSourceVO -> designName.equals(ncDataSourceVO.getDataSourceName()));
        if (hasDesign) {
            return;
        }

        try {
            NCDataSourceVO design = NCPropXmlUtil.get(ui.comboBox_datasource
                    .getSelectedIndex()).clone();
            design.setDataSourceName(designName);
            design.setIsBase("false");
            NCPropXmlUtil.add(design);
            putDataSourceVOs2UI(NCPropXmlUtil.getDataSourceVOS());

        } catch (CloneNotSupportedException e1) {
            e1.printStackTrace();
            Messages.showErrorDialog(ProjectUtil.getDefaultProject(), e.toString(), "新增design数据源错误");
        }

    }

    /**
     * 当点击了  保存确认
     *
     * @param e
     */
    public void OnSave(@Nullable ActionEvent e) {
        ProjectNCConfigUtil.setNCHomePath(ui.textField_home.getText());
        ProjectNCConfigUtil.setNCClientIP(ui.textField_clientip.getText());
        ProjectNCConfigUtil.setNCClientPort(ui.textField_cientport.getText());

        ProjectNCConfigUtil.saveConfig2File();
        NCPropXmlUtil.saveDataSources();
    }

    /**
     * 数据源选择改变
     */
    private void onDataSourceSelectChange(ItemEvent event) {
        if (event.getStateChange() != ItemEvent.ITEM_STATE_CHANGED) {
            return;
        }

        showDataSource2UI(event.getItem().toString());
    }

    /**
     * 显示指定的数据源名字到ui界面
     *
     * @param dsName
     */
    private void showDataSource2UI(String dsName) {
        NCDataSourceVO ds =  NCPropXmlUtil.stream().filter(e -> {
            return e.getDataSourceName().equals(dsName);
        }).findFirst().get();

        ui.textField_ip.setText(ds.getDatabaseUrl());
        ui.textField_oidmark.setText(ds.getOidMark());
        ui.textField_user.setText(ds.getUser());
        ui.textField_pass.setText(new Encode().decode(ds.getPassword()));

        ui.comboBox_dbtype.setSelectedItem(ds.getDatabaseType());
    }

    /**
     * 当点击了  取消关闭
     *
     * @param e
     */
    private void OnCanel(ActionEvent e) {
        NC5HomePathConfigAction.ui.dispose();
    }

    /**
     * 载入NC配置到界面
     */
    private void loadConf() {
        ui.textField_home.setText(ProjectNCConfigUtil.getNCHomePath());
        List<NCDataSourceVO> dataSourceVOS = NCPropXmlUtil.getDataSourceVOS();

        if (null != dataSourceVOS) {
            putDataSourceVOs2UI(dataSourceVOS);
        }
    }

    /**
     * 把一堆数据源放入 界面
     *
     * @param dataSourceVOS
     */
    public void putDataSourceVOs2UI(List<NCDataSourceVO> dataSourceVOS) {
        ui.comboBox_datasource.removeAllItems();
        dataSourceVOS.forEach(ds -> ui.comboBox_datasource.addItem(ds.getDataSourceName()));

        final int firstDsIndex = 0;
        ui.comboBox_datasource.setSelectedIndex(firstDsIndex);
        if (dataSourceVOS != null && !dataSourceVOS.isEmpty()) {
            showDataSource2UI(dataSourceVOS.get(firstDsIndex).getDataSourceName());
        }
    }

    /**
     * 初始化 按钮监听
     */
    private void initEventListeners() {
        ui.button_testdb.addActionListener(this::OnTestDb);
        ui.button_choseDir.addActionListener(this::OnChoseHomeDir);
        ui.button_adddesign.addActionListener(this::OnAddDesgin);
        ui.comboBox_datasource.addItemListener(this::onDataSourceSelectChange);
    }

    public static final NC5HomeConfigDialogUIListner build(NC5HomeConfigDiaLogPanel setUI) {
        NC5HomeConfigDialogUIListner nC5HomeConfigDialogUIListner = new NC5HomeConfigDialogUIListner(setUI);

        nC5HomeConfigDialogUIListner.initEventListeners();
        nC5HomeConfigDialogUIListner.loadConf();

        return nC5HomeConfigDialogUIListner;
    }

    private NC5HomeConfigDialogUIListner(NC5HomeConfigDiaLogPanel setUI) {
        this.ui = setUI;
    }
}
