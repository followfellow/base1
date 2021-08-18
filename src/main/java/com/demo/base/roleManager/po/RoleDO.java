package com.demo.base.roleManager.po;

import com.demo.dbutils.BaseApplicationDO;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 角色 DO
 * @author sw
 * @date 2021-06-13 17:30:58
 */
@Entity
@Table(name = "sys_role_t")
@DynamicUpdate
@DynamicInsert
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class RoleDO extends BaseApplicationDO {

    /**
     * 主键ID
     */
    @Id
    @Column(name = "roleId")
    private Long roleId;

    /**
     * 上级角色ID，无上级存0
     */
    @Column(name = "rolePid")
    private Long rolePid;

    /**
     * 角色名称
     */
    @Column(name = "roleName")
    private String roleName;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

    /**
     * 单位id
     */
    @Column(name = "businessId")
    private Long businessId;

}
