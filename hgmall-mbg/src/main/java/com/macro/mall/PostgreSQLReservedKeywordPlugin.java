package com.macro.mall;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.VisitableElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * PostgreSQL 保留关键字处理插件
 * 自动为 PostgreSQL 保留关键字表名添加双引号
 */
public class PostgreSQLReservedKeywordPlugin extends PluginAdapter {
    
    /**
     * PostgreSQL 保留关键字集合
     * 包含常见的保留关键字，特别是表名中可能用到的
     */
    private static final Set<String> RESERVED_KEYWORDS = new HashSet<>();
    
    static {
        // PostgreSQL 保留关键字
        RESERVED_KEYWORDS.add("order");
        RESERVED_KEYWORDS.add("user");
        RESERVED_KEYWORDS.add("group");
        RESERVED_KEYWORDS.add("select");
        RESERVED_KEYWORDS.add("table");
        RESERVED_KEYWORDS.add("index");
        RESERVED_KEYWORDS.add("view");
        RESERVED_KEYWORDS.add("column");
        RESERVED_KEYWORDS.add("key");
        RESERVED_KEYWORDS.add("value");
        RESERVED_KEYWORDS.add("type");
        RESERVED_KEYWORDS.add("default");
        RESERVED_KEYWORDS.add("primary");
        RESERVED_KEYWORDS.add("foreign");
        RESERVED_KEYWORDS.add("references");
        RESERVED_KEYWORDS.add("constraint");
        RESERVED_KEYWORDS.add("check");
        RESERVED_KEYWORDS.add("unique");
        RESERVED_KEYWORDS.add("not");
        RESERVED_KEYWORDS.add("null");
        RESERVED_KEYWORDS.add("true");
        RESERVED_KEYWORDS.add("false");
        RESERVED_KEYWORDS.add("case");
        RESERVED_KEYWORDS.add("when");
        RESERVED_KEYWORDS.add("then");
        RESERVED_KEYWORDS.add("else");
        RESERVED_KEYWORDS.add("end");
        RESERVED_KEYWORDS.add("as");
        RESERVED_KEYWORDS.add("from");
        RESERVED_KEYWORDS.add("where");
        RESERVED_KEYWORDS.add("join");
        RESERVED_KEYWORDS.add("inner");
        RESERVED_KEYWORDS.add("left");
        RESERVED_KEYWORDS.add("right");
        RESERVED_KEYWORDS.add("full");
        RESERVED_KEYWORDS.add("outer");
        RESERVED_KEYWORDS.add("on");
        RESERVED_KEYWORDS.add("union");
        RESERVED_KEYWORDS.add("intersect");
        RESERVED_KEYWORDS.add("except");
        RESERVED_KEYWORDS.add("distinct");
        RESERVED_KEYWORDS.add("all");
        RESERVED_KEYWORDS.add("having");
        RESERVED_KEYWORDS.add("limit");
        RESERVED_KEYWORDS.add("offset");
        RESERVED_KEYWORDS.add("by");
        RESERVED_KEYWORDS.add("asc");
        RESERVED_KEYWORDS.add("desc");
        RESERVED_KEYWORDS.add("insert");
        RESERVED_KEYWORDS.add("into");
        RESERVED_KEYWORDS.add("values");
        RESERVED_KEYWORDS.add("update");
        RESERVED_KEYWORDS.add("set");
        RESERVED_KEYWORDS.add("delete");
        RESERVED_KEYWORDS.add("create");
        RESERVED_KEYWORDS.add("drop");
        RESERVED_KEYWORDS.add("alter");
        RESERVED_KEYWORDS.add("grant");
        RESERVED_KEYWORDS.add("revoke");
        RESERVED_KEYWORDS.add("commit");
        RESERVED_KEYWORDS.add("rollback");
        RESERVED_KEYWORDS.add("transaction");
        RESERVED_KEYWORDS.add("begin");
        RESERVED_KEYWORDS.add("savepoint");
        RESERVED_KEYWORDS.add("release");
        RESERVED_KEYWORDS.add("lock");
        RESERVED_KEYWORDS.add("unlock");
    }
    
    /**
     * 检查表名是否为保留关键字
     */
    private boolean isReservedKeyword(String tableName) {
        if (tableName == null || tableName.isEmpty()) {
            return false;
        }
        // 转换为小写进行比较（PostgreSQL 关键字不区分大小写）
        return RESERVED_KEYWORDS.contains(tableName.toLowerCase());
    }
    
    /**
     * 为表名添加双引号（如果是保留关键字）
     */
    private String quoteTableName(String tableName) {
        if (isReservedKeyword(tableName)) {
            return "\"" + tableName + "\"";
        }
        return tableName;
    }
    
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }
    
    /**
     * 在生成 SQL Map 文档后处理
     * 替换所有 SQL 语句中的表名
     */
    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
        String quotedTableName = quoteTableName(tableName);
        
        // 如果表名需要加引号，则替换文档中的所有表名引用
        if (!tableName.equals(quotedTableName)) {
            replaceTableNameInDocument(document, tableName, quotedTableName);
        }
        
        return true;
    }
    
    /**
     * 递归替换文档中的表名
     */
    private void replaceTableNameInDocument(XmlElement element, String oldName, String newName) {
        List<VisitableElement> elements = element.getElements();
        for (int i = 0; i < elements.size(); i++) {
            VisitableElement obj = elements.get(i);
            if (obj instanceof TextElement) {
                TextElement textElement = (TextElement) obj;
                String content = textElement.getContent();
                String originalContent = content;
                
                // 转义特殊字符用于正则表达式
                String escapedOldName = oldName.replaceAll("([\\\\\\[\\]{}()*+?.^$|])", "\\\\$1");
                
                // 替换表名（使用正则表达式确保只替换完整的表名，而不是部分匹配）
                // 匹配模式：from tableName、insert into tableName、update tableName、delete from tableName
                content = content.replaceAll("(?i)\\bfrom\\s+" + escapedOldName + "\\b", "from " + newName);
                content = content.replaceAll("(?i)\\binsert\\s+into\\s+" + escapedOldName + "\\b", "insert into " + newName);
                content = content.replaceAll("(?i)\\bupdate\\s+" + escapedOldName + "\\b", "update " + newName);
                content = content.replaceAll("(?i)\\bdelete\\s+from\\s+" + escapedOldName + "\\b", "delete from " + newName);
                content = content.replaceAll("(?i)\\bcount\\(\\*\\)\\s+from\\s+" + escapedOldName + "\\b", "count(*) from " + newName);
                
                // 如果内容被修改，替换元素
                if (!content.equals(originalContent)) {
                    elements.set(i, new TextElement(content));
                }
            } else if (obj instanceof XmlElement) {
                replaceTableNameInDocument((XmlElement) obj, oldName, newName);
            }
        }
    }
    
    /**
     * 处理 XmlElement 中的表名替换
     */
    private void replaceTableNameInDocument(Document document, String oldName, String newName) {
        XmlElement rootElement = document.getRootElement();
        replaceTableNameInDocument(rootElement, oldName, newName);
    }
}

