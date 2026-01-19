<!--
  Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.

  https://www.mall4j.com/

  未经允许，不可做商业用途！

  版权所有，侵权必究！
-->

<template>
  <view class="Mall4j page-pay-success  container">
    <view class="pay-success">
      <view class="pay-det">
        <view class="text">
          {{ $t('orderPaymentSuccessful') }}
        </view>
        <view class="number">
          <text>{{ useCurrencyStore().defMark }}</text>
          <text class="price">
            {{ actualTotal?actualTotal.toFixed(2):0.00 }}
          </text>
        </view>
      </view>
      <view class="pay-bottom">
        <view class="other-action">
          <view
            class="item"
            @tap="onToIndex"
          >
            {{ $t('continueShopping') }}
          </view>
          <view
            class="item bl"
            @tap="onToOrderList"
          >
            {{ $t('checkOrder') }}
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { useCurrencyStore } from '@/stores/modules/currency'

let orderNumbers = ''
const actualTotal = ref(0) // 实际金额

onShow(() => {
  // 头部导航标题
  uni.setNavigationBarTitle({
    title: $t('paymentSuccessful')
  })
})

onLoad((options) => {
  if (options.orderNumbers) {
    orderNumbers = options.orderNumbers
  }
  queryActualTotal()
})

/**
 * 请求实付金额
 */
const queryActualTotal = () => {
  const params = {
    url: '/p/myOrder/orderDetail',
    method: 'GET',
    data: {
      orderNumber: orderNumbers
    },
    callBack: (res) => {
      actualTotal.value = res.actualTotal
    }
  }
  http.request(params).then(({ data: res }) => {
    actualTotal.value = res.actualTotal
  })
}

// 继续购物
const onToIndex = () => {
  util.toHomePage()
}

// 查看详情
const onToOrderList = () => {
  uni.navigateTo({
    url: '/package-user/pages/order-list/order-list?sts=0'
  })
}

</script>

<style lang="scss" scoped>
  @use "./pay-success.scss";
</style>
