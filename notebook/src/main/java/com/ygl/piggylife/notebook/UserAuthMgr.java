package com.ygl.piggylife.notebook;

import android.content.Context;
import android.content.Intent;

import static com.ygl.piggylife.notebook.SysUtil.GetTodayStr;

/**
 * 当前用户权限管理类
 * Created by yanggavin on 14-3-21.
 */
public class UserAuthMgr {
    private static String _curUserName = "";
    private static String _lastLoginDate = "";


    /**
     * 初始化用户验证管理类
     * @param userName  登录成功用户名
     */
    public static void Init(String userName){
        _curUserName = userName;
        _lastLoginDate = GetTodayStr();
    }

    /**
     * 检查用户是否登录
     * @param context   上下文
     */
    public static void CheckUserLogin(Context context){
        if (SysUtil.StringIsEmpty(_curUserName))
            NoUserLogin(context);
    }

    /**
     * 获取当前登录用户名
     * @return  用户名
     */
    public static String GetCurUserName(Context context){
        CheckUserLogin(context);

        return _curUserName;
    }

    /**
     * 跳转到Home页
     */
    public static void GoToHomeActivity(Context context){
        context.startActivity(
                new Intent(context, HomeActivity.class)
        );
    }

    /**
     * 跳转到Register页
     * @param context
     */
    public static void GoToRegisterActivity(Context context){
        context.startActivity(
                new Intent(context, RegisterActivity.class)
        );
    }

    /**
     * 跳转到分类管理页面
     * @param context
     */
    public static void GoToCategoryManagerActivity(Context context){
        context.startActivity(
                new Intent(context, CategoryManagerActivity.class)
        );
    }

    /**
     * 跳转到查询记录页面
     * @param context
     */
    public static void GoToQueryrActivity(Context context){
        context.startActivity(
                new Intent(context, QueryActivity.class)
        );
    }

    /**
     * 跳转到记账页面
     * @param context
     */
    public static void GoToMainActivity(Context context){
        context.startActivity(
                new Intent(context, MainActivity.class)
        );
    }

    /**
     * 跳转到Login页
     * @param context
     */
    public static void GoToLoginActivity(Context context){
        // 清空本地缓存数据
        SysUtil.ClearSharedPrefByKeyName(context, SysConst._SHARED_PREF_USER_INFO);
        // 跳转到登陆界面
        context.startActivity(
                new Intent(context, LoginActivity.class)
        );
    }

    /**
     * 无用户登录情况
     * @param context   当前页面上下文
     */
    private static void NoUserLogin(Context context) {
        // 提示
        SysUtil.ShowMsgToUser(context, "请先登录,再操作.");
        // 清空用户缓存数据
        SysUtil.ClearSharedPrefByKeyName(context, SysConst._SHARED_PREF_USER_INFO);
        // 返回登录界面
        context.startActivity(
                new Intent(context, LoginActivity.class)
        );
    }
}
