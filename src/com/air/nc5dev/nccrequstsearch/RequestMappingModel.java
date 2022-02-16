package com.air.nc5dev.nccrequstsearch;


import com.air.nc5dev.util.StringUtil;
import com.air.nc5dev.util.idea.ProjectUtil;
import com.air.nc5dev.vo.NCCActionInfoVO;
import com.intellij.featureStatistics.FeatureUsageTracker;
import com.intellij.ide.IdeEventQueue;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManager;
import com.intellij.ide.actions.searcheverywhere.statistics.SearchEverywhereUsageTriggerCollector;
import com.intellij.ide.util.gotoByName.ChooseByNameItemProvider;
import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.internal.statistic.eventLog.FeatureUsageData;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * </br>
 * </br>
 * </br>
 *
 * @author 唐粟 Email:209308343@qq.com
 * @date 2022/2/15 0015 11:09
 * @project
 * @Version
 */
@Getter
public class RequestMappingModel extends FilteringGotoByModel implements DumbAware {
    volatile boolean onlySearchPorjectUrl;
    AnActionEvent e;

    public RequestMappingModel(@NotNull Project project, @NotNull List list, AnActionEvent e) {
        super(project, list);
        this.e = e;
    }

    @NotNull
    @Override
    public ChooseByNameItemProvider getItemProvider(@Nullable PsiElement context) {
        return new RequestMappingItemProvider(this);
    }

    @Nullable
    @Override
    protected Object filterValueFor(NavigationItem navigationItem) {
        return null;
    }

    @Override
    public String getPromptText() {
        return "输入请求地址nccloud/aim/acceptance/querylist.do或acceptance.querylist等";
    }

    @NotNull
    @Override
    public String getNotInMessage() {
        return "没有匹配器";
    }

    @NotNull
    @Override
    public String getNotFoundMessage() {
        return "没有匹配到任何结果!";
    }

    @Nullable
    @Override
    public String getCheckBoxName() {
        return "只搜索项目内URL映射(不包含HOME)";
    }

    @Override
    public boolean loadInitialCheckBoxState() {
        return onlySearchPorjectUrl;
    }

    @Override
    public void saveInitialCheckBoxState(boolean b) {
        onlySearchPorjectUrl = b;
    }

    @NotNull
    @Override
    public String[] getSeparators() {
        return new String[0];
    }

    @Nullable
    @Override
    public String getFullName(@NotNull Object o) {
        String name = o instanceof NCCActionInfoVO ? ((NCCActionInfoVO) o).getClazz().trim() : getElementName(o);
        if (StringUtil.isNotBlank(name)) {
            Project project = ProjectUtil.getDefaultProject();
            SearchEverywhereManager seManager = SearchEverywhereManager.getInstance(project);
            FeatureUsageTracker.getInstance().triggerFeatureUsed("SearchEverywhere");
            FeatureUsageData data = SearchEverywhereUsageTriggerCollector.createData("ClassSearchEverywhereContributor").addInputEvent(e);
            SearchEverywhereUsageTriggerCollector.trigger(project, "dialogOpen", data);
            IdeEventQueue.getInstance().getPopupManager().closeAllPopups(false);
            seManager.show("ClassSearchEverywhereContributor", name, e);
        }

        return null;
    }

    @Override
    public boolean willOpenEditor() {
        return true;
    }
}
