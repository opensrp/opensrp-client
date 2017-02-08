package org.ei.opensrp.core.template;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 05-Jan-16.
 */
public class RegisterActivityPagerAdapter extends RegisterFragmentStatePagerAdapter {
    public static final String ARG_PAGE = "page";
    String[] dialogOptions;
    Fragment mBaseFragment;
    Fragment[] otherFragments;
    FragmentManager fragmentManager;

    ViewPager viewPager;
    int currentPage;
    ViewPager.SimpleOnPageChangeListener pageChangeListener;

    public int getCurrentPage() {
        return currentPage;
    }

    public RegisterActivityPagerAdapter(ViewPager viewPager, FragmentManager fragmentManager, String[] dialogOptions, Fragment baseFragment) {
        this(viewPager, fragmentManager, dialogOptions, baseFragment, null);
    }

    public RegisterActivityPagerAdapter(ViewPager viewPagr, FragmentManager fragmentManager, String[] dialogOptions,
            Fragment baseFragment, Fragment[] otherFragments) {
        super(fragmentManager);

        this.fragmentManager = fragmentManager;
        this.viewPager = viewPagr;
        this.otherFragments = otherFragments;
        this.dialogOptions = dialogOptions;
        this.mBaseFragment = baseFragment;

        viewPager.setOffscreenPageLimit(getCount());
        viewPager.setAdapter(this);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                if (pageChangeListener != null){
                    pageChangeListener.onPageSelected(position);//custom page listener execution that user provided
                }
            }
        });
    }

    public void onPageChanged(ViewPager.SimpleOnPageChangeListener pageChangeListener){
        this.pageChangeListener = pageChangeListener;
    }

    @Override
    public Fragment getItem(int position) {
        Log.v(getClass().getName(), "Getting fragment at "+position);
        Fragment fragment = null;
        if (isBaseFragment(position)){
            fragment = mBaseFragment; // donot use getBaseFragment method
        }
        // if has other fragments and position lessthan of.size+basefragment
        else if (otherFragmentsSize() > 0 && position <= otherFragmentsSize()){ // base fragment counted
            fragment = otherFragments[position - 1];// account for the base fragment
        }
        else {
            String formName = dialogOptions[position - (otherFragmentsSize()+1)]; // account for the base fragment and other fragments
            FormFragment f = new FormFragment();
            f.setFormName(formName);
            fragment = f;
        }

        Log.v(getClass().getName(), "Got fragment "+fragment.toString());

        return fragment;
    }

    public boolean isFormFragment(int position){
        if (position == 0){
            return false;
        }
        else if (position <= otherFragmentsSize()){//size is index+1 so baseFragment handled
            return false;
        }
        else {
            return true;
        }
    }

/*  todo  public android.support.v4.app.Fragment findFragmentByPosition(ViewGroup view, int position) {
        return fragmentManager.findFragmentByTag("android:switcher:" + view.getId() + ":" + getItemId(position));
    }*/

    public boolean isBaseFragment(int position){
        if (position == 0){
            return true;
        }
        return false;
    }

    public int otherFragmentsSize(){
        return otherFragments == null ? 0 : otherFragments.length;
    }

    public int formFragmentsSize(){
        return dialogOptions == null ? 0 : dialogOptions.length;
    }

    public int getFormIndex(String formName){
        for (int i = 0; i < dialogOptions.length; i++){
            if (formName.equalsIgnoreCase(dialogOptions[i])){
                return i;
            }
        }
        return -1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Page # "+position;
    }

    @Override
    public int getCount() {
        int count = otherFragmentsSize() + formFragmentsSize() + 1; // index 0 is always occupied by the base fragment
        return count;
    }

    public Fragment getBaseFragment(){
        return getRegisteredFragment(0);
    }

    public Fragment getOtherFragment(int position){
        if (otherFragmentsSize() == 0){
            throw new IllegalStateException("No Detail or Non-Form fragments configured");
        }
        return getRegisteredFragment(position+1);//0 would be occupied by base fragment
    }

    public Fragment getFormFragment(int position){
        if (formFragmentsSize() == 0){
            throw new IllegalStateException("No Form fragments configured");
        }
        return getRegisteredFragment(position+1+otherFragmentsSize());//0 would be occupied by base fragment, next bunch would be other fragments
    }

    public Fragment getFormFragment(String formName){
        if (formFragmentsSize() == 0){
            throw new IllegalStateException("No Form fragments configured");
        }

        int index = getFormIndex(formName);
        return getFormFragment(index);//0 would be occupied by base fragment, next bunch would be other fragments
    }

    public void showBaseFragment(){
        showPage(0);
    }

    public void showOtherFragment(int position){
        showPage(position+1);//0 would be occupied by base fragment
    }

    public void showForm(String formName){
        int i = getFormIndex(formName);
        showPage(i+1+otherFragmentsSize());//0 would be occupied by base fragment, next bunch would be other fragments
    }

    private void showPage(int position){
        viewPager.setCurrentItem(position, false);// removing 2nd param leads to fragment overlap issue
    }

    public void cleanup(){

    }
}
