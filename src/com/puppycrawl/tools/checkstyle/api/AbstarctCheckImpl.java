package com.puppycrawl.tools.checkstyle.api;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import com.air.nc5dev.util.CollUtil;
import com.air.nc5dev.util.IoUtil;
import com.air.nc5dev.util.ProjectNCConfigUtil;
import com.air.nc5dev.util.idea.LogUtil;
import com.air.nc5dev.util.idea.ProjectUtil;
import com.google.common.collect.Sets;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.impl.libraries.LibraryEx;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.vfs.VirtualFile;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * NC 三大文件夹互相导入检查 </br>
 * </br>
 * </br>
 *
 * @author 唐粟 Email:209308343@qq.com
 * @date 2023/2/16 0016 16:40
 * @project
 * @Version
 */
@Data
public abstract class AbstarctCheckImpl implements FileSetCheck {
    public static final String COMMA_SEPARATOR = ",";
    private final ThreadLocal<FileContext> context = ThreadLocal.withInitial(() -> {
        return new FileContext();
    });
    MessageDispatcher messageDispatcher;
    String[] fileExtensions;
    SeverityLevel severityLevel;
    String id;
    Configuration configuration;
    int tabWidth;
    Project project;
    LibraryTable.ModifiableModel modifiableModel;
    Map<Project, Cecha> projectCechaMap = new ConcurrentHashMap<>();
    public static volatile AtomicReference<Cecha> PROJECT_SOURCE_CECHA = new AtomicReference<>();

    //1 public 2 client 3 private 4 test
    public static final int FROM_PUBLIC = 1;
    public static final int FROM_CLIENT = 2;
    public static final int FROM_PRIVATE = 3;
    public static final int FROM_TEST = 4;

    @Data
    public static class Cecha {
        Map<String, Set<String>> class2PathMap = new ConcurrentHashMap<>();
        Map<String, Set<Integer>> path2TypeMap = new ConcurrentHashMap<>();
    }

    public abstract void processFiltered(File file, FileText fileText, FileContext fileContext) throws CheckstyleException;

    @Override
    public final SortedSet<Violation> process(File file, FileText fileText) throws CheckstyleException {
        project = ProjectUtil.getProject();
        modifiableModel = LibraryTablesRegistrar.getInstance().getLibraryTable(project).getModifiableModel();

        if (!projectCechaMap.containsKey(project)) {
            initMap();
        }

        FileContext fileContext = (FileContext) this.context.get();
        fileContext.fileContents = new FileContents(fileText);
        fileContext.violations.clear();
        if (CommonUtil.matchesFileExtension(file, this.fileExtensions)) {
            this.processFiltered(file, fileText, fileContext);
        }

        SortedSet<Violation> result = new TreeSet(fileContext.violations);
        fileContext.violations.clear();
        return result;
    }

    public static void initProjectSourceMap(Project project) {
        Cecha cecha = new Cecha();
        Cecha hope = PROJECT_SOURCE_CECHA.get();
        PROJECT_SOURCE_CECHA.compareAndSet(hope, cecha);

        Module[] modules = ModuleManager.getInstance(project).getModules();
        if (CollUtil.isEmpty(modules)) {
            return;
        }

        for (Module module : modules) {
            File src = new File(new File(module.getModuleFile().getPath()).getParentFile(), "src");

            for (File fs : src.listFiles()) {
                int from = 1;
                if (fs.getName().equals("public")) {
                } else if (fs.getName().equals("client")) {
                    from = 2;
                } else if (fs.getName().equals("private")) {
                    from = 3;
                } else if (fs.getName().equals("test")) {
                    continue;
                }

                File[] fs2 = fs.listFiles();
                if (CollUtil.isEmpty(fs2)) {
                    continue;
                }

                for (File file : fs2) {
                    if (file.isFile()) {
                        continue;
                    }
                    initMap4Dir("", file, cecha, from);
                }
            }

            System.out.println();
        }
    }

