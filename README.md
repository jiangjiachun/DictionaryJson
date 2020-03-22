# DictionaryJson
数据字典excel表格转Json
访问地址：http://127.0.0.1

## 测试

运行测试类[DictionaryExcelHolderTest](https://github.com/jiangjiachun/DictionaryJson/blob/master/src/test/java/com/dictionary/config/DictionaryExcelHolderTest.java)生成static/dictionary.js在src下。

## 运行

[analyze()](https://github.com/jiangjiachun/DictionaryJson/blob/master/src/main/java/com/dictionary/config/DictionaryExcelHolder.java)随应用启动。

## Excel格式

1. 一个Sheet表示一个字典数据，Sheet中文名方便查看。
2. 表格第一行转换成json后的变量英文名，保持唯一。
3. 表格第二行json的key。
4. 表格第三行开始对应的字典数据。

## Js插件

Demo：[index.html](https://github.com/jiangjiachun/DictionaryJson/blob/master/src/main/resources/static/index.html)

