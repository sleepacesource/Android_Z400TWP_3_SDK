package com.sleepace.z400twp_3sdk.demo.fragment;

import com.sleepace.sdk.interfs.IMonitorManager;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.manager.DeviceType;
import com.sleepace.sdk.util.SdkLog;
import com.sleepace.sdk.z400twp.Z400TWPHelper;
import com.sleepace.sdk.z400twp.Z400TWPManager.OnlineStateListener;
import com.sleepace.sdk.z400twp.Z400TWPManager.RealtimeDataListener;
import com.sleepace.sdk.z400twp.Z400TWPManager.RealtimeSleepStateListener;
import com.sleepace.sdk.z400twp.Z400TWPManager.SleepReportUploadStateListener;
import com.sleepace.sdk.z400twp.constants.SleepStatusType;
import com.sleepace.sdk.z400twp.domain.CollectState;
import com.sleepace.sdk.z400twp.domain.EnvironmentData;
import com.sleepace.sdk.z400twp.domain.RealTimeData;
import com.sleepace.sdk.z400twp.domain.SleepState;
import com.sleepace.z400twp_3sdk.demo.MainActivity;
import com.sleepace.z400twp_3sdk.demo.R;
import com.sleepace.z400twp_3sdk.demo.util.ActivityUtil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
public class DeviceFragment extends BaseFragment {
	
	private Button btnStartRealtimeData, btnStopRealtimeData, btnStopCollect, btnQueryEnvirData, btnQueryOnlineState, btnQuerySleepState;
	private TextView tvSleepState, tvHeartRate, tvBreathRate, /*tvTemp, tvHumidity,*/ tvCurTemp, tvCurHumidity, tvCurOnlineState, tvCurSleepState,
		tvCurRealtimeSleepState;
	private Z400TWPHelper z400twpHelper;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_control, null);
