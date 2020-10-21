package com.zh.android.circle.mall

import android.app.Activity
import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.zh.android.base.constant.ARouterUrl
import com.zh.android.base.util.web.BrowserActivity
import com.zh.android.chat.service.AppConstant
import com.zh.android.chat.service.ext.startNavigation
import com.zh.android.chat.service.module.mall.MallService

/**
 * @author wally
 * @date 2020/10/16
 */
@Route(path = ARouterUrl.MALL_SERVICE, name = "商城模块服务")
class MallServiceImpl : MallService {
    override fun init(context: Context?) {
    }

    override fun goMall(activity: Activity) {
        ARouter.getInstance()
            .build(ARouterUrl.MALL_MAIN)
            .startNavigation(activity)
    }

    override fun goMallWeb(activity: Activity) {
        BrowserActivity.start(activity, "http://47.99.134.126:5000/#/home")
    }

    override fun goGoodsCategory(activity: Activity) {
        ARouter.getInstance()
            .build(ARouterUrl.MALL_GOODS_CATEGORY)
            .startNavigation(activity)
    }

    override fun goGoodsSearch(activity: Activity, goodsCategoryId: String) {
        ARouter.getInstance()
            .build(ARouterUrl.MALL_GOODS_SEARCH)
            .withString(AppConstant.Key.MALL_GOODS_CATEGORY_ID, goodsCategoryId)
            .startNavigation(activity)
    }

    override fun goGoodsDetail(activity: Activity, goodsId: String) {
        ARouter.getInstance()
            .build(ARouterUrl.MALL_GOODS_DETAIL)
            .withString(AppConstant.Key.MALL_GOODS_ID, goodsId)
            .startNavigation(activity)
    }

    override fun goShoppingCar(activity: Activity) {
        ARouter.getInstance()
            .build(ARouterUrl.MALL_SHOPPING_CAR)
            .startNavigation(activity)
    }

    override fun goMyOrder(activity: Activity) {
        ARouter.getInstance()
            .build(ARouterUrl.MALL_MY_ORDER)
            .startNavigation(activity)
    }
}