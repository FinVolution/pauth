package com.ppdai.auth.service;

import com.ppdai.auth.dao.AuditLogRepository;
import com.ppdai.auth.exception.BaseException;
import com.ppdai.auth.po.AuditLogEntity;
import com.ppdai.auth.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 审计服务：记录各种操作日志，用于后续审计
 *
 */
@Service
@Slf4j
public class AuditService {

    @Autowired
    AuditLogRepository auditLogRepo;

    @Transactional(rollbackFor = BaseException.class)
    public void recordOperation(AuditLogEntity actionItem) {
        auditLogRepo.save(actionItem);
    }

    public PageVO<AuditLogEntity> fetchAuditLogsByPage(int page, int size) {
        PageVO<AuditLogEntity> pageVO = new PageVO<>();
        Pageable pageable = new PageRequest(page, size);
        Page<AuditLogEntity> auditLogPage = auditLogRepo.findAll(pageable);
        pageVO.setContent(auditLogPage.getContent());
        pageVO.setTotalElements(auditLogPage.getTotalElements());
        return pageVO;
    }
}