//		SdkLog.log(TAG+" onCreateView-----------");
		z400twpHelper = Z400TWPHelper.getInstance(getActivity().getApplicationContext());
		findView(view);
		initListener();
		initUI();
		return view;
	}
	
	
	protected void findView(View root) {
		// TODO Auto-generated method stub
		super.findView(root);
		btnStartRealtimeData = (Button) root.findViewById(R.id.btn_start_realtime_data);
		btnStopRealtimeData = (Button) root.findViewById(R.id.btn_stop_realtime_data);
		btnStopCollect = (Button) root.findViewById(R.id.btn_stop_collect);
		btnQueryEnvirData = (Button) root.findViewById(R.id.btn_query_envir_data);
		btnQueryOnlineState = (Button) root.findViewById(R.id.btn_query_device_online_state);
		btnQuerySleepState = (Button) root.findViewById(R.id.btn_query_sleep_state);
		
		tvSleepState = (TextView) root.findViewById(R.id.tv_sleep_state);
		tvHeartRate = (TextView) root.findViewById(R.id.tv_heartrate);
		tvBreathRate = (TextView) root.findViewById(R.id.tv_breathrate);
//		tvTemp = (TextView) root.findViewById(R.id.tv_temp);
//		tvHumidity = (TextView) root.findViewById(R.id.tv_humidity);
		tvCurTemp = (TextView) root.findViewById(R.id.tv_cur_temp);
		tvCurHumidity = (TextView) root.findViewById(R.id.tv_cur_humidity);
		tvCurOnlineState = (TextView) root.findViewById(R.id.tv_device_online_state);
		tvCurSleepState = (TextView) root.findViewById(R.id.tv_cur_sleep_state);
		tvCurRealtimeSleepState = (TextView) root.findViewById(R.id.tv_cur_realtime_sleep_state);
	}


	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		btnStartRealtimeData.setOnClickListener(this);
		btnStopRealtimeData.setOnClickListener(this);
		btnStopCollect.setOnClickListener(this);
		btnQueryEnvirData.setOnClickListener(this);
		btnQueryOnlineState.setOnClickListener(this);
		btnQuerySleepState.setOnClickListener(this);
	}


	protected void initUI() {
		// TODO Auto-generated method stub
		mActivity.setTitle(R.string.device);
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		z400twpHelper.registOnlineStateListener(onlineStateListener);
		z400twpHelper.registSleepReportUploadStateListener(sleepReportUploadStateListener);
		z400twpHelper.registRealtimeDataListener(realtimeDataListener);
		z400twpHelper.registRealtimeSleepStateListener(realtimeSleepStateListener);
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		z400twpHelper.unregistOnlineStateListener(onlineStateListener);
		z400twpHelper.unregistSleepReportUploadStateListener(sleepReportUploadStateListener);
		z400twpHelper.unregistRealtimeDataListener(realtimeDataListener);
		z400twpHelper.unregistRealtimeSleepStateListener(realtimeSleepStateListener);
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		SdkLog.log(TAG+" onDestroyView----------------");
	}
	
	/**
	 * 睡眠报告上传状态
	 * 0:无任何数据(无需再等待)
	 * 1:准备
	 * 2:正在同步
	 * 3:正在上传
	 * 4:上传失败(无需再等待)
	 * 5:上传成功
	 * 6:上传结束
	 */
	private SleepReportUploadStateListener sleepReportUploadStateListener = new SleepReportUploadStateListener() {
		@Override
		public void onStateChanged(final byte state) {
			// TODO Auto-generated method stub
			SdkLog.log(TAG+" SleepReportUploadStateListener state:" + state);
		}
	};
	
	private OnlineStateListener onlineStateListener = new OnlineStateListener() {
		@Override
		public void onlineStateChanged(final short deviceType, final String deviceId, final byte onlineState) {
			// TODO Auto-generated method stub
			//注意，这个回调在子线程中
			SdkLog.log(TAG+" onlineStateChanged state:" + onlineState);
			if(isAdded()) {
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						initOnlineStateView(onlineState);
						if(onlineState == 1) {
							Toast.makeText(mActivity, R.string.tips_device_online, Toast.LENGTH_SHORT).show();
						}else if(onlineState == 0) {
							Toast.makeText(mActivity, R.string.tips_device_offline, Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		}
	};
	
	private RealtimeDataListener realtimeDataListener = new RealtimeDataListener() {
		@Override
		public void onReceive(final RealTimeData realTimeData) {
			// TODO Auto-generated method stub
			if(ActivityUtil.isActivityAlive(mActivity)) {
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						initRealtimeDataView(realTimeData);
					}
				});
			}
		}
	};
	
	private RealtimeSleepStateListener realtimeSleepStateListener = new RealtimeSleepStateListener() {
		@Override
		public void onReceive(final SleepState sleepState) {
			// TODO Auto-generated method stub
			if(ActivityUtil.isActivityAlive(mActivity)) {
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						initRealtimeSleepStateView(sleepState);
					}
				});
			}
		}
	};
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if(v == btnStartRealtimeData){
			if(!isAuthorize()) {
				showTips(getString(R.string.err_not_connect_server));
				return;
			}
			
			z400twpHelper.startRealTimeData(MainActivity.deviceType, MainActivity.deviceId, new IResultCallback<RealTimeData>() {
				@Override
				public void onResultCallback(final CallbackData<RealTimeData> cd) {
					// TODO Auto-generated method stub
					if(ActivityUtil.isActivityAlive(mActivity)) {
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								if(cd.getCallbackType() == IMonitorManager.METHOD_REALTIME_DATA_OPEN) {
									if(cd.isSuccess()) {
										Toast.makeText(mActivity, R.string.get_success, Toast.LENGTH_SHORT).show();
									}else {
										if(cd.getStatus() == 8) {
											showTips(getString(R.string.err_device_offline));
										}else {
											Toast.makeText(mActivity, R.string.failure, Toast.LENGTH_SHORT).show();
										}
									}
								}
							}
						});
					}
				}
			});
		}else if(v == btnStopRealtimeData){
			if(!isAuthorize()) {
				showTips(getString(R.string.err_not_connect_server));
				return;
			}
			
			z400twpHelper.stopRealTimeData(MainActivity.deviceType, MainActivity.deviceId, new IResultCallback<Void>() {
				@Override
				public void onResultCallback(final CallbackData<Void> cd) {
					// TODO Auto-generated method stub
					if(ActivityUtil.isActivityAlive(mActivity)) {
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								if(cd.isSuccess()) {
									Toast.makeText(mActivity, R.string.stop_data_successfully, Toast.LENGTH_SHORT).show();
									initRealtimeDataView(null);
								}else {
									if(cd.getStatus() == 8) {
										showTips(getString(R.string.err_device_offline));
									}else {
										Toast.makeText(mActivity, R.string.failure, Toast.LENGTH_SHORT).show();
									}
								}
							}
						});
					}
				}
			});
		}else if(v == btnStopCollect){
			if(!isAuthorize()) {
				showTips(getString(R.string.err_not_connect_server));
				return;
			}
			
			z400twpHelper.queryCollectState(MainActivity.deviceType, MainActivity.deviceId, new IResultCallback<CollectState>() {
				@Override
				public void onResultCallback(final CallbackData<CollectState> cd) {
					// TODO Auto-generated method stub
					if(ActivityUtil.isActivityAlive(mActivity)) {
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								if(cd.isSuccess()) {
									CollectState collectState = cd.getResult();
									int curTime = (int) (System.currentTimeMillis() / 1000);
									if(collectState.getState() == 1) {
										if(collectState.getStartTime() > 0 && (curTime - collectState.getStartTime() > 10 * 60)) {
											z400twpHelper.stopCollection(MainActivity.userId, MainActivity.deviceType, MainActivity.deviceId, new IResultCallback<Void>() {
												@Override
												public void onResultCallback(final CallbackData<Void> cd) {
													// TODO Auto-generated method stub
													if(ActivityUtil.isActivityAlive(mActivity)) {
														mActivity.runOnUiThread(new Runnable() {
															@Override
															public void run() {
																// TODO Auto-generated method stub
																if(cd.isSuccess()) {
																	Toast.makeText(mActivity, R.string.close_acquisition_success, Toast.LENGTH_SHORT).show();
																}else {
																	if(cd.getStatus() == 8) {
																		showTips(getString(R.string.err_device_offline));
																	}else {
																		Toast.makeText(mActivity, R.string.failure, Toast.LENGTH_SHORT).show();
																	}
																}
															}
														});
													}
												}
											});
										}else {//监测时间小于10分钟
											
										}
									}
								}else {
									if(cd.getStatus() == 8) {
										showTips(getString(R.string.err_device_offline));
									}else {
										Toast.makeText(mActivity, R.string.failure, Toast.LENGTH_SHORT).show();
									}
								}
							}
						});
					}
				}
			});
		}else if(v == btnQueryEnvirData){
			if(!isAuthorize()) {
				showTips(getString(R.string.err_not_connect_server));
				return;
			}
			
			z400twpHelper.queryEnvironmentData(MainActivity.deviceId, new IResultCallback<EnvironmentData>() {
				@Override
				public void onResultCallback(final CallbackData<EnvironmentData> cd) {
					// TODO Auto-generated method stub
					if(ActivityUtil.isActivityAlive(mActivity)) {
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								if(cd.isSuccess()) {
									EnvironmentData envirData = cd.getResult();
									tvCurTemp.setText(String.valueOf(envirData.getTemperature())+"℃");
									tvCurHumidity.setText(String.valueOf(envirData.getHumidity())+"%");
								}else {
									tvCurTemp.setText("--");
									tvCurHumidity.setText("--");
									if(cd.getStatus() == 8) {
										showTips(getString(R.string.err_device_offline));
									}else {
										Toast.makeText(mActivity, R.string.failure, Toast.LENGTH_SHORT).show();
									}
								}
							}
						});
					}
				}
			});
		}else if(v == btnQueryOnlineState){
			if(!isAuthorize()) {
				showTips(getString(R.string.err_not_connect_server));
				return;
			}
			
			z400twpHelper.queryDeviceOnlineState(MainActivity.deviceType, MainActivity.deviceId, new IResultCallback<Byte>() {
				@Override
				public void onResultCallback(final CallbackData<Byte> cd) {
					// TODO Auto-generated method stub
					if(ActivityUtil.isActivityAlive(mActivity)) {
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								if(cd.isSuccess()) {
									byte state = cd.getResult();
									initOnlineStateView(state);
								}else {
									tvCurOnlineState.setText("--");
								}
							}
						});
					}
				}
			});
		}else if(v == btnQuerySleepState){
			if(!isAuthorize()) {
				showTips(getString(R.string.err_not_connect_server));
				return;
			}
			
			z400twpHelper.querySleepState(MainActivity.deviceType, MainActivity.deviceId, new IResultCallback<SleepState>() {
				@Override
				public void onResultCallback(final CallbackData<SleepState> cd) {
					// TODO Auto-generated method stub
					if(ActivityUtil.isActivityAlive(mActivity)) {
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								if(cd.isSuccess()) {
									SleepState sleepState = cd.getResult();
									if(sleepState != null) {
										if(sleepState.getWakeupFlag() == 1) {
											tvCurSleepState.setText(R.string.wake);
										}else if(sleepState.getAsleepFlag() == 1) {
											tvCurSleepState.setText(R.string.asleep);
										}else if(sleepState.getOutOfBedFlag() == 0) {
											tvCurSleepState.setText(R.string.in_bed);
										}else if(sleepState.getOutOfBedFlag() == 1) {
											tvCurSleepState.setText(R.string.out_bed);
										}else {
											tvCurSleepState.setText("--");
										}
									}else {
										tvCurSleepState.setText("--");
									}
								}else {
									tvCurSleepState.setText("--");
									if(cd.getStatus() == 8) {
										showTips(getString(R.string.err_device_offline));
									}else {
										Toast.makeText(mActivity, R.string.failure, Toast.LENGTH_SHORT).show();
									}
								}
							}
						});
					}
				}
			});
		}
	}
	
	private void initOnlineStateView(byte state) {
		if(state == 1) {
			tvCurOnlineState.setText(R.string.online);
		}else if(state == 0) {
			tvCurOnlineState.setText(R.string.offline);
		}else { //未知状态
			tvCurOnlineState.setText("--");
		}
	}
	
	private void initRealtimeSleepStateView(SleepState sleepState) {
		if(sleepState != null) {
			if(sleepState.getWakeupFlag() == 1) {
				tvCurRealtimeSleepState.setText(R.string.wake);
			}else if(sleepState.getAsleepFlag() == 1) {
				tvCurRealtimeSleepState.setText(R.string.asleep);
			}else if(sleepState.getOutOfBedFlag() == 0) {
				tvCurRealtimeSleepState.setText(R.string.in_bed);
			}else if(sleepState.getOutOfBedFlag() == 1) {
				tvCurRealtimeSleepState.setText(R.string.out_bed);
			}else {
				tvCurRealtimeSleepState.setText("--");
			}
		}else {
			tvCurRealtimeSleepState.setText("--");
		}
	}
	
	private void initRealtimeDataView(RealTimeData data) {
		if(data != null) {
			if(data.getStatus() == SleepStatusType.SLEEP_INIT) {
				tvSleepState.setText(R.string.Initializing);
			}else if(data.getStatus() == SleepStatusType.SLEEP_OK) {
				tvSleepState.setText(R.string.normal_status);
			}else if(data.getStatus() == SleepStatusType.SLEEP_B_STOP) {
				tvSleepState.setText(R.string.Apnea);
			}else if(data.getStatus() == SleepStatusType.SLEEP_H_STOP) {
				tvSleepState.setText(R.string.Pause_heartbeat);
			}else if(data.getStatus() == SleepStatusType.SLEEP_BODYMOVE) {
				tvSleepState.setText(R.string.Body_movement);
			}else if(data.getStatus() == SleepStatusType.SLEEP_LEAVE) {
				tvSleepState.setText(R.string.Out_of_bed);
			}else if(data.getStatus() == SleepStatusType.SLEEP_TURN_OVER) {
				tvSleepState.setText(R.string.Turn_over);
			}else if(data.getStatus() == SleepStatusType.SLEEP_BODYMOVE_TEMP) {
				tvSleepState.setText(R.string.Body_motion_range);
			}else if(data.getStatus() == SleepStatusType.SLEEP_INVALID) {
				tvSleepState.setText(R.string.invalid);
			}else {
				tvSleepState.setText("--");
			}
			
			if(data.getHeartRate() == 255) {
				tvHeartRate.setText("--");
			}else {
				tvHeartRate.setText(data.getHeartRate() + getString(R.string.unit_heart));
			}
			if(data.getBreathRate() == 255) {
				tvBreathRate.setText("--");
			}else {
				tvBreathRate.setText(data.getBreathRate() + getString(R.string.unit_respiration));
			}
//			tvTemp.setText(String.valueOf(data.getTemp()));
//			tvHumidity.setText(String.valueOf(data.getHumidity()));
		}else {
			tvSleepState.setText("--");
			tvHeartRate.setText("--");
			tvBreathRate.setText("--");
//			tvTemp.setText("--");
//			tvHumidity.setText("--");
		}
	}
	

		
}



