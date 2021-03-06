package com.demo.base.districtManager.dao;

import com.demo.base.districtManager.dto.DistrictDTO;
import com.demo.base.districtManager.request.FindDistrictParam;
import com.demo.contants.Constants;
import com.demo.dbutils.BaseDAOHibernateImpl;
import com.demo.utils.StringUtils;
import net.logstash.logback.encoder.org.apache.commons.lang.StringEscapeUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author kj
 * @date 2021/8/13 14:43
 */
@Repository(Constants.MYSQL + "DistrictDao")
public class DistrictDaoImpl extends BaseDAOHibernateImpl implements DistrictDao {
    @Override
    public List<DistrictDTO> findDistrictList(FindDistrictParam findDistrictParam) {
        String sql = "select districtId,districtName,districtChar,cityId,certificateNo from pub_district_t where 1 = 1";
        if (findDistrictParam != null) {
            if (!StringUtils.isEmpty(findDistrictParam.getCityId())) {
                sql += " and cityId = '" + findDistrictParam.getCityId() + "' ";
            }
        }
        return findObjectBySql(sql, DistrictDTO.class);
    }

    @Override
    public List<DistrictDTO> finddistrictByName(String districtName, Long districtId) {
        String sql = " select  districtId from  pub_district_t  where districtName = '" + StringEscapeUtils.escapeSql(districtName) + "' ";
        if (districtId != null) {
            sql += " and districtId <> " + districtId;
        }
//        sql += " and businessId = " + getCurrUserOrgId();
        sql += " limit 1  ";
        return findObjectBySql(sql, DistrictDTO.class);
    }

    @Override
    public List<DistrictDTO> findDistrictSelect(FindDistrictParam findDistrictParam) {
        String sql = "select districtId,districtName from pub_district_t where 1 = 1";
        if (findDistrictParam != null) {
            if (!StringUtils.isEmpty(findDistrictParam.getCityId())) {
                sql += " and cityId = '" + findDistrictParam.getCityId() + "' ";
            }
        }
        return findObjectBySql(sql, DistrictDTO.class);
    }
}
