package cn.firgavin.iradar;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.baidu.platform.comapi.map.E;

import java.util.ArrayList;
import java.util.List;

public class EnemyList extends AppCompatActivity {

    private StaticStorage publicStorage;
    private static boolean initFlagOfEnemy = false; //标识第一次初始化

    private Button btnReturn = null;
    private Button btnEdit = null;
    private Button btnAdd = null;

    private List<Contacts> EnemyList = new ArrayList<Contacts>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enemy_list);

        //initEnemy();
        if(!initFlagOfEnemy) {
            initEnemy();
            publicStorage.enemyList = EnemyList;
            initFlagOfEnemy = true;
        }
        EnemyAdapter adapter = new EnemyAdapter(EnemyList.this,R.layout.enemy_item,publicStorage.enemyList);
        ListView listview = (ListView) findViewById(R.id.listViewOfEnemy);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {

                LayoutInflater factory = LayoutInflater.from(EnemyList.this);
                final View textEntryView = factory.inflate(R.layout.logindialog,null);
                AlertDialog dlg = new AlertDialog.Builder(EnemyList.this)
                        .setTitle("编辑联系人")
                        .setView(textEntryView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String inputUserName = "";
                                String inputPhoneNum = "";

                                EditText userName = (EditText) textEntryView.findViewById(R.id.edit_username);
                                if(userName!= null) {
                                    inputUserName = userName.getText().toString();
                                }
                                EditText phoneNum = (EditText) textEntryView.findViewById(R.id.edit_password);
                                if (phoneNum!= null) {
                                    inputPhoneNum = phoneNum.getText().toString();
                                }

                                publicStorage.enemyList.get(position).setFlag(true);
                                publicStorage.enemyList.get(position).setName(inputUserName);
                                publicStorage.enemyList.get(position).setPhoneNum(inputPhoneNum);

                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
                dlg.show();
            }
        });


        btnReturn = (Button) findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initEnemy(){
        Contacts Allen = new Contacts("Sunflower","10086",22.257827,113.542177,true);
        EnemyList.add(Allen);
       /* Contacts Amy = new Contacts("Autumn","laji",4,4,true);
        EnemyList.add(Amy);*/
        for(int i=0;i<20;++i){
            Contacts test = new Contacts("ENEMY NAME","444444",4,4,false);
           EnemyList.add(test);
        }
    }
}
