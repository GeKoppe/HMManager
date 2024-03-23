package org.hmdms.hmmanager.sys.cache;

import org.hmdms.hmmanager.core.user.UserTicket;

import java.util.ArrayList;

public abstract class AuthCache {
    private static ArrayList<UserTicket> tickets;

    public static void addTicket(UserTicket ticket) {
        tickets.add(ticket);
    }
    public static ArrayList<UserTicket> getTickets() {
        return tickets;
    }
}
