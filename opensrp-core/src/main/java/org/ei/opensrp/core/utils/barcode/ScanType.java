package org.ei.opensrp.core.utils.barcode;

public class ScanType <T>{
    final String type;
    final String id;
    final T data;

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public T getData() {
        return data;
    }

    public ScanType(String type, String id, T data){
        this.type = type;
        this.id = id;
        this.data = data;
    }

    @Override
    public String toString() {
        return type+":"+id+"::"+data;
    }
}