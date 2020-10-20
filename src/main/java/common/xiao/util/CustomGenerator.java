package common.xiao.util;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DbType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

public class CustomGenerator {
	// 保存的文件目录
	static String outputDir = "d://test//";
	// 此处可以修改为您的表前缀
	static String[] tablePrefix = new String[] { "a_", "t_", "w_" };

	// 需要生成的表
	static String[] includeTables = new String[] { "tp_plan_emergency_content", "tp_plan_emergency_personliable" };
	// 基础包名
	static String parentPackage = "com.linkcm.opp";
	// 包下面的模块名称
	static String moduleName = "planmanagement";
	// 代码生成者
	static String author = "Salmon Ou";

	public static void main(String[] args) throws Exception {
		createCode();
	}

	private static void createCode() throws IOException {

		AutoGenerator mpg = new AutoGenerator();

		// 全局配置
		GlobalConfig gc = new GlobalConfig();
		gc.setOutputDir(outputDir);
		gc.setFileOverride(true);// 覆盖之前生成的代码
		gc.setActiveRecord(true);// 数据对象Object到关系数据库的映射
		gc.setEnableCache(false);// XML 二级缓存
		gc.setBaseResultMap(true);// XML ResultMap
		gc.setBaseColumnList(true);// XML columList
		gc.setAuthor(author);// 作者

		// 自定义文件命名，注意 %s 会自动填充表实体属性！
		gc.setMapperName("%sMapper");
		gc.setXmlName("%sMapper");
		gc.setServiceName("I%sService");
		gc.setServiceImplName("%sServiceImpl");
		gc.setControllerName("%sController");
		mpg.setGlobalConfig(gc);

		// 数据源配置
		DataSourceConfig dsc = new DataSourceConfig();
		dsc.setDbType(DbType.POSTGRE_SQL);
		/*
		 * dsc.setTypeConvert(new MySqlTypeConvert(){ // 自定义数据库表字段类型转换【可选】
		 * 
		 * @Override public DbColumnType processTypeConvert(String fieldType) {
		 * System.out.println("转换类型：" + fieldType); return
		 * super.processTypeConvert(fieldType); } });
		 */
		Properties prop = PropertiesLoaderUtils.loadAllProperties("spring.properties");
		dsc.setDriverName(prop.getProperty("jdbc.driver"));
		dsc.setUrl(prop.getProperty("jdbc.url"));
		dsc.setUsername(prop.getProperty("jdbc.username"));
		dsc.setPassword(prop.getProperty("jdbc.password"));
		mpg.setDataSource(dsc);

		// 策略配置
		StrategyConfig strategy = new StrategyConfig();
		// strategy.setCapitalMode(true);// 全局大写命名 ORACLE 注意
		strategy.setTablePrefix(tablePrefix);// 此处可以修改为您的表前缀
		strategy.setNaming(NamingStrategy.underline_to_camel);// 表名生成策略
		strategy.setInclude(includeTables); // 需要生成的表
		// strategy.setExclude(new String[]{"test"}); // 排除生成的表
		mpg.setStrategy(strategy);

		// 包配置
		PackageConfig pc = new PackageConfig();

		pc.setParent(parentPackage);

		pc.setModuleName(moduleName);
		mpg.setPackageInfo(pc);

		// 执行生成
		mpg.execute();
		
		List<TableInfo> tableInfoList = mpg.getConfig().getTableInfoList();
		for(TableInfo table: tableInfoList){
			System.out.println(table.getName());
			System.out.println(ToStringBuilder.reflectionToString(table));
			List<TableField> fields = table.getFields();
			for(TableField tf: fields ){
				System.out.println(ToStringBuilder.reflectionToString(tf));
			}
		}
	}

}