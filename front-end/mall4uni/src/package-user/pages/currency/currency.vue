<!--
  Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.

  https://www.mall4j.com/

  未经允许，不可做商业用途！

  版权所有，侵权必究！
-->

<template>
  <view class="Mall4j container page-lang">
    <view class="lang-list">
      <view
        v-for="(item, index) in currencyList"
        :key="index"
        class="lang-item"
        @tap="selectLang(item)"
      >
        <view class="lang-name">
          {{ item.currencyCode + ' - ' + item.currencyName }}
        </view>
        <view :class="[currency === item.currencyCode?'check-style':'']" />
      </view>
    </view>
    <view class="pop-foot">
      <view
        class="foot-btn"
        @tap="confirmLang"
      >
        {{ $t('finish') }}
      </view>
    </view>
  </view>
</template>

<script setup>
import { useCurrencyStore } from '@/stores/modules/currency'

const currencyStore = useCurrencyStore()
const currencyList = computed(() => {
  return currencyStore.currencyList
})

onShow(() => {
  uni.setNavigationBarTitle({
    title: $t('setCurrency')
  })
})

const currency = ref(currencyStore.currency)
const lang = ref('')
const selectLang = (item) => {
  currency.value = item.currencyCode
  lang.value = item.lang
}

const confirmLang = () => {
  currencyStore.SET_CURRENCY(currency.value)

  // 重新获取网站配置信息
  useWebConfigStore().getUniWebConfig()
  uni.reLaunch({
    url: '/pages/user/user'
  })
}

</script>

<style lang="scss" scoped>
@use "currency";
</style>
