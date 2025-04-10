package com.example.resource.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.model.entity.resource.Satellite;
import com.example.resource.mapper.SatelliteMapper;
import com.example.resource.service.SatelliteService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class SatelliteServiceImpl extends ServiceImpl<SatelliteMapper, Satellite> implements SatelliteService {

  @Override
  @Transactional
  public Satellite create(Satellite satellite) {
    save(satellite);
    return satellite;
  }

  @Override
  @Transactional
  public Satellite update(Satellite satellite) {
    updateById(satellite);
    return getById(satellite.getId());
  }

  @Override
  @Transactional
  public void delete(Long id) {
    removeById(id);
  }

  @Override
  public IPage<Satellite> page(Page<Satellite> page, String keyword, String status, List<String> tags) {
    LambdaQueryWrapper<Satellite> wrapper = new LambdaQueryWrapper<>();

    if (StringUtils.hasText(keyword)) {
      wrapper.like(Satellite::getSatelliteNameCn, keyword)
          .or()
          .like(Satellite::getSatelliteNameEn, keyword)
          .or()
          .like(Satellite::getSatelliteCode, keyword)
          .or()
          .like(Satellite::getNoradId, keyword);
    }

    if (StringUtils.hasText(status)) {
      wrapper.eq(Satellite::getStatus, status);
    }

    if (tags != null && !tags.isEmpty()) {
      wrapper.apply("JSON_CONTAINS(tags, JSON_ARRAY(?1))", String.join(",", tags));
    }

    return page(page, wrapper);
  }

  @Override
  public List<Satellite> getByTags(List<String> tags) {
    LambdaQueryWrapper<Satellite> wrapper = new LambdaQueryWrapper<>();
    if (tags != null && !tags.isEmpty()) {
      wrapper.apply("JSON_CONTAINS(tags, JSON_ARRAY(?1))", String.join(",", tags));
    }
    return list(wrapper);
  }

  @Override
  @Transactional
  public Satellite updateOrbitalElements(Long id, Satellite satellite) {
    Satellite existing = getById(id);
    if (existing == null) {
      throw new RuntimeException("Satellite not found");
    }

    // 更新轨道根数相关字段
    existing.setSemiMajorAxis(satellite.getSemiMajorAxis());
    existing.setEccentricity(satellite.getEccentricity());
    existing.setInclination(satellite.getInclination());
    existing.setRightAscension(satellite.getRightAscension());
    existing.setArgumentOfPerigee(satellite.getArgumentOfPerigee());
    existing.setMeanAnomaly(satellite.getMeanAnomaly());
    existing.setEpochTime(satellite.getEpochTime());
    existing.setTleLine1(satellite.getTleLine1());
    existing.setTleLine2(satellite.getTleLine2());

    updateById(existing);
    return existing;
  }

  @Override
  @Transactional
  public Satellite updateStatus(Long id, String status) {
    Satellite satellite = getById(id);
    if (satellite == null) {
      throw new RuntimeException("Satellite not found");
    }

    satellite.setStatus(status);
    updateById(satellite);
    return satellite;
  }
} 