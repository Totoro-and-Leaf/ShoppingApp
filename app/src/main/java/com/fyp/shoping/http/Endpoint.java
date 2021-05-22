package com.fyp.shoping.http;

import java.net.InetAddress;

public interface Endpoint {

    String HOST = "http://192.168.0.111:8080";
        //change ip depending on network

    String ORDER_ADD = HOST + "/1.0/order/add";

    String ORDER_REMOVE = HOST + "/1.0/order/remove";

    String ORDER_CHECKOUT = HOST + "/1.0/order/checkout";

    String SCAN_BARCODE = HOST + "/1.0/scan";

    String ORDER_PAYMENT = HOST + "/1.0/order/pay";


}
