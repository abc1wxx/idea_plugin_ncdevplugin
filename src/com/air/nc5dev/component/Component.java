package com.air.nc5dev.component;

import com.air.nc5dev.util.ReflectUtil;
import com.air.nc5dev.util.idea.LogUtil;
import com.intellij.compiler.server.BuildManagerListener;
import com.intellij.debugger.impl.DebuggerManagerListener;
import com.intellij.execution.ui.RunContentManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.profiler.api.configurations.ProfilerRunConfigurationsManagerListener;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.messages.Topic;
import com.intellij.xdebugger.impl.XDebuggerManagerImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

import static com.intellij.AppTopics.FILE_DOCUMENT_SYNC;

/**
 * The plugin entry class that instanciates (or reuse) and delegates to {@link BuildManagerListenerImpl}. This is not a
 * singleton, for java based ide the corresponding component will also get instanciated (check {@link JavaComponent}).
 *
 * @see BuildManagerListenerImpl
 */
public class Component implements ApplicationComponent {

    public static final String COMPONENT_NAME = "Build Manager ";

    @Override
    public void initComponent() {
        LogUtil.info("Starting component: " + COMPONENT_NAME);


        BuildManagerListenerImpl manager = BuildManagerListenerImpl.getInstance();

        MessageBus bus = ApplicationManager.getApplication().getMessageBus();
        MessageBusConnection connection = bus.connect();

        //监听编译动作
        connection.subscribe(BuildManagerListener.TOPIC, manager);
        //监听debug run动作
        connection.subscribe(DebuggerManagerListener.TOPIC, manager);
        //监听其他 运行
        connection.subscribe(RunContentManager.TOPIC, manager);
        connection.subscribe(XDebuggerManagerImpl.TOPIC, manager);

    }

    @Override
    public void disposeComponent() {
        LogUtil.info("Stopping component: " + COMPONENT_NAME);
    }

    @NotNull
    @Override
    public String getComponentName() {
        return COMPONENT_NAME;
    }

}
