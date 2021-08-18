package com.demo.base;

import cn.hutool.core.date.DateUtil;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 生成一个模块包的工具类
 *
 * @author:wxc
 * @date:2021/7/23 13:28
 */
public class GeneratorPackge {
    //只用配置下面三个参数
    //包根目录路径
    String root = "src/main/java/com/demo/base" + "/";
    //包上一级目录名称
    String rootName = "base";
    private String tableName = "sys_juris_group_t";
    //实体类名大写
    String entityName = "JurisGroup";
    //实体类名大写
    String chineseName = "JurisGroup";

    @Test
    public void doCreate() throws IOException {
     


        String packageName = changeFirstCharacterCase(entityName, false) + "Manager";
        String[] subPackageNames = {"action", "po", "dao", "service", "dto"};
        //创建文件夹
        File file = new File(root + packageName);
        if (!file.exists()) {
            file.mkdir();
        }
        for (String subPackageName : subPackageNames) {
            file = new File(root + packageName + "/" + subPackageName);
            if (!file.exists()) {
                file.mkdir();
            }
            if ("action".equals(subPackageName)) {
                createActionFile(rootName, entityName, file.getAbsolutePath(), packageName);
            }
            if ("po".equals(subPackageName)) {
                createPoFile(rootName, entityName, file.getAbsolutePath(), packageName);
            }
            if ("dao".equals(subPackageName)) {
                createDaoFile(rootName, entityName, file.getAbsolutePath(), packageName);
            }
            if ("service".equals(subPackageName)) {
                createServiceFile(rootName, entityName, file.getAbsolutePath(), packageName);
            }
            if ("dto".equals(subPackageName)) {
                createDtoFile(rootName, entityName, file.getAbsolutePath(), packageName);
            }

        }


    }


