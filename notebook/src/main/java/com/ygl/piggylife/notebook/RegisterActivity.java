package com.ygl.piggylife.notebook;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.ygl.piggylife.notebook.SysUtil.ShowMsgToUser;
import static com.ygl.piggylife.notebook.SysUtil.StringIsEmpty;


public class RegisterActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ((Button)findViewById(R.id.btn_register_go)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 向服务端发送注册请求
                RegisterUserToServer();
            }
        });
    }

    /**
     * 向服务器端发送注册用户请求
     */
    private void RegisterUserToServer(){
        String userName = ((EditText)findViewById(R.id.et_register_username)).getText().toString();
        String psw = ((EditText)findViewById(R.id.et_register_password)).getText().toString();
        String confirm_psw = ((EditText)findViewById(R.id.et_register_confirm_password)).getText().toString();

        // 检查用户输入
        if (CheckUserInput(userName, psw, confirm_psw)) {
            // post参数
            List<NameValuePair> nvp = new ArrayList<NameValuePair>();
            nvp.add(new BasicNameValuePair("username", userName));
            nvp.add(new BasicNameValuePair("passwd", psw));
            nvp.add(new BasicNameValuePair("passwd2", confirm_psw));
            nvp.add(new BasicNameValuePair("email", "android_client@ygl.com"));

            // 发送，获取
            HttpPostMgr hpm = new HttpPostMgr(SysConst._REQUEST_URL_REGISTER_USER, nvp);
            String res = hpm.GetPostResContent();

            // 判断结果，并进行跳转
            CheckResResult(res);
        }
    }

    /**
     * 根据返回结果，进行相关处理
     * @param res    返回结果字符串
     */
    private void CheckResResult(String res){
        String msg = "";
        Boolean success = false;

        if (res.equals("success")){
            msg = "注册成功";
            success = true;
        }
        else if (res.equals("exist")){
            msg = "该账号已被注册,换个试试.";
        }
        else{
            msg = "注册失败";
        }

        // 提示
        ShowMsgToUser(this, msg);

        // 结果处理
        if (success){
            UserAuthMgr.GoToLoginActivity(this);
        }
        else{
            ResetActivity(false);
        }
    }

    /**
     * 检查用户输入内容
     * @param userName      用户名
     * @param psw           密码
     * @param confirm_psw   重复密码
     * @return              是否通过
     */
    private Boolean CheckUserInput(String userName, String psw, String confirm_psw){
        Boolean isValid = false;

        // 判断用户名是否为空
        if (StringIsEmpty(userName)){
            ShowMsgToUser(this, "账户不能为空.");
        }
        // 密码不能为空
        else if (psw.length() == 0 || confirm_psw.length() == 0){
            ShowMsgToUser(this, "密码不能为空.");
        }
        // 判断两次密码是否一致
        else if (!psw.equals(confirm_psw)){
            ShowMsgToUser(this, "两次输入的密码不一致，请重试.");
            ResetActivity(true);
        }
        // 密码长度需大于6位，且小于16位字符
        else if (psw.length() < 6 || psw.length() > 16){
            ShowMsgToUser(this, "密码长度需大于6位，且小于16位字符.");
            ResetActivity(true);
        }
        else{
            isValid = true;
        }

        return isValid;
    }

    /**
     * 重置页面文本框
     * @param pswOnly   是否仅重置密码框
     */
    private void ResetActivity(Boolean pswOnly){
        if (!pswOnly) {
            ((EditText)findViewById(R.id.et_register_username)).setText("");
        }
        ((EditText)findViewById(R.id.et_register_password)).setText("");
        ((EditText)findViewById(R.id.et_register_confirm_password)).setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);
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

}
