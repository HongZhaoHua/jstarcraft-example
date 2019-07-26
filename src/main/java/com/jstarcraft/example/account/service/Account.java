package com.jstarcraft.example.account.service;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.jstarcraft.core.cache.annotation.CacheChange;
import com.jstarcraft.core.cache.annotation.CacheConfiguration;
import com.jstarcraft.core.cache.annotation.CacheConfiguration.Unit;
import com.jstarcraft.core.common.identification.IdentityObject;

/**
 * 账号
 * 
 * @author Birdy
 *
 */
@Entity
@Table(indexes = { @Index(columnList = "name") })
@CacheConfiguration(unit = Unit.ENTITY, indexes = { "name" }, transienceStrategy = "lruMemoryStrategy", persistenceStrategy = "queuePersistenceStrategy")
public class Account implements IdentityObject<Long> {

    /** 账号标识 */
    @Id
    private long id;

    /** 账号名称 */
    @Column(unique = true, nullable = false)
    private String name;

    /** 分段索引 */
    private int sectionIndex;

    /** 注册时间 */
    @Column(nullable = false)
    private Instant registerInstant;

    /** 注册IP */
    private String registerIp;

    /** 登入时间 */
    private Instant loginInstant;

    /** 登出时间 */
    private Instant logoutInstant;

    protected Account() {
    }

    public Account(long id, String name, int section, Instant now, String ip) {
        this.id = id;
        this.name = name;
        this.sectionIndex = section;
        this.registerInstant = now;
        this.registerIp = ip;
    }

    /**
     * 登入
     * 
     * @param now
     */
    @CacheChange
    void login(Instant now) {
        loginInstant = now;
    }

    /**
     * 登出
     * 
     * @param now
     */
    @CacheChange
    void logout(Instant now) {
        logoutInstant = now;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSectionIndex() {
        return sectionIndex;
    }

    public Instant getRegisterInstant() {
        return registerInstant;
    }

    public String getRegisterIp() {
        return registerIp;
    }

    public Instant getLoginInstant() {
        return loginInstant;
    }

    public Instant getLogoutInstant() {
        return logoutInstant;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (!(object instanceof Account))
            return false;
        Account that = (Account) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.id, that.id);
        return equal.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hash = new HashCodeBuilder();
        hash.append(id);
        return hash.toHashCode();
    }

}
