package com.demo.base.countryManager.po;

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
 * 国家DO
 *
 * @author kj
 * @date 2021/8/10 13:16
 */

@Entity
@Data
@Builder
@Table(name = "pub_country_t")
@DynamicUpdate(true)
@DynamicInsert(true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class CountryDO extends BaseApplicationDO {
    /**
     * 国家表主键ID
     */
    @Id
    @Column(name = "countryId")
    private Long countryId;

    /**
     * 国家地区
     */
    @Column(name = "countryName")
    private String countryName;

    /**
     * 国家拼首字母缩写
     */
    @Column(name = "countryChar")
    private String countryChar;

    /**
     * 三位代码
     */
    @Column(name = "threeBitCode")
    private String threeBitCode;

    /**
     * 两位代码
     */
    @Column(name = "twoBitCode")
    private String twoBitCode;

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
