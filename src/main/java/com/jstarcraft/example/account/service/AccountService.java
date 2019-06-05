package com.jstarcraft.example.account.service;

import java.time.Instant;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jstarcraft.core.cache.CacheIndex;
import com.jstarcraft.core.cache.CacheObjectFactory;
import com.jstarcraft.core.cache.EntityManager;
import com.jstarcraft.core.cache.annotation.AfterCacheStarted;
import com.jstarcraft.core.cache.annotation.CacheAccessor;
import com.jstarcraft.core.common.lockable.HashLockableStrategy;
import com.jstarcraft.core.common.lockable.LockableMethod;
import com.jstarcraft.core.common.lockable.LockableParameter;
import com.jstarcraft.core.orm.OrmAccessor;
import com.jstarcraft.core.orm.identification.CacheIdentityFactory;
import com.jstarcraft.core.orm.identification.IdentityDefinition;
import com.jstarcraft.core.orm.identification.IdentityFactory;
import com.jstarcraft.core.utility.HashUtility;
import com.jstarcraft.example.account.exception.AccountException;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

@Component
public class AccountService {

    private Logger logger = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private OrmAccessor accessor;

    @CacheAccessor
    private EntityManager<Long, Account> accountManager;

    @Autowired
    private IdentityDefinition identityDefinition;

    private Int2ObjectOpenHashMap<CacheIdentityFactory> identityFactories;

    @AfterCacheStarted
    void afterCache() {
        identityFactories = new Int2ObjectOpenHashMap<>();
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

    public int getSection(String accountName) {
        long hash = HashUtility.bkdrStringHash32(accountName);
        int sectionSize = 1 << identityDefinition.getSections().get(0).getBit();
        int sectionIndex = (int) (hash % sectionSize);
        return Math.abs(sectionIndex);
    }

    @LockableMethod(strategy = HashLockableStrategy.class)
    public Account registerAccount(@LockableParameter String accountName, int sectionIndex, Instant now, String ip) {
        // 判断账号是否已存在?
        Account account = getAccount(accountName);
        if (account != null) {
            throw AccountException.ACCOUNT_EXISTENT;
        }

        IdentityFactory identityFactory = identityFactories.get(sectionIndex);
        account = accountManager.loadInstance(identityFactory.getSequence(), new CacheObjectFactory<Long, Account>() {

            @Override
            public Account instanceOf(Long id) {
                return new Account(id, accountName, sectionIndex, now, ip);
            }

        });
        return account;
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