    public void createActionFile(String root, String entityName, String path, String packageName) throws IOException {
        String lowerCase = changeFirstCharacterCase(entityName, false);
        BufferedWriter bw = new BufferedWriter(new FileWriter(path + "/" + entityName + "Action.java"));
        String sb = "package com.demo." + root + "." + packageName + ".action;\n" +
                "\n" +
                "\n" +
                "import cn.hutool.core.collection.CollectionUtil;;\n" +
                "import com.demo.action.BaseAction;\n" +
                "import com.demo.action.result.ResultCode;\n" +
                "import com.demo.action.vo.QueryPage;\n" +
                "import com.demo.aop.CommonBusiness;\n" +
                "import com.demo." + root + "." + packageName + ".dto." + entityName + "DTO;\n" +
                "import com.demo." + root + "." + packageName + ".po." + entityName + "DO;\n" +
                "import com.demo." + root + "." + packageName + ".service." + entityName + "Service;\n" +
                "import com.demo.contants.CodeConstants;\n" +
                "import com.demo.contants.Constants;\n" +
                "import com.demo.contants.NumberMachineConstants;\n" +
                "import com.demo.utils.PropertyUtils;\n" +
                "import com.demo.utils.StringUtils;\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.security.crypto.password.PasswordEncoder;\n" +
                "import org.springframework.web.bind.annotation.RequestBody;\n" +
                "import org.springframework.web.bind.annotation.RequestMapping;\n" +
                "import org.springframework.web.bind.annotation.RestController;\n" +
                "\n" +
                "\n" +
                "import java.util.List;\n" +
                "\n" +
                "\n" +
                "/**\n" +
                " * " + entityName + "Action\n" +
                " *\n" +
                " * @author:wxc\n" +
                " * @date:" + DateUtil.now() + "\n" +
                " */" +
                "\n" +
                "\n" +
                "@RestController\n" +
                "@RequestMapping(\"" + lowerCase + "Action\")\n" +
                "public class " + entityName + "Action extends BaseAction {\n" +
                "\n" +
                "    @Autowired\n" +
                "    private " + entityName + "Service " + lowerCase + "Service;\n" +
                "    @Autowired\n" +
                "    private PasswordEncoder passwordEncoder;\n" +
                "\n" +
                "\n" +
                "    /**\n" +
                "     * 查询"+chineseName+"列表\n" +
                "     *\n" +
                "     * @param " + lowerCase + "DTO\n" +
                "     * @author wxc\n" +
                "     * @date " + DateUtil.now() + "\n" +
                "     */" +
                "\n" +
                "    @RequestMapping(\"find" + entityName + "List\")\n" +
                "    @CommonBusiness(logRemark = \"查询"+chineseName+"列表\")\n" +
                "    public Object find" + entityName + "List(@RequestBody(required = false) " + entityName + "DTO " + lowerCase + "DTO) {\n" +
                "\n" +
                "        if (" + lowerCase + "DTO == null) {\n" +
                "            return returnFail(ResultCode.AUTH_PARAM_ERROR, \"\");\n" +
                "        }\n" +
                "        QueryPage queryPage = initQueryPage(" + lowerCase + "DTO);\n" +
                "        List<" + entityName + "DTO> " + lowerCase + "List = " + lowerCase + "Service.find" + entityName + "List(" + lowerCase + "DTO, queryPage);\n" +
                "        if (CollectionUtil.isNotEmpty(" + lowerCase + "List)) {\n" +
                "        }\n" +
                "        return returnSuccessListByPage(" + lowerCase + "List, queryPage);\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "\n" +
                "    /**\n" +
                "     * 通过id查询"+chineseName+"\n" +
                "     *\n" +
                "     * @param " + lowerCase + "DTO\n" +
                "     * @author wxc\n" +
                "     * @date " + DateUtil.now() + "\n" +
                "     */" +
                "\n" +
                "    @RequestMapping(\"query" + entityName + "ById\")\n" +
                "    @CommonBusiness(logRemark = \"根据id查询"+chineseName+"\")\n" +
                "    public Object query" + entityName + "ById(@RequestBody(required = false) " + entityName + "DTO " + lowerCase + "DTO) {\n" +
                "        if (" + lowerCase + "DTO == null || " + lowerCase + "DTO.get" + entityName + "Id() == null) {\n" +
                "            return returnFail(ResultCode.AUTH_PARAM_ERROR, \"请选择查询"+chineseName+"!\");\n" +
                "        }\n" +
                "        return returnSuccess(\"查询"+chineseName+"成功!\", " + lowerCase + "Service.query" + entityName + "ById(" + lowerCase + "DTO.get" + entityName + "Id()));\n" +
                "    }" +
                "\n" +
                "\n" +
                "\n" +
                "    /**\n" +
                "     * 添加"+chineseName+"\n" +
                "     *\n" +
                "     * @param " + lowerCase + "DTO\n" +
                "     * @author wxc\n" +
                "     * @date " + DateUtil.now() + "\n" +
                "     */" +
                "\n" +
                "    @RequestMapping(\"add" + entityName + "\")\n" +
                "    @CommonBusiness(logRemark = \"添加"+chineseName+"\")\n" +
                "    public Object add" + entityName + "(@RequestBody(required = false) " + entityName + "DTO " + lowerCase + "DTO) {\n" +
                "        String resultError = checkAddOrUpdate" + entityName + "(" + lowerCase + "DTO, CodeConstants.COMMON_ONE);\n" +
                "        if (!StringUtils.isEmpty(resultError)) {\n" +
                "            return returnFail(ResultCode.AUTH_PARAM_ERROR, resultError);\n" +
                "        }\n" +
                "        " + entityName + "DO " + lowerCase + "DO = new " + entityName + "DO();\n" +
                "        PropertyUtils.copyNotNullProperties(" + lowerCase + "DO, " + lowerCase + "DTO);\n" +
                "        " + lowerCase + "Service.add" + entityName + "(" + lowerCase + "DO);\n" +
                "        return returnSuccess(\"添加"+chineseName+"成功!\");\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "    /**\n" +
                "     * 修改"+chineseName+"\n" +
                "     *\n" +
                "     * @param " + lowerCase + "DTO\n" +
                "     * @author wxc\n" +
                "     * @date " + DateUtil.now() + "\n" +
                "     */" +
                "\n" +
                "    @RequestMapping(\"update" + entityName + "\")\n" +
                "    @CommonBusiness(logRemark = \"修改"+chineseName+"\")\n" +
                "    public Object update" + entityName + "(@RequestBody(required = false) " + entityName + "DTO " + lowerCase + "DTO) {\n" +
                "        String resultError = checkAddOrUpdate" + entityName + "(" + lowerCase + "DTO, CodeConstants.COMMON_TWO);\n" +
                "        if (!StringUtils.isEmpty(resultError)) {\n" +
                "            return returnFail(ResultCode.AUTH_PARAM_ERROR, resultError);\n" +
                "        }\n" +
                "        " + entityName + "DO " + lowerCase + "DO = " + lowerCase + "Service.query" + entityName + "ById(" + lowerCase + "DTO.get" + entityName + "Id());\n" +
                "        if (" + lowerCase + "DO == null) {\n" +
                "            return returnFail(ResultCode.BIS_DATA_NO_EXIST, \"未查询到"+chineseName+"信息!\");\n" +
                "        }\n" +
                "        PropertyUtils.copyNotNullProperties(" + lowerCase + "DO, " + lowerCase + "DTO);\n" +
                "        " + lowerCase + "Service.update" + entityName + "(" + lowerCase + "DO);\n" +
                "        return returnSuccess(\"修改"+chineseName+"成功!\");\n" +
                "    }" +
                "\n" +
                "\n" +
                "  /**\n" +
                "     * 删除"+chineseName+"\n" +
                "     *\n" +
                "     * @param " + lowerCase + "DTO\n" +
                "     * @author wxc\n" +
                "     * @date " + DateUtil.now() + "\n" +
                "     */" +
                "\n" +
                "    @RequestMapping(\"delete" + entityName + "\")\n" +
                "    @CommonBusiness(logRemark = \"删除"+chineseName+"\")\n" +
                "    public Object delete" + entityName + "(@RequestBody(required = false) " + entityName + "DTO " + lowerCase + "DTO) {\n" +
                "        if (" + lowerCase + "DTO == null || " + lowerCase + "DTO.get" + entityName + "Id() == null) {\n" +
                "            return returnFail(ResultCode.AUTH_PARAM_ERROR, \"请选择删除"+chineseName+"\");\n" +
                "        }\n" +
                "        " + entityName + "DO " + lowerCase + "DO = " + lowerCase + "Service.query" + entityName + "ById(" + lowerCase + "DTO.get" + entityName + "Id());\n" +
                "        if (" + lowerCase + "DO == null) {\n" +
                "            return returnFail(ResultCode.BIS_DATA_NO_EXIST, \"未查询到"+chineseName+"信息!\");\n" +
                "        }\n" +
                "        " + lowerCase + "Service.delete" + entityName + "(" + lowerCase + "DTO.get" + entityName + "Id());\n" +
                "        return returnSuccess(\"删除"+chineseName+"成功!\");\n" +
                "    }" +
                "\n" +
                "\n" +
                "   /**\n" +
                "     * 校验新增或者修改数据\n" +
                "     *\n" +
                "     * @param " + lowerCase + "DTO\n" +
                "     * @param type\n" +
                "     * @author wxc\n" +
                "     * @date " + DateUtil.now() + "\n" +
                "     */" +
                "\n" +
                "    private String checkAddOrUpdate" + entityName + "(" + entityName + "DTO " + lowerCase + "DTO, String type) {\n" +
                "        if (" + lowerCase + "DTO == null) {\n" +
                "            return \"请输入"+chineseName+"信息!\";\n" +
                "        }\n" +
                "        //添加时独有验证\n" +
                "        if (CodeConstants.COMMON_ONE.equals(type)) {\n" +
                "            " + lowerCase + "DTO.set" + entityName + "Id(numberMachineUtils.getTableID(NumberMachineConstants.ORG_BUSINESS_TABLE_ID_SEQ));\n" +
                "        }\n" +
                "        //修改时独有验证\n" +
                "        if (CodeConstants.COMMON_TWO.equals(type)) {\n" +
                "        }\n" +
                "        return null;\n" +
                "    }" +
                "\n}";
        bw.write(sb);
        bw.close();

    }


