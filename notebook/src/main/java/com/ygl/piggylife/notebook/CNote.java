package com.ygl.piggylife.notebook;

/**
 * 系统使用基础类
 * Created by yanggavin on 14-3-20.
 */
public class CNote {
    public String UserName = "";
    public String NoteType = "";
    public float Amount = 0;
    public String CategoryName = "";
    public String CategoryValue = "";
    public String Remark = "";
    public String CreateTime = "";
    public String LastModifiedTime = "";
    public String StatType = "";
    public String NoteId = "";


    public CNote(
            String userName,
            String noteType,
            float amount,
            String categoryName,
            String categoryValue,
            String remark,
            String createTime,
            String lastModifiedTime,
            String statType,
            String noteId){

        UserName = userName;
        NoteType = noteType;
        Amount = amount;
        CategoryName = categoryName;
        CategoryValue = categoryValue;
        Remark = remark;
        CreateTime = createTime;
        LastModifiedTime = lastModifiedTime;
        StatType = statType;
        NoteId = noteId;
    }
}


