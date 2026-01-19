<!--
  Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.

  https://www.mall4j.com/

  未经允许，不可做商业用途！

  版权所有，侵权必究！
-->
<template>
  <view class="Mall4j page-prod-classify container">
    <view>
      <block
        v-for="(item, key) in prodList"
        :key="key"
      >
        <production
          :item="item"
          :show-cancel-collect="showCancelCollect"
          :is-show-sold="false"
          @get-collection-prod="onGetCollectionProd"
        />
      </block>

      <!-- 空列表或加载全部提示 -->
      <empty-all-tips
        v-if="isLoaded"
        :img-sty="{height:'320px'}"
        :is-empty="!prodList.length"
        :is-all="prodList.length > 10 && loadAll"
        :empty-img="prodEmptyImg"
        :empty-tips="$t('noProducts')"
      />
    </view>
  </view>
</template>

<script setup>
import prodEmptyImg from '@/package-user/static/images/empty-img/coupon.png'

const sts = ref(0)
const prodList = ref([])
let current = 1
const size = 20
let pages = 0
const showCancelCollect = ref(false) // 是否展示取消收藏按钮，用于传值到商品列表页；
const loadAll = ref(false) // 已加载全部
const isLoaded = ref(false)

/**
 * 生命周期函数--监听页面加载
 */
onLoad((options) => {
  current = 1
  pages = 0
  sts.value = options.sts
  if (sts.value == 5) {
    uni.setNavigationBarTitle({
      title: $t('myCollection')
    })
    showCancelCollect.value = true
  }
  loadProdData(options)
})

/**
 * 页面上拉触底事件的处理函数
 */
onReachBottom(() => {
  if (current < pages) {
    current = current + 1

    loadProdData()
  } else {
    loadAll.value = true
  }
})
/**
 * 加载商品数据
 */
const loadProdData = () => {
  const _sts = sts.value
  if (_sts == 5) {
    // 收藏商品列表
    onGetCollectionProd()
  }
}

/**
 * 获取我的收藏商品
 */
const onGetCollectionProd = () => {
  isLoaded.value = false
  const params = {
    url: '/p/user/collection/page',
    method: 'GET',
    data: {
      current,
      size
    }
  }
  http.request(params).then(({ data: res }) => {
    isLoaded.value = true
    let list = []

    if (res.current === 1) {
      list = (res.records && res.records) || []
    } else {
      list = prodList.value
      list = list.concat((res.records && res.records) || [])
    }

    prodList.value = list
    pages = res.pages
  })
}

</script>
<style lang="scss" scoped>
  @use './prod-classify.scss';
</style>
