package com.ppdai.auth.controller;

import com.ppdai.auth.common.response.MessageType;
import com.ppdai.auth.common.response.Response;
import com.ppdai.auth.po.AuditLogEntity;
import com.ppdai.auth.service.AuditService;
import com.ppdai.auth.vo.PageVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    @Autowired
    private AuditService auditService;

    @ApiOperation(value = "获取分页audit_log列表")
    @RequestMapping(method = RequestMethod.GET)
    public Response<PageVO<AuditLogEntity>> getAuditLogsByPage(@RequestParam Integer page,
                                                               @RequestParam Integer size) {
        PageVO<AuditLogEntity> auditLogPageVO = auditService.fetchAuditLogsByPage(page, size);
        return Response.mark(MessageType.SUCCESS, auditLogPageVO);
    }
}
