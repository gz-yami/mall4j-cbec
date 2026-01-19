<!--
  Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.

  https://www.mall4j.com/

  未经允许，不可做商业用途！

  版权所有，侵权必究！
-->
<template>
  <view class="Mall4j page-index index-stytle">
    <!-- #ifndef H5-->
    <lh-navigation-bar
      v-if="navigationBarIsShow"
      bg-color="bg-gradual-pink"
      :show-back="false"
      :navigation-bar-style="tabConfig"
      :is-left="false"
      :is-bg-img="isBgImg"
      :title="title"
      :show-title="showTitle"
      :bg-img-mode="'aspectFill'"
    />
    <!-- #endif -->
    <view class="container">
      <!-- 搜索框 -->
      <view class="bg-sear">
        <view class="scrolltop">
          <view
            class="section"
            @tap="onToSearchPage"
          >
            <image
              src="/static/images/icon/search.png"
              class="search-img"
            />
            <text class="placeholder">
              {{ hotProdSearchTie || $t('search') }}
            </text>
          </view>
        </view>
      </view>
      <!-- 搜索框end -->

      <!-- 导航&公告 -->
      <view :class="['content']">
        <view
          v-if="indexImgs.length"
          class="bg-color"
        />
        <!-- swiper -->
        <view
          v-if="indexImgs.length"
          class="swiper-wrap"
        >
          <swiper
            circular
            :autoplay="autoplay"
            :indicator-color="indicatorColor"
            :interval="interval"
            :duration="duration"
            :indicator-active-color="indicatorActiveColor"
            class="card-swiper"
            indicator-dots
          >
            <block
              v-for="(item, seq1) in indexImgs"
              :key="seq1"
            >
              <swiper-item class="banner-item">
                <view class="img-box">
                  <img-show
                    :src="item.imgUrl"
                    :class-list="['banner']"
                    :def-img="'/static/images/empty-img/bg-def.png'"
                    @handle-tap="()=>onToIndexImgContent(item)"
                  />
                </view>
              </swiper-item>
            </block>
          </swiper>
        </view>
        <!-- end swiper -->
      </view>
      <!-- 导航&公告end -->

      <!-- 商城热卖 -->
      <view
        v-if="hotSalesList.length"
        class="goods-list"
      >
        <view class="title">
          <image
            src="/static/images/icon/index-title-icon.png"
            class="title-img title-img-left"
          />
          <text>{{ $t('RecommendedYou2') }}</text>
          <image
            src="/static/images/icon/index-title-icon.png"
            class="title-img"
          />
        </view>
        <view class="goods-item-cont">
          <prod-list
            :show-vip="true"
            :prods="hotSalesList"
            :new-data="isNewData"
            @load-first-page="loadFirstPage"
          />
        </view>
      </view>
      <!-- 空列表或加载全部提示 -->
      <empty-all-tips
        v-if="isLoaded"
        :is-all="isAll"
        :all-tips="$t('allLoaded')"
      />
    </view>

    <!-- 回到顶部 -->
    <back-top-btn v-if="showBacktop" />
    <privacy-pop
      v-if="showPop"
      @hide-pop="onHidePop"
    />
  </view>
</template>

<script setup>
const navigationBarIsShow = ref(false)

const indicatorColor = 'rgba(255, 255, 255, .6)'
const indicatorActiveColor = '#fff'
const autoplay = true
const interval = 3000
const duration = 1000
const indexImgs = ref([])
const seq = ref(0)
const hotSalesList = ref([])
let current = 1
const isAll = ref(false)
let pages = ''
let scrollTop = ''
const showBacktop = ref(false)
const hotProdSearchTie = ref('') // 热搜

const pageScorllTop = ref(0) // 页面滚动距离

const showPop = ref(false)
const tabConfig = ref({
  background: '',
  fontColor: '#FFFFFF',
  iconColor: '#FFFFFF'
})
const title = ref('')
const isBgImg = ref(false)
const prodTypeArr = ref([])

