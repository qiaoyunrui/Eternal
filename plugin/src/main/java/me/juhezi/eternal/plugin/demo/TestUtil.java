package me.juhezi.eternal.plugin.demo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.juhezi.eternal.sdk.demo.TestInterface;

/**
 * 测试插件中包含的工具类
 */
public class TestUtil implements TestInterface {

    @Override
    public String getDateFromTimeStamp(String dateFormat, long timeStamp) {
        DateFormat format = new SimpleDateFormat(dateFormat);
        Date date = new Date(timeStamp);
        return format.format(date);
    }
}
