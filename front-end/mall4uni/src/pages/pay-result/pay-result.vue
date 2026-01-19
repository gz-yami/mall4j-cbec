<!--
  Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.

  https://www.mall4j.com/

  未经允许，不可做商业用途！

  版权所有，侵权必究！
-->

<template>
  <view class="Mall4j page-pay-result container">
    <!-- 成功 -->
    <view>
      <view class="pay-sts succ">
        {{ $t('paymentSuccessful') }}
      </view>
      <view class="tips">
        {{ $t('thankPurchase') }}
      </view>
      <view class="btns">
        <text
          class="button checkorder"
          @tap="onToOrderList"
        >
          {{ $t('checkOrder') }}
        </text>
        <text
          class="button shopcontinue"
          @tap="onToIndex"
        >
          {{ $t('continueShopping') }}
        </text>
      </view>
    </view>
  </view>
</template>

<script setup>

/**
 * 生命周期函数--监听页面显示
 */
onShow(() => {
  // 设置头部导航标题
  uni.setNavigationBarTitle({
    title: $t('paymentResults')
  })
})

const onToOrderList = function () {
  uni.redirectTo({
    url: '/package-user/pages/order-list/order-list?sts=0'
  })
}
const onToIndex = function () {
  util.toHomePage()
}

</script>
<style lang="scss" scoped>
@use "./pay-result.scss";
</style>
