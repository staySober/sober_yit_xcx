package com.yit.product.service;

import com.yit.disconf.client.common.annotations.DisconfFile;
import com.yit.disconf.client.common.annotations.DisconfFileItem;

@DisconfFile(filename = "config.properties")
public class TestConfig {

    @DisconfFileItem(name = "test1", associateField = "value")
    public void setValue(String value) {
        this.value = value;
    }

    public String value;

}
