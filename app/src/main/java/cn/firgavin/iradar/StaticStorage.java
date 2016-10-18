package cn.firgavin.iradar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/3.
 */
public class StaticStorage {
    public static double curLatitude;
    public static double curLonggitude;

    public static int numFriend = 0;
    public static int numEnemy = 0;
    public static List<Contacts> friendList = new ArrayList<Contacts>();
    public static List<Contacts> enemyList = new ArrayList<Contacts>();
}
