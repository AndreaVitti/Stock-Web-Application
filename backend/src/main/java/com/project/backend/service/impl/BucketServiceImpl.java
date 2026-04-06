package com.project.backend.service.impl;

import com.project.backend.service.BucketService;
import com.project.backend.utility.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class BucketServiceImpl implements BucketService {
    @Value(value = "${rate-limit.bucket-capacity}")
    private int bucketCapacity;
    @Value(value = "${rate-limit.refill-interval}")
    private int refillTimer;

    private final Map<String, Bucket> bucketsMap = new ConcurrentHashMap<>();

    @Override
    public Bucket resolveBucket(String clientKey) {
        return bucketsMap.computeIfAbsent(clientKey, v -> createNewBucket());
    }

    private Bucket createNewBucket() {
        return new Bucket(bucketCapacity, refillTimer);
    }
}