    public void createServiceFile(String root, String entityName, String path, String packageName) throws IOException {
        String lowerCase = changeFirstCharacterCase(entityName, false);
        ;
        //service
        BufferedWriter bw = new BufferedWriter(new FileWriter(path + "/" + entityName + "Service.java"));
        String sb = "package com.demo." + root + "." + packageName + ".service;\n" +
                "\n" +
                "import com.demo.action.vo.QueryPage;\n" +
                "import com.demo." + root + "." + packageName + ".dto." + entityName + "DTO;\n" +
                "import com.demo." + root + "." + packageName + ".po." + entityName + "DO;\n" +
                "\n" +
                "\n" +
                "import java.util.List;\n" +
                "\n" +
                "\n" +
                "/**\n" +
                " * service\n" +
                " *\n" +
                " * @author:wxc\n" +
                " * @date:" + DateUtil.now() + "\n" +
                " */\n" +
                "public interface " + entityName + "Service {\n" +
                "\n" +
                "\n" +
                "    /**\n" +
                "     * 添加"+chineseName+"\n" +
                "     *\n" +
                "     * @param " + lowerCase + "DO\n" +
                "     * @author wxc\n" +
                "     * @date " + DateUtil.now() + "\n" +
                "     */\n" +
                "    void add" + entityName + "(" + entityName + "DO " + lowerCase + "DO);\n" +
                "\n" +
                "    /**\n" +
                "     * 通过id查询"+chineseName+"\n" +
                "     *\n" +
                "     * @param " + lowerCase + "Id\n" +
                "     * @author wxc\n" +
                "     * @date " + DateUtil.now() + "\n" +
                "     */\n" +
                "    " + entityName + "DO query" + entityName + "ById(Long " + lowerCase + "Id);\n" +
                "\n" +
                "    /**\n" +
                "     * 查询"+chineseName+"列表\n" +
                "     *\n" +
                "     * @param " + lowerCase + "DTO\n" +
                "     * @param queryPage\n" +
                "     * @author wxc\n" +
                "     * @date " + DateUtil.now() + "\n" +
                "     */\n" +
                "    List<" + entityName + "DTO> find" + entityName + "List(" + entityName + "DTO " + lowerCase + "DTO, QueryPage queryPage);\n" +
                "\n" +
                "    /**\n" +
                "     * 更新"+chineseName+"\n" +
                "     *\n" +
                "     * @param " + lowerCase + "DO\n" +
                "     * @author wxc\n" +
                "     * @date " + DateUtil.now() + "\n" +
                "     */\n" +
                "    void update" + entityName + "(" + entityName + "DO " + lowerCase + "DO);\n" +
                "\n" +
                "    /**\n" +
                "     * 删除"+chineseName+"\n" +
                "     *\n" +
                "     * @param " + lowerCase + "Id\n" +
                "     * @author wxc\n" +
                "     * @date " + DateUtil.now() + "\n" +
                "     */\n" +
                "    void delete" + entityName + "(Long " + lowerCase + "Id);\n" +
                "}\n";
        bw.write(sb);
        bw.close();
        //serviceImpl
        bw = new BufferedWriter(new FileWriter(path + "/" + entityName + "ServiceImpl.java"));
        sb = "package com.demo." + root + "." + packageName + ".service;\n" +
                "\n" +
                "import com.demo.action.service.BaseServiceImpl;\n" +
                "import com.demo.action.vo.QueryPage;\n" +
                "import com.demo." + root + "." + packageName + ".dao." + entityName + "Dao;\n" +
                "import com.demo." + root + "." + packageName + ".dto." + entityName + "DTO;\n" +
                "import com.demo." + root + "." + packageName + ".po." + entityName + "DO;\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.stereotype.Service;\n" +
                "\n" +
                "import javax.annotation.Resource;\n" +
                "import javax.transaction.Transactional;\n" +
                "import java.util.List;\n" +
                "\n" +
                "/**\n" +
                " * serviceImpl\n" +
                " *\n" +
                " * @author:wxc\n" +
                " * @date:" + DateUtil.now() + "\n" +
                " */\n" +
                "@Service(\"" + lowerCase + "Service\")\n" +
                "public class " + entityName + "ServiceImpl extends BaseServiceImpl implements " + entityName + "Service {\n" +
                "    @Resource(name = \"${sql}\" + \"" + entityName + "Dao\")\n" +
                "    private " + entityName + "Dao " + lowerCase + "Dao;\n" +
                "\n" +
                "    /**\n" +
                "     * 添加"+chineseName+"\n" +
                "     *\n" +
                "     * @param " + lowerCase + "DO\n" +
                "     * @author wxc\n" +
                "     * @date " + DateUtil.now() + "\n" +
                "     */\n" +
                "    @Override\n" +
                "    @Transactional(rollbackOn = Exception.class)\n" +
                "    public void add" + entityName + "(" + entityName + "DO " + lowerCase + "DO) {\n" +
                "        " + lowerCase + "Dao.save(" + lowerCase + "DO);\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 通过id查询"+chineseName+"\n" +
                "     *\n" +
                "     * @param " + lowerCase + "Id\n" +
                "     * @author wxc\n" +
                "     * @date " + DateUtil.now() + "\n" +
                "     */\n" +
                "    @Override\n" +
                "    public " + entityName + "DO query" + entityName + "ById(Long " + lowerCase + "Id) {\n" +
                "        return (" + entityName + "DO) " + lowerCase + "Dao.getEntityById(" + entityName + "DO.class, " + lowerCase + "Id);\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 查询"+chineseName+"列表\n" +
                "     *\n" +
                "     * @param " + lowerCase + "DTO\n" +
                "     * @param queryPage\n" +
                "     * @author wxc\n" +
                "     * @date " + DateUtil.now() + "\n" +
                "     */\n" +
                "    @Override\n" +
                "    public List<" + entityName + "DTO> find" + entityName + "List(" + entityName + "DTO " + lowerCase + "DTO, QueryPage queryPage) {\n" +
                "        return " + lowerCase + "Dao.find" + entityName + "List(" + lowerCase + "DTO, queryPage);\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 更新"+chineseName+"\n" +
                "     *\n" +
                "     * @param " + lowerCase + "DO\n" +
                "     * @author wxc\n" +
                "     * @date " + DateUtil.now() + "\n" +
                "     */\n" +
                "    @Override\n" +
                "    @Transactional(rollbackOn = Exception.class)\n" +
                "    public void update" + entityName + "(" + entityName + "DO " + lowerCase + "DO) {\n" +
                "        " + lowerCase + "Dao.update(" + lowerCase + "DO);\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 删除"+chineseName+"\n" +
                "     *\n" +
                "     * @param " + lowerCase + "Id\n" +
                "     * @author wxc\n" +
                "     * @date " + DateUtil.now() + "\n" +
                "     */\n" +
                "    @Override\n" +
                "    @Transactional(rollbackOn = Exception.class)\n" +
                "    public void delete" + entityName + "(Long " + lowerCase + "Id) {\n" +
                "        String sql = \"delete from " + tableName + "  where " + lowerCase + "Id = \" + " + lowerCase + "Id  ;\n" +
                "        " + lowerCase + "Dao.executeSql(sql);\n" +
                "    }\n" +
                "}\n";
        bw.write(sb);
        bw.close();
    }


