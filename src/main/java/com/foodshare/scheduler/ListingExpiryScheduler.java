package com.foodshare.scheduler;

import com.foodshare.service.FoodListingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler to auto-expire food listings that have passed their safe-to-eat-until time.
 * Runs every minute to check for and expire stale listings.
 */
@Component
public class ListingExpiryScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ListingExpiryScheduler.class);

    @Autowired
    private FoodListingService foodListingService;

    /**
     * Runs every minute to auto-expire listings past their safe-to-eat-until time.
     */
    @Scheduled(fixedRate = 60000) // Every 60 seconds
    public void expireStaleListings() {
        int expiredCount = foodListingService.expireListings();
        if (expiredCount > 0) {
            logger.info("Auto-expired {} food listing(s) that passed their safe-to-eat-until time.", expiredCount);
        }
    }
}
