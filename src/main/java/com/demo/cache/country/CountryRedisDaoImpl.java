package com.demo.cache.country;

import com.demo.base.countryManager.cache.FindCountryCacheParam;
import com.demo.base.countryManager.dto.CountryDTO;
import com.demo.dbutils.BaseDAOHibernateImpl;
import com.demo.utils.StringUtils;
import net.logstash.logback.encoder.org.apache.commons.lang.ObjectUtils;
import net.logstash.logback.encoder.org.apache.commons.lang.StringEscapeUtils;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.Null;
import java.util.List;

/**
 * @author kj
 * @date 2021/8/19 11:01
 */
@Repository
public class CountryRedisDaoImpl extends BaseDAOHibernateImpl implements CountryRedisDao {
    @Override
    public List<CountryDTO> findCountryList(FindCountryCacheParam findCountryCacheParam) {
        String sql = " SELECT countryId,countryName,countryChar,threeBitCode,twoBitCode from pub_country_t where 1 = 1 ";
        if (findCountryCacheParam != null) {
            if (findCountryCacheParam.getCountryId()!= null) {
                sql += " and countryId = '" + StringEscapeUtils.escapeSql(findCountryCacheParam.getCountryId().toString()) + "' ";
            }
        }

        sql += " order by countryId ";
        return super.findObjectBySql(sql, CountryDTO.class);
    }
}
