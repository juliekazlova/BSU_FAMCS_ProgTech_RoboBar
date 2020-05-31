package models;

public enum OrderStatus {
    CREATED, PROCESSING, READY_FOR_CLIENT;

    public static OrderStatus orderStatusByInt(int status){
        if(status==1) return CREATED;
        if(status==2) return PROCESSING;
        return READY_FOR_CLIENT;
    }
}
