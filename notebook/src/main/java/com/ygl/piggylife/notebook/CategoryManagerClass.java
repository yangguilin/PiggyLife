package com.ygl.piggylife.notebook;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.ygl.piggylife.notebook.SysUtil.ShowMsgToUser;
import static com.ygl.piggylife.notebook.SysUtil.StringIsEmpty;

/**
 * 用户分类信息管理类
 * Created by yanggavin on 3/13/14.
 */
public class CategoryManagerClass {

    private static List<Map<String, Object>> _categoryList = new ArrayList<Map<String, Object>>();
    private static String _userName = "";


    /**
     * 初始化分类列表数据
     * @param context   上下文
     * @param userName  用户名
     */
    public static void Init(Context context, String userName){
        _userName = userName;

        // 清空内部数组
        _categoryList.clear();

        // 从服务器获取分类数据
        LoadDataFromServer();

        // 保存分类信息到SharedPref中
        SaveDataToSharedPref(context);
    }

    /**
     * 获取分类数据AdapterList，用于绑定ListView
     * @return      适配数据数组
     */
    public static List<Map<String, Object>> GetAdapterList() {
        return _categoryList;
    }

    /**
     * 根据账目类型来获取不同的分类列表（收入/支出）
     * @param context   上下文
     * @param nType     账目类型
     * @return          分类列表
     */
    public static ArrayAdapter<String> GetCategoryArrayAdapterByNoteType(Context context, String nType){
        ArrayAdapter<String> ad = null;

        ArrayList<String> incomeArr = new ArrayList<String>();
        ArrayList<String> costArr = new ArrayList<String>();
        for (Map<String, Object> item : _categoryList){
            String cName = item.get("name").toString();
            if (item.get("value").toString().length() >= 3)
                incomeArr.add(cName);
            else
                costArr.add(cName);
        }

        if (nType.equals("cost"))
            ad = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, costArr);
        else if (nType.equals("income"))
            ad = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, incomeArr);

        return ad;
    }


    /**
     * 根据分类名称获取分类数值
     * @param cName     分类名称
     * @return          分类数值
     */
    public static String GetCategoryValueByName(String cName){
        String val = "";

        Map<String, Object> item = null;
        for (int i=0; i<_categoryList.size(); i++){
            item = _categoryList.get(i);
            if (item.get("name").equals(cName)){
                val = item.get("value").toString();
                break;
            }
        }

        return val;
    }

    /**
     * 根据分类数值，获取分类名称
     * @param val   分类数值
     * @return      分类名称
     */
    public static String GetCategoryNameByValue(String val){
        String name = "";

        Map<String, Object> item = null;
        for (int i=0; i<_categoryList.size(); i++){
            item = _categoryList.get(i);
            if (item.get("value").equals(val)){
                name = item.get("name").toString();
                break;
            }
        }

        return name;
    }

    /**
     * 更新分类条目
     * @param context   上下文
     * @param cValue    分类值（id）
     * @param cName     分类名称
     * @param cDesc     分类描述
     * @param cTime     使用次数
     * @return          true：成功；false：失败
     */
    public static boolean UpdateCategoryItem(Context context, String cValue, String cName, String cDesc, String cTime){
        if (!StringIsEmpty(_userName)){
            if (UpdateCategoryItemToServer(cValue, cName, cDesc, cTime)
                    && UpdateCategoryItemToSharedPref(context, cValue, cName, cDesc, cTime)
                    && UpdateInnerCategoryList(cValue, cName, cDesc, cTime)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 更新内部分类列表数据
     * @param cValue    分类数值
     * @param cName     分类名称
     * @param cDesc     分类描述
     * @param cTime     分类使用次数
     * @return          true：成功；false：失败
     */
    private static boolean UpdateInnerCategoryList(String cValue, String cName, String cDesc, String cTime){
        boolean ret = true;

        // 更新内部成员变量
        Map<String, Object> item = null;
        for (int i=0; i<_categoryList.size(); i++){
            if (_categoryList.get(i).get("value").toString().equals(cValue)) {
                item = _categoryList.get(i);
                break;
            }
        }

        if (item != null){
            item.put("name", cName);
            item.put("description", cDesc);
        }
        else
            ret = false;

        return ret;
    }

    /**
     * 更新分类条目数据到SharedPref
     * @param context   上下文
     * @param cValue    分类值（id）
     * @param cName     分类名称
     * @param cDesc     分类描述
     * @param cTime     使用次数
     * @return          true：成功；false：失败
     */
    private static boolean UpdateCategoryItemToSharedPref(Context context, String cValue, String cName, String cDesc, String cTime){
        boolean ret = true;

        SharedPreferences sp = context.getSharedPreferences(SysConst._SHARED_PREF_CATEGORY_LIST, 0);
        if (sp.getAll().size() > 0 && sp.getAll().containsKey(cValue)) {
            SharedPreferences.Editor editor = sp.edit();

            String val = cTime
                    + SysConst._SPLIT_CHAR_CATEGORY_DATA
                    + cName
                    + SysConst._SPLIT_CHAR_CATEGORY_DATA
                    + cDesc;

            editor.putString(cValue, val);
            editor.commit();
        }

        return ret;
    }

    /**
     * 更新分类条目数据到服务器
     * @param cValue    分类值（id）
     * @param cName     分类名称
     * @param cDesc     分类描述
     * @param cTime     使用次数
     * @return          true：成功；false：失败
     */
    private static boolean UpdateCategoryItemToServer(String cValue, String cName, String cDesc, String cTime){
        boolean success = false;

        // 准备发送url及发送键值对
        List<NameValuePair> nvp = new ArrayList<NameValuePair>();
        nvp.add(new BasicNameValuePair("user_name", _userName));
        nvp.add(new BasicNameValuePair("operation_type", SysConst._OPERATION_TYPE_UPDATE_CATEGORY_ITEM));
        nvp.add(new BasicNameValuePair("_ctgid", cValue));
        nvp.add(new BasicNameValuePair("_ctgName", cName));
        nvp.add(new BasicNameValuePair("_ctgDes", cDesc));

        // 执行Post请求，并获取结果
        HttpPostMgr hpm = new HttpPostMgr(SysConst._REQUEST_URL_UPDATE_CATEGORY_ITEM, nvp);
        String res = hpm.GetPostResContent();

        // 处理结果
        if (res.equals("success")){
            success = true;
        }

        return success;
    }

    /**
     * 保存分类信息到SharedPref中
     * 保存的键值对格式为：
     * key=value
     * value=time::name::description
     */
    private static void SaveDataToSharedPref(Context context){
        if (_categoryList.size() > 0){
            SharedPreferences sharedPref = context.getSharedPreferences(SysConst._SHARED_PREF_CATEGORY_LIST, 0);
            // 如果存在记录，则首先清空
            if (sharedPref.getAll().size() > 0)
                sharedPref.getAll().clear();

            SharedPreferences.Editor editor = sharedPref.edit();
            Map<String, Object> curItem = null;
            for (int i=0; i<_categoryList.size(); i++){
                curItem = _categoryList.get(i);
                String key = curItem.get("value").toString();
                String val = curItem.get("time")
                        + SysConst._SPLIT_CHAR_CATEGORY_DATA
                        + curItem.get("name")
                        + SysConst._SPLIT_CHAR_CATEGORY_DATA
                        + curItem.get("description");

                editor.putString(key, val);
            }
            editor.commit();
        }
    }

    /**
     * 从sharedPref载入分类数据
     */
    private static void LoadDataFromSharedPref(Context context){
        SharedPreferences sp = context.getSharedPreferences(SysConst._SHARED_PREF_CATEGORY_LIST, 0);
        if (sp.getAll().size() > 0){
            // 清空成员变量
            _categoryList.clear();

            // 循环读取分类数据
            Map<String, Object> cItem = null;
            for (Map.Entry entry : sp.getAll().entrySet()){
                cItem = new HashMap<String, Object>();
                String[] arr = entry.getValue().toString()
                        .split(SysConst._SPLIT_CHAR_CATEGORY_DATA);

                cItem.put("value", entry.getKey());
                cItem.put("time", arr[0]);
                cItem.put("name", arr[1]);
                cItem.put("description", arr[2]);
                cItem.put("type", SysCommon.GetCategoryTypeByValue(entry.getKey().toString()));

                _categoryList.add(cItem);
            }
        }
    }

    /**
     * 载入分类数据
     */
    private static void LoadDataFromServer(){

        // 请求参数
        List<NameValuePair> nvp = new ArrayList<NameValuePair>();
        nvp.add(new BasicNameValuePair("user_name", _userName));
        nvp.add(new BasicNameValuePair("operation_type", SysConst._OPERATION_TYPE_GET_CATEGORY_LIST));

        // 执行Post请求，并获取结果
        HttpPostMgr hpm = new HttpPostMgr(SysConst._REQUEST_URL_GET_CATEGORY_LIST, nvp);
        String res = hpm.GetPostResContent();

        // 处理请求结果
        if (!StringIsEmpty(res))
        {
            // 清空原有数组
            _categoryList.clear();

            // 去除返回分类结果字符串前后的双引号
            res = res.replace("\"", "");

            // 截取字符串，获取分类列表
            String[] cateArr = res.split(";");
            if (cateArr.length == 2){
                String[] cateArrCost = cateArr[0].split(",");
                String[] cateArrIncome = cateArr[1].split(",");

                // 保存支出列表
                SaveCategoryItemIntoList(cateArrCost);
                // 保存收入列表
                SaveCategoryItemIntoList(cateArrIncome);
            }
        }
    }

    /**
     * 将获取的分类数据保存到列表中
     * @param cateArr   分类信息数组
     */
    private static void SaveCategoryItemIntoList(String[] cateArr){

        Map<String, Object> map = null;
        String[] arrItem = null;
        for (int i=0; i<cateArr.length; i++){

            arrItem = cateArr[i].split(":");
            map = new HashMap<String, Object>();
            map.put("value", arrItem[0]);
            map.put("name", SysUtil.UnEscapeUnicode(arrItem[1]));
            map.put("description", SysUtil.UnEscapeUnicode(arrItem[2]));
            map.put("time", arrItem[3]);
            map.put("type", SysCommon.GetCategoryTypeByValue(arrItem[0]));

            _categoryList.add(map);
        }
    }
}
