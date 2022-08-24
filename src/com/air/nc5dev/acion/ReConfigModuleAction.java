package com.air.nc5dev.acion;

import com.air.nc5dev.acion.base.AbstractIdeaAction;
import com.air.nc5dev.util.IdeaProjectGenerateUtil;
import com.air.nc5dev.util.ProjectNCConfigUtil;
import com.air.nc5dev.util.RemindUtil;
import com.air.nc5dev.util.StringUtil;
import com.air.nc5dev.util.idea.ApplicationLibraryUtil;
import com.air.nc5dev.util.idea.ProjectUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

/**
 *
 */
public class ReConfigModuleAction extends AbstractIdeaAction {

    @Override
    protected void doHandler(AnActionEvent e) {
        Project project = e.getProject();
        ProjectUtil.setProject(project);
        ProjectNCConfigUtil.initConfigFile();
        if (StringUtil.isBlank(ProjectNCConfigUtil.getNCHomePath())) {
            //没有配置NC home！
            return;
        }

        Module module = LangDataKeys.MODULE.getData(e.getDataContext());

        if (module == null) {
            Module[] modules = ModuleManager.getInstance(project).getModules();
            for (Module module1 : modules) {
                setModuel(module1);
            }
        } else {
            setModuel(module);
        }
    }

    private void setModuel(Module module) {
        IdeaProjectGenerateUtil.generateSrcDir4Modules(module);
        ApplicationLibraryUtil.addLibs2Module(module);
        //设置模块 源文件结构 ModulesStructureConfigurable
        IdeaProjectGenerateUtil.setModuleStructureConfigurable(module);
    }
}
