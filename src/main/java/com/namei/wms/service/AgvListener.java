package com.namei.wms.service;

/**
 * ${DESCRIPTION}
 *
 * @author 14684
 * @create 2020-12-07 16:49
 */
public class AgvListener {

    AgvService agvService = new AgvService();



    public String onFetchEntry() {
        agvService.sendLoactionBin();
        return "";
    }

    public String onDeliveryEntry() {
        agvService.sendLoactionBin();
        return "";
    }

}
