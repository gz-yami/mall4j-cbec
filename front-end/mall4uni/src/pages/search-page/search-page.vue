<!--
  Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.

  https://www.mall4j.com/

  未经允许，不可做商业用途！

  版权所有，侵权必究！
-->

<template>
  <view class="Mall4j page-search-page container">
    <!-- 搜索框 -->
    <view class="search-bar">
      <view class="search-box">
        <input
          :placeholder="$t('enterKeywordSearch')"
          class="sear-input"
          confirm-type="search"
          :value="searchKey"
          @confirm="onToSearchProdPage"
          @input="onGetSearchContent"
        >
        <image
          src="/static/images/icon/search.png"
          class="search-img"
        />
      </view>
      <text
        class="search-hint"
        @tap="onToSearchProdPage"
      >
        {{ $t('search') }}
      </text>
    </view>

    <view class="search-display">
      <!-- 热门搜索 -->
      <view
        v-if="hotProdSearchList.length"
        class="hot-search"
      >
        <view class="title-text">
          {{ $t('popularSearches') }}
        </view>
        <view class="hot-search-tags">
          <block v-if="hotProdSearchList.length">
            <block
              v-for="(item, index) in hotProdSearchList"
              :key="index"
            >
              <text
                class="tags"
                :data-name="item.content"
                @tap="onHistSearch"
              >
                {{ item.title }}
              </text>
            </block>
          </block>
        </view>
      </view>

      <!-- 搜索历史 -->
      <view
        v-if="recentProdSearch && recentProdSearch.length > 0"
        class="history-search"
      >
        <view
          class="title-text history-line"
          :style="{borderTop:(hotProdSearchList.length ? '2rpx solid #e1e1e1;': 'none')}"
        >
          {{ $t('searchHistory') }}
          <view class="clear-history">
            <image
              src="/static/images/icon/clear-his.png"
              @tap="onClearSearch"
            />
          </view>
        </view>
        <!-- 商品 -->
        <block v-if="recentProdSearch && recentProdSearch.length > 0">
          <block
            v-for="(item, index) in recentProdSearch"
            :key="index"
          >
            <view
              class="his-search-tags"
            >
              <text
                class="tags"
                :data-name="item"
                @tap="onHistSearch"
              >
                {{ item }}
              </text>
            </view>
          </block>
        </block>
      </view>
      <!-- 空列表或加载全部提示 -->
      <empty-all-tips
        v-if="isLoaded"
        :empty-img="emptyImg"
        :img-sty="{width:'260rpx',height:'260rpx'}"
        :is-empty="!hotProdSearchList.length && !recentProdSearch.length"
      />
    </view>
  </view>
</template>

<script setup>
import emptyImg from '@/static/images/empty-img/not-found.png'

const hotProdSearchList = ref([]) // 商品热搜
const searchKey = ref('') // 搜索关键字
const recentProdSearch = ref([]) // 商品搜索历史
const isLoaded = ref(false)

/**
 * 用户点击右上角分享 (勿删)
 */
onShareAppMessage(() => {})

/**
 * 生命周期函数--监听页面显示
 */
onShow(() => {
  // 加载导航标题
  uni.setNavigationBarTitle({
    title: $t('search')
  })
  // 商品热搜
  prodHotSearch()
  // 获取搜索历史
  getRecentSearch()
})

/**
 * 生命周期函数--监听页面隐藏
 */
onHide(() => {
  searchKey.value = ''
})

/**
 * 商品热搜
 */
const prodHotSearch = () => {
  isLoaded.value = false
  const params = {
    url: '/search/hotSearch',
    method: 'GET',
    data: {
      type: 1
    }
  }
  // 获取历史搜索
  http.request(params).then(({ data: res }) => {
    isLoaded.value = true
    hotProdSearchList.value = res
  })
}

/**
 * 搜索店铺(商品)提交
 */
const onToSearchProdPage = () => {
  // searchKey = encodeURIComponent(searchKey)
  // 判断搜索框是否为空
  if (searchKey.value === '' || !searchKey.value.trim()) {
    uni.showToast({
      title: $t('inpKeyWords'),
      icon: 'none'
    })
    return
  }
  // 商品搜索关键字
  let recentProdSearch = uni.getStorageSync('cbecB2cRecentProdSearch') || []
  recentProdSearch = recentProdSearch.filter(item => item !== searchKey.value)
  recentProdSearch.unshift(searchKey.value)
  if (recentProdSearch.length > 10) {
    recentProdSearch.pop()
  }
  // 将历史放到缓存中
  uni.setStorageSync('cbecB2cRecentProdSearch', recentProdSearch)
  // 跳转到商品(店铺)列表页
  uni.redirectTo({
    url: `/package-prod/pages/search-prod-show/search-prod-show?prodName=${encodeURIComponent(searchKey.value)}&typeSelectIndex=0`
  })
}

/**
 * 获取历史搜索
 */
const getRecentSearch = () => {
  const _recentProdSearch = uni.getStorageSync('cbecB2cRecentProdSearch')
  recentProdSearch.value = _recentProdSearch
}

/**
 * 清空搜索历史
 */
const onClearSearch = () => {
  uni.removeStorageSync('cbecB2cRecentProdSearch')
  getRecentSearch()
}
// 输入商品名获取数据 || 绑定输入值
const onGetSearchContent = (e) => {
  searchKey.value = e.detail.value
  // data.searchKey.value=e.detail.value
}
// 点击搜素历史
const onHistSearch = (e) => {
  const name = e.currentTarget.dataset.name

  searchKey.value = name

  onToSearchProdPage()
}

</script>
<style lang="scss" scoped>
@use "./search-page.scss";
</style>