    public void initMap() {
        Cecha cecha = new Cecha();
        if (projectCechaMap.putIfAbsent(project, cecha) != null) {
            return;
        }
        initMap0(cecha, FROM_PUBLIC,
                (LibraryEx) modifiableModel.getLibraryByName(ProjectNCConfigUtil.LIB_Product_Common_Library));
        initMap0(cecha, FROM_PUBLIC,
                (LibraryEx) modifiableModel.getLibraryByName(ProjectNCConfigUtil.LIB_Middleware_Library));
        initMap0(cecha, FROM_PUBLIC,
                (LibraryEx) modifiableModel.getLibraryByName(ProjectNCConfigUtil.LIB_Framework_Library));
        initMap0(cecha, FROM_PUBLIC,
                (LibraryEx) modifiableModel.getLibraryByName(ProjectNCConfigUtil.LIB_NC_Module_Public_Library));

        initMap0(cecha, FROM_CLIENT,
                (LibraryEx) modifiableModel.getLibraryByName(ProjectNCConfigUtil.LIB_Module_Client_Library));
        initMap0(cecha, FROM_CLIENT,
                (LibraryEx) modifiableModel.getLibraryByName(ProjectNCConfigUtil.LIB_NCCloud_Library));

        initMap0(cecha, FROM_PRIVATE,
                (LibraryEx) modifiableModel.getLibraryByName(ProjectNCConfigUtil.LIB_Module_Private_Library));
    }

    public void initMap0(Cecha cecha, int from, LibraryEx library) {
        VirtualFile[] fs = library.getFiles(OrderRootType.CLASSES);
        if (CollUtil.isEmpty(fs)) {
            return;
        }

        for (VirtualFile f : fs) {
            if (f.isDirectory()) {
                try {
                    File ff = new File(f.getPath());
                    for (File f2 : ff.listFiles()) {
                        initMap4Dir("", f2, cecha, from);
                    }
                } catch (Exception e) {
                    initMap4File(f, cecha, from);
                }
            } else {
                initMap4File(f, cecha, from);
            }
        }
    }

    public static void initMap4Dir(String packge, File f, Cecha cecha, int from) {
        String simpleName = FileNameUtil.getPrefix(f);
        if (f.isDirectory()) {
            File[] fs = f.listFiles();
            if (CollUtil.isEmpty(fs)) {
                return;
            }

            for (File f2 : fs) {
                if (StrUtil.isNotBlank(packge)) {
                    packge += '.';
                }
                initMap4Dir(packge + simpleName, f2, cecha, from);
            }
        } else if (f.isFile()) {
            if (StrUtil.endWith(f.getPath().toLowerCase().trim(), ".class")) {
                String clz = packge + '.' + simpleName;
                cecha.getClass2PathMap().putIfAbsent(clz, Sets.newConcurrentHashSet());
                cecha.getClass2PathMap().get(clz).add(f.getPath());

                cecha.getPath2TypeMap().putIfAbsent(f.getPath(), Sets.newConcurrentHashSet());
                cecha.getPath2TypeMap().get(f.getPath()).add(from);
            }
        }
    }

    public static void initMap4File(VirtualFile vf, Cecha cecha, int from) {
        String path = vf.getPath();
        if (!StrUtil.endWithAny(path.toLowerCase(), ".jar!/", ".jar")) {
            return;
        }

        if (StrUtil.endWith(path.toLowerCase(), ".jar!/")) {
            path = path.substring(0, path.length() - 2);
        }

        File file = new File(path);
        if (!file.isFile()) {
            return;
        }
        JarFile jar = null;
        try {
            jar = new JarFile(file);
            Enumeration<JarEntry> entries = jar.entries();
            String clz;
            String name;
            JarEntry jarEntry;
            while (entries.hasMoreElements()) {
                jarEntry = entries.nextElement();
                name = jarEntry.getName();
                if (!StrUtil.endWith(name.toLowerCase(), ".class")) {
                    continue;
                }

                //nc/ui/bi/graph/DefaultGraph.class
                clz = StrUtil.replace(name, "/", ".");
                clz = StrUtil.removeSuffix(clz, ".class");
                cecha.getClass2PathMap().putIfAbsent(clz, Sets.newConcurrentHashSet());
                cecha.getClass2PathMap().get(clz).add(file.getPath());

                cecha.getPath2TypeMap().putIfAbsent(file.getPath(), Sets.newConcurrentHashSet());
                cecha.getPath2TypeMap().get(file.getPath()).add(from);
            }
        } catch (IOException e) {
            LogUtil.error(e.getMessage(), e);
        }finally {
            IoUtil.close(jar);
        }
    }

    /**
     * 找到class全限定名 来源 1 public 2 client 3 private 4 test
     *
     * @param className
     * @return
     */
    public Set<Integer> findClassFrom(String className) {
        if (StrUtil.isBlank(className)) {
            return Sets.newHashSet(FROM_PUBLIC);
        }

        //可能是工程 源码里面的
        HashSet<Integer> all = Sets.newHashSet();
        Set<Integer> from;
        Cecha cecha = PROJECT_SOURCE_CECHA.get();
        Set<String> paths = cecha.getClass2PathMap().get(className);
        if (CollUtil.isNotEmpty(paths)) {
            for (String path : paths) {
                from = cecha.getPath2TypeMap().get(path);
                if (from != null) {
                    all.addAll(from);
                }
            }

            if (!all.isEmpty()) {
                return all;
            }
        }

        cecha = getCecha();
        paths = cecha.getClass2PathMap().get(className);
        if (CollUtil.isNotEmpty(paths)) {
            for (String path : paths) {
                from = cecha.getPath2TypeMap().get(path);
                if (from != null) {
                    all.addAll(from);
                }
            }

            if (!all.isEmpty()) {
                return all;
            }
        }

        return Sets.newHashSet(FROM_PUBLIC);
    }

    public Cecha getCecha() {
        Cecha cecha = projectCechaMap.get(project);
        if (cecha == null) {
            initMap();
        }
        cecha = projectCechaMap.get(project);

        if (cecha == null) {
            throw new RuntimeException("无法初始化读取依赖库，请检查.");
        }
        return cecha;
    }


    @Override
    public void setMessageDispatcher(MessageDispatcher messageDispatcher) {
    }

    @Override
    public void init() {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void beginProcessing(String s) {
    }

    @Override
    public void finishProcessing() {
    }

    @Override
    public void configure(Configuration configuration) throws CheckstyleException {
    }

    @Override
    public void contextualize(Context context) throws CheckstyleException {
    }

    public void fireErrors(String fileName) {
        FileContext fileContext = (FileContext) this.context.get();
        SortedSet<Violation> errors = new TreeSet(fileContext.violations);
        fileContext.violations.clear();
        this.messageDispatcher.fireErrors(fileName, errors);
    }


    public void log(int line, String key, Object... args) {
        ((FileContext) this.context.get()).violations.add(new Violation(line, this.getMessageBundle(), key, args,
                this.getSeverityLevel(), this.getId(), this.getClass(), (String) this.getCustomMessages().get(key)));
    }

    public void log(int lineNo, int colNo, String key, Object... args) {
        FileContext fileContext = (FileContext) this.context.get();
        int col = 1 + CommonUtil.lengthExpandedTabs(fileContext.fileContents.getLine(lineNo - 1), colNo, this.tabWidth);
        fileContext.violations.add(new Violation(lineNo, col, this.getMessageBundle(), key, args,
                this.getSeverityLevel(), this.getId(), this.getClass(), (String) this.getCustomMessages().get(key)));
    }

    public void setFileExtensions(String... extensions) {
        if (extensions == null) {
            throw new IllegalArgumentException("Extensions array can not be null");
        } else {
            this.fileExtensions = new String[extensions.length];

            for (int i = 0; i < extensions.length; ++i) {
                String extension = extensions[i];
                if (CommonUtil.startsWithChar(extension, '.')) {
                    this.fileExtensions[i] = extension;
                } else {
                    this.fileExtensions[i] = "." + extension;
                }
            }

        }
    }

    public Map<String, String> getCustomMessages() {
        return this.getConfiguration().getMessages();
    }

    public String getMessageBundle() {
        String className = this.getClass().getName();
        return getMessageBundle(className);
    }

    public static String getMessageBundle(String className) {
        int endIndex = className.lastIndexOf(46);
        String messages = "messages";
        String messageBundle;
        if (endIndex < 0) {
            messageBundle = "messages";
        } else {
            String packageName = className.substring(0, endIndex);
            messageBundle = packageName + "." + "messages";
        }

        return messageBundle;
    }

    @Data
    public static class FileContext {
        private final SortedSet<Violation> violations = new TreeSet();
        private FileContents fileContents;

        private FileContext() {
        }
    }
}

