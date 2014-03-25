package com.ygl.piggylife.notebook;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yanggavin on 3/4/14.
 */
public class SysUtil {

    /**
     * 字符串是否为数字
     * @param str   字符串
     * @return  是否为数字
     */
    public static boolean IsNumber(String str)
    {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 判断字符是否为空
     * @param s 待判断字符串
     * @return  true为空；false为非空
     */
    protected static boolean StringIsEmpty(String s)
    {
        return (s == null || s.length() <= 0);
    }

    /**
     * 两个日期字符串相减，获取其相差的天数
     * @param fDate 起始日期字符串
     * @param sDate 结束日期字符串
     * @return  间隔天数
     */
    protected static int SubDateStrForDayValue(String fDate, String sDate)
    {
        long dayVal = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date d1 = sdf.parse(fDate);
            Date d2 = sdf.parse(sDate);

            dayVal = Math.abs((d2.getTime() - d1.getTime())/(24*3600*1000));
        } catch (ParseException e) {
            dayVal = 0;
            e.printStackTrace();
        }

        return (int)dayVal;
    }

    /**
     * 获取今天的日期字符串，格式为：yyyy-MM-dd
     * @return  今天的日期字符串
     */
    protected static String GetTodayStr()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    /**
     * 向用户显示提示信息
     * 注：子线程调用
     * @param context   当前activity所在context
     * @param msg   要显示的信息内容
     */
    protected static void ShowMsgToUserInSubThread(android.content.Context context ,String msg){
        Looper.prepare();
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        Looper.loop();
    }

    /**
     * 向用户显示提示信息
     * @param context   当前activity所在context
     * @param msg   要显示的信息内容
     */
    protected static void ShowMsgToUser(android.content.Context context ,String msg){
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * 根据key清空SharedPref中的数据
     * @param context   当前Activity的上下文
     * @param keyName   key值
     */
    protected static void ClearSharedPrefByKeyName(Context context, String keyName)
    {
        SharedPreferences sp = context.getSharedPreferences(SysConst._SHARED_PREF_USER_INFO, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 解码服务端通过php_json_encode加密后的内容
     * @param str   待处理字符串
     * @return  处理结果
     */
    protected static String UnEscapeUnicode(String str){
        Matcher m = Pattern.compile("\\\\u([0-9a-fA-F]{4})").matcher(str);

        // 逐字替换字符串中的字符
        while (m.find()){
            str = str.replace(
                    m.group(),
                    ((char) (Integer.parseInt(m.group(1), 16)) + "")
            );
        }

        return str;
    }
}
