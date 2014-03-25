package com.ygl.piggylife.notebook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.ygl.piggylife.notebook.SysCommon.CheckUserLoginStatus;
import static com.ygl.piggylife.notebook.SysUtil.ShowMsgToUser;
import static com.ygl.piggylife.notebook.SysUtil.StringIsEmpty;

public class HomeActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 用户登录状态检查
        UserAuthMgr.CheckUserLogin(this);

        // 初始化分类数据
        CategoryManagerClass.Init(this, UserAuthMgr.GetCurUserName(this));

        // 绑定元素事件
        BindEvents();

        // 获取用户本月已消费金额数据
        GetUserCurMonthCostTotalAndShow();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
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

    /**
     * 页面元素事件绑定
     */
    private void BindEvents()
    {
        // 绑定“记一笔”按钮事件
        findViewById(R.id.btn_add_note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserAuthMgr.GoToMainActivity(HomeActivity.this);
            }
        });

        // “分类管理”按钮
        findViewById(R.id.btn_home_categorymgr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserAuthMgr.GoToCategoryManagerActivity(HomeActivity.this);
            }
        });

        // “近期记录”按钮
        findViewById(R.id.btn_home_latestnotes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserAuthMgr.GoToQueryrActivity(HomeActivity.this);
            }
        });
    }

    /**
     * 获取用户本月已消费金额数据
     */
    private void GetUserCurMonthCostTotalAndShow()
    {
        // 准备发送url及发送键值对
        String reqUrl = SysConst._REQUEST_URL_QUERY_CURRENT_MONTH_COST;

        List<NameValuePair> nvp = new ArrayList<NameValuePair>();
        nvp.add(new BasicNameValuePair("user_name", UserAuthMgr.GetCurUserName(this)));
        nvp.add(new BasicNameValuePair("operation_type", SysConst._OPERATION_TYPE_GET_USER_SUMMARY_DATA));

        HttpPostMgr hpm = new HttpPostMgr(reqUrl, nvp);
        String res = hpm.GetPostResContent();

        // 获取数据
        String[] arr = res.split(",");
        ((TextView)findViewById(R.id.home_tv_cur_month_cost)).setText(arr[3]);
    }
}
