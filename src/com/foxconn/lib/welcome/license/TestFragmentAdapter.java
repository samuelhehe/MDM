package com.foxconn.lib.welcome.license;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class TestFragmentAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
    protected static final String[] CONTENT = new String[] { "1", "2", "3"  };

    private int mCount = CONTENT.length;

	private int[] values = new int[]{0,1,2};

	private int count = values.length;
	
    public TestFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

//    @Override
//    public Fragment getItem(int position) {
//        return TestFragment.newInstance(CONTENT[position % CONTENT.length]);
//    }
    @Override
    public Fragment getItem(int position) {
    	return TestFragment.newInstance(values[position %count]);
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return TestFragmentAdapter.CONTENT[position % CONTENT.length];
    }


    public void setCount(int count) {
        if (count > 0 && count <= 10) {
            this.count = count;
            notifyDataSetChanged();
        }
    }

	@Override
	public int getIconResId(int index) {
		// TODO Auto-generated method stub
		return 0;
	}
}