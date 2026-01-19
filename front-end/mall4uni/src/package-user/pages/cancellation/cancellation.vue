<!--
  Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.

  https://www.mall4j.com/

  未经允许，不可做商业用途！

  版权所有，侵权必究！
-->

<template>
  <view class="Mall4j login-success page-cancellation">
    <view class="card">
      <view class="card-title">
        1. {{ $t('cancellationNotice') }}
      </view>
      <view class="text">
        {{ $t('cancellationCon') }}
      </view>
      <view class="text">
        1.1. {{ $t('cancellationTips1') }}
      </view>
      <view class="text">
        1.2. {{ $t('cancellationTips2') }}
      </view>
      <view class="text">
        1.3. {{ $t('cancellationTips3') }}
      </view>
      <view class="text">
        1.4. {{ $t('cancellationTips4') }}
      </view>
    </view>
    <!-- 其他登录方式 -->
    <view class="footer">
      <view class="logout-text">
        {{ $t('cancellationConfirmTips') }}
      </view>
      <view class="logout-btn">
        <button
          class="btn-left"
          @tap="cancellation"
        >
          {{ $t('cancellationConfirm') }}
        </button>
        <button
          class="btn-right"
          @tap="back"
        >
          {{ $t('cancellationCancel') }}
        </button>
      </view>
    </view>
    <confirm-pop
      v-model:show-pop="showPop"
      :pop-content="popContent"
      :show-title="false"
      show-cancel
      @confirm="confirm"
    />
  </view>
</template>

<script setup>
import util from '@/utils/util.js'

/**
 * 生命周期函数--监听页面加载
 */
onLoad(() => {
  // 头部导航标题
  uni.setNavigationBarTitle({
    title: $t('accountCancellation')
  })
})

/**
 * 生命周期函数--监听页面显示
 */
onShow(() => {
  // 头部导航标题
  uni.setNavigationBarTitle({
    title: $t('accountCancellation')
  })
})

let confirmIndex = null // 1 注销 2 返回 3 强制注销
const confirm = () => {
  switch (confirmIndex) {
    // 1 注销 2 返回 3 强制注销
    case 1:
      destroyUser(false)
      break
    case 2:
      back()
      break
    case 3:
      destroyUser(true)
      break
    default:
      break
  }
}

const back = () => {
  uni.switchTab({
    url: '/pages/user/user'
  })
}

const showPop = ref(false)
const popContent = ref({})
/**
 * 退出登录
 */
const cancellation = () => {
  popContent.value = {
    content: $t('cancellationConfirmTips'),
    cancelText: $t('cancel'),
    confirmText: $t('confirm')
  }
  confirmIndex = 1
  showPop.value = true
}
const destroyUser = (forceDestroy) => {
  const params = {
    url: '/p/user/destroy?forceDestroy=' + forceDestroy,
    method: 'GET',
    hasCatch: true
  }
  http.request(params).then(() => {
    uni.removeStorageSync('cbecB2cLoginResult')
    uni.removeStorageSync('cbecB2cToken')
    uni.removeStorageSync('cbecB2cHadLogin')
    uni.removeStorageSync('cbecB2cRecentSearch')
    uni.removeStorageSync('cbecB2cHadBindUser')
    uni.removeStorageSync('cbecB2cCode')
    uni.removeStorageSync('cbecB2cUserInfo')
    util.removeTabBadge()
    getApp().globalData.reloadHomePopupAd = true // 是否重新加载首页弹窗
    util.toHomePage()
  }).catch((err) => {
    // 弹窗
    if (err.code === 'A00001') {
      let flag = false
      if (err.msg === '您的账户当前存在未完成的订单，请待所有订单完成后再注销账户' || err.msg === 'you still have unfinished orders. Please wait until all orders have been finish') {
        flag = true
      }
      popContent.value = {
        content: err.msg,
        cancelText: $t('cancel'),
        confirmText: $t('confirm')
      }
      confirmIndex = flag ? 2 : 3
      showPop.value = true
    } else {
      uni.showToast({
        title: err.msg || 'Error',
        icon: 'none'
      })
    }
  })
}

</script>
<style lang="scss" scoped>
@use "cancellation";
</style>
