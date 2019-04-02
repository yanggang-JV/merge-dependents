## 操作步骤：
### 使用方式一:

1. 在自己的gradle工程中执行 dependencies 命令，会在控制台输出内容。
2. 将控制台输出的内容，以记事本保存起来存放到merge-dependents工程的dependents目录中。
3. 执行AppMain方法，（此步骤可能较慢，需要循环到maven中央仓库爬数据）。
4. 查看output输出结果。

注: 如果发现not found请手动核对。

### 使用方式二:
1. 将您的build.gradle文件放入文件夹buildGradleFile中。
2. 执行AppMain1方法 （此步骤可能较慢，需要循环到maven中央仓库爬数据）。
3. 查看output输出结果。

注: 默认生成的文件名为: dependents.txt & license-dependents.txt

由于部分依赖可能没有pom文件，并且支持任意build.gradle文件，所以暂不支持本地获取license