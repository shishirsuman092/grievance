package org.upsmf.grievance.model.enums;

public enum TicketPriority {

    HIGH(1), MEDIUM(2), LOW(3);

    private int id;


    TicketPriority(int id) {
        this.id = id;
    }
}
