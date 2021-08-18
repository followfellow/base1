package com.demo.base.jurisGroupManager.po;

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
 * 单位DO
 *
 * @author:wxc
 * @date:2021-07-23 17:03:23
 */
@Entity
@Table(name = "sys_juris_group_t")
@DynamicUpdate
@DynamicInsert
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class JurisGroupDO extends BaseApplicationDO {

    /**
     * 主键ID
     */
    @Id
    @Column(name = "jurisGroupId")
    private Long jurisGroupId;
    /**
     * 权限分组父ID，0为一级
     */
    @Column(name = "jurisGroupPid")
    private Long jurisGroupPid;


    /**
     * 分组名称
     */
    @Column(name = "jurisGroupName")
    private String jurisGroupName;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

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


}
