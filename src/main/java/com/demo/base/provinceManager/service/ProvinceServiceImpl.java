package com.demo.base.provinceManager.service;

import cn.hutool.core.collection.CollectionUtil;
import com.demo.action.service.BaseServiceImpl;
import com.demo.action.vo.QueryPage;
import com.demo.base.provinceManager.dao.ProvinceDao;
import com.demo.base.provinceManager.dto.ProvinceDTO;
import com.demo.base.provinceManager.po.ProvinceDO;
import com.demo.base.provinceManager.request.FindProvinceParam;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;

/**
 * 省份serviceImpl
 *
 * @author:kj
 * @date:2021-08-20 16:54
 */
@Service
public class ProvinceServiceImpl extends BaseServiceImpl implements ProvinceService {
    @Resource
    private ProvinceDao provinceDao;

    /**
     * 添加省份
     *
     * @param provinceDO
     * @author kj
     * @date 2021-08-20 16:54
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void addProvince(ProvinceDO provinceDO) {
        provinceDao.save(provinceDO);
    }

    /**
     * 通过id查询省份
     *
     * @param ProvinceId
     * @author kj
     * @date 2021-08-20 16:54
     */
    @Override
    public ProvinceDO queryProvinceById(Long ProvinceId) {
        return (ProvinceDO) provinceDao.getEntityById(ProvinceDO.class, ProvinceId);
    }

    /**
     * 查询省份列表
     *
     * @param findProvinceParam
     * @param queryPage
     * @author kj
     * @date 2021-08-20 16:54
     */
    @Override
    public List<ProvinceDTO> findProvinceList(FindProvinceParam findProvinceParam, QueryPage queryPage) {
        return provinceDao.findProvinceList(findProvinceParam, queryPage);
    }

    /**
     * 更新省份
     *
     * @param provinceDO
     * @author kj
     * @date 2021-08-20 16:54
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void updateProvince(ProvinceDO provinceDO) {
        provinceDao.update(provinceDO);
    }

    /**
     * 删除省份
     *
     * @param ProvinceId
     * @author kj
     * @date 2021-08-20 16:54
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void deleteProvince(Long ProvinceId) {
        String sql = "delete from pub_province_t  where ProvinceId = " + ProvinceId + " limit 1";
        provinceDao.executeSql(sql);
    }

    @Override
    public boolean checkNameIfExist(String provinceName, Long provinceId) {
        return CollectionUtil.isNotEmpty(provinceDao.findProvinceByName(provinceName, provinceId));
    }

}
