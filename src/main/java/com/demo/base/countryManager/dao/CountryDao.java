package com.demo.base.countryManager.dao;

import com.demo.action.vo.QueryPage;
import com.demo.base.countryManager.dto.CountryDTO;
import com.demo.base.countryManager.request.FindCountryParam;
import com.demo.dbutils.BaseDAO;

import java.util.List;

/**
 * 国家DAO
 *
 * @author kj
 * @date 2021/8/10 16:15
 */
public interface CountryDao extends BaseDAO {

    /**
     * 查询国家列表
     * @author kj
     * @date 2021/8/11 10:20
     * @param findCountryParam
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
     * 通过Id查询国家
     * @author kj
     * @date 2021/8/12 11:11
     * @param [countryId]
     * @return java.util.List<com.demo.base.countryManager.dto.CountryDTO>
     */
    List<CountryDTO> findUserByCountryId(Long countryId);

    /*
     * 通过国家名称查找国家
     * @author kj
     * @date 2021/8/12 11:11
     * @param [countryName, countryId]
     * @return java.util.List<com.demo.base.countryManager.dto.CountryDTO>
     */
    List<CountryDTO> findCountryByName(String countryName, Long countryId);
}
