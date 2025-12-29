package com.macro.mall;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * MyBatis Generator 代码生成器
 * 运行此类的main方法即可生成代码
 */
public class Generator {
    public static void main(String[] args) {
        try {
            System.out.println("================== MyBatis Generator 开始生成代码 ==================");
            
            // 警告信息列表
            List<String> warnings = new ArrayList<>();
            // 是否覆盖已存在的文件
            boolean overwrite = true;
            
            // 获取配置文件输入流
            InputStream configFile = Generator.class.getClassLoader()
                    .getResourceAsStream("generatorConfig.xml");
            
            if (configFile == null) {
                System.err.println("错误：无法找到配置文件 generatorConfig.xml");
                return;
            }
            
            // 解析配置文件
            ConfigurationParser cp = new ConfigurationParser(warnings);
            Configuration config = cp.parseConfiguration(configFile);
            configFile.close();
            
            // 创建回调对象
            DefaultShellCallback callback = new DefaultShellCallback(overwrite);
            
            // 创建生成器
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
            
            // 执行生成
            myBatisGenerator.generate(null);
            
            // 输出警告信息
            if (!warnings.isEmpty()) {
                System.out.println("\n警告信息：");
                for (String warning : warnings) {
                    System.out.println("  - " + warning);
                }
            }
            
            System.out.println("\n================== MyBatis Generator 代码生成完成 ==================");
        } catch (IOException e) {
            System.err.println("IO异常：" + e.getMessage());
            e.printStackTrace();
        } catch (XMLParserException e) {
            System.err.println("XML解析异常：" + e.getMessage());
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            System.err.println("配置无效异常：" + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("SQL异常：" + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("线程中断异常：" + e.getMessage());
            e.printStackTrace();
        }
    }
}

