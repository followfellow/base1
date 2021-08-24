package com.demo.cache.province;

import com.demo.base.provinceManager.dto.ProvinceDTO;
import com.demo.base.provinceManager.cache.FindProvinceCacheParam;
import com.demo.base.provinceManager.dto.ProvinceDTO;
import com.demo.dbutils.BaseDAOHibernateImpl;
import net.logstash.logback.encoder.org.apache.commons.lang.StringEscapeUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author kj
 * @date 2021/8/23 18:44
 */
@Repository
public class ProvinceRedisDaoImpl extends BaseDAOHibernateImpl implements ProvinceRedisDao{
    @Override
    public List<ProvinceDTO> findProvinceList(FindProvinceCacheParam findProvinceCacheParam) {
        String sql = " SELECT provinceId,provinceName,provinceChar,certificateNo from pub_province_t where 1 = 1 ";
        if (findProvinceCacheParam != null) {
            if (findProvinceCacheParam.getProvinceId()!= null) {
                sql += " and provinceId = '" + StringEscapeUtils.escapeSql(findProvinceCacheParam.getProvinceId().toString()) + "' ";
            }
        }

        sql += " order by provinceId ";
        return super.findObjectBySql(sql, ProvinceDTO.class);
    }
}
