package com.zh.android.base.util.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.zh.android.base.lifecycle.AppDelegateFragment;
import com.zh.android.base.lifecycle.BaseDelegateFragment;
import com.zh.android.base.util.toast.ToastUtil;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author wally
 * @date 2020/10/06
 * 位置信息获取代理Fragment
 */
public class LocationMonitorDelegateFragment extends AppDelegateFragment {
    public static final int LOCATION_CODE = 301;

    private LocationManager mLocationManager;
    private String mLocationProvider = null;

    private CopyOnWriteArrayList<OnLocationChangeCallback> mCallbacks = new CopyOnWriteArrayList<>();

    /**
     * 位置改变回调
     */
    public interface OnLocationChangeCallback {
        /**
         * 位置改变时，回调
         *
         * @param longitude 经度
         * @param latitude  纬度
         */
        void onLocationChange(double longitude, double latitude);
    }

    /**
     * 注册回调
     */
    public void registerCallback(OnLocationChangeCallback callback) {
        if (!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    /**
     * 注销回调
     */
    public void unRegisterCallback(OnLocationChangeCallback callback) {
        mCallbacks.remove(callback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销监听
        if (mLocationManager != null && mLocationListener != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
        mCallbacks.clear();
    }

    /**
     * 开启位置监听
     */
    public void startLocationMonitor() {
        runTaskOnStart(new LifecycleTask() {
            @Override
            public void execute(BaseDelegateFragment delegateFragment) {
                Activity activity = getActivity();
                if (activity == null) {
                    return;
                }
                mLocationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
                //获取所有可用的位置提供器
                List<String> providers = mLocationManager.getProviders(true);
                if (providers.contains(LocationManager.GPS_PROVIDER)) {
                    //如果是GPS
                    mLocationProvider = LocationManager.GPS_PROVIDER;
                } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
                    //如果是Network
                    mLocationProvider = LocationManager.NETWORK_PROVIDER;
                } else {
                    //没有开启位置
                    if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        ToastUtil.showMsg(activity, "请开启位置，查找附近的小伙伴");
                    }
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //获取权限（如果没有开启权限，会弹出对话框，询问是否开启权限）
                    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        //请求权限
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_CODE);
                    } else {
                        //监视地理位置变化
                        mLocationManager.requestLocationUpdates(mLocationProvider, 3000, 1, mLocationListener);
                    }
                } else {
                    //监视地理位置变化
                    mLocationManager.requestLocationUpdates(mLocationProvider, 3000, 1, mLocationListener);
                }
            }
        });
    }

    /**
     * 位置监听
     */
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        }

        @Override
        public void onProviderEnabled(String provider) {
            //Provider被enable时触发此函数，比如GPS被打开
        }

        @Override
        public void onProviderDisabled(String provider) {
            //Provider被disable时触发此函数，比如GPS被关闭
        }

        @Override
        public void onLocationChanged(Location location) {
            //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
            if (location != null) {
                //不为空,显示地理位置经纬度
                for (OnLocationChangeCallback callback : mCallbacks) {
                    callback.onLocationChange(location.getLongitude(), location.getLatitude());
                }
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (requestCode == LOCATION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                try {
                    List<String> providers = mLocationManager.getProviders(true);
                    if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
                        //如果是Network
                        mLocationProvider = LocationManager.NETWORK_PROVIDER;
                    } else if (providers.contains(LocationManager.GPS_PROVIDER)) {
                        //如果是GPS
                        mLocationProvider = LocationManager.GPS_PROVIDER;
                    }
                    //监视地理位置变化
                    mLocationManager.requestLocationUpdates(mLocationProvider, 3000, 1, mLocationListener);
                    Location location = mLocationManager.getLastKnownLocation(mLocationProvider);
                    if (location != null) {
                        //不为空,显示地理位置经纬度
                        for (OnLocationChangeCallback callback : mCallbacks) {
                            callback.onLocationChange(location.getLongitude(), location.getLatitude());
                        }
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            } else {
                ToastUtil.showMsg(getContext(), "请允许位置权限");
            }
        }
    }
}