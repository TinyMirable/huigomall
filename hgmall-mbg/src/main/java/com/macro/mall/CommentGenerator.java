package com.macro.mall;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.DefaultCommentGenerator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * MyBatis Generator 自定义注释生成器
 * 用于生成中文注释
 */
public class CommentGenerator extends DefaultCommentGenerator {
    private Properties properties;
    private Properties systemProperties;
    private boolean suppressDate;
    private boolean suppressAllComments;
    private String currentDateStr;

    public CommentGenerator() {
        super();
        properties = new Properties();
        systemProperties = System.getProperties();
        suppressDate = false;
        suppressAllComments = false;
        currentDateStr = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
    }

    /**
     * 为类添加注释
     */
    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            return;
        }

        topLevelClass.addJavaDocLine("/**");
        String remarks = introspectedTable.getRemarks();
        if (remarks != null && remarks.length() > 0) {
            topLevelClass.addJavaDocLine(" * " + remarks);
            topLevelClass.addJavaDocLine(" *");
        }
        topLevelClass.addJavaDocLine(" * " + introspectedTable.getFullyQualifiedTable().toString());
        topLevelClass.addJavaDocLine(" *");
        topLevelClass.addJavaDocLine(" * @author MyBatis Generator");
        if (!suppressDate) {
            topLevelClass.addJavaDocLine(" * @date " + currentDateStr);
        }
        topLevelClass.addJavaDocLine(" */");
    }

    /**
     * 为字段添加注释
     */
    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable,
                                 IntrospectedColumn introspectedColumn) {
        if (suppressAllComments) {
            return;
        }

        field.addJavaDocLine("/**");
        String remarks = introspectedColumn.getRemarks();
        if (remarks != null && remarks.length() > 0) {
            field.addJavaDocLine(" * " + remarks);
        } else {
            field.addJavaDocLine(" * " + introspectedColumn.getActualColumnName());
        }
        field.addJavaDocLine(" */");
    }

    /**
     * 为getter方法添加注释
     */
    @Override
    public void addGetterComment(Method method, IntrospectedTable introspectedTable,
                                  IntrospectedColumn introspectedColumn) {
        if (suppressAllComments) {
            return;
        }

        method.addJavaDocLine("/**");
        String remarks = introspectedColumn.getRemarks();
        if (remarks != null && remarks.length() > 0) {
            method.addJavaDocLine(" * 获取 " + remarks);
        } else {
            method.addJavaDocLine(" * 获取 " + introspectedColumn.getActualColumnName());
        }
        method.addJavaDocLine(" *");
        method.addJavaDocLine(" * @return " + (remarks != null && remarks.length() > 0 ? remarks : introspectedColumn.getActualColumnName()));
        method.addJavaDocLine(" */");
    }

    /**
     * 为setter方法添加注释
     */
    @Override
    public void addSetterComment(Method method, IntrospectedTable introspectedTable,
                                  IntrospectedColumn introspectedColumn) {
        if (suppressAllComments) {
            return;
        }

        method.addJavaDocLine("/**");
        String remarks = introspectedColumn.getRemarks();
        if (remarks != null && remarks.length() > 0) {
            method.addJavaDocLine(" * 设置 " + remarks);
        } else {
            method.addJavaDocLine(" * 设置 " + introspectedColumn.getActualColumnName());
        }
        method.addJavaDocLine(" *");
        FullyQualifiedJavaType parameterType = method.getParameters().get(0).getType();
        method.addJavaDocLine(" * @param " + method.getParameters().get(0).getName() + " " + 
                             (remarks != null && remarks.length() > 0 ? remarks : introspectedColumn.getActualColumnName()));
        method.addJavaDocLine(" */");
    }
}
