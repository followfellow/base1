package com.demo.base.countryManager.service;

import com.demo.action.vo.QueryPage;
import com.demo.base.countryManager.dto.CountryDTO;
import com.demo.base.countryManager.po.CountryDO;
import com.demo.base.countryManager.request.FindCountryParam;

import java.util.List;

/**
 * 国家Service
 *
 * @author kj
 * @date 2021/8/10 16:14
 */
public interface CountryService {
    /*
     * 查询国家列表
     * @author kj
     * @date 2021/8/11 14:04  
     * @param [findCountryParam]
     * @return java.util.List<com.demo.base.countryManager.response.FindCountryResult>
     */
    List<CountryDTO> findCountryList(FindCountryParam findCountryParam, QueryPage queryPage);

    /*
     * 
     * @author kj
     * @date 2021/9/2 11:10
     * @param []
     * @return java.util.List<com.demo.base.countryManager.dto.CountryDTO>
     */
    List<CountryDTO> findCountrySelect();

    /*
     * 添加国家
     * @author kj
     * @date 2021/8/11 14:09
     * @param [countryDO]
     * @return void
     */
    void addCountry(CountryDO countryDO);
    
    /*
     * 根据 id 查询 国家
     * @author kj
     * @date 2021/8/11 18:09  
     * @param [countryId]
     * @return com.demo.base.countryManager.po.CountryDO
     */
    CountryDO queryCountryById(Long countryId);
    
    /*
     * 修改国家
     * @author kj
     * @date 2021/8/11 18:16  
     * @param [countryDO]
     * @return void
     */
    void updateCountry(CountryDO countryDO);

    /*
     * 校验国家是否分配了用户
     * @author kj
     * @date 2021/8/11 18:47  
     * @param [countryId]
     * @return boolean
     */
    boolean checkUserIfExist(Long countryId);
    
    /*
     * 根据id 删除 国家
     * @author kj
     * @date 2021/8/12 9:06
     * @param [countryId]
     * @return void
     */
    void deleteCountry(Long countryId);

    /*
     * 校验国家名是否存在
     * @author kj
     * @date 2021/8/12 11:11
     * @param [countryName, countryId]
     * @return boolean
     */
    boolean checkNameIfExist(String countryName, Long countryId);
}
