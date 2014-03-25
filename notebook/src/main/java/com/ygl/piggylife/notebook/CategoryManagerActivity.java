package com.ygl.piggylife.notebook;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import java.util.Map;

public class CategoryManagerActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 用户登录状态检查
        UserAuthMgr.CheckUserLogin(this);

        // 实例化SimpleAdapter
        SimpleAdapter adapter =
                new SimpleAdapter(
                        this,
                        CategoryManagerClass.GetAdapterList(),
                        R.layout.activity_category_manager,
                        new String[]{"name","value", "description", "type"},
                        new int[]{R.id.tv_name, R.id.tv_value, R.id.tv_description, R.id.tv_categorymgr_type});

        // 绑定数据到ListView
        setListAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.category_manager, menu);
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

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id){
        super.onListItemClick(l, v, position, id);

        Map<String, Object> curItem = CategoryManagerClass.GetAdapterList().get(position);

        Intent intent = new Intent(this, CategoryEditActivity.class);
        intent.putExtra("c_name", curItem.get("name").toString());
        intent.putExtra("c_type",
                SysCommon.GetCategoryTypeByValue(curItem.get("value").toString())
        );
        intent.putExtra("c_value", curItem.get("value").toString());
        intent.putExtra("c_description", curItem.get("description").toString());

        startActivity(intent);
    }
}
