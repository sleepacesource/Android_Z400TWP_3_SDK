package com.sleepace.z400twp_3sdk.demo.fragment;

import com.sleepace.sdk.wifidevice.WiFiDeviceSdkHelper;
import com.sleepace.z400twp_3sdk.demo.MainActivity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment implements OnClickListener{
	
	protected String TAG = getClass().getSimpleName();
	protected MainActivity mActivity;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mActivity = (MainActivity) getActivity();
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	

	protected void findView(View root) {
		// TODO Auto-generated method stub
	}


	protected void initListener() {
		// TODO Auto-generated method stub
	}


	protected void initUI() {
		// TODO Auto-generated method stub
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	public void showTips(String msg) {
		mActivity.showTips(msg);
	}

	public boolean isAuthorize() {
		return WiFiDeviceSdkHelper.getUserId() > 0;
	}
}