const isLoaded = ref(false)
onMounted(() => {
  const res = uni.getSystemInfoSync()
  if (res.system.toString().indexOf('Windows') < 0) {
    navigationBarIsShow.value = true
  }
  prodTypeArr.value = [
    $t('prodType1'),
    $t('prodType2'),
    $t('prodType3'),
    $t('prodType4')
  ]
})
onLoad(() => {
  uni.showNavigationBarLoading()
  util.transTabbar()
  // #ifdef APP-PLUS
  if (
    !uni.getStorageSync('cbecB2cIsPrivacy') ||
    uni.getStorageSync('cbecB2cIsPrivacy') === -1
  ) {
    uni.hideTabBar()
    showPop.value = true
  }
  // #endif
  getAllData()
})

onShow(() => {
  http.getCartCount() // 重新计算购物车总数量
  if (getApp().globalData.reloadHomePopupAd) {
    getApp().globalData.reloadHomePopupAd = false
  }
})

/**
 * 页面上拉触底事件的处理函数
 */
onReachBottom(() => {
  getNextPage()
})

const isNewData = ref(false)
onPullDownRefresh(() => {
  // 模拟加载
  current = 1
  isNewData.value = true
  setTimeout(() => {
    uni.stopPullDownRefresh() // 停止下拉刷新
  }, 100)
})

// 页面滚动到指定位置指定元素固定在顶部
onPageScroll(function (e) {
  scrollTop = e.scrollTop
  pageScorllTop.value = e.scrollTop
  if (scrollTop > 800) {
    showBacktop.value = true
  } else if (scrollTop < 800) {
    showBacktop.value = false
  }
})

/**
 * 用户点击右上角分享 (勿删)
 */
onShareAppMessage(() => {})

const onHidePop = () => {
  showPop.value = false
  uni.showTabBar()
}

const webConfigStore = useWebConfigStore()
watch(() => webConfigStore.webConfig.uniTitleContent, (titleVal) => {
  uni.setNavigationBarTitle({
    title: titleVal
  })
  // // 头部标题
  title.value = titleVal
}, { immediate: true })

const showTitle = ref(true)

// 点击轮播图跳转相应页面
const onToIndexImgContent = (item) => {
  if (item.type !== 0) {
    return
  }
  const prodId = item.relation
  http.request({
    url: '/prod/isStatus',
    method: 'GET',
    data: {
      prodId
    }
  }).then(({ data: res }) => {
    if (res) {
      uni.navigateTo({
        url: '/package-prod/pages/prod/prod?bannerEnter=1&prodId=' + prodId
      })
    }
  })
}

// 跳转搜索页
const onToSearchPage = () => {
  uni.navigateTo({
    url: '/pages/search-page/search-page'
  })
}

const getAllData = () => {
  getIndexImgs()
  getApp().globalData.indexGetHotSalesProds = () => {
    isNewData.value = true
    current = 1
    getHotSalesProds().finally(() => {
      isNewData.value = false
    })
  }
  getHotSalesProds()
}

// 加载轮播图
const getIndexImgs = () => {
  // 加载轮播图
  const params = {
    url: '/indexImgs',
    method: 'GET',
    data: {}
  }
  http.request(params).then(({ data: res }) => {
    indexImgs.value = res || []
    seq.value = res
  })
}

/**
 * 加载热销商品列表
 */
const getHotSalesProds = () => {
  let userId = null

  if (uni.getStorageSync('cbecB2cToken') && uni.getStorageSync('cbecB2cUserInfo')) {
    userId = uni.getStorageSync('cbecB2cUserInfo').userId
  }
  isLoaded.value = false
  const param = {
    // url: "/search/searchProdPage",
    url: '/search/page',
    method: 'GET',
    data: {
      current,
      size: 20,
      sort: 2,
      userId,
      orderBy: 1,
      isActive: 1 // 过滤掉活动商品
    }
  }
  return http.request(param).then(({ data: res }) => {
    isLoaded.value = true
    let tempHotSalesList = []
    if (current == 1) {
      hotSalesList.value = res.records
      pages = res.pages
    } else {
      tempHotSalesList = hotSalesList.value
      tempHotSalesList.push(...res.records)

      hotSalesList.value = tempHotSalesList
    }
  })
}

// 触底加载下一页
const getNextPage = () => {
  if (pages > current) {
    current = current + 1

    getHotSalesProds()
  } else {
    isAll.value = true
  }
}

// 上拉加载
const loadFirstPage = () => {
  isNewData.value = false
}

</script>
<style lang="scss" scoped>
@use './index.scss';
</style>
