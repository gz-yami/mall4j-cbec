<!--
  Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.

  https://www.mall4j.com/

  未经允许，不可做商业用途！

  版权所有，侵权必究！
-->

<template>
  <view class="Mall4j  page-category  container">
    <!-- 头部搜索区 -->
    <view class="search-bar">
      <view
        class="search-box"
        @tap="toSearchPage"
      >
        <image
          src="/static/images/icon/search.png"
          class="search-img"
        />
        <text class="sear-input">
          {{ $t('searchGoods') }}
        </text>
      </view>
    </view>
    <!-- 滚动内容区 -->
    <view class="main">
      <!-- 左侧菜单start -->
      <view class="left-bg">
        <scroll-view
          scroll-y
          class="leftmenu"
          @scroll="leftmenuScroll"
        >
          <block
            v-for="(item, index) in categoryList"
            :key="index"
          >
            <view
              :class="['menu-item', (selIndex == index ? 'active' : ''), (selIndex-1 == index ? 'active-top' : ''), (selIndex+1 == index ? 'active-bottom' : '')]"
              :data-index="index"
              :data-id="item.categoryId"
              :data-categoryname="item.categoryName"
              @tap="onMenuTab"
            >
              <text class="menu-text">
                {{ item.categoryName }}
              </text>
            </view>
          </block>
          <view
            v-if="showLastMenuitem&&categoryList&&categoryList.length>=1"
            :class="['menu-item','last-menu-item', (selIndex+1 === categoryList.length ? 'active-bottom' : '')]"
          />
        </scroll-view>
      </view>

      <!-- 左侧菜单end -->

      <!-- 右侧内容start -->
      <scroll-view
        scroll-y
        class="rightcontent"
        :style=" {'border-radius': selIndex !== 0 ? '12rpx 0 0 0' : ''}"
      >
        <view
          v-if="categoryImg"
          class="adver-map"
        >
          <view class="item-a">
            <img-show
              :src="categoryImg"
              mode="widthFix"
              @error="handlePicError"
            />
          </view>
        </view>

        <view
          v-if="brandList.length > 0"
          class="sub-category"
        >
          <view class="sub-category-con">
            <view class="sub-cate-title">
              <text class="sub-cate-text">
                {{ $t('brandClass') }}
              </text>
            </view>
            <view class="th-cate-con">
              <block
                v-for="(item, index) in brandList"
                :key="index"
              >
                <view
                  class="sub-category-item"
                  :data-brandid="item.brandId"
                  :data-brandname="item.name"
                  @tap="toBrandPage"
                >
                  <image
                    v-if="item.imgUrl"
                    :src="util.checkFileUrl(item.imgUrl)"
                    mode="aspectFill"
                    class="sub-category-item-pic"
                    @error="item.imgUrl=''"
                  />
                  <!-- 默认图 -->
                  <image
                    v-else
                    src="/static/images/icon/def.png"
                    class="sub-category-item-pic"
                    mode="aspectFill"
                  />
                  <text class="sub-category-item-name">
                    {{ item.name || '' }}
                  </text>
                </view>
              </block>
            </view>
          </view>
        </view>

        <block
          v-for="(item, subCateIndex) in proSubCategoryList"
          :key="subCateIndex"
        >
          <view class="sub-category">
            <view class="sub-category-con">
              <view class="sub-cate-title">
                <text class="sub-cate-text">
                  {{ item.categoryName }}
                </text>
              </view>
              <view
                v-if="item.categories"
                class="th-cate-con"
              >
                <block
                  v-for="(thCateItem, categoryId) in item.categories"
                  :key="categoryId"
                >
                  <view
                    class="sub-category-item"
                    :data-categoryid="thCateItem.categoryId"
                    :data-parentid="item.categoryId"
                    @tap="toCatePage"
                  >
                    <image
                      v-if="thCateItem.imgsrcTail"
                      :src="util.checkFileUrl(thCateItem.pic)"
                      mode="aspectFill"
                      class="sub-category-item-pic"
                      @error="thCateItem.pic=''"
                    />
                    <!-- 默认图 -->
                    <image
                      v-else
                      src="/static/images/icon/def.png"
                      class="sub-category-item-pic"
                      mode="aspectFill"
                    />
                    <text class="sub-category-item-name">
                      {{ thCateItem.categoryName }}
                    </text>
                  </view>
                </block>
              </view>
              <view v-else>
                <empty-all-tips :is-empty="true" />
              </view>
            </view>
          </view>
        </block>
      </scroll-view>
      <!-- 右侧内容end -->
    </view>
  </view>