    public void createDaoFile(String root, String entityName, String path, String packageName) throws IOException {
        String lowerCase = changeFirstCharacterCase(entityName, false);
        ;
        //dao
        BufferedWriter bw = new BufferedWriter(new FileWriter(path + "/" + entityName + "Dao.java"));
        String sb = "package com.demo." + root + "." + packageName + ".dao;\n" +
                "\n" +
                "import com.demo.action.vo.QueryPage;\n" +
                "import com.demo." + root + "." + packageName + ".dto." + entityName + "DTO;\n" +
                "import com.demo.dbutils.BaseDAO;\n" +
                "\n" +
                "import java.util.List;\n" +
                "\n" +
                "/**\n" +
                " * dao\n" +
                " *\n" +
                " * @author:wxc\n" +
                " * @date:" + DateUtil.now() + "\n" +
                " */\n" +
                "public interface " + entityName + "Dao extends BaseDAO {\n" +
                "    /**\n" +
                "     * 查询"+chineseName+"列表\n" +
                "     *\n" +
                "     * @param " + lowerCase + "DTO\n" +
                "     * @param queryPage\n" +
                "     * @author wxc\n" +
                "     * @date " + DateUtil.now() + "\n" +
                "     */\n" +
                "    List<" + entityName + "DTO> find" + entityName + "List(" + entityName + "DTO " + lowerCase + "DTO, QueryPage queryPage);\n" +
                "}\n";
        bw.write(sb);
        bw.close();
        //daoImpl
        bw = new BufferedWriter(new FileWriter(path + "/" + entityName + "DaoImpl.java"));
        sb = "package com.demo." + root + "." + packageName + ".dao;\n" +
                "\n" +
                "import com.demo.action.vo.QueryPage;\n" +
                "import com.demo." + root + "." + packageName + ".dto." + entityName + "DTO;\n" +
                "import com.demo.contants.Constants;\n" +
                "import com.demo.dbutils.BaseDAOHibernateImpl;\n" +
                "import org.springframework.stereotype.Repository;\n" +
                "\n" +
                "import java.util.List;\n" +
                "\n" +
                "/**\n" +
                " * daoImpl\n" +
                " *\n" +
                " * @author:wxc\n" +
                " * @date:" + DateUtil.now() + "\n" +
                " */\n" +
                "@Repository(Constants.MYSQL + \"" + entityName + "Dao\")\n" +
                "public class " + entityName + "DaoImpl extends BaseDAOHibernateImpl implements " + entityName + "Dao {\n" +
                "    /**\n" +
                "     * 查询"+chineseName+"列表\n" +
                "     *\n" +
                "     * @param " + lowerCase + "DTO\n" +
                "     * @param queryPage\n" +
                "     * @author wxc\n" +
                "     * @date " + DateUtil.now() + "\n" +
                "     */\n" +
                "    @Override\n" +
                "    public List<" + entityName + "DTO> find" + entityName + "List(" + entityName + "DTO " + lowerCase + "DTO, QueryPage queryPage) {\n" +
                "        String sql = \"select " + lowerCase + "Id \" +\n" +
                "                \" from " + tableName + "  where 1 = 1 \";\n" +
                "        if (" + lowerCase + "DTO.get" + entityName + "Id() != null) {\n" +
                "            sql += \" and " + lowerCase + "Id =  \" + " + lowerCase + "DTO.get" + entityName + "Id();\n" +
                "        }\n" +
                "        return findObjectBySql(sql, " + entityName + "DTO.class, queryPage);\n" +
                "    }\n" +
                "}\n";
        bw.write(sb);
        bw.close();


    }

