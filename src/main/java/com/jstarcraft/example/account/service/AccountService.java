package com.jstarcraft.example.account.service;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jstarcraft.core.cache.CacheIndex;
import com.jstarcraft.core.cache.EntityManager;
import com.jstarcraft.core.cache.annotation.AfterCacheStarted;
import com.jstarcraft.core.cache.annotation.CacheAccessor;
import com.jstarcraft.core.common.lockable.HashLockableStrategy;
import com.jstarcraft.core.common.lockable.LockableMethod;
import com.jstarcraft.core.common.lockable.LockableParameter;
import com.jstarcraft.core.orm.OrmAccessor;
import com.jstarcraft.core.orm.identification.CacheIdentityFactory;
import com.jstarcraft.core.orm.identification.IdentityDefinition;

@Component
public class AccountService {

    private Logger logger = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private OrmAccessor accessor;

    @CacheAccessor
    private EntityManager<Long, Account> accountManager;

    @Autowired
    private IdentityDefinition identityDefinition;

    private Map<Integer, CacheIdentityFactory> identityFactories;

    @AfterCacheStarted
    void afterCache() {
        identityFactories = new HashMap<>();
        int sectionSize = 1 << identityDefinition.getSections().get(0).getBit();
        logger.info("分段数量[{}]", sectionSize);
        for (int sectionIndex = 0; sectionIndex < sectionSize; sectionIndex++) {
            Long maximum = identityDefinition.make(sectionIndex, -1L);
            Long minimum = identityDefinition.make(sectionIndex, 0L);
            Long current = accessor.maximumIdentity(Account.class, minimum, maximum);
            CacheIdentityFactory identityFactory = new CacheIdentityFactory(identityDefinition, sectionIndex, current);
            identityFactories.put(sectionIndex, identityFactory);
        }
    }

    public int getSection(long accountId) {
        return (int) identityDefinition.parse(accountId)[0];
    }

    @LockableMethod(strategy = HashLockableStrategy.class)
    public void loginAccount(@LockableParameter Account account) {
        Instant now = Instant.now();
        account.login(now);
    }

    @LockableMethod(strategy = HashLockableStrategy.class)
    public void logoutAccount(@LockableParameter Account account) {
        Instant now = Instant.now();
        account.logout(now);
    }

    public Account getAccount(Long accountId) {
        Account account = accountManager.getInstance(accountId);
        return account;
    }

    public Account getAccount(String accountName) {
        Collection<Long> accountIds = accountManager.getIdentities(new CacheIndex("name", accountName));
        if (accountIds.isEmpty()) {
            return null;
        }
        Long accountId = accountIds.toArray(new Long[accountIds.size()])[0];
        return getAccount(accountId);
    }

}
