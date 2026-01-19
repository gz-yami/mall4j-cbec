<template>
  <div
    :class="$t('language') !== 'zh_CN'&&homeHidden&&isDecorate&&!sidebarFold?'site-content-en': ''"
  >
    <el-card
      v-if="route.meta.isTab && isDecorate"
      class="main-head"
    >
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item
          v-for="(item, index) in selectMenu"
          :key="index"
          class="breadcrumb-item"
        >
          <span>{{ item }}</span>
        </el-breadcrumb-item>
      </el-breadcrumb>
    </el-card>
    <main
      class="site-content"
      :class="{
        'site-content--tabs': route.meta.isTab,
        'padding-none' : !isDecorate
      }"
    >
      <!-- 新增/编辑装修页 -->
      <div v-if="!isDecorate">
        <router-view />
      </div>
      <el-card
        v-else-if="homeHidden"
        class="card-content-h"
        style="border-radius: 0 !important; box-shadow: none"
        :body-style="siteContentViewHeight"
      >
        <router-view v-slot="{Component}">
          <keep-alive :include="includePageList">
            <component :is="Component" />
          </keep-alive>
        </router-view>
      </el-card>
      <div v-else>
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup>
import { useRouterStore } from '../stores/router'

const route = useRoute()
const documentClientHeight = ref(document.documentElement.clientHeight)
window.addEventListener('resize', () => {
  documentClientHeight.value = document.documentElement.clientHeight
})

const isDecorate = computed(() => {
  return !(route.path === '/fitment/feature/create/edit/index' || route.path === '/fitment/decorate/create/edit/index')
})

const siteContentViewHeight = computed(() => {
  const height = documentClientHeight.value - 50 - 30 - 2
  return { minHeight: height + 'px' }
})
const includePageList = computed(() => {
  return useRouterStore().includePageList
})

const commonStore = useCommonStore()
const selectMenu = computed(() => commonStore.selectMenu)
const sidebarFold = computed(() => commonStore.sidebarFold)
const homeHidden = computed(() => route.name !== 'home')
</script>
<style scoped>
.main-head {
  background: #ffffff;
  width: 100%;
  height: 40px;
  position: fixed;
  top: 50px;
  z-index: 10;
  display: flex;
  align-items: center;
  border-radius: 0;
  box-shadow: none;
  border-top: none;
}
.breadcrumb-item:last-child span {
  color: #155bd4 !important;
}
.card-content-h {
  min-height: calc(100vh - 50px - 60px - 20px);
}
.padding-none {
  padding: 0 !important;
}
.site-content-en {
  margin-left: 46px;
}
</style>
