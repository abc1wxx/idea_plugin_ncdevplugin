package com.air.nc5dev.acion;

import com.air.nc5dev.util.ExceptionUtil;
import com.air.nc5dev.util.ExportNCPatcherUtil;
import com.air.nc5dev.util.IoUtil;
import com.air.nc5dev.util.idea.ProjectUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.jar.Manifest;

/**
 * 把一个NC补丁的模块文件夹  转换成Jar包        </br>
 * </br>
 * </br>
 * </br>
 *
 * @author air Email: 209308343@qq.com
 * @date 2020/4/1 0001 22:03
 * @Param
 * @return
 */
public class MakeNcModuleJarAction extends AbstractIdeaAction {

    @Override
    protected void doHandler(AnActionEvent e) {
        //选择路径
        JFileChooser fileChooser = new JFileChooser(ProjectUtil.getDefaultProject().getBasePath()
                + File.separatorChar + "patchers");
        fileChooser.setDialogTitle("选择要转换NC补丁的模块的文件夹位置:");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int flag = fileChooser.showOpenDialog(null);
        if (flag != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File dir = fileChooser.getSelectedFile();

        //选择jar 的 manifest 文件 位置
        File manifest = null;
        fileChooser = new JFileChooser(dir);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("选择jar 的 manifest 文件，可以不选择 就是用默认格式:");
        fileChooser.showOpenDialog(null);
        if (flag != JFileChooser.APPROVE_OPTION
                && fileChooser.getSelectedFile() != null) {
            manifest = fileChooser.getSelectedFile();
        }

        Manifest maniFest = ExportNCPatcherUtil.getManiFest(manifest, dir.getName());


        //public
        File srcDir = new File(dir, "classes");
        IoUtil.makeJar(srcDir, new File(dir
                , "lib" + File.separatorChar + "public_"
                        + dir.getName() + ".jar")
                , maniFest, new String[]{".java"});
        IoUtil.makeJar(srcDir, new File(dir
                        , "lib" + File.separatorChar + "public_"
                        + dir.getName() + "_src.jar")
                , maniFest, new String[]{".class"});
        //private
        srcDir = new File(dir, "META-INF");
        IoUtil.makeJar(srcDir, new File(dir
                        , "lib" + File.separatorChar + "private_"
                        + dir.getName() + ".jar")
                , maniFest, new String[]{".java"});
        IoUtil.makeJar(srcDir, new File(dir
                        , "META-INF" + File.separatorChar +"lib" + File.separatorChar + "private_"
                        + dir.getName() + "_src.jar")
                , maniFest, new String[]{".class"});
        //client
        srcDir = new File(dir, "client");
        IoUtil.makeJar(srcDir, new File(dir
                        , "lib" + File.separatorChar + "ui_"
                        + dir.getName() + ".jar")
                , maniFest, new String[]{".java"});
        IoUtil.makeJar(srcDir, new File(dir
                        , "client" + File.separatorChar +"lib" + File.separatorChar + "ui_"
                        + dir.getName() + "_src.jar")
                , maniFest, new String[]{".class"});

        Messages.showInfoMessage("生成成功： " + dir.getPath(), "转换完成");

        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.open(dir);
        } catch (Throwable iae) {
            ProjectUtil.errorNotification("自动打开路径失败: " + ExceptionUtil.getExcptionDetall(iae), null);
        }
    }
}
