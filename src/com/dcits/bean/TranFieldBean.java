package com.dcits.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: huangth tel:13246649002
 * Date: 2020/9/3 20:15
 * @Version:
 * @Description:
 */
public class TranFieldBean implements Serializable {

    //递归处理数组
    private List<TranFieldBean> beanList;

    public List<TranFieldBean> getBeanList() {
        return beanList;
    }

    public void setBeanList(List<TranFieldBean> beanList) {
        this.beanList = beanList;
    }

    //左侧
    private String lfFieldNm;
    private String lfFieldZhNm;
    private String lfFieldType;
    private int lfFieldLen;
    private int lfFieldScale;

    //右侧
    private String rtFieldNm;
    private String rtFieldZhNm;
    private String rtFieldType;
    private int rtFieldLen;
    private int rtFieldScale;

    //是否数组
    private boolean isArrayField;

    public String getLfFieldNm() {
        return lfFieldNm;
    }

    public void setLfFieldNm(String lfFieldNm) {
        this.lfFieldNm = lfFieldNm;
    }

    public String getLfFieldZhNm() {
        return lfFieldZhNm;
    }

    public void setLfFieldZhNm(String lfFieldZhNm) {
        this.lfFieldZhNm = lfFieldZhNm;
    }

    public String getLfFieldType() {
        return lfFieldType;
    }

    public void setLfFieldType(String lfFieldType) {
        this.lfFieldType = lfFieldType;
    }

    public int getLfFieldLen() {
        return lfFieldLen;
    }

    public void setLfFieldLen(int lfFieldLen) {
        this.lfFieldLen = lfFieldLen;
    }

    public int getLfFieldScale() {
        return lfFieldScale;
    }

    public void setLfFieldScale(int lfFieldScale) {
        this.lfFieldScale = lfFieldScale;
    }

    public String getRtFieldNm() {
        return rtFieldNm;
    }

    public void setRtFieldNm(String rtFieldNm) {
        this.rtFieldNm = rtFieldNm;
    }

    public String getRtFieldZhNm() {
        return rtFieldZhNm;
    }

    public void setRtFieldZhNm(String rtFieldZhNm) {
        this.rtFieldZhNm = rtFieldZhNm;
    }

    public String getRtFieldType() {
        return rtFieldType;
    }

    public void setRtFieldType(String rtFieldType) {
        this.rtFieldType = rtFieldType;
    }

    public int getRtFieldLen() {
        return rtFieldLen;
    }

    public void setRtFieldLen(int rtFieldLen) {
        this.rtFieldLen = rtFieldLen;
    }

    public int getRtFieldScale() {
        return rtFieldScale;
    }

    public void setRtFieldScale(int rtFieldScale) {
        this.rtFieldScale = rtFieldScale;
    }

    public boolean isArrayField() {
        return isArrayField;
    }

    public void setArrayField(boolean arrayField) {
        isArrayField = arrayField;
    }

    @Override
    public String toString() {
        return "TranFieldBean{" +
                "lfFieldNm='" + lfFieldNm + '\'' +
                ", lfFieldZhNm='" + lfFieldZhNm + '\'' +
                ", lfFieldType='" + lfFieldType + '\'' +
                ", lfFieldLen=" + lfFieldLen +
                ", lfFieldScale=" + lfFieldScale +
                ", rtFieldNm='" + rtFieldNm + '\'' +
                ", rtFieldZhNm='" + rtFieldZhNm + '\'' +
                ", rtFieldType='" + rtFieldType + '\'' +
                ", rtFieldLen=" + rtFieldLen +
                ", rtFieldScale=" + rtFieldScale +
                ", isArrayField=" + isArrayField +
                '}';
    }
}
