<html>
<head>
    <meta charset="UTF-8" />
    <title>我是一个模板</title>
</head>
<body>
<#include "header.ftl"/>

<#--我是注释-->

${name},你好. ${message}

<#assign linkman="周先生"/>
联系人:  ${linkman}

<#assign info={
   "mobile":"13900000000",
    "address":"湖北武汉"
}/>
电话: ${info.mobile}
地址: ${info.address}

<#if success=true>
    你已通过实名认证
    <#else>
        你未通过实名认证
</#if>

<hr>
<#list goodsList as goods>
    ${goods_index}
    名称: ${goods.name}
    价格: ${goods.price}<br>
</#list>
<hr>
共 ${goodsList?size} 条记录

<hr>

<#assign test="{'bank':'工商银行','account':'1245849112456'}"/>
<#assign data=test?eval/>
开户行: ${data.bank}
账号: ${data.account}
<hr>
当前时间:${today?date} <br>
当前时间:${today?time} <br>
当前时间日期:${today?datetime} <br>
日期格式化输出: ${today?string("yyyy年MM月dd日")} <br>
当前积分: ${point?c} <br>

<#if aaa??>
aaa值被定义:    ${aaa}
    <#else>
    aaa值未定义
</#if>
<br>

bbb的值 ${bbb!"0"}

<hr>

<#if bbb gt 90>
    bbb>90 满足
</#if>
</body>
</html>