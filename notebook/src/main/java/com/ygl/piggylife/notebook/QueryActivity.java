package com.ygl.piggylife.notebook;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.media.JetPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ygl.piggylife.notebook.SysCommon.CheckUserLoginStatus;
import static com.ygl.piggylife.notebook.SysUtil.ShowMsgToUser;
import static com.ygl.piggylife.notebook.SysUtil.StringIsEmpty;


public class QueryActivity extends ListActivity {

    private List<Map<String, Object>> _notesList = null;
    private Map<String, Object> _selectedItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 用户登录状态检查
        UserAuthMgr.CheckUserLogin(this);

        // 从服务器获取近一个月的记录
        GetLatestMonthNotesFromServer();

        // 绑定记录数据到页面
        BindDataToListView();

        // 绑定事件
        BindEvents();
    }

    /**
     * 从服务器获取用户近一个月内的所有数据
     */
    private void GetLatestMonthNotesFromServer(){
        // 请求参数
        List<NameValuePair> nvp = new ArrayList<NameValuePair>();
        nvp.add(new BasicNameValuePair("user_name", UserAuthMgr.GetCurUserName(this)));
        nvp.add(new BasicNameValuePair("operation_type", SysConst._OPERATION_TYPE_GET_LATEST_MONTH_NOTES));

        // 执行Post请求，并获取结果
        HttpPostMgr hpm = new HttpPostMgr(SysConst._REQUEST_URL_GET_LATEST_MONTH_NOTES, nvp);
        String res = hpm.GetPostResContentWithGBK();

        // 处理请求结果
        if (!StringIsEmpty(res)){
            // 清空原有数组
            if (_notesList != null)
                _notesList.clear();

            // 实例化记录json字符串为数组
            Gson gson = new Gson();
            Type t = new TypeToken<List<CNoteJson>>(){}.getType();
            List<CNoteJson> notes = gson.fromJson(res, t);

            // 获取可绑定数组
            _notesList = GetAdapterList(notes);
        }
    }

    /**
     * 绑定记录到页面
     */
    private void BindDataToListView(){

        if (_notesList != null){
            SimpleAdapter scAdapter = new SimpleAdapter(this,
                    _notesList,
                    R.layout.activity_query,
                    new String[]{"amount", "category", "remark", "createtime", "stattype", "noteid"},
                    new int[]{
                            R.id.tv_query_amount,
                            R.id.tv_query_category,
                            R.id.tv_query_remark,
                            R.id.tv_query_createtime,
                            R.id.tv_query_stattype,
                            R.id.tv_query_noteid
                    });

            setListAdapter(scAdapter);
        }
        else {
            ShowMsgToUser(this, "查询数据失败.");
        }
    }

/*    *//**
     * 查询读取并绑定记录数据到ListView
     *//*
    private void QueryAndBindDataToListView(){
        // 查询本地数据库记录，并显示
        Cursor cursor = DBService.CurDB(this).QueryNotesByUserName(UserAuthMgr.GetCurUserName(this));

        _notesList = GetAdapterListByCursor(cursor);

        SimpleAdapter scAdapter = new SimpleAdapter(this,
                _notesList,
                R.layout.activity_query,
                new String[]{"amount", "category", "remark", "createtime", "stattype", "noteid"},
                new int[]{
                        R.id.tv_query_amount,
                        R.id.tv_query_category,
                        R.id.tv_query_remark,
                        R.id.tv_query_createtime,
                        R.id.tv_query_stattype,
                        R.id.tv_query_noteid
                });

        setListAdapter(scAdapter);
    }*/

    /**
     * 绑定页面事件
     */
    private void BindEvents(){
        ListView lv = getListView();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                _selectedItem = _notesList.get(i);
                // 询问是否删除该条记录
                AskForDeleteNoteItem();
                return false;
            }
        });
    }

    /**
     * 询问是否删除该条记录
     */
    private void AskForDeleteNoteItem(){

        new AlertDialog.Builder(this).setIcon(null).setTitle("是否删除该条记录？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String msg = "删除失败.";
                        // 删除记录
                        if (DeleteNoteItem()){
                            msg = "删除成功";
                            RemoveItemFromList();
                        }
                        // 显示信息
                        ShowMsgToUser(QueryActivity.this, msg);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    /**
     * 从当前列表清除所选记录
     */
    private void RemoveItemFromList(){
        // 删除内部成员列表
        _notesList.remove(_selectedItem);
        // 重新加载列表
        BindDataToListView();
    }

    /**
     * 删除服务器端一条记录
     */
    private Boolean DeleteNoteItem(){
        Boolean success = false;

        String noteId = _selectedItem.get("noteid").toString();
        // 清除服务端数据
        // 整理发送数据
        List<NameValuePair> nvp = new ArrayList<NameValuePair>();
        nvp.add(new BasicNameValuePair("id", noteId));
        nvp.add(new BasicNameValuePair("operation_type", SysConst._OPERATION_TYPE_DELETE_ONE_NOTE_BY_ID));

        HttpPostMgr hpm = new HttpPostMgr(SysConst._REQUEST_URL_DELETE_ONE_NOTE_BY_ID, nvp);
        String res = hpm.GetPostResContent();

        if (res.equals("success"))
            success = true;

        return success;
    }

    /**
     * 根据CNoteJson数组，获取可绑定数据列表
     * @param notes     记录数组
     * @return          数组集合
     */
    private List<Map<String, Object>> GetAdapterList(List<CNoteJson> notes){
        List<Map<String, Object>> adapter = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = null;
        CNoteJson note = null;
        for (int i=0; i<notes.size(); i++){
            map = new HashMap<String, Object>();
            note = notes.get(i);

            String nType = note.moneytype;
            map.put("remark",
                    SysUtil.UnEscapeUnicode(note.remark));
            map.put("createtime",
                    DecorateCreateTimeString(note.createtime)
            );
            map.put("amount",
                    DecorateAmountString(
                            note.amount.toString(),
                            nType
                    )
            );
            map.put("category",
                    DecorateCategoryString(
                            note.category,
                            nType
                    )
            );
            map.put("stattype",
                    SysCommon.GetStatNameByValue(note.stattype)
            );
            map.put("noteid", note.id);
            adapter.add(map);
        }

        return adapter;
    }

/*    *//**
     * 根据查询到得cursor结果，获取ListView的数组结果集
     * @param cursor    结果游标
     * @return          数组集合
     *//*
    private List<Map<String, Object>> GetAdapterListByCursor(Cursor cursor){
        List<Map<String, Object>> adapter = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = null;
        while (cursor.moveToNext()){
            map = new HashMap<String, Object>();
            String nType = cursor.getString(cursor.getColumnIndex("moneytype"));
            map.put("remark", cursor.getString(cursor.getColumnIndex("remark")));
            map.put("createtime",
                    DecorateCreateTimeString(
                            cursor.getString(cursor.getColumnIndex("createtime"))
                    )
            );
            map.put("amount",
                    DecorateAmountString(
                            cursor.getString(cursor.getColumnIndex("amount")),
                            nType
                    )
            );

            map.put("category",
                    DecorateCategoryString(
                            cursor.getString(cursor.getColumnIndex("category")),
                            nType
                    )
            );
            map.put("stattype",
                    SysCommon.GetStatNameByValue(
                            cursor.getString(cursor.getColumnIndex("stattype"))
                    )
            );
            map.put("noteid", cursor.getString(cursor.getColumnIndex("noteid")));
            adapter.add(map);
        }

        return adapter;
    }*/

    /**
     * 修饰创建时间字符串
     * @param createtime    创建时间字符串
     * @return              可显示字符串
     */
    private String DecorateCreateTimeString(String createtime){
        return createtime.substring(5, 10);
    }

    /**
     * 根据记录类型，修饰分类字符串
     * @param categoryVal   分类数值
     * @param nType         记录类型
     * @return              可显示分类类型字符串
     */
    private String DecorateCategoryString(String categoryVal, String nType){
        String newStr = "";

        if (nType.equals("cost"))
            newStr = "支出：";
        else if (nType.equals("income"))
            newStr = "收入：";

        newStr += CategoryManagerClass.GetCategoryNameByValue(categoryVal);

        return newStr;
    }

    /**
     * 根据记录类型，修饰消费或收入金额数字
     * @param amount    金额数字
     * @param nType     记录类型
     * @return          可显示金额字符串
     */
    private String DecorateAmountString(String amount, String nType){
        String newStr = "";

        if (nType.equals("cost"))
            newStr = "- ";
        else if (nType.equals("income"))
            newStr = "+ ";

        newStr += amount + " 元";

        return newStr;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.query, menu);
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
