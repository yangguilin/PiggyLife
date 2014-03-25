package com.ygl.piggylife.notebook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.ygl.piggylife.notebook.SysUtil.GetTodayStr;
import static com.ygl.piggylife.notebook.SysUtil.ShowMsgToUser;
import static com.ygl.piggylife.notebook.SysUtil.StringIsEmpty;
import static com.ygl.piggylife.notebook.SysUtil.SubDateStrForDayValue;

public class LoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 绑定事件
        BindEvents();

        // 判断本地保存的账户信息，若没有过期自动跳转，否则重新输入
        AuthLocalUserInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // 根据所选菜单id执行不同动作
        SysCommon.DoSomethingBySelectedMenuId(this, item.getItemId());


        return super.onOptionsItemSelected(item);
    }

    //
    // 绑定页面事件
    private void BindEvents()
    {
        // 用户登陆事件绑定
        ((Button)findViewById(R.id.login_go)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthUserAndDirectActivity();
            }
        });

        // 用户注册事件绑定
        ((Button)findViewById(R.id.btn_login_register)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserAuthMgr.GoToRegisterActivity(LoginActivity.this);
            }
        });
    }

    /**
     * 判断本地保存的账户信息，若没有过期自动跳转，否则重新输入
     */
    private void AuthLocalUserInfo()
    {
        SharedPreferences sharedPref = getSharedPreferences(SysConst._SHARED_PREF_USER_INFO, 0);
        if (sharedPref.getAll().size() > 0){
            String userName = sharedPref.getString("username", "");
            String lastLoginDate = sharedPref.getString("lastlogindate", "");

            // 自动填充用户名
            ((EditText)findViewById(R.id.login_username)).setText(userName);
            ((EditText)findViewById(R.id.login_password)).setText("123456");

            // 判断最后登录时间，3天内直接登录
            if (SubDateStrForDayValue(lastLoginDate, GetTodayStr()) <= 3){
                // 保存用户验证信息
                UserAuthMgr.Init(userName);
                // 自动跳转页面
                (new Timer()).schedule(new TimerTask() {
                    @Override
                    public void run() {
                        UserAuthMgr.GoToHomeActivity(LoginActivity.this);
                    }
                }, 100);
            }
        }
    }

    /**
     * 验证用户密码是否正确，并实现自动跳转
     */
    private void AuthUserAndDirectActivity() {
        String userName = ((EditText)findViewById(R.id.login_username)).getText().toString().trim();
        String password = ((EditText)findViewById(R.id.login_password)).getText().toString();

        if (StringIsEmpty(userName) || StringIsEmpty(password)) {
            ShowMsgToUser(this, "账户或密码不能为空！");
        }

        // 准备发送数据，并发送请求，获取数据
        List<NameValuePair> nvp = new ArrayList<NameValuePair>();
        nvp.add(new BasicNameValuePair("name", userName));
        nvp.add(new BasicNameValuePair("password", password));

        HttpPostMgr hpm = new HttpPostMgr(SysConst._REQUEST_URL_USER_LOGIN, nvp);
        String res = hpm.GetPostResContent();

        // 处理结果
        if (res.equals("success")){
            // 保存用户验证信息
            UserAuthMgr.Init(userName);
            // 保存当前登录用户信息到SharedPreferences
            SaveUserInfoToSharedPref();
            // 跳转Activity
            UserAuthMgr.GoToHomeActivity(this);
        }
        else{
            ShowMsgToUser(this, "账户或密码不匹配！");
        }
    }

    /**
     * 保存当前登录用户信息到SharedPreferences
     */
    private void SaveUserInfoToSharedPref()
    {
        // 将用户名和密码保存到SharePref
        SharedPreferences sharedPref = getSharedPreferences(SysConst._SHARED_PREF_USER_INFO, 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username", UserAuthMgr.GetCurUserName(this));
        editor.putString("lastlogindate", GetTodayStr());
        editor.commit();
    }
}
