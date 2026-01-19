<!--
  Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.

  https://www.mall4j.com/

  未经允许，不可做商业用途！

  版权所有，侵权必究！
-->

<template>
  <view class="Mall4j login-success page-logout">
    <view class="top">
      <view class="img-text">
        <view class="circle">
          <image
            :src="userInfo.pic? userInfo.pic : head04Png"
            mode="aspectFill"
            @error="handlePicError(userInfo)"
          />
        </view>
        <view class="text">
          {{ $t('logged') }}
        </view>
      </view>
      <view class="text">
        {{ userInfo.nickName }}
      </view>
    </view>
    <view class="card">
      <view class="card-title">
        {{ $t('logoutNotice') }}
      </view>
      <view class="cart-item-con">
        <view
          v-if="appType <= 2"
          class="item"
        >
          <image
            src="@/package-user/static/images/icon/weixin.png"
            mode="heightFix"
          />
          <text class="text">
            {{ $t('logoutNotice1') }}
          </text>
        </view>
        <view class="item">
          <image
            src="@/package-user/static/images/icon/icon-acount.png"
            mode="heightFix"
          />
          <text class="text">
            {{ $t('logoutNotice2') }}
          </text>
        </view>
        <view class="item">
          <image
            src="@/package-user/static/images/icon/icon-order.png"
            mode="heightFix"
          />
          <text class="text">
            {{ $t('logoutNotice3') }}
          </text>
        </view>
        <view class="item">
          <image
            src="@/package-user/static/images/icon/icon-preferential.png"
            mode="heightFix"
          />
          <text class="text">
            {{ $t('logoutNotice4') }}
          </text>
        </view>
      </view>
    </view>
    <!-- 其他登录方式 -->
    <view class="footer">
      <view class="logout-text">
        {{ $t('logoutConfirm') }}
      </view>
      <view class="logout-btn">
        <button
          class="btn-left"
          @tap="logout"
        >
          {{ $t('logoutBtn') }}
        </button>
        <button
          class="btn-right"
          @tap="back"
        >
          {{ $t('keepLogin') }}
        </button>
      </view>
    </view>
  </view>
</template>

<script setup>
import head04Png from '@/static/images/icon/head04.png'
import util from '@/utils/util.js'

const userInfo = ref('')
const appType = ref(uni.getStorageSync('cbecB2cAppType'))

/**
 * 生命周期函数--监听页面加载
 */
onLoad(() => {
  // 头部导航标题
  uni.setNavigationBarTitle({
    title: $t('logoutBtn')
  })
})

/**
 * 生命周期函数--监听页面显示
 */
onShow(() => {
  // 头部导航标题
  uni.setNavigationBarTitle({
    title: $t('logoutBtn')
  })
  // 查询用户信息
  userInfo.value = uni.getStorageSync('cbecB2cUserInfo')
})

const back = () => {
  uni.switchTab({
    url: '/pages/user/user'
  })
}

/**
 * 退出登录
 */
const logout = () => {
  const params = {
    url: '/logOut',
    method: 'POST'
  }
  http.request(params).then(() => {
    uni.removeStorageSync('cbecB2cLoginResult')
    uni.removeStorageSync('cbecB2cToken')
    uni.removeStorageSync('cbecB2cHadLogin')
    uni.removeStorageSync('cbecB2cRecentSearch')
    uni.removeStorageSync('cbecB2cHadBindUser')
    uni.removeStorageSync('cbecB2cCode')
    uni.removeStorageSync('cbecB2cUserInfo')
    uni.removeStorageSync('cbecB2cCartSelectAddrInfo')
    uni.removeStorageSync('cbecB2cDistCardNo')
    uni.removeStorageSync('cbecB2cParentDistributionUser')
    // util.removeTabBadge()
    getApp().globalData.reloadHomePopupAd = true // 是否重新加载首页弹窗
    util.toHomePage()
    getApp().globalData.userPagesGetHotSalesProds && getApp().globalData.userPagesGetHotSalesProds()
    getApp().globalData.indexGetHotSalesProds && getApp().globalData.indexGetHotSalesProds()
  })
}

const handlePicError = (item) => {
  // 替换为默认图片
  item.pic = head04Png
}

</script>
<style lang="scss" scoped>
@use "logout";
</style>
