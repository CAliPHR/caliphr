package com.ainq.caliphr.website.service.hqmf;

import com.ainq.caliphr.common.model.json.JsonResponse;

/**
 * Created by mmelusky on 8/25/2015.
 */
public interface HqmfProcessService {
    JsonResponse processHqmf(Integer providerId, String startDate, String endDate, Integer userId);
}
