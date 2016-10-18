package cn.firgavin.iradar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/7.
 */
public class EnemyAdapter extends ArrayAdapter<Contacts> {
    private int resourceId;

    public EnemyAdapter(Context context, int textViewResourceId, List<Contacts> objects) {
        super(context, textViewResourceId, objects);
        resourceId=textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Contacts enemy = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        TextView enemyName = (TextView) view.findViewById(R.id.enemy_name);
        enemyName.setText(enemy.getName());
        return view;
    }
}
