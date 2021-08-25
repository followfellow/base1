package com.demo.base.cityManager.po;

import com.demo.dbutils.BaseApplicationDO;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 *
 *
 * @author kj
 * @date 2021/8/12 18:33
 */
@Entity
@Data
@Builder
@Table(name = "pub_city_t")
@DynamicUpdate(true)
@DynamicInsert(true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class CityDO extends BaseApplicationDO {
    /**
     * 城市ID主键
     */
    @Id
    @Column(name = "cityId")
    private Long cityId;

    /**
     * 城市名称
     */
    @Column(name = "cityName")
    private String cityName;

    /**
     * 城市首字母缩写
     */
    @Column(name = "cityChar")
    private String cityChar;

    /**
     * 省份ID
     */
    @Column(name = "provinceId")
    private Long provinceId;

    /**
     * 城市的邮编号码
     */
    @Column(name = "certificateNo")
    private String certificateNo;

    /**
     * 创建时间
     */
    @Column(name = "createdDate")
    private Date createdDate;

    /**
     * 创建用户
     */
    @Column(name = "createdUser")
    private String createdUser;

    /**
     * 创建用户id
     */
    @Column(name = "createdUserId")
    private Long createdUserId;

    /**
     * 修改时间
     */
    @Column(name = "updatedDate")
    private Date updatedDate;

    /**
     * 修改用户
     */
    @Column(name = "updatedUser")
    private String updatedUser;

    /**
     * 修改用户id
     */
    @Column(name = "updateUserId")
    private Long updateUserId;

    /**
     * 删除标记，CodeName：DELETE_MARK
     0未删除
     1删除
     */
    @Column(name = "markDelete")
    private Integer markDelete;

}
