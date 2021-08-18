package com.demo.base.userManager.po;

import com.demo.dbutils.BaseApplicationDO;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 角色关联用户表
 *
 * @author :sw
 * @create 2021-06-15 14:20
 **/
@Entity
@Table(name = "sys_role_join_user_t")
@DynamicUpdate
@DynamicInsert
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RoleJoinUserDO extends BaseApplicationDO {

    /**
     * 角色关联用户表主键Id
     */
    @Id
    @Column(name = "roleJoinUserId")
    private Long roleJoinUserId;

    /**
     * 角色ID，对应表sys_role_t同名字段
     */
    @Column(name = "roleId")
    private Long roleId;

    /**
     * 用户ID，对应表“sys_user_t”中的同名字段
     */
    @Column(name = "userId")
    private Long userId;
    /**
     * 单位id
     */
    @Column(name = "businessId")
    private Long businessId;


}
