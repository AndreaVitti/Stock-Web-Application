package com.project.backend.utility;

public class Bucket {
    private final int bucketCapacity;
    private int currentInBucket;
    private final long refillTimer;
    private long lastRefill = System.nanoTime();

    public Bucket(int bucketCapacity, int refillTimer) {
        this.bucketCapacity = bucketCapacity;
        this.refillTimer = refillTimer * 1_000_000_000L;
        this.currentInBucket = bucketCapacity;
    }

    public synchronized boolean consume() {
        refill();
        if (currentInBucket > 0) {
            currentInBucket--;
            return true;
        }
        return false;
    }

    private void refill() {
        if (currentInBucket < bucketCapacity) {
            int currentInBucketTemp = currentInBucket;
            currentInBucket += (int) Math.floor((double) (System.nanoTime() - lastRefill) / refillTimer);
            if (currentInBucket != currentInBucketTemp) {
                lastRefill += (currentInBucket - currentInBucketTemp) * refillTimer;
            }
            if (currentInBucket > bucketCapacity) {
                currentInBucket = bucketCapacity;
            }
        }
    }

    public synchronized int getRemainingTokens() {
        return currentInBucket;
    }

    public synchronized long getTimeBeforeNextRefill() {
        long remaining = (refillTimer - (System.nanoTime() - lastRefill)) / 1_000_000_000L;
        return Math.max(remaining, 0);
    }
}
