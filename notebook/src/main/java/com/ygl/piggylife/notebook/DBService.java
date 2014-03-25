package com.ygl.piggylife.notebook;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite数据库服务类
 * Created by yanggavin on 14-3-14.
 */
public class DBService extends SQLiteOpenHelper {

    private static DBService _curDB = null;

    /**
     * 初始化内部数据库对象
     * @param context   上下文
     */
    private static void Init(Context context){
        if (_curDB == null)
            _curDB = new DBService(context);
    }

    /**
     * 获取当前数据库对象
     * @param context   上下文
     * @return
     */
    public static DBService CurDB(Context context){
        if (_curDB == null)
            Init(context);

        return _curDB;
    }


    public DBService(Context context) {
        super(context, SysConst._DB_NAME, null, SysConst._DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE [dailynotes]([_id] AUTOINC, "
                + "[userid] VARCHAR(50) NOT NULL ON CONFLICT FAIL, "
                + "[moneytype] VARCHAR(10) NOT NULL, "
                + "[amount] FLOAT NOT NULL, "
                + "[category] VARCHAR(10) NOT NULL, "
                + "[remark] VARCHAR(100), "
                + "[createtime] VARCHAR(50) NOT NULL, "
                + "[lastmodifiedtime] VARCHAR(50) NOT NULL, "
                + "[stattype] VARCHAR(1) NOT NULL, "
                + "CONSTRAINT [autoindex_dailynotes_1] PRIMARY KEY ([_id]))";

        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

        // 清除原有数据表
        String sql = "DROP TABLE IF EXISTS [dailynotes]";
        sqLiteDatabase.execSQL(sql);

        // 更新新的数据表
        sql = "CREATE TABLE [dailynotes]([_id] AUTOINC, "
                + "[userid] VARCHAR(50) NOT NULL ON CONFLICT FAIL, "
                + "[moneytype] VARCHAR(10) NOT NULL, "
                + "[amount] FLOAT NOT NULL, "
                + "[category] VARCHAR(10) NOT NULL, "
                + "[remark] VARCHAR(100), "
                + "[createtime] VARCHAR(50) NOT NULL, "
                + "[lastmodifiedtime] VARCHAR(50) NOT NULL, "
                + "[stattype] VARCHAR(1) NOT NULL, "
                + "[noteid] VARCHAR(10) NOT NULL, "
                + "CONSTRAINT [autoindex_dailynotes_1] PRIMARY KEY ([_id]))";

        sqLiteDatabase.execSQL(sql);
    }

    /**
     * 查询数据
     * @param sql   查询语句
     * @param args  语句参数
     * @return      查询结果
     */
    public Cursor Query(String sql, String[] args){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, args);
        return cursor;
    }

    /**
     * 执行非查询操作
     * @param sql   sql语句
     * @param args  数据参数
     */
    public void Execute(String sql, String[] args){
        this.getWritableDatabase().rawQuery(sql, args);
    }

    /**
     * 插入一条记录
     * @param args
     */
    public void InsertNote(Object[] args){
        String sql = "INSERT INTO dailynotes(userid, moneytype, amount, category, remark, createtime, lastmodifiedtime, stattype, noteid) VALUES(?,?,?,?,?,?,?,?,?)";
        this.getWritableDatabase().execSQL(sql, args);
    }

    /**
     * 根据noteidid删除某条记录
     * @param noteid    记录id
     */
    public void DeleteNoteById(String noteid){
        String sql = "DELETE FROM dailynotes WHERE noteid=?";
        String[] args = new String[]{ noteid };

        this.getWritableDatabase().execSQL(sql, args);
    }

    /**
     * 根据用户名查询所有记录数据
     * @param userName
     * @return
     */
    public Cursor QueryNotesByUserName(String userName){
        String sql = "SELECT * FROM dailynotes WHERE userid=? ORDER BY _id DESC";
        String[] args = new String[]{userName};

        return this.getReadableDatabase().rawQuery(sql, args);
    }

    /**
     * 清空数据库所有数据（调试期间使用）
     */
    public void ClearAllData(){
        String sql = "DELETE FROM [dailynotes] WHERE userid='ygl'";
        SQLiteDatabase db = this.getWritableDatabase();
        if (db != null) {
            db.execSQL(sql);
        }
    }
}
