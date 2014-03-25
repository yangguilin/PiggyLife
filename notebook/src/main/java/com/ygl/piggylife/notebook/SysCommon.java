package com.ygl.piggylife.notebook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import static com.ygl.piggylife.notebook.SysUtil.ShowMsgToUser;
import static com.ygl.piggylife.notebook.SysUtil.StringIsEmpty;

/**
 * 系统通用相关内容，包括系统常量、系统特殊功能函数
 * Created by yanggl on 2/25/14.
 */
public class SysCommon {
    //
    // 根据操作类型名称，获取其保存数值
    public static String GetProcTypeValueByName(String procName)
    {
        String ret = "";
        if (procName != null && procName != "")
        {
            if (procName.equals("支出"))
                ret = "cost";
            else if (procName.equals("收入"))
                ret = "income";
        }
        return ret;
    }

    //
    // 根据统计类型名称，获取其保存数值
    public static String GetStatTypeValueByName(String statName)
    {
        String ret = "";
        if (statName != "" && statName != null)
        {
            if (statName.equals("日常"))
                ret = "1";
            else if (statName.equals("阶段"))
                ret = "2";
            else if (statName.equals("大额"))
                ret = "3";
        }
        return ret;
    }

    /**
     * 根据统计类型值获取显示名
     * @param val   统计类型值
     * @return      统计类型名
     */
    public static String GetStatNameByValue(String val){
        String showName = "";
        if (val.equals("1"))
            showName = "日常";
        else if (val.equals("2"))
            showName = "阶段";
        else if (val.equals("3"))
            showName = "大额";

        return showName;
    }

    /**
     * 检查用户是否处于退出状态
     * @param context   上下文
     * @return  true：登录；false：退出
     */
    protected static boolean CheckUserLoginStatus(android.content.Context context)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(SysConst._SHARED_PREF_USER_INFO, 0);
        return (sharedPref.getAll().size() > 0);
    }

    /**
     * 根据分类值判断分类类型
     * @param cValue    分类值
     * @return  （支出/收入）
     */
    protected static String GetCategoryTypeByValue(String cValue){
        String cType = "支出";
        if (cValue.length() == 3)
            cType = "收入";

        return cType;
    }

    /**
     * 根据所选菜单Id，执行不同的页面跳转
     * @param context   上下文
     * @param menuId    所选菜单Id
     */
    protected static void DoSomethingBySelectedMenuId(Context context, int menuId){
        if (menuId == R.id.action_quit){
            UserAuthMgr.GoToLoginActivity(context);
        }
        else if (menuId == R.id.action_about) {
            new AlertDialog.Builder(context)
                    .setTitle("关于小猪便签")
                    .setMessage("小猪爱记账V1.0，还请多多指教！")
                    .setPositiveButton("关闭", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id)                        {
                            dialog.cancel();
                        }
                    }).show();
        }
        else if (menuId == R.id.action_category_manager){
            UserAuthMgr.GoToCategoryManagerActivity(context);
        }
        else if (menuId == R.id.action_query){
            UserAuthMgr.GoToQueryrActivity(context);
        }
        else if (menuId == R.id.action_home){
            UserAuthMgr.GoToHomeActivity(context);
        }
    }
}
