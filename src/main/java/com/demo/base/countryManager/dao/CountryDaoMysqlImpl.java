package com.demo.base.countryManager.dao;

import com.demo.action.vo.QueryPage;
import com.demo.base.countryManager.dto.CountryDTO;
import com.demo.base.countryManager.request.FindCountryParam;
import com.demo.contants.Constants;
import com.demo.dbutils.BaseDAOHibernateImpl;
import com.demo.utils.StringUtils;
import net.logstash.logback.encoder.org.apache.commons.lang.StringEscapeUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 国家DaoMysqlImpl
 *
 * @author kj
 * @date 2021/8/10 16:16
 */
@Repository(Constants.MYSQL + "CountryDao")
public class CountryDaoMysqlImpl extends BaseDAOHibernateImpl implements CountryDao {

    /*
     * 查询国家列表
     * @author kj
     * @date 2021/8/12 11:11
     * @param [findCountryParam]
     * @return java.util.List<com.demo.base.countryManager.dto.CountryDTO>
     */
    @Override
    public List<CountryDTO> findCountryList(FindCountryParam findCountryParam, QueryPage queryPage) {
        String sql = "select countryId,countryName,countryChar,threeBitCode,twoBitCode,updatedDate,updatedUser from pub_country_t where 1 = 1";
        if (findCountryParam != null) {
            if (!StringUtils.isEmpty(findCountryParam.getCountryId())) {
                sql += " and countryId = '" + findCountryParam.getCountryId() + "' ";
            }
        }
        return findObjectBySql(sql, CountryDTO.class,queryPage);
    }

    /*
     * 
     * @author kj
     * @date 2021/9/2 11:10
     * @param []
     * @return java.util.List<com.demo.base.countryManager.dto.CountryDTO>
     */
    @Override
    public List<CountryDTO> findCountrySelect(){
        String sql = "select countryId,countryName from pub_country_t where 1 = 1";
        return findObjectBySql(sql, CountryDTO.class);
    }

    /*
     * 通过Id查询国家
     * @author kj
     * @date 2021/8/12 11:11
     * @param [countryId]
     * @return java.util.List<com.demo.base.countryManager.dto.CountryDTO>
     */
    @Override
    public List<CountryDTO> findUserByCountryId(Long countryId) {
//        String sql = " select t2.roleId,t2.roleName from sys_role_join_user_t t1 " +
//                " left join sys_role_t t2 on t1.roleId = t2.roleId " +
//                " where t1.userId  = " + countryId;
//        return findObjectBySql(sql, CountryDTO.class);
        return null;
    }

    /*
     * 通过国家名称查找国家
     * @author kj
     * @date 2021/8/12 11:11
     * @param [countryName, countryId]
     * @return java.util.List<com.demo.base.countryManager.dto.CountryDTO>
     */
    @Override
    public List<CountryDTO> findCountryByName(String countryName, Long countryId) {
        String sql = " select  countryId from  pub_country_t  where countryName = '" + StringEscapeUtils.escapeSql(countryName) + "' ";
        if (countryId != null) {
            sql += " and countryId <> " + countryId;
        }
//        sql += " and businessId = " + getCurrUserOrgId();
        sql += " limit 1  ";
        return findObjectBySql(sql, CountryDTO.class);
    }
}
