package com.jeetx.service.system.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.system.Station;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.system.StationService;

@Service
@Transactional
public class StationServiceImpl extends DaoSupport<Station> implements StationService {

}