    public void createPoFile(String root, String entityName, String path, String packageName) throws IOException {
        String lowerCase = changeFirstCharacterCase(entityName, false);
        ;
        BufferedWriter bw = new BufferedWriter(new FileWriter(path + "/" + entityName + "DO.java"));
        String sb = "package com.demo." + root + "." + packageName + ".po;\n" +
                "\n" +
                "import com.demo.dbutils.BaseApplicationDO;\n" +
                "import lombok.*;\n" +
                "import org.hibernate.annotations.DynamicInsert;\n" +
                "import org.hibernate.annotations.DynamicUpdate;\n" +
                "\n" +
                "import javax.persistence.Column;\n" +
                "import javax.persistence.Entity;\n" +
                "import javax.persistence.Id;\n" +
                "import javax.persistence.Table;\n" +
                "import java.util.Date;\n" +
                "\n" +
                "/**\n" +
                " * DO\n" +
                " *\n" +
                " * @author:wxc\n" +
                " * @date:" + DateUtil.now() + "\n" +
                " */\n" +
                "@Entity\n" +
                "@Table(name = \"" + tableName + "\")\n" +
                "@DynamicUpdate\n" +
                "@DynamicInsert\n" +
                "@Data\n" +
                "@Builder\n" +
                "@NoArgsConstructor\n" +
                "@AllArgsConstructor\n" +
                "@EqualsAndHashCode(callSuper=false)\n" +
                "public class " + entityName + "DO extends BaseApplicationDO {\n" +
                "    /**\n" +
                "     * ID\n" +
                "     */\n" +
                "    @Id\n" +
                "    @Column(name = \"" + lowerCase + "Id\")\n" +
                "    private Long " + lowerCase + "Id;" +
                "\n" +
                "\n" +
                "}\n";
        bw.write(sb);
        bw.close();
    }


