package com.ygl.piggylife.notebook;

import java.security.ProtectionDomain;

/**
 * 系统相关常量
 * Created by yanggavin on 3/11/14.
 */
public class SysConst {

    // 访问链接
    protected static final String _REQUEST_URL_USER_LOGIN = "http://sunfloweryouandme.org/piggynotes/m_login.php";
    protected static final String _REQUEST_URL_ADD_NOTE = "http://sunfloweryouandme.org/piggynotes/financial_process.php";
    protected static final String _REQUEST_URL_QUERY_CURRENT_MONTH_COST = "http://sunfloweryouandme.org/piggynotes/financial_process.php";
    protected static final String _REQUEST_URL_GET_CATEGORY_LIST = "http://sunfloweryouandme.org/piggynotes/financial_process.php";
    protected static final String _REQUEST_URL_UPDATE_CATEGORY_ITEM = "http://sunfloweryouandme.org/piggynotes/financial_process.php";
    protected static final String _REQUEST_URL_GET_USER_NOTE_MAX_ID = "http://sunfloweryouandme.org/piggynotes/financial_process.php";
    protected static final String _REQUEST_URL_DELETE_ONE_NOTE_BY_ID = "http://sunfloweryouandme.org/piggynotes/financial_process.php";
    protected static final String _REQUEST_URL_GET_LATEST_MONTH_NOTES = "http://sunfloweryouandme.org/piggynotes/financial_process.php";
    protected static final String _REQUEST_URL_REGISTER_USER = "http://sunfloweryouandme.org/piggynotes/m_register.php";

    // 请求参数常量
    protected static final String _OPERATION_TYPE_GET_USER_SUMMARY_DATA = "get_user_summary_data";
    protected static final String _OPERATION_TYPE_ADD_NOTE = "add";
    protected static final String _OPERATION_TYPE_GET_CATEGORY_LIST = "get_user_category_data";
    protected static final String _OPERATION_TYPE_UPDATE_CATEGORY_ITEM = "update_category_item";
    protected static final String _OPERATION_TYPE_GET_USER_NOTE_MAX_ID = "query_user_note_max_id";
    protected static final String _OPERATION_TYPE_DELETE_ONE_NOTE_BY_ID = "delete";
    protected static final String _OPERATION_TYPE_GET_LATEST_MONTH_NOTES = "get_latest_month_notes";


    // SharedPref保存Key值
    protected static final String _SHARED_PREF_USER_INFO = "username";
    protected static final String _SHARED_PREF_CATEGORY_LIST = "category_list";

    // 分类数据内容分隔符
    protected static final String _SPLIT_CHAR_CATEGORY_DATA = "::";

    // 数据库
    protected static final String _DB_NAME = "piggy_financial.db";
    protected static final int _DB_VERSION = 3;



}
