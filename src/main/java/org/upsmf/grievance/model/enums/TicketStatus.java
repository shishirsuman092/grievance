package org.upsmf.grievance.model.enums;

public enum TicketStatus {

    OPEN(1), CLOSED(2);

    private int id;

    TicketStatus(int id) {
        this.id = id;
    }
}
