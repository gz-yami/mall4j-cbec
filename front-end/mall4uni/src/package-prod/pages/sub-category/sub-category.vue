<!--onGetProdList
  Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.

  https://www.mall4j.com/

  未经允许，不可做商业用途！

  版权所有，侵权必究！
-->

<template>
  <z-paging
    ref="pagingRef"
    v-model="prodList"
    :refresher-enabled="false"
    :default-page-size="10"
    :safe-area-inset-bottom="false"
    :auto-scroll-to-top-when-reload="false"
    :auto-clean-list-when-reload="false"
    :paging-style="{'top': '0','background': '#f4f4f4'}"
    @query="onGetProdList"
  >
    <view class="Mall4j page-sub-category container">
      <!-- 顶部子分类tab -->
      <scroll-view
        scroll-x="true"
        class="category-tit"
        :scroll-into-view="intoView"
        :scroll-with-animation="true"
      >
        <block v-if="brandId">
          <block
            v-for="item in brandList"
            :key="item.brandId"
          >
            <view
              class="category-item"
              :class="{ on: item.name === brandName }"
              @tap="onBrandTap(item)"
            >
              {{ item.name }}
            </view>
          </block>
        </block>
        <block v-else>
          <block
            v-for="(item, index) in subCategoryList"
            :key="index"
          >
            <view
              :id="'sw' + item.categoryId"
              :class="
                'category-item ' + (item.categoryId == categoryId ? 'on' : '')
              "
              :data-id="item.categoryId"
              @tap="onSubCategoryTap"
            >
              {{ item.categoryName }}
            </view>
          </block>
        </block>
      </scroll-view>
      <!-- 商品列表 -->
      <view class="prod-item">
        <prod-item
          ref="prodItemRef"
          :show-vip="true"
          :prods="prodList"
        />
      </view>
    </view>
  </z-paging>
</template>

<script setup>
import prodItem from '@/components/prod-list/prod-list'

const subCategoryList = ref([])
const categoryId = ref(0)
const prodList = ref([])
let parentId = ''
const brandId = ref('')
const brandName = ref('')
const intoView = ref('')
const isLoaded = ref(false)
const brandList = ref([])
const isPageBottom = ref(false) // 页面是否触底
let titleName = ''
/**
   * 生命周期函数--监听页面加载
   */
onLoad((options) => {
  let _brandName = options.brandName

  parentId = options.parentId || ''
  categoryId.value = options.categoryId || ''
  brandId.value = options.brandId || ''
  brandName.value = _brandName
  titleName = options.titleName || ''
  if (!brandId.value) {
    getSubCategory()
  }
  brandList.value = uni.getStorageSync('cbecB2cBrandList')
  // 设置标题
  uni.setNavigationBarTitle({
    title: titleName || uni.getStorageSync('cbecB2cCategoryName') || $t('classifiedGoods')
  })
})

onUnload(() => {
  uni.removeStorageSync('cbecB2cBrandList')
})

/**
 * 获取顶栏子分类数据
 */
const getSubCategory = () => {
  const params = {
    url: '/category/categoryInfo',
    method: 'GET',
    data: {
      parentId
    }
  }
  http.request(params).then(({ data: res }) => {
    subCategoryList.value = res.categoryInfo

    nextTick(() => {
      intoView.value = 'sw' + categoryId.value
    })
  })
}

const pagingRef = ref(null)
/**
 * 根据分类id获取商品列表数据
 */
const onGetProdList = (pageNum, pageSize) => {
  let userId = null

  if (uni.getStorageSync('cbecB2cToken') && uni.getStorageSync('cbecB2cUserInfo')) {
    userId = uni.getStorageSync('cbecB2cUserInfo').userId
  }
  isLoaded.value = false
  const params = {
    // url: "/search/searchProdPage",
    url: '/search/page',
    method: 'GET',
    data: {
      categoryId: categoryId.value,
      brandIds: brandId.value,
      current: pageNum,
      size: pageSize,
      userId,
      isAllProdType: true,
      isActive: 1 // 过滤掉活动商品
    }
  }
  http.request(params).then(({ data: res }) => {
    isLoaded.value = true
    const reg = /^[a-z0-9]+$/ // 数字小写字母
    const reg2 = /^[A-Z]+$/ // 大写字母
    const reg3 = /[%&',.;=?_$\x22]+/ // 特殊字符
    const size = (item, sub, index) => {
      let count = 0
      let num = 0
      if (index > item.prodName.lenght) return count
      for (let i = sub; i < index; i++) {
        if (
          reg.test(item.prodName[i]) ||
            reg3.test(item.prodName[i]) ||
            item.prodName[i] === ' '
        ) {
          num += 1.2
        } else if (reg2.test(item.prodName[i])) {
          num += 0.9
        }
      }
      const floor = Math.floor(num / 2)
      count += floor
      if (floor !== 0) {
        count += size(item, index, index + sub)
        return count
      } else {
        return count
      }
    }
    res.records[0].products.forEach((item) => {
      item.sub = size(item, 0, 10)
    })
    pagingRef?.value?.complete(res.records[0].products)
  })
}

const prodItemRef = ref(null)
/**
 * 切换顶部分类
 */
const onSubCategoryTap = (e) => {
  if (prodItemRef.value) {
    prodItemRef.value.showProdList[0] = []
    prodItemRef.value.showProdList[1] = []
    prodItemRef.value.setIndex(0)
  }

  categoryId.value = e.currentTarget.dataset.id
  intoView.value = 'sw' + e.currentTarget.dataset.id
  pagingRef?.value?.reload()
}
const onBrandTap = (item) => {
  if (prodItemRef.value) {
    prodItemRef.value.showProdList[0] = []
    prodItemRef.value.showProdList[1] = []
    prodItemRef.value.setIndex(0)
  }
  isPageBottom.value = false

  brandId.value = item.brandId
  brandName.value = item.name
  pagingRef?.value?.reload()
}

</script>
<style lang="scss" scoped>
@use './sub-category.scss';
</style>
