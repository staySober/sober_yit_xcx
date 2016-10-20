package com.yit.product.service;

import com.yit.disconf.client.common.annotations.DisconfFile;
import com.yit.disconf.client.common.annotations.DisconfFileItem;

@DisconfFile(filename = "config2.properties")
public class TestConfig2 {

    @DisconfFileItem(name = "test2", associateField = "value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private String value;

}