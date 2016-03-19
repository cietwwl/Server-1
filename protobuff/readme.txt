protobuff生成工具备注
1.统一编辑protobuff/proto/目录下的.proto文件
2.运行文件:双击protobuff目录下的start.bat文件
3.需要修改protobuff/proto/startJava.bat文件中的变量myPath的值,把它设置成你服务端工程中对应的位置,如C:\Development\Server\FSApp\src\main\java
4.需要修改protobuff/proto/startCS.bat文件中的变量myPath的值,把它设置成你服务端工程中对应的位置,如C:\Development\Client\trunk\Unity3D\RXFS\Assets\Script\ProtobufClass\
5.如果添加了一个新的.proto文件,需要同时在startJava.bat,startCS.bat文件中添加对应的命令,格式就参考文件中已有的条目
