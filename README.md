# idea_plugin_nc5devplugin
Yonyou NC5x or U8Cloud Idea devtool Plugin   
实现了 Idea NC5x+U8Cloud 开发插件，功能比较简单

# 使用方式
插件安装后使用方式:
```
第一次新建项目-必须步骤：
1. Tools -> 配置NC HOME   进行NC HOME配置！ 
2. 第一步保存后，如果没有选更新依赖，请在 Tools -> 更新NC 库依赖 执行依赖更新 
3. 第2步后，请在 Tools -> 生成默认NC运行配置 执行Idea的运行配置,注意 执行后请运行时候根据提示手工修改里面的modelu项目名 
4. 第4步后，会生成项目默认的几个文件夹和xml，请手工在 项目结构修改几个文件夹的IDEA属性为正确值！
```


# 常见问题
```
1. Intellij IDEA运行报Command line is too long解法 ： 
修改项目下 .idea\workspace.xml，
找到标签 <component name="PropertiesComponent"> ，
 在标签里加一行  <property name="dynamic.classpath" value="true" /> 
```

# 已知BUG


# 未来待实现功能
1. 导出补丁包