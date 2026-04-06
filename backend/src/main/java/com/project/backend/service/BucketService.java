package com.project.backend.service;

import com.project.backend.utility.Bucket;

public interface BucketService {
    Bucket resolveBucket(String clientIp);
}
