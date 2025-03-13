package com.zhiqiong.generator;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

public class MyGenerator {
    private static final String URL = "jdbc:mysql://192.168.159.128:3306/zhiqiong?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai";
    private static final String DriverName = "com.mysql.jdbc.Driver";
    private static final String Username = "root";
    private static final String Password = "123456";

    public static void main(String[] args) {
        AutoGenerator generator = new AutoGenerator();
        GlobalConfig config = new GlobalConfig();
        config.setOutputDir(System.getProperty("user.dir") + "/generator/");
        config.setAuthor("hk");
        config.setOpen(false);
        config.setSwagger2(true);
        config.setEntityName("%sEntity");
        config.setServiceName("%sService");
        config.setServiceImplName("%sServiceImpl");
        config.setDateType(DateType.ONLY_DATE);
        config.setFileOverride(true);
        generator.setGlobalConfig(config);

        //配置数据源
        DataSourceConfig dataSource = new DataSourceConfig();
        dataSource.setUrl(URL);
        dataSource.setDriverName(DriverName);
        dataSource.setUsername(Username);
        dataSource.setPassword(Password);
        dataSource.setTypeConvert(new MySqlTypeConvert());
        dataSource.setSchemaName("mybatis-plus");
        generator.setDataSource(dataSource);

        PackageConfig pc = new PackageConfig();
        //设置父目录
        pc.setParent("com.hk");
        //自定义entity实体类代码的生成位置
        pc.setEntity("entity");
        // 配置 controller 包名
        pc.setController("controller");
        // 配置 service 包名
        pc.setService("service");
        // 配置 service.impl 包名
        pc.setServiceImpl("service.impl");
        // 配置 mapper 包名
        pc.setMapper("mapper");
        generator.setPackageInfo(pc);

        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {

            }
        };
        generator.setCfg(cfg);


        // 如果模板引擎是 freemarker
//        String templatePath = "/templates/mapper.xml.ftl";
        // 如果模板引擎是 velocity
        //String templatePath = "/templates/mapper.xml.vm";

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig.setXml(null);
        generator.setTemplate(templateConfig);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        // 配置数据表与实体类名之间映射的策略
        strategy.setNaming(NamingStrategy.underline_to_camel);
        // 配置数据表的字段与实体类的属性名之间映射的策略
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        //公共字段
        strategy.setSuperEntityColumns(new String[]{ "create_time", "update_time"});
        // 配置 lombok 模式.实体类加lombok注解
        strategy.setEntityLombokModel(true);
        // 配置 rest 风格的控制器（@RestController）
        strategy.setRestControllerStyle(true);
        //配置同时生成多个表的实体代码，用逗号隔开
        strategy.setInclude("app","question","scoring_result","user","user_answer");
        // 配置驼峰转连字符
        strategy.setControllerMappingHyphenStyle(true);
        // 设置表的前缀，如果数据库表名有前缀，但是实体类不想有前缀，可设置前缀。例：表为t_users 生成的实体将为users
//        strategy.setTablePrefix("sys_");
        generator.setStrategy(strategy);
        generator.setTemplateEngine(new FreemarkerTemplateEngine());
        generator.execute();

    }


}
