<!--
  Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.

  https://www.mall4j.com/

  未经允许，不可做商业用途！

  版权所有，侵权必究！
-->

<template>
  <view class="Mall4j page-terms">
    <!-- 服务条款/隐私协议 -->
    <rich-text :nodes="content" />
  </view>
</template>

<script setup>
import util from '@/utils/util.js'

const content = ref('')
let sts = ''
onLoad((options) => {
  ServiceTermsConfig(options.sts)
  sts = options.sts
})

onShow(() => {
  uni.setNavigationBarTitle({
    title: sts === 'serviceTerms' ? $t('termsOfService') : $t('privacyPolicy')
  })
})

/* key = 'service-terms'代表服务条款，key = 'service-policy'代表隐私策略 */
const ServiceTermsConfig = (key) => {
  const params = {
    url: '/sys/config/info/' + key,
    method: 'GET'
  }
  http.request(params).then(({ data }) => {
    content.value = util.formatHtml(JSON.parse(data).content)
  })
}

</script>

<style lang="scss" scoped>
@use "terms-page";
</style>
