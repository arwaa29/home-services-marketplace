package com.homeservices.Bookingservice.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("singleton")
public class BookingStatsManager {

    private int totalBookings = 0;
    private int confirmedBookings = 0;
    private int cancelledBookings = 0;

    public synchronized void incrementTotal() {
        totalBookings++;
    }

    public synchronized void incrementConfirmed() {
        confirmedBookings++;
        totalBookings++;
    }

    public synchronized void incrementCancelled() {
        cancelledBookings++;
        totalBookings++;
    }

    public int getTotalBookings() { return totalBookings; }
    public int getConfirmedBookings() { return confirmedBookings; }
    public int getCancelledBookings() { return cancelledBookings; }
}