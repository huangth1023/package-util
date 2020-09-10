package com.dcits.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: huangth tel:13246649002
 * Date: 2020/9/8 15:04
 * @Version:
 * @Description:
 */
public class ArrayField extends TranFieldBean implements Serializable {

    private List<TranFieldBean> fieldBeans;

    private List<ArrayField> arrayFieldList;

    public List<TranFieldBean> getFieldBeans() {
        return fieldBeans;
    }

    public void addFieldBeans(TranFieldBean tranFieldBean) {
        this.fieldBeans = fieldBeans;
    }

}
