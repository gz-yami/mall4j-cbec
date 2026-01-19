<template>
  <el-config-provider
    :locale="language"
    namespace="el"
  >
    <router-view />
  </el-config-provider>
</template>

<script setup>
import zh from 'element-plus/es/locale/lang/zh-cn'
import en from 'element-plus/es/locale/lang/en'
import { useCurrencyStore } from '@/stores/currency.js'
import { useLangStore } from '@/stores/lang.js'

const language = localStorage.getItem('cbecB2cLang')?.indexOf('zh') !== -1 ? zh : en
const langStore = useLangStore() // 加载语言列表
const currencyStore = useCurrencyStore() // 默认货币
onMounted(async () => {
  langStore.initLangList()
  await currencyStore.INIT_CURRENCY_DATA()
})
</script>

<!-- eslint-disable-next-line vue-scoped-css/enforce-style-type -->
<style lang="scss">
@use '@/assets/app.scss';
</style>
