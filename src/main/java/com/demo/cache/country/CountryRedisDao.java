package com.demo.cache.country;

import com.demo.base.countryManager.cache.FindCountryCacheParam;
import com.demo.base.countryManager.dto.CountryDTO;
import com.demo.dbutils.BaseDAO;

import java.util.List;

/**
 * @author kj
 * @date 2021/8/19 11:00
 */
public interface CountryRedisDao extends BaseDAO {
    List<CountryDTO> findCountryList(FindCountryCacheParam findCountryCacheParam);
}
