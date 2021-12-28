1. 清空本地 maven 仓库的 com 和 org 目录
2. DRC 所有携程相关的 maven 依赖放置在项目 mvn_repo 分支下
```
 git checkout mvn_repo
```
在 drc 目录下, 运行 sh install.sh, 自动将非公共 maven 仓库的依赖装载在本地 maven 系统中