package com.jarry.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.jarry.app.base.MVPBaseFragment;

import java.util.List;

/**
 * Created by Jarry 2016/8/2.
 */
public class ViewPagerFgAdapter extends FragmentPagerAdapter {

    private String tag;

    private List<MVPBaseFragment> fragmentList;


    public ViewPagerFgAdapter(FragmentManager supportFragmentManager, List<MVPBaseFragment> fragmentList, String tag) {
        super(supportFragmentManager);
        this.fragmentList = fragmentList;
        this.tag = tag;
    }

    @Override
    public Fragment getItem(int position) {

        return fragmentList.get(position);
    }


    @Override
    public int getCount() {
        if (fragmentList != null) {
            return fragmentList.size();
        }
        return 0;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (tag.equals("WeiBo_Detail")) {
            switch (position) {
                case 0:
                    return "转发";
                case 1:
                    return "评论";
                case 2:
                    return "赞";
            }
        } else if (tag.equals("Message")) {
            switch (position) {
                case 0:
                    return "我的动态";
                case 1:
                    return "我赞的";
                case 2:
                    return "我评论的";
            }
        } else if (tag.equals("FriendShips")) {
            switch (position) {
                case 0:
                    return "关注";
                case 1:
                    return "粉丝";
                case 2:
                    return "互粉";
            }
        }
        return null;
    }
}