</template>

<script setup>
import util from '@/utils/util'

const selIndex = ref(0)
const categoryList = ref([])
const brandList = ref([])
const categoryImg = ref('')
const subCategoryList = ref([])
let parentId = ''
const showLastMenuitem = ref(true)

const proSubCategoryList = computed(() => {
  const prepareArr = subCategoryList.value
  prepareArr.map(item => {
    if (!item.categories) return item
    item.categories.map(items => {
      // 添加 判断图片路径是否有效的 标识
      items.imgsrcTail = false
      if (!items.pic) return items
      const imgPic = items.pic
      // 检索路径后四位是否为null
      const imgsrcTail = imgPic.slice(imgPic.length - 4, imgPic.length)
      if (imgsrcTail !== 'null') items.imgsrcTail = true
      return items
    })
    return item
  })
  return prepareArr
})

/**
 * 生命周期函数--监听页面加载
 */
onLoad(() => {
  getCategory()
})

/**
 * 用户点击右上角分享 (勿删)
 */
onShareAppMessage(() => {})

/**
 * 生命周期函数--监听页面显示
 */
onShow(async () => {
  setTimeout(() => {
    // 加载导航标题
    uni.setNavigationBarTitle({
      title: $t('classifiedGoods')
    })
  }, 100)

  util.transTabbar()

  http.getCartCount()
})

/**
 * 页面相关事件处理函数--监听用户下拉动作
 */
onPullDownRefresh(() => {
  setTimeout(() => {
    getCategory()
    wx.stopPullDownRefresh() // 停止下拉刷新
  }, 100)
})

const getCategory = () => {
  const params = {
    url: '/category/categoryInfo',
    method: 'GET',
    data: {
      parentId: ''
    }
  }
  http.request(params).then(({ data: res }) => {
    categoryImg.value = res.categoryInfo[0].pic
    categoryList.value = res.categoryInfo
    brandList.value = res.brandInfo || []
    uni.setStorageSync('cbecB2cCategoryName', res.categoryInfo[0]?.categoryName)

    if (parentId == '') {
      getProdList(res.categoryInfo[0].categoryId)
    } else {
      getProdList(res.categoryInfo[selIndex.value].categoryId)
    }
  })
}
/**
 * 图片加载失败时，现实默认图片
 */
const handlePicError = () => {
  categoryImg.value = ''
}
/**
 * 分类点击事件，获取子分类
 */
const onMenuTab = (e) => {
  // 用于回显子分类的标题
  const categoryname = e.currentTarget.dataset.categoryname
  uni.setStorageSync('cbecB2cCategoryName', categoryname)

  const index = e.currentTarget.dataset.index
  getProdList(categoryList.value[index].categoryId)

  categoryImg.value = categoryList.value[index].pic
  selIndex.value = index
}
// 跳转搜索页
const toSearchPage = () => {
  uni.navigateTo({
    url: '/pages/search-page/search-page'
  })
}

const getProdList = (categoryId) => {
  parentId = categoryId
  // 加载子分类列表

  const params = {
    url: '/category/categoryInfo',
    method: 'GET',
    data: {
      parentId: categoryId
    }
  }
  http.request(params).then(({ data: res }) => {
    subCategoryList.value = res.categoryInfo
    brandList.value = res.brandInfo || []
  })
}

// 跳转子分类商品页面
const toCatePage = (e) => {
  const { type, parentid, categoryid, subcateindex } =
    e.currentTarget.dataset
  uni.navigateTo({
    url: `/package-prod/pages/sub-category/sub-category?parentId=${parentid}&categoryId=${
      type == 'all' ? subCategoryList.value[subcateindex].categories[0].categoryId : categoryid
    }`
  })
}
const toBrandPage = (e) => {
  const { brandid, brandname } = e.currentTarget.dataset
  uni.setStorageSync('cbecB2cBrandList', brandList.value)
  uni.navigateTo({
    url: `/package-prod/pages/sub-category/sub-category?brandId=${brandid}&brandName=${brandname}`
  })
}
// leftmenu滚动
const leftmenuScroll = () => {
  if (showLastMenuitem.value) {
    showLastMenuitem.value = false
  }
}

</script>
<style lang="scss" scoped>
@use "./category.scss";
</style>
