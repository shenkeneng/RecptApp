package com.frxs.receipt.comms;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.frxs.receipt.R;
import com.frxs.receipt.fragment.HomeFragment;
import com.frxs.receipt.fragment.MineFragment;
import com.frxs.receipt.fragment.ReceivedOrderListFragment;
import com.frxs.receipt.fragment.ReceivingFragment;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2017/05/09
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class DataGenerator {

    public static final int[] mTabRes = new int[]{R.mipmap.ic_receiving, R.mipmap.ic_details, R.mipmap.ic_mine};//R.mipmap.ic_waiting_rc, R.mipmap.ic_waiting_rc_selected,"等待收货",
    public static final int[] mTabResPressed = new int[]{R.mipmap.ic_receiving_selected, R.mipmap.ic_details_selected, R.mipmap.ic_mine_selected};
    public static final String[] mTabTitle = new String[]{"扫描收货", "收货明细", "关于我"};

    public static Fragment[] getFragments(String from) {
        Fragment fragments[] = new Fragment[3];
//        fragments[0] = HomeFragment.newInstance(from);
        fragments[0] = ReceivingFragment.newInstance(from);
        fragments[1] = ReceivedOrderListFragment.newInstance(from);
        fragments[2] = MineFragment.newInstance(from);
        return fragments;
    }

    /**
     * 获取Tab 显示的内容
     *
     * @param context
     * @param position
     * @return
     */
    public static View getTabView(Context context, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_tab_content, null);
        ImageView tabIcon = (ImageView) view.findViewById(R.id.tab_content_image);
        tabIcon.setImageResource(DataGenerator.mTabRes[position]);
        TextView tabText = (TextView) view.findViewById(R.id.tab_content_text);
        tabText.setText(mTabTitle[position]);

        return view;
    }
}
