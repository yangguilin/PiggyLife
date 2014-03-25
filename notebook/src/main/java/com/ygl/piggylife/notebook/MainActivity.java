package com.ygl.piggylife.notebook;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.LogRecord;

import static com.ygl.piggylife.notebook.SysCommon.CheckUserLoginStatus;
import static com.ygl.piggylife.notebook.SysUtil.GetTodayStr;
import static com.ygl.piggylife.notebook.SysUtil.IsNumber;
import static com.ygl.piggylife.notebook.SysUtil.ShowMsgToUser;
import static com.ygl.piggylife.notebook.SysUtil.StringIsEmpty;

public class MainActivity extends ActionBarActivity {

    // private Handler _handler = null;
    // private CNote _newNote = null;
    // private DBService _curDB = DBService.CurDB(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 用户登录状态检查
        UserAuthMgr.CheckUserLogin(this);

        // 1. 绑定事件
        BindEvents();

        // 2. 初始化下拉列表
        InitSpinnerListData("cost");

//        _handler = new Handler(){
//            @Override
//            public void handleMessage(Message msg){
//                if (msg.what == 1)
//                    SaveNoteToSQLite();
//            }
//        };
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
     * 绑定初始事件
     */
    private void BindEvents()
    {
        // 1. “保存/继续”按钮事件
        findViewById(R.id.btn_ad_save_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 检查输入内容，并保存记录
                CheckAndSaveNote();
                // 重置页面所有元素
                ResetActivityContent();
            }
        });

        // 2. "保存/退出"按钮事件绑定
        ((Button)findViewById(R.id.btn_ad_save_quit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 检查输入内容，并保存记录
                CheckAndSaveNote();
                // 返回HomeActivity
                UserAuthMgr.GoToHomeActivity(MainActivity.this);
            }
        });

        // 3. “退出”按钮事件绑定
        ((Button)findViewById(R.id.btn_ad_quit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 返回HomeActivity
                UserAuthMgr.GoToHomeActivity(MainActivity.this);
            }
        });

        // 6. 操作类型radioGroup因选择内容不同而修改下面的分类列表数据事件绑定
        ((RadioGroup)findViewById(R.id.rg_notetype)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                UpdateCategorySpinnerContentByNoteType(radioGroup);
            }
        });

        // 4. 金额输入框数字修改事件
        ((EditText)findViewById(R.id.et_amount)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                int readyStatusVal = View.VISIBLE;
                int notReadyStatusVal = View.GONE;
                if (!CheckUserInput()){
                    readyStatusVal = View.GONE;
                    notReadyStatusVal = View.VISIBLE;
                }

                findViewById(R.id.btn_ad_save_continue).setVisibility(readyStatusVal);
                findViewById(R.id.btn_ad_save_quit).setVisibility(readyStatusVal);
                findViewById(R.id.btn_ad_quit).setVisibility(notReadyStatusVal);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    /**
     * 检查输入内容，并保存记录
     */
    private void CheckAndSaveNote(){

        // 按钮状态不可用
        SetSaveButtonEnableStatus(false);

        // 检查用户输入内容
        if (CheckUserInput()) {
            // 保存数据数据到服务端
            SaveNoteToDB();
        }

        // 恢复按钮可用状态
        SetSaveButtonEnableStatus(true);
    }

    /**
     * 检查输入内容合法性
     * @return  true:正确；false:有误
     */
    private boolean CheckUserInput(){
        String amount = ((EditText)findViewById(R.id.et_amount)).getText().toString();

        return  (!StringIsEmpty(amount)
                && IsNumber(amount)
                && Integer.parseInt(amount) > 0);
    }


    /**
     * 重置该Activity页面的所有内容
     */
    private void ResetActivityContent(){
        ((RadioButton)findViewById(R.id.rb_notetype_cost)).setChecked(true);
        ((RadioButton)findViewById(R.id.rb_stattype_normal)).setChecked(true);
        ((EditText)findViewById(R.id.et_amount)).setText("");
        ((Spinner)findViewById(R.id.spinner_category)).setSelection(0);
        ((EditText)findViewById(R.id.et_remark)).setText("");
    }

    /**
     * 初始化页面下拉列表的数据
     * @param nType     账目类型
     */
    public void InitSpinnerListData(String nType){
        Spinner sp_category = (Spinner)findViewById(R.id.spinner_category);

        // 根据操作类型，初始化不同的列表内容
        ArrayAdapter<String> adp_category = CategoryManagerClass.GetCategoryArrayAdapterByNoteType(this, nType);
        sp_category.setAdapter(adp_category);
    }

    //
    // 根据所选的操作类型，来更新分类下拉框的内容
    private void UpdateCategorySpinnerContentByNoteType(RadioGroup rg){
        RadioButton rb = (RadioButton)findViewById(rg.getCheckedRadioButtonId());
        String noteType = rb.getText().toString();
        if (noteType.equals("收入")){
            InitSpinnerListData("income");
            ShowStatTypeContentByNoteType(false);
        }
        else
        {
            InitSpinnerListData("cost");
            ShowStatTypeContentByNoteType(true);
        }
    }

    /**
     * 统计类型RadioGroup子项显示控制
     * @param isCost    记录类型是否为支出
     */
    private void ShowStatTypeContentByNoteType(Boolean isCost){
        int v = View.INVISIBLE;
        if (isCost)
            v = View.VISIBLE;

        ((RadioButton)findViewById(R.id.rb_stattype_normal)).setChecked(true);
        findViewById(R.id.rb_stattype_period).setVisibility(v);
        findViewById(R.id.rb_stattype_big).setVisibility(v);
    }

    /**
     * 保存记录到数据库
     */
    public void SaveNoteToDB() {
        String msg = "";

        // 获取控件数据
        CNote note = GetDataFromControls();

        if (note != null) {
            // post参数
            List<NameValuePair> nvp = new ArrayList<NameValuePair>();
            nvp.add(new BasicNameValuePair("user_name", UserAuthMgr.GetCurUserName(this)));
            nvp.add(new BasicNameValuePair("operation_type", SysConst._OPERATION_TYPE_ADD_NOTE));
            nvp.add(new BasicNameValuePair("_type", note.NoteType));
            nvp.add(new BasicNameValuePair("_amount", String.valueOf(note.Amount)));
            nvp.add(new BasicNameValuePair("_category", note.CategoryValue));
            nvp.add(new BasicNameValuePair("_remark", note.Remark));
            nvp.add(new BasicNameValuePair("_datetime", note.CreateTime));
            nvp.add(new BasicNameValuePair("_stattype", note.StatType));

            // 发送，获取
            HttpPostMgr hpm = new HttpPostMgr(SysConst._REQUEST_URL_ADD_NOTE, nvp);
            String res = hpm.GetPostResContent();

            // 处理结果
            if (res.toLowerCase().equals("success")) {
                msg = "添加成功";
            }else{
                msg = "添加失败";
            }
        }else{
            msg = "输入数据有误,请修改";
        }

        ShowMsgToUser(this, msg);
    }

    /**
     * 获取页面数据，并返回CNote实例
     * @return  CNote实例
     */
    private CNote GetDataFromControls(){
        CNote nNote = null;

        // 1. 操作类型
        RadioGroup rg_noteType = (RadioGroup)findViewById(R.id.rg_notetype);
        RadioButton rb_noteType = (RadioButton)findViewById(rg_noteType.getCheckedRadioButtonId());
        String pType = SysCommon.GetProcTypeValueByName(rb_noteType.getText().toString());

        // 2. 金额
        String amountStr = ((EditText)findViewById(R.id.et_amount)).getText().toString();
        int amount = 0;
        if(SysUtil.IsNumber(amountStr))
            amount = Integer.parseInt(amountStr);

        // 3. 分类
        String categoryName = ((Spinner)findViewById(R.id.spinner_category)).getSelectedItem().toString();
        String categoryVal = CategoryManagerClass.GetCategoryValueByName(categoryName);

        // 4. 统计类型
        RadioGroup rg_statType = (RadioGroup)findViewById(R.id.rg_stattype);
        RadioButton rb_statType = (RadioButton)findViewById(rg_statType.getCheckedRadioButtonId());
        String statType = SysCommon.GetStatTypeValueByName(rb_statType.getText().toString());

        // 5. 备注
        String remark = ((EditText)findViewById(R.id.et_remark)).getText().toString();

        // 6. 保存内部对象
        nNote = new CNote(
                UserAuthMgr.GetCurUserName(this),
                pType,
                amount,
                categoryName,
                categoryVal,
                remark,
                GetTodayStr(),
                GetTodayStr(),
                statType,
                "-1");

        return nNote;
    }

    /**
     * 设置保存按钮的可用状态
     * @param enable    是否可用
     */
    private void SetSaveButtonEnableStatus(boolean enable){
        findViewById(R.id.btn_ad_save_continue).setEnabled(enable);
        findViewById(R.id.btn_ad_save_quit).setEnabled(enable);
    }


    /**
     * 保存记录到本地的SQLite
     */
 /*   private void SaveNoteToSQLite(){
        // 获取刚插入的记录noteid
        Thread thread = new Thread(){
            @Override
            public void run(){
                GetNewNoteIdInServer();
            }
        };
        thread.start();

        while (true) {
            if (thread.getState() == Thread.State.TERMINATED)
                break;
        }

        // 插入数据到本地SQLite
        Object[] args = new Object[]{
                _newNote.UserName,
                _newNote.NoteType,
                _newNote.Amount,
                _newNote.CategoryValue,
                _newNote.Remark,
                _newNote.CreateTime,
                _newNote.LastModifiedTime,
                _newNote.StatType,
                _newNote.NoteId
        };

        _curDB.InsertNote(args);
    }*/

    /**
     * 获取用户刚插入的最新记录的noteid
     */
/*    private void GetNewNoteIdInServer(){
        String newId = "";

        // 整理发送数据
        String reqUrl = SysConst._REQUEST_URL_GET_USER_NOTE_MAX_ID;
        List<NameValuePair> nvp = new ArrayList<NameValuePair>();
        nvp.add(new BasicNameValuePair("user_name", _userName));
        nvp.add(new BasicNameValuePair("operation_type", SysConst._OPERATION_TYPE_GET_USER_NOTE_MAX_ID));


        // 发送用户验证请求
        String resContent = HttpPostToServer2(reqUrl, nvp);

        if (!StringIsEmpty(resContent))
            newId = resContent;

        _newNote.NoteId = newId;
    }*/
}
