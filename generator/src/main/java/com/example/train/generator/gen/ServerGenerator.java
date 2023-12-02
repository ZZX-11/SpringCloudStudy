package com.example.train.generator.gen;
import com.example.train.generator.util.DbUtil;
import com.example.train.generator.util.Field;
import com.example.train.generator.util.FreemarkerUtil;
import freemarker.template.TemplateException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ServerGenerator {
    static boolean readOnly = false;
    static String vuePath = "admin/src/views/main/";
    static String serverPath = "[module]/src/main/java/com/example/train/[module]/";
    static String pomPath = "generator/pom.xml";
    static String module = "";
    static {
        new File(serverPath).mkdirs();
    }

    public static void main(String[] args) throws Exception {
        // 通过都pom，找到要操作的数据库的名字，以及相应生成器配置文件的路径。
        // 结果：src/main/resources/generator-config-member.xml，member数据库
        String generatorPath = getGeneratorPath();
        // 替换前缀为空，得到要操作的表的名字。 member
        module = generatorPath.replace("src/main/resources/generator-config-", "").replace(".xml", "");
        // 拼接好持久层生成器的位置。
        generatorPath = "generator/" + generatorPath;

        System.out.println("module: " + module);

        serverPath = serverPath.replace("[module]", module); //处理路径信息。

        System.out.println("servicePath: " + serverPath);
       // 读取table节点，找到要操作数据库里的哪个表
        Document document = new SAXReader().read(generatorPath);
        Node table = document.selectSingleNode("//table");
        Node tableName = table.selectSingleNode("@tableName");
        Node domainObjectName = table.selectSingleNode("@domainObjectName");
        // 找到要操作的表的名字和要生成的实体的名字
        System.out.println(tableName.getText() + "/" + domainObjectName.getText());
        // 示例：表名 jiawa_test
        // Domain = JiawaTest
        String Domain = domainObjectName.getText();
        // domain = jiawaTest
        String domain = Domain.substring(0, 1).toLowerCase() + Domain.substring(1);
        // do_main = jiawa-test  为controller做准备
        String do_main = tableName.getText().replaceAll("_", "-");

        // 为DbUtil设置数据源
        Node connectionURL = document.selectSingleNode("//@connectionURL");
        Node userId = document.selectSingleNode("//@userId");
        Node password = document.selectSingleNode("//@password");

        DbUtil.url = connectionURL.getText();
        DbUtil.user = userId.getText();
        DbUtil.password = password.getText();
        // 表中文名
        String tableNameCn = DbUtil.getTableComment(tableName.getText());
        List<Field> fieldList = DbUtil.getColumnByTableName(tableName.getText());
        Set<String> typeSet = getJavaTypes(fieldList);
        System.out.println(typeSet);
        buildJavaCodeAndVue(Domain,domain,do_main,tableNameCn,fieldList,typeSet);
    }


    private static String getGeneratorPath() throws DocumentException {
//      在pom下找到要操作哪个数据库，以及相应生成器配置文件的路径。
        SAXReader saxReader = new SAXReader();
        Map<String, String> map = new HashMap<String, String>();
        map.put("pom", "http://maven.apache.org/POM/4.0.0");
        saxReader.getDocumentFactory().setXPathNamespaceURIs(map);
        Document document = saxReader.read(pomPath);
        // 读取pom下的configurationFile这个标签，即<configurationFile> </configurationFile>，找到要生成的表是哪个
        Node node = document.selectSingleNode("//pom:configurationFile");
        System.out.println(node.getText());
        return node.getText();
    }
    private static void buildJavaCodeAndVue(String Domain, String domain, String do_main, String tableNameCn, List<Field> fieldList, Set<String> typeSet) throws TemplateException, IOException {
        // 组装参数
        Map<String, Object> param = new HashMap<>();
        param.put("module", module);
        param.put("Domain", Domain);
        param.put("domain", domain);
        param.put("do_main", do_main);
        param.put("tableNameCn", tableNameCn);
        param.put("fieldList", fieldList);
        param.put("typeSet", typeSet);
        param.put("readOnly", readOnly);
        System.out.println("组装参数：" + param);

        gen(Domain, param, "service", "service");
        gen(Domain, param, "controller/admin", "adminController");
        gen(Domain, param, "req", "saveReq");
        gen(Domain, param, "req", "queryReq");
        gen(Domain, param, "resp", "queryResp");
        genVue(do_main, param);
    }

    private static void gen(String Domain, Map<String, Object> param, String packageName, String target) throws IOException, TemplateException {
        FreemarkerUtil.initConfig(target + ".ftl");
        String toPath = serverPath + packageName + "/";
//        String toPath = "D:\\javaProject\\train\\generator\\src\\main\\java\\com\\example\\train\\generator\\test\\";
        new File(toPath).mkdirs();
        String Target = target.substring(0, 1).toUpperCase() + target.substring(1);
        String fileName = toPath + Domain + Target + ".java";
        System.out.println("开始生成：" + fileName);
        FreemarkerUtil.generator(fileName, param);
    }

    private static void genVue(String do_main, Map<String, Object> param) throws IOException, TemplateException {
        FreemarkerUtil.initConfig("vue.ftl");
        new File(vuePath + module).mkdirs();
        String fileName = vuePath + module + "/" + do_main + ".vue";
        System.out.println("开始生成：" + fileName);
        FreemarkerUtil.generator(fileName, param);
    }

    /**
     * 获取所有的Java类型，使用Set去重
     */
    private static Set<String> getJavaTypes(List<Field> fieldList) {
        Set<String> set = new HashSet<>();
        fieldList.stream().forEach(field -> set.add(field.getJavaType()));
        return set;
    }
}
