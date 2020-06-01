package models;

import java.io.Serializable;

public enum OrderStatus implements Serializable {
    CREATED, PROCESSING, READY_FOR_CLIENT;

    public static OrderStatus orderStatusByInt(int status) {
        switch (status) {
            case 1:
                return CREATED;
            case 2:
                return PROCESSING;
            default:
                return READY_FOR_CLIENT;
        }
    }
}
