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
 * @author:wxc
 * @date:2021/7/26 11:01
 */
@Entity
@Table(name = "sys_juris_group_menu_t")
@DynamicUpdate
@DynamicInsert
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class JurisGroupMenuDO extends BaseApplicationDO {
    /**
     * 主键ID
     */
    @Id
    @Column(name = "jurisGroupMenuId")
    private Long jurisGroupMenuId;

    /**
     * 权限分组ID，对应表sys_juris_group_t同名字段
     */
    @Column(name = "jurisGroupId")
    private Long jurisGroupId;

    /**
     * 权限编号，对应表pub_menu_t中的同名字段
     */
    @Column(name = "menuNo")
    private String menuNo;

}
