package com.demo.base.districtManager.service;

import cn.hutool.core.collection.CollectionUtil;
import com.demo.action.service.BaseServiceImpl;
import com.demo.action.vo.QueryPage;
import com.demo.base.districtManager.dao.DistrictDao;
import com.demo.base.districtManager.dto.DistrictDTO;
import com.demo.base.districtManager.po.DistrictDO;
import com.demo.base.districtManager.request.FindDistrictParam;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;

/**
 * @author kj
 * @date 2021/8/13 14:44
 */
@Service
public class DistrictServiceImpl extends BaseServiceImpl implements DistrictService {

    @Resource(name = "${sql}" + "DistrictDao")
    private DistrictDao districtDao;

    @Override
    public List<DistrictDTO> findDistrictList(FindDistrictParam findDistrictParam, QueryPage queryPage) {
        return districtDao.findDistrictList(findDistrictParam,queryPage);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void addDistrict(DistrictDO districtDO) {
        districtDao.save(districtDO);
    }

    @Override
    public DistrictDO queryDistrictById(Long districtId) {
        return (DistrictDO) districtDao.getEntityById(DistrictDO.class, districtId);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void updateDistrict(DistrictDO districtDO) {
        districtDao.update(districtDO);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void deleteDistrict(Long districtId) {
        String sql = "delete from pub_district_t where districtId = " + districtId;
        districtDao.executeSql(sql);
    }

    @Override
    public boolean checkNameIfExist(String districtName, Long districtId) {
        return CollectionUtil.isNotEmpty(districtDao.finddistrictByName(districtName, districtId));
    }
}
