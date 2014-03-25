package com.ygl.piggylife.notebook;

import android.content.Intent;
import android.opengl.Visibility;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.ygl.piggylife.notebook.SysCommon.CheckUserLoginStatus;
import static com.ygl.piggylife.notebook.SysUtil.ShowMsgToUser;
import static com.ygl.piggylife.notebook.SysUtil.StringIsEmpty;

public class CategoryEditActivity extends ActionBarActivity {

    private CCategory _category = new CCategory();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_edit);

        // 用户登录状态检查
        UserAuthMgr.CheckUserLogin(this);

        // 获取Activity传递的参数
        GetParasFromExtra();
        
        // 初始化页面元素
        InitContent();
        
        // 初始化事件
        BindEvents();
    }

    /**
     * 绑定页面事件
     */
    private void BindEvents() {

        // 更新按钮
        ((Button)findViewById(R.id.btn_ce_update)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 置灰按钮
                SetSaveButtonEnableStatus(false);

                // 更新分类条目
                String cName = ((EditText)findViewById(R.id.et_ce_name)).getText().toString();
                String cDescription = ((EditText)findViewById(R.id.et_ce_description)).getText().toString();


                Boolean ret = CategoryManagerClass.UpdateCategoryItem(
                        CategoryEditActivity.this,
                        _category.Value,
                        cName,
                        cDescription,
                        "0");

                // 提示
                String msg = "更新失败";
                if (ret){
                    msg = "更新成功";
                }
                ShowMsgToUser(CategoryEditActivity.this, msg);

                // 返回分类列表页面
                UserAuthMgr.GoToCategoryManagerActivity(CategoryEditActivity.this);
            }
        });

        // 返回按钮
        ((Button)findViewById(R.id.btn_ce_return)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserAuthMgr.GoToCategoryManagerActivity(CategoryEditActivity.this);
            }
        });

        // 分类名称和描述内容修改事件
        ((EditText)findViewById(R.id.et_ce_name)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                // 更新按钮显示状态
                UpdateProButtonStatusByCategoryInfoModify();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ((EditText)findViewById(R.id.et_ce_description)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                // 更新按钮显示状态
                UpdateProButtonStatusByCategoryInfoModify();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    /**
     * 根据分类信息修改来更新操作按钮的显示状态
     */
    private void UpdateProButtonStatusByCategoryInfoModify(){
        String nName = ((EditText)findViewById(R.id.et_ce_name)).getText().toString();
        String nDescription = ((EditText)findViewById(R.id.et_ce_description)).getText().toString();

        int updateBtnStatus = View.GONE;    // 更新按钮显示状态
        int returnBtnStatus = View.VISIBLE;     // 返回按钮显示状态
        if ((!nName.equals(_category.Name) || !nDescription.equals(_category.Description))
                && !StringIsEmpty(nName)){

            updateBtnStatus = View.VISIBLE;
            returnBtnStatus = View.GONE;
        }

        findViewById(R.id.btn_ce_return).setVisibility(returnBtnStatus);
        findViewById(R.id.btn_ce_update).setVisibility(updateBtnStatus);
    }

    /**
     * 初始化页面内容
     */
    private void InitContent() {
        ((TextView)findViewById(R.id.tv_ce_notetype)).setText(_category.TypeName);
        ((EditText)findViewById(R.id.et_ce_name)).setText(_category.Name);
        ((EditText)findViewById(R.id.et_ce_description)).setText(_category.Description);
    }

    /**
     * 获取Activity传递的参数
     */
    private void GetParasFromExtra(){
        _category.Name = this.getIntent().getStringExtra("c_name");
        _category.TypeName = this.getIntent().getStringExtra("c_type");
        _category.Value = this.getIntent().getStringExtra("c_value");
        _category.Description = this.getIntent().getStringExtra("c_description");

        if (StringIsEmpty(_category.Name)
                || StringIsEmpty(_category.TypeName)
                || StringIsEmpty(_category.Value)){
            ShowMsgToUser(this, "页面传递参数出错.");
        }
    }

    /**
     * 设置保存按钮的可用状态
     * @param enable    是否可用
     */
    private void SetSaveButtonEnableStatus(boolean enable)
    {
        findViewById(R.id.btn_ce_return).setEnabled(enable);
        findViewById(R.id.btn_ce_update).setEnabled(enable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.category_edit, menu);
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
