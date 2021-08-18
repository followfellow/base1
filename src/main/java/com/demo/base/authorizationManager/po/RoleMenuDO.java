package com.demo.base.authorizationManager.po;

import com.demo.dbutils.BaseApplicationDO;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 角色权限表
 * @author :sw
 * @create 2021-06-24 13:04
 **/
@Entity
@Table(name = "sys_role_menu_t")
@DynamicUpdate
@DynamicInsert
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class RoleMenuDO extends BaseApplicationDO {

    /**
     * 角色权限主键id
     */
    @Id
    @Column(name = "roleMenuId")
    private Long roleMenuId;

    /**
     * 角色id
     */
    @Column(name = "roleId")
    private Long roleId;
    /**
     * 单位id
     */
    @Column(name = "businessId")
    private Long businessId;

    /**
     * 权限编号，唯一,对应表pub_menu_t
     */
    @Column(name = "menuNo")
    private String menuNo;

}
