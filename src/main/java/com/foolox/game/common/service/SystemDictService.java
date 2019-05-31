package com.foolox.game.common.service;

import com.foolox.game.common.repo.dao.SystemDictRepository;
import com.foolox.game.common.repo.domain.SystemDict;
import com.foolox.game.constants.DictType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 17/05/2019
 */
@Service
public class SystemDictService {
    @Autowired
    private SystemDictRepository systemDictRepository;

    /**
     * 读取租户配置信息
     *
     * @param code
     * @return
     */
    public SystemDict findOneByCodeType(String code, DictType type) {
        SystemDict probe = new SystemDict();
        probe.setCode(code);
        probe.setDictType(type);
        probe.setDicts(null);
        Example<SystemDict> example = Example.of(probe);
        try {
            return systemDictRepository.findOne(example).get();
        } catch (NoSuchElementException e) {
            return null;
        }
    }
}
