package com.ainq.caliphr.website.service.qrda;

/**
 * Created by mmelusky on 10/21/2015.
 */
public interface QrdaService {

    byte[] processQrdaCat1Export(Iterable<Long> hqmfIds, Integer userId);

    String processQrdaCat3Export(Iterable<Long> hqmfIds, Integer userId);
    
    String processQrdaCat1Import(String filename, byte[] uploadedBytes, Integer userId);
}
