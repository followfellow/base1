package com.demo.base.companyManager.po;

import com.demo.dbutils.BaseApplicationDO;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 单位分组绑定表
 *
 * @author:wxc
 * @date:2021/7/29 8:59
 */
@Entity
@Table(name = "sys_juris_group_org_t")
@DynamicUpdate
@DynamicInsert
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class JurisGroupOrgDO extends BaseApplicationDO {
    /**
     * 主键ID
     */
    @Id
    @Column(name = "jurisGroupOrgId")
    private Long jurisGroupOrgId;

    /**
     * 权限分组ID，对应表sys_juris_group_t同名字段
     */
    @Column(name = "jurisGroupId")
    private Long jurisGroupId;

    /**
     * 单位类型，对应表“org_business_t”中的同名字段
     * BUSINESS_TYPE
     * 0：运营商
     * 1：目的地
     * 2：分销商
     * 3：旅行社
     */
    @Column(name = "businessType")
    private Integer businessType;

    /**
     * 单位id，对应表“org_business_t”中的同名字段
     */
    @Column(name = "businessId")
    private Long businessId;

}
