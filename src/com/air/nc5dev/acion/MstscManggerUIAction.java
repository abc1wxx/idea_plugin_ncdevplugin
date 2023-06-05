package com.air.nc5dev.acion;

import com.air.nc5dev.acion.base.AbstractIdeaAction;
import com.air.nc5dev.ui.MstscDialog;
import com.air.nc5dev.util.idea.LogUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;

import java.sql.SQLException;

public class MstscManggerUIAction extends AbstractIdeaAction {
    @Override
    protected void doHandler(AnActionEvent e) {
        MstscDialog dialog = new MstscDialog(e);
        dialog.showAndGet();

        try {
            dialog.getMstscEntitService().getDao().submit();
        } catch (SQLException ex) {
            ex.printStackTrace();
            LogUtil.error(ex.getMessage(), ex);
        }
    }
}
