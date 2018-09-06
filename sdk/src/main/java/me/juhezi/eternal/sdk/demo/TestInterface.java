package me.juhezi.eternal.sdk.demo;

public interface TestInterface {
    /**
     * 定义方法: 将时间戳转换成日期
     *
     * @param dateFormat 日期格式
     * @param timeStamp  时间戳,单位为ms
     */
    String getDateFromTimeStamp(String dateFormat, long timeStamp);
}
