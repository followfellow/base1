package com.demo.cache.district;

import com.demo.base.districtManager.dto.DistrictDTO;
import com.demo.base.districtManager.cache.FindDistrictCacheParam;
import com.demo.base.districtManager.dto.DistrictDTO;
import com.demo.dbutils.BaseDAOHibernateImpl;
import net.logstash.logback.encoder.org.apache.commons.lang.StringEscapeUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author kj
 * @date 2021/8/23 18:50
 */
@Repository
public class DistrictRedisDaoImpl  extends BaseDAOHibernateImpl implements DistrictRedisDao{
    @Override
    public List<DistrictDTO> findDistrictList(FindDistrictCacheParam findDistrictCacheParam) {
        String sql = " SELECT districtId,districtName,districtChar,certificateNo,cityId from pub_district_t where 1 = 1 ";
        if (findDistrictCacheParam != null) {
            if (findDistrictCacheParam.getDistrictId()!= null) {
                sql += " and districtId = '" + StringEscapeUtils.escapeSql(findDistrictCacheParam.getDistrictId().toString()) + "' ";
            }
        }

        sql += " order by districtId ";
        return super.findObjectBySql(sql, DistrictDTO.class);
    }
}
