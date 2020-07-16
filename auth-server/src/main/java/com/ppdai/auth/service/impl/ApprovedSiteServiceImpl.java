package com.ppdai.auth.service.impl;

import com.ppdai.auth.dao.ApprovedScopeRepository;
import com.ppdai.auth.dao.ApprovedSiteRepository;
import com.ppdai.auth.exception.BaseException;
import com.ppdai.auth.po.ApprovedScopeEntity;
import com.ppdai.auth.po.ApprovedSiteEntity;
import com.ppdai.auth.service.ApprovedSiteService;
import com.ppdai.auth.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Please add description here.
 *
 */
@Service
public class ApprovedSiteServiceImpl implements ApprovedSiteService {

    @Autowired
    private ApprovedSiteRepository approvedSiteRepo;
    @Autowired
    private ApprovedScopeRepository approvedScopeRepo;

    @Override
    public Iterable<ApprovedSiteEntity> getSitesByUserName(String username) {
        Iterable<ApprovedSiteEntity> sites = approvedSiteRepo.findByUserName(username);
        sites.forEach(site -> {
            Long siteId = site.getId();
            site.setScopes(approvedScopeRepo.findBySiteId(siteId));
        });
        return sites;
    }

    @Override
    public PageVO<ApprovedSiteEntity> getSitesByPage(String username, String clientId, Integer page, Integer size) {
        PageVO<ApprovedSiteEntity> pageVO = new PageVO<>();
        Pageable pageable = new PageRequest(page, size);

        Page<ApprovedSiteEntity> sitePage = approvedSiteRepo.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            if (!username.isEmpty()) {
                list.add(criteriaBuilder.equal(root.get("userName").as(String.class), username));
            }
            if (!clientId.isEmpty()) {
                list.add(criteriaBuilder.equal(root.get("clientId").as(String.class), clientId));
            }
            list.add(criteriaBuilder.equal(root.get("isActive").as(Boolean.class), Boolean.TRUE));
            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        }, pageable);

        List<ApprovedSiteEntity> sites = sitePage.getContent();
        sites.forEach(site -> {
            Long siteId = site.getId();
            site.setScopes(approvedScopeRepo.findBySiteId(siteId));
        });

        pageVO.setContent(sites);
        pageVO.setTotalElements(sitePage.getTotalElements());
        return pageVO;
    }

    @Override
    public ApprovedSiteEntity record(String userName, String clientId, Set<String> scopes) {
        ApprovedSiteEntity site = null;
        // save approved site and scopes
        if (!this.existApprovalFor(userName, clientId)) {
            ApprovedSiteEntity siteEntity = new ApprovedSiteEntity();
            siteEntity.setUserName(userName);
            siteEntity.setClientId(clientId);
            Set<ApprovedScopeEntity> scopesX = new HashSet<>();
            scopes.forEach(scope -> {
                ApprovedScopeEntity s = new ApprovedScopeEntity();
                s.setScopeName(scope);
                scopesX.add(s);
            });
            siteEntity.setScopes(scopesX);
            site = this.addSite(siteEntity);
        }
        return site;
    }


    @Transactional(rollbackFor = BaseException.class)
    public ApprovedSiteEntity addSite(ApprovedSiteEntity site) {
        ApprovedSiteEntity newSite = approvedSiteRepo.save(site);

        Long siteId = newSite.getId();
        Iterable<ApprovedScopeEntity> scopes = site.getScopes();
        if (scopes != null) {
            scopes.forEach(scope -> {
                scope.setApprovedSiteId(siteId);
                approvedScopeRepo.save(scope);
            });
        }

        return newSite;
    }

    @Transactional(rollbackFor = BaseException.class)
    @Override
    public void removeSiteById(Long siteId) {
        approvedSiteRepo.removeById(siteId);
    }

    @Override
    public Boolean existApprovalFor(String username, String clientId) {
        Boolean bExist = Boolean.FALSE;

        Iterable<ApprovedSiteEntity> approvedSites = approvedSiteRepo.findByUserNameAndClientId(username, clientId);
        if (approvedSites.iterator().hasNext()) {
            bExist = Boolean.TRUE;
        }

        return bExist;
    }

}
