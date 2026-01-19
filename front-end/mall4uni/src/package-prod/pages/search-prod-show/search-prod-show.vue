<!--
  Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.

  https://www.mall4j.com/

  未经允许，不可做商业用途！

  版权所有，侵权必究！
-->

<template>
  <view class="Mall4j page-search-prod-show container">
    <!-- 搜索框 -->
    <view class="fixed-box">
      <view class="search-bar">
        <view class="search-box">
          <input
            v-model="prodName"
            class="sear-input"
            :placeholder="$t('inpKeyWords')"
            confirm-type="search"
            @input="onGetSearchContent"
            @confirm="onToSearchConfirm"
          >
          <image
            src="/static/images/icon/search.png"
            class="search-img"
          />
          <image
            src="/static/images/icon/icon_close.png"
            class="search-close"
            @tap="onSearchClose()"
          />
        </view>
        <view
          class="search-text"
          @tap="onToSearchConfirm"
        >
          {{ $t('search') }}
        </view>
      </view>
    </view>

    <!-- 商品列表 -->
    <view class="prod-list">
      <!-- 横向列表 -->
      <view class="prod-show">
        <view class="hotsale-item-cont">
          <prod-list
            :prods="searchProdList"
            :show-type="showType"
            :new-data="current === 1"
            :show-vip="true"
          />
        </view>
      </view>
    </view>
    <!-- 空列表或加载全部提示 -->
    <empty-all-tips
      v-if="isLoaded"
      class="shopEmpty-wrap"
      :is-empty="!searchProdList.length"
      :empty-tips="$t('noProducts')"
      :is-all="loadAll && isPageBottom"
      :empty-img="orderEmptyImg"
    />

    <!-- 回到顶部 -->
    <back-top-btn v-if="showBacktop" />
  </view>
</template>

<script setup>
import temProdEmptyImg from '@/package-prod/static/images/empty-img/empty_order.png'

let orderEmptyImg = ''

// #ifndef MP-WEIXIN
orderEmptyImg = temProdEmptyImg
// #endif

const searchProdList = ref([])
const prodName = ref('')
let pages = 0
const current = ref(1)
const size = 20
const loadAll = ref(false) // 是否加载完成
let scrollTop = ''
const showBacktop = ref(false)
const isLoaded = ref(false)
let routeQuery = {}

const isPageBottom = ref(false) // 是否页面触底

/**
 * 生命周期函数--监听页面加载
 */
onLoad((options) => {
  routeQuery = options || {}

  let keyword = ''
  // #ifndef MP-WEIXIN
  keyword = options.prodName ? options.prodName : ''
  // #endif
  // #ifdef APP-PLUS
  keyword = options.prodName ? decodeURIComponent(options.prodName) : ''
  // #endif

  prodName.value = keyword

  toLoadData()
})

/**
 * 生命周期函数--监听页面显示
 */
onShow(() => {
  // 设置头部导航标题
  uni.setNavigationBarTitle({
    title: $t('searchResult')
  })
})

/**
 * 页面上拉触底事件的处理函数
 */
onReachBottom(() => {
  isPageBottom.value = true
  if (current.value < pages) {
    current.value = current.value + 1

    toLoadData()
  } else {
    if (!pages) {
      return
    }
    loadAll.value = true
  }
})

// 页面滚动到指定位置指定元素固定在顶部
onPageScroll((e) => {
  scrollTop = e.scrollTop
  if (scrollTop > 200) {
    showBacktop.value = true
  } else if (scrollTop < 200) {
    showBacktop.value = false
  }
})

const showType = ref(2)

// 输入商品获取数据
const onGetSearchContent = () => {
  if (!prodName.value) {
    uni.redirectTo({
      url: '/pages/search-page/search-page'
    })
  }
}
const onSearchClose = () => {
  uni.redirectTo({
    url: '/pages/search-page/search-page'
  })
}
// 请求商品接口
const toLoadData = () => {
  const data = {
    current: current.value,
    keyword: prodName.value || routeQuery.prodName,
    size
  }
  isLoaded.value = false
  const params = {
    url: '/search/page',
    method: 'GET',
    data: removeNullUndefined(data)
  }
  http.request(params).then(({ data: res }) => {
    isLoaded.value = true
    let list = []
    if (params.data.current == 1) {
      list = res.records
    } else {
      list = searchProdList.value
      list = searchProdList.value.concat(res.records)
    }
    searchProdList.value = list
    pages = res.pages
    if (res.pages == res.current && res.pages) {
      loadAll.value = true
    }
  })
}
// 当前搜索页二次搜索商品
const onToSearchConfirm = () => {
  if (prodName.value === '' || !prodName.value.trim()) {
    uni.showToast({
      title: $t('inpKeyWords'),
      duration: 1000,
      icon: 'none'
    })
    return
  }
  // 商品搜索关键字
  let recentProdSearch = uni.getStorageSync('cbecB2cRecentProdSearch') || []
  recentProdSearch = recentProdSearch.filter(item => item !== prodName.value)
  recentProdSearch.unshift(prodName.value)
  if (recentProdSearch.length > 10) {
    recentProdSearch.pop()
  }
  // 将历史放到缓存中
  uni.setStorageSync('cbecB2cRecentProdSearch', recentProdSearch)
  uni.redirectTo({
    url: `/package-prod/pages/search-prod-show/search-prod-show?prodName=${encodeURIComponent(prodName.value)}`
  })
}

const removeNullUndefined = (obj) => {
  return Object.entries(obj).reduce((a, [k, v]) => (v == null ? a : ((a[k] = v), a)), {})
}

</script>
<style lang="scss" scoped>
@use "./search-prod-show.scss";
</style>