    public void createDtoFile(String root, String entityName, String path, String packageName) throws IOException {
        String lowerCase = changeFirstCharacterCase(entityName, false);
        ;
        BufferedWriter bw = new BufferedWriter(new FileWriter(path + "/" + entityName + "DTO.java"));
        String sb = "package com.demo." + root + "." + packageName + ".dto;\n" +
                "\n" +
                "import com.demo.common.BaseParam.BasePageParam;\n" +
                "import lombok.*;\n" +
                "\n" +
                "import java.util.Date;\n" +
                "\n" +
                "/**\n" +
                " * dto\n" +
                " *\n" +
                " * @author:wxc\n" +
                " * @date:" + DateUtil.now() + "\n" +
                " */\n" +
                "@Data\n" +
                "@Builder\n" +
                "@NoArgsConstructor\n" +
                "@AllArgsConstructor\n" +
                "@EqualsAndHashCode(callSuper=false)\n" +
                "public class " + entityName + "DTO  extends BasePageParam {\n" +
                "    /**\n" +
                "     * ID\n" +
                "     */\n" +
                "    private Long " + lowerCase + "Id;" +
                "\n" +
                "\n" +
                "}\n";
        bw.write(sb);
        bw.close();
    }


    private String changeFirstCharacterCase(String str, boolean capitalize) {
        if (str == null || str.length() == 0) {
            return str;
        }
        StringBuilder buf = new StringBuilder(str.length());
        if (capitalize) {
            buf.append(Character.toUpperCase(str.charAt(0)));
        } else {
            buf.append(Character.toLowerCase(str.charAt(0)));
        }
        buf.append(str.substring(1));
        return buf.toString();
    }
}
