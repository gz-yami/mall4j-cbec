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
        v-for="(item, index) in langList"
        :key="index"
        class="lang-item"
        @tap="selectLang(item)"
      >
        <view class="lang-name">
          {{ item.name }}
        </view>
        <view :class="[language===item.language?'check-style':'']" />
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
import { i18n } from '@/lang'

const langList = ref([])

onLoad(() => {
  getLangList()
})

onShow(() => {
  uni.setNavigationBarTitle({
    title: $t('setLanguages')
  })
})

const language = ref('')
const lang = ref('')
const selectLang = (item) => {
  language.value = item.language
  lang.value = item.lang
}

const currencyStore = useCurrencyStore()
const confirmLang = () => {
  i18n.global.locale.value = language.value
  uni.setStorageSync('cbecB2cLang', language.value)
  uni.setStorageSync('cbecB2cLangKey', lang.value + '')

  uni.setLocale(language.value)

  // 重新获取货币信息
  currencyStore.INIT_CURRENCY_DATA()
  // 重新获取网站配置信息
  useWebConfigStore().getUniWebConfig()
  uni.reLaunch({
    url: '/pages/user/user'
  })
}

// 获取国际化语言列表
const getLangList = () => {
  const param = {
    url: '/lang/default',
    method: 'GET'
  }
  http.request(param).then(({ data }) => {
    const langListPar = data.langItemList.filter(f => !f.hide) // 过滤掉隐藏的语言
    const defLangObj = data.langItemList.find(f => f.master === 1) || data.langItemList[0] // 获取系统默认语言
    uni.setStorageSync('cbecB2cDefLangObj', defLangObj)
    const b2cLang = uni.getStorageSync('cbecB2cLang')
    const b2cLangKey = uni.getStorageSync('cbecB2cLangKey')
    // 当前语言存在语言列表中
    let isExist = false
    if (b2cLang) {
      for (const it of langListPar) {
        if (it.language === b2cLang) {
          language.value = it.language
          isExist = true
          break
        }
      }
    }
    // 当前无缓存语言或当前缓存语言不在语言列表时
    if (!b2cLang || !isExist) {
      language.value = data.language
      uni.setStorageSync('cbecB2cLang', data.language)
      uni.setStorageSync('cbecB2cLangKey', data.lang + '')
    } else if (!b2cLangKey) {
      const curLangItem = langListPar.find(f => f.language === b2cLang)
      uni.setStorageSync('cbecB2cLangKey', curLangItem.lang + '')
    }
    langList.value = langListPar
  })
}

</script>

<style lang="scss" scoped>
@use "lang";
</style>
