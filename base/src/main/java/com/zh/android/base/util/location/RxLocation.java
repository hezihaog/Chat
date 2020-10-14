package com.zh.android.base.util.location;

import androidx.fragment.app.FragmentActivity;

import com.zh.android.base.lifecycle.DelegateFragmentFinder;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * @author wally
 * @date 2020/10/06
 * RxJava封装位置获取
 */
public class RxLocation {
    public static class LocationEvent {
        /**
         * 经度
         */
        private double longitude;
        /**
         * 纬度
         */
        private double latitude;

        private LocationEvent(double longitude, double latitude) {
            this.longitude = longitude;
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public double getLatitude() {
            return latitude;
        }
    }

    /**
     * 获取位置信息
     */
    public Observable<LocationEvent> getLocation(FragmentActivity activity) {
        return getLocationMonitorObservable(activity)
                .flatMap(new Function<LocationMonitorDelegateFragment, ObservableSource<LocationEvent>>() {
                    @Override
                    public ObservableSource<LocationEvent> apply(LocationMonitorDelegateFragment fragment) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<LocationEvent>() {
                            @Override
                            public void subscribe(ObservableEmitter<LocationEvent> emitter) throws Exception {
                                fragment.registerCallback(new LocationMonitorDelegateFragment.OnLocationChangeCallback() {
                                    @Override
                                    public void onLocationChange(double longitude, double latitude) {
                                        //获取一次后，取消注册
                                        fragment.unRegisterCallback(this);
                                        emitter.onNext(new LocationEvent(longitude, latitude));
                                    }
                                });
                                fragment.startLocationMonitor();
                            }
                        });
                    }
                });
    }

    /**
     * 获取代理Fragment的实例
     */
    private Observable<LocationMonitorDelegateFragment> getLocationMonitorObservable(FragmentActivity activity) {
        return Observable.create(new ObservableOnSubscribe<FragmentActivity>() {
            @Override
            public void subscribe(ObservableEmitter<FragmentActivity> emitter) throws Exception {
                if (activity == null || activity.isFinishing()) {
                    emitter.onError(new NullPointerException());
                } else {
                    emitter.onNext(activity);
                }
            }
        }).map(new Function<FragmentActivity, LocationMonitorDelegateFragment>() {
            @Override
            public LocationMonitorDelegateFragment apply(FragmentActivity activity) throws Exception {
                return DelegateFragmentFinder
                        .getInstance().find(activity, LocationMonitorDelegateFragment.class);
            }
        });
    }
}