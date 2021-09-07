package com.demo.base.countryManager.service;

import cn.hutool.core.collection.CollectionUtil;
import com.demo.action.service.BaseServiceImpl;
import com.demo.action.vo.QueryPage;
import com.demo.base.countryManager.dao.CountryDao;
import com.demo.base.countryManager.dto.CountryDTO;
import com.demo.base.countryManager.po.CountryDO;
import com.demo.base.countryManager.request.FindCountryParam;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;

/**
 * 角色
 *
 * @author kj
 * @date 2021/8/10 16:15
 */
@Service
public class CountryServiceImpl extends BaseServiceImpl implements CountryService {

    @Resource(name = "${sql}" + "CountryDao")

    private CountryDao countryDao;

    /*
     * 查询国家列表
     * @author kj
     * @date 2021/8/11 16:04
     * @param [findCountryParam]
     * @return java.util.List<com.demo.base.countryManager.response.FindCountryResult>
     */
    @Override
    public List<CountryDTO> findCountryList(FindCountryParam findCountryParam, QueryPage queryPage) {
        return countryDao.findCountryList(findCountryParam,queryPage);
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
        return countryDao.findCountrySelect();
    }

    /*
     * 添加国家
     * @author kj
     * @date 2021/8/11 16:04
     * @param [countryDO]
     * @return void
     */

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void addCountry(CountryDO countryDO) {
        countryDao.save(countryDO);
    }
    /*
     * 根据 id 查询 国家
     * @author kj
     * @date 2021/8/11 18:07
     * @param [countryId]
     * @return com.demo.base.countryManager.po.CountryDO
     */
    @Override
    public CountryDO queryCountryById(Long countryId) {
        //        if (countryDO != null && countryDao.getCurrUserOrgId().equals(countryDO.getBusinessId())) {
        return (CountryDO) countryDao.getEntityById(CountryDO.class, countryId);
    }

    /*
     * 修改国家
     * @author kj
     * @date 2021/8/11 18:08
     * @param [countryDO]
     * @return void
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void updateCountry(CountryDO countryDO) {
        countryDao.update(countryDO);
    }

    /*
     * 校验国家是否分配了用户
     * @author kj
     * @date 2021/8/12 8:59  
     * @param [countryId]
     * @return boolean
     */
    @Override
    public boolean checkUserIfExist(Long countryId) {
        return CollectionUtil.isNotEmpty(countryDao.findUserByCountryId(countryId));
    }

    /*
     * 根据id 删除 国家
     * @author kj
     * @date 2021/8/12 9:06
     * @param [countryId]
     * @return void
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public void deleteCountry(Long countryId) {
        String sql = "delete from pub_country_t where countryId = " + countryId;
        countryDao.executeSql(sql);
    }

    /*
     * 校验国家名是否存在
     * @author kj
     * @date 2021/8/12 11:11
     * @param [countryName, countryId]
     * @return boolean
     */
    @Override
    public boolean checkNameIfExist(String countryName, Long countryId) {
        return CollectionUtil.isNotEmpty(countryDao.findCountryByName(countryName, countryId));
    }
}
