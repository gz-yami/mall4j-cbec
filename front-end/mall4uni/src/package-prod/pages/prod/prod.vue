<!--
  Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.

  https://www.mall4j.com/

  未经允许，不可做商业用途！

  版权所有，侵权必究！
-->
<template>
  <!-- page-meta页面属性配置节点，用于指定页面的一些属性、监听页面事件。可部分替代pages.json的功能。 -->
  <page-meta page-style="overflow:auto" />
  <!-- 商品详情 -->
  <view :class="['Mall4j contenta', popupShowHiden ? 'page-hidden' : '','page-prod']">
    <view
      v-show="!commentShow"
      :class="['container', skuShow || commentShow ? 'overflow' : '']"
    >
      <!-- 头部 -->
      <view
        class="det-header"
        :style="{
          opacity: topTabOpacity,
          zIndex: topTabOpacity > 0.2 ? '99' : '-1',
        }"
      >
        <!-- #ifdef H5 -->
        <image
          src="/static/images/icon/icon_left.png"
          class="det-header-left"
          @tap="back"
        />
        <!-- #endif -->

        <view class="det-header-content">
          <view
            :class="[
              'item',
              ((topTabSts === 0 && pageScorllTop < heightList[1])||(topTabSts === 1&&prodType===3)) && !scrolling
                ? 'active'
                : '',
            ]"
            @tap="hanleTopTabClick(0)"
          >
            {{ $t('commodity') }}
          </view>
          <view
            v-if="prodType!==3"
            :class="[
              'item',
              topTabSts === 1 ||
                (pageScorllTop > heightList[1] &&
                  pageScorllTop < heightList[2] &&
                  !scrolling)||(heightList[2]<1&&topTabSts === 2)
                ? 'active'
                : '',
            ]"
            @tap="hanleTopTabClick(1)"
          >
            {{ $t('evaluation') }}
          </view>
          <view
            v-if="content"
            :class="[
              'item',
              topTabSts === 2 || (pageScorllTop > heightList[2] && !scrolling)
                ? 'active'
                : '',
            ]"
            @tap="hanleTopTabClick(2)"
          >
            {{ $t('details') }}
          </view>
        </view>

        <!-- #ifdef H5 -->
        <image
          src="/static/images/icon/icon_right.png"
          class="det-header-right"
          @tap="tabShowHandler"
        />
        <!-- #endif -->
      </view>

      <!-- 轮播图 & 商品视频-->
      <prod-imgs-video
        ref="mediaRef"
        :imgs="prodInfo.imgs"
        :video="prodInfo.video"
        @video-sts="videoSts"
      />
      <!-- 轮播图 & 商品视频end -->

      <!-- 商品信息 -->
      <view
        v-if="prodName"
        :class="['prod-info',prodType !== 3?'no-integral':'']"
      >
        <view>
          <view :class="shopLevel ? 'back' : ''">
            <!-- 商品价格 -->
            <view class="prod-price">
              <view class="price">
                <view v-if="defaultSku.price&&prodType !== 3">
                  <show-price
                    :type="1"
                    :price="defaultSku.price"
                    big-font-size="40rpx"
                    small-font-size="28rpx"
                  />
                  <text
                    v-if="defaultSku.price && defaultSku.oriPrice && defaultSku.oriPrice > defaultSku.price"
                    class="oriPrice"
                  >
                    {{ useCurrencyStore().defMark }}{{ wxs.parsePrice(defaultSku.oriPrice)[0] }}.{{ wxs.parsePrice(defaultSku.oriPrice)[1] }}
                  </text>
                </view>
                <view v-if="prodType == 3">
                  <text class="price">
                    <show-price
                      :type="1"
                      :price="defaultSku.price"
                      :score="defaultSku.skuScore"
                      big-font-size="40rpx"
                      small-font-size="28rpx"
                      :is-by-side="false"
                      :is-align-center="false"
                    />
                    <text
                      v-if="defaultSku.oriPrice && defaultSku.oriPrice > defaultSku.price"
                      class="oriPrice"
                    >
                      {{ useCurrencyStore().defMark }}{{ wxs.parsePrice(defaultSku.oriPrice)[0] }}.{{ wxs.parsePrice(defaultSku.oriPrice)[1] }}
                    </text>
                  </text>
                </view>
                <view
                  v-if="defaultSku.price&&defaultSku.memberAmount&&defaultSku.memberAmount<defaultSku.price"
                  class="vip-price"
                >
                  <text v-if="defaultSku.memberAmount&&defaultSku.memberAmount<defaultSku.price">
                    {{ useCurrencyStore().defMark }}{{ wxs.parsePrice(defaultSku.memberAmount)[0] }}.{{
                      wxs.parsePrice(defaultSku.memberAmount)[1]
                    }}
                  </text>
                  <view
                    v-if="shopLevel"
                    class="vip"
                  >
                    Lv.{{ shopLevel }}
                  </view>
                </view>
              </view>
            </view>
          </view>
        </view>
        <!-- 商品名称 -->
        <view class="prod-message">
          <view>
            <view :class="['prod-name','integral']">
              {{ prodName }}
            </view>
            <!-- 商品卖点 -->
            <view
              v-if="brief"
              :class="['prod-brief','integral','weixin-prod-brief',briefUnfold?'brief-unfold':'brief-fold']"
              @tap="onOpenDrief()"
            >
              {{ brief }}
              <view
                v-if="!briefUnfold&&briefUnfoldUnfoldShow"
                class="right"
              >
                <view class="cmt-more" />
              </view>
            </view>
          </view>
        </view>
      </view>
      <!-- 分享and收藏 -->

      <view
        v-if="prodType!==3&&prodName"
        class="share-collect"
      >
        <view
          class="left"
          @tap="sharePopupShow=true;shareType=1"
        >
          <image
            class="icon"
            src="/static/images/icon/icon_share.png"
          />
          <view>{{ $t('shareIt') }}</view>
        </view>

        <view
          class="right"
          @tap="addOrCannelCollection"
        >
          <image
            v-if="!isCollection"
            class="icon"
            src="@/package-prod/static/images/icon/collection2.png"
          />
          <image
            v-if="isCollection"
            class="icon"
            src="@/package-prod/static/images/icon/Have-collected.png"
          />
          <view class="text">
            {{ isCollection?$t('collected'):$t('collection') }}
          </view>
        </view>
      </view>

      <!-- 配送+规格+活动+参数 -->
      <view
        class="parameters"
        :class="{parametersEn: $t('lang') !== 'zh_CN' && $t('lang') !== 'zh_HK'}"
      >
        <!-- sku弹窗组件 -->
        <prod-sku-select
          v-show="skuLoad"
          ref="prodSkuSelectRef"
          :dvy-type="dvyType"
          :mold="mold"
          :cart-or-buy="cartOrBuy"
          :pic="pic"
          :delivery-mode-v-o="deliveryModeVO"
          :sku-list="skuList"
          :delivery-addr-info="addrInfo"
          :sku-id="skuId"
          :prod-id="prodId"
          :default-price="defaultPrice"
          :virtual-info="virtualInfo"
          :sku-show-type="skuShowType"
          :prod-type="prodType"
          :user-info-flag="!!userInfo.nickName"
          :prod-num="prodNum"
          :page-type="prodType === 2 ? 2 : 1"
          :sku-show="skuShow"
          @set-sku="setSku"
          @show-sku="showSku"
          @close-sku-pop="closePopup"
          @add-to-cart="addToCart"
          @buy-now="buyNow"
          @set-prod-num="setProdNum"
          @address-list-pop="addressListPop"
          @dvy-type-change="dvyTypeChange"
        />
        <!-- 已选规格end -->
        <!-- 参数 -->
        <view
          v-if="prodParameterList&&prodParameterList.length>0"
          class="item"
          @tap="parameterPopupShow=true"
        >
          <text class="tit">
            {{ $t('parameter') }}
          </text>
          <view class="content">
            <view class="list">
              <view class="parameter-text">
                <text
                  v-for="(item,prodParameterIndex) in prodParameterList"
                  :key="prodParameterIndex"
                  class="param-name"
                >
                  {{ item.parameterKey }}
                </text>
              </view>
              <view class="cmt-more" />
            </view>
          </view>
        </view>
        <!-- 配送 -->
        <view
          v-if="(deliveryModeVO && mold !== 1 && prodType != 5&& prodType != 3)"
          class="item"
        >
          <text class="tit">
            {{ $t('delivery') }}
          </text>
          <view class="content">
            <view class="list">
              <view v-if="mold===1">
                {{ $t('noNeedDelivery') }}
              </view>
              <view v-else-if="deliveryModeVO">
                <text v-if="deliveryModeVO.hasShopDelivery">
                  {{ $t('expressDelivery') }}
                </text>
              </view>
            </view>
          </view>
        </view>
        <!-- 快递 -->
        <view
          v-if="addrInfo&&totalTransFee!==null&&mold!==1&&prodType != 5"
          class="item expressage"
          @tap="addressListPop"
        >
          <text class="tit">
            {{ $t('courier') }}
          </text>
          <view class="content">
            <view class="list">
              <view>
                <text>{{ totalTransFee===0?$t('freeShipping'): useCurrencyStore().defMark + totalTransFee.toFixed(2) }}</text>
              </view>
              <view class="cmt-more" />
            </view>
            <view class="shipping-region">
              {{ $t('deliveryTo') }}: {{ addrInfo.province }}
              <span v-if="addrInfo.city">
                / {{ addrInfo.city }}
              </span>
              <span v-if="addrInfo.area">
                / {{ addrInfo.area }}
              </span>
            </view>
          </view>
        </view>
      </view>
      <!-- 配送+规格+活动+参数 -->

      <!-- 商品详情 -->
      <view
        v-show="content"
        class="prod-detail"
      >
        <view class="tit">
          <view class="tit-wire" />
          <view class="text">
            {{ $t('commodityDetails') }}
          </view>
          <view class="tit-wire" />
        </view>
        <view>
          <uv-parse :content="content" />
        </view>
      </view>
      <!-- 商品详情end -->

      <!-- 底部按钮 -->
      <view
        v-if="prodName"
        class="cart-footer"
      >
        <view
          v-if="prodType!==3"
          class="btn icon"
          @tap="toIndexPage"
        >
          <image src="/static/images/tabbar/uncheck-home.png" />{{ $t('homepage') }}
        </view>
        <view
          v-if="prodType!==3"
          class="btn icon to-cart"
          @tap="toCartPage"
        >
          <image src="/static/images/icon/shopping.png" />
          {{ $t('shoppingCart') }}
          <view
            v-if="totalCartNum > 0"
            class="badge"
          >
            {{ totalCartNum > 99 ? "99+" : totalCartNum }}
          </view>
        </view>
        <view :class="['cart-footer-btn']">
          <!-- 普通商品按钮: 普通商品   或未启动或未开始的团购-->
          <view class="add-cart-buy">
            <view
              class="add-cart"
              @tap="addToCart"
            >
              {{ $t('addShoppingCart') }}
            </view>
            <view
              :class="['buy']"
              @tap="buyNow(0)"
            >
              {{ $t('buyNow') }}
            </view>
          </view>
        </view>
      </view>
      <!-- 底部按钮 end -->
    </view>

    <!-- 参数弹窗 -->
    <view
      v-if="parameterPopupShow"
      class="activitys"
      @touchmove.stop.prevent="preventHandler"
      @tap="parameterPopupShow = false; popupShowHiden = false; "
    >
      <view
        class="activitys-box parameter-box radius"
        @tap.stop
      >
        <view class="activitys-tit radius">
          <view class="text">
            {{ $t('productParameter') }}
          </view>
          <image
            class="img"
            src="/static/images/icon/icon_close.png"
            @tap="closePopup"
          />
        </view>
        <scroll-view
          scroll-y
          class="parameter-cnt"
        >
          <view
            v-for="(item,prodParameterIndex) in prodParameterList"
            :key="prodParameterIndex"
            class="parameter-list"
          >
            <view class="Parameter-key">
              {{ item.parameterKey }}
            </view>
            <view class="Parameter-value">
              {{ item.parameterValue }}
            </view>
          </view>
        </scroll-view>
      </view>
    </view>

    <!-- h5||app分享弹窗 -->
    <view
      v-if="sharePopupShow"
      class="activitys"
      @touchmove.stop.prevent="preventHandler"
      @tap="sharePopupShow = false"
    >
      <view
        class="activitys-box share"
        @tap.stop
      >
        <view class="activitys-tit radius">
          <view class="text">
            {{ $t('shareTo') }}
          </view>
          <image
            class="img"
            src="/static/images/icon/icon_close.png"
            @tap="closePopup"
          />
        </view>
        <view class="share-cnt">
          <view
            class="item left"
            @tap="onShowShare(1)"
          >
            <image
              class="img"
              src="/static/images/icon/icon_Generate.png"
            />
            <view>
              {{ $t('convertTextToPicture') }}
            </view>
          </view>

          <!-- #ifndef MP-WEIXIN -->
          <view
            class="item right"
            @tap="copylink"
          >
            <image
              class="img"
              src="/static/images/icon/icon_copy.png"
            />
            <view class="text">
              {{ $t('copyLink') }}
            </view>
          </view>
          <!-- #endif -->
        </view>
      </view>
    </view>

    <!-- 分享弹窗 -->
    <view
      v-if="shareShow"
      class="promo-share"
      @touchmove.stop.prevent="preventHandler"
    >
      <!-- #ifndef H5  -->
      <image
        v-if="picture&&showCanvasImg"
        class="picture-img"
        :src="picture"
      />
      <!-- #endif -->
      <!-- #ifdef H5  -->
      <canvas
        style="width: 100rpx;height: 100rpx;"
      >
        <img
          v-if="picture&&showCanvasImg"
          style="-webkit-touch-callout: default;"
          class="picture-img"
          :src="picture"
        >
      </canvas>
      <!-- #endif -->
      <l-painter
        :board="poster"
        :path-type="painterPathType"
        hidden
        is-canvas-to-temp-file-path
        @success="pictureSuccess"
      />
      <view
        v-if="showShareBtn"
        class="promo-main"
      >
        <!-- #ifndef H5  -->
        <view class="promo-tit">
          <title>{{ $t('shareImgTo') }}</title>
        </view>
        <!-- #endif -->
        <!-- #ifdef H5  -->
        <view
          v-if="isWechat"
          class="promo-tit"
        >
          <title>{{ $t('shareImgTo') }}</title>
        </view>
        <!-- #endif -->

        <view class="promo-icons">
          <!-- #ifdef H5  -->
          <view
            v-if="isWechat"
            class="promo-img1"
            @tap="onShareWay"
          >
            <view class="promo-img1-icon">
              <image
                src="/static/images/icon/wetchat.png"
                mode="aspectFit"
              />
            </view>{{ $t('shareIt') }}
          </view>
          <view
            v-if="!isWechat"
            class="promo-tit1"
          >
            {{ $t('saveImgH5') }}
          </view>
          <!-- #endif -->
          <!-- #ifndef H5  -->
          <view
            class="promo-img1"
            @tap="saveImg"
          >
            <view class="promo-img1-icon">
              <image
                src="/static/images/icon/export.png"
                mode="aspectFit"
              />
            </view>{{ $t('saveImg') }}
          </view>
          <!-- #endif -->
        </view>
        <view
          class="promo-close"
          @tap="closeEarn"
        >
          {{ $t('cancel') }}
        </view>
      </view>
    </view>
    <!-- 分享弹窗 end -->

    <!-- 二维码弹窗 -->
    <view
      v-if="wxCodeShow"
      class="code-popup"
      @touchmove.stop.prevent="preventHandler"
      @tap=" wxCodeShow = false; popupShowHiden = false; "
    >
      <view
        class="code-main"
        @tap.stop
      >
        <view
          class="close-v"
          @tap.stop="closeCodePopup"
        >
          <image
            src="/static/images/icon/close.png"
            class="close-png"
          />
        </view>
        <view class="code-v">
          <image
            :src="shareWxCode"
            class="wx-code"
          />
        </view>
        <!-- #ifndef H5 -->
        <view
          class="code-txt"
          @tap="downloadImg"
        >
          {{ $t('saveAlbum') }}
        </view>
        <!-- #endif -->
      </view>
    </view>
    <!-- 二维码弹窗 end -->

    <!-- 引导分享蒙版 -->
    <view
      v-if="guideShare"
      class="guide-share-mask"
      @touchmove.stop.prevent="preventHandler"
    >
      <view class="mask" />
      <view
        class="guide-share-close"
        @tap="guideShare = false"
      >
        <image src="@/static/images/icon/close.png" />
      </view>
      <view class="guide-content">
        <view class="share-img">
          <image src="@/package-prod/static/images/icon/shareIcon.png" />
        </view>
        <view class="share-word">
          <view class="big-word">
            {{ $t('shareFriendsNow') }}
          </view>
          <view class="small-word">
            {{ $t('shareFriendsTips2') }}
          </view>
        </view>
      </view>
    </view>
    <!-- 引导分享蒙版 end -->

    <!-- 回到顶部 && 普通分享 -->
    <view class="promo-con">
      <view
        v-if="showBacktop"
        class="suspension-btn"
        @tap="handleScorllTop"
      >
        <image
          class="img"
          src="@/static/images/icon/back-to-top.png"
        />
      </view>
    </view>
    <!-- 返回上一页按钮 -->
    <!-- #ifndef MP-WEIXIN -->
    <!-- 左侧悬浮球 -->
    <view
      class="refund"
      @tap="back"
    >
      <image src="@/static/images/icon/icon_refund2.png" />
    </view>
    <!-- 右侧悬浮球 -->
    <skip-suspension
      ref="skipSuspensionRef"
      :tab-data="tabData"
      class="refund-right"
    />
    <!-- #endif -->
    <!-- 回到顶部 && 分享 end-->
  </view>
</template>

<script setup>
import Big from 'big.js'
import { createQrCodeImg } from '@/utils/wxqrcode.js'
import prodSkuSelect from '../../components/prod-sku-select/index.vue'
import util from '@/utils/util'
// #ifndef MP-WEIXIN
import skipSuspension from '../../components/skip-suspension/index.vue'
// #endif

import { useCurrencyStore } from '@/stores/modules/currency'

const wxs = number()

const pageScorllTop = ref(0) // 页面滚动的高度
let isLogin = false

const scene = ref('')

const isWechat = ref(false) // 是否为微信环境
let addrId = null // 地址id
/**
 * 生命周期函数--监听页面加载
 */
onLoad((options) => {
  uni.removeStorageSync('cbecB2cHadOnloaded')
  uni.removeStorageSync('cbecB2cOrderSkuList') // 进入商品详情页清空b2cOrderSkuList缓存，防止使用了上个提交订单中存储的缓存
  if (uni.getStorageSync('cbecB2cToken')) {
    getUserInfo()
    isLogin = true
  }
  // 配送地址信息
  const addrInfoPra = uni.getStorageSync('stationSaddress')
  if (addrInfoPra) {
    addrId = addrInfoPra.addrId
    addrInfo.value = addrInfoPra
    uni.setStorageSync('cbecB2cCartSelectAddrInfo', addrInfoPra)
    uni.removeStorageSync('stationSaddress')
  } else {
    const saveAddrInfo = uni.getStorageSync('cbecB2cCartSelectAddrInfo')
    if (saveAddrInfo) {
      addrId = saveAddrInfo.addrId
      addrInfo.value = saveAddrInfo
    }
  }

  prodId.value = options.prodId
  // 普通的跳转
  if (options.prodId) {
    executionFunction()
  }
  // 扫码进入 (小程序)
  if (options.scene) {
    const scenePra = decodeURIComponent(options.scene)
    // 如果是微信小程序中下载保存的二维码
    if (scenePra.indexOf(',') !== -1) {
      prodId.value = scenePra.split(',')[0]
      executionFunction()
    } else {
      scene.value = scenePra
    }
  }
  // if (prodId.value) {
  //   // 加载评论数据
  //   getProdCommData()
  //   // 加载评论分页
  //   getProdCommPage(1)
  // }
})

/**
 * 生命周期函数--监听页面显示
 */
onShow(() => {
  let change = false
  // 配送地址信息
  const addrInfoPar = uni.getStorageSync('stationSaddress')
  if (addrInfoPar) {
    change = true
    addrId = addrInfoPar.addrId
    addrInfo.value = addrInfoPar
    uni.setStorageSync('cbecB2cCartSelectAddrInfo', addrInfoPar)
    uni.removeStorageSync('stationSaddress')
  } else {
    const saveAddrInfoPra = uni.getStorageSync('cbecB2cCartSelectAddrInfo')
    if (saveAddrInfoPra) {
      change = true

      addrId = saveAddrInfoPra.addrId
      addrInfo.value = saveAddrInfoPra
    }
  }
  uni.setNavigationBarTitle({
    title: $t('commodityDetails')
  })
  if (prodId.value) {
    if (uni.getStorageSync('cbecB2cToken')) {
      // 加载购物车数字
      getCartCount()
    }
    if ((hadOnloaded.value && change) || (uni.getStorageSync('cbecB2cToken') && (!isLogin || uni.getStorageSync('cbecB2cHadOnloaded')))) {
      uni.removeStorageSync('cbecB2cHadOnloaded')
      if (uni.getStorageSync('cbecB2cToken')) {
        isLogin = true
      }
      skuLoad.value = false
      getProdInfo()
      skuShowType.value = 0 // 重置sku类型
    }
  }
})

const mediaRef = ref()
const scrollTop = ref(-1) // 滚动监听
let isHid = false // 是否隐藏过
/**
 * 页面相关事件处理函数--监听页面隐藏
 */
onHide(() => {
  // #ifndef MP-WEIXIN
  tabData.value.isTabShow = false
  // #endif
  if (!isToDeliveryAddress) {
    skuShow.value = false
  } else {
    isToDeliveryAddress = false
  }
  popupShowHiden.value = false
  if (isPlaying.value) {
    mediaRef.value.stopPlay()
  }
  if (!isHid) {
    isHid = true
  }
})

const tabData = ref(// 顶部 topTab 数据
  {
    isTabShow: false,
    fixed: false
  }
)
const showBacktop = ref(false)
const topTabOpacity = ref(0) // 顶部 topTab 的透明度
onPageScroll((e) => {
  scrollTop.value = e.scrollTop
  if (scrollTop.value > 1500) {
    showBacktop.value = true
  } else if (scrollTop.value < 1500) {
    showBacktop.value = false
  }

  if (!(tabData.value.fixed)) {
    tabData.value.isTabShow = false
  }

  topTabOpacity.value = e.scrollTop / 46
  if (topTabOpacity.value < 1) {
    tabData.value.isTabShow = false
  }
  if (!scrolling.value && heightList.value[1]) {
    topTabSts.value = e.scrollTop < heightList.value[1] ? 0 : e.scrollTop < heightList.value[2] ? 1 : 2
  }
})

const preventHandler = () => {}

/**
 * 用户点击转发
 */
onShareAppMessage((res) => {
  sharePopupShow.value = false

  const cardno = uni.getStorageSync('cbecB2cDistCardNo')
  return {
    title: prodName.value,
    path: '/package-prod/pages/prod/prod?prodId=' + prodId.value + '&isShare=1' + (res.from === 'button' ? '&cardno=' + cardno : ''),
    imageUrl: util.checkFileUrl(pic.value),
    desc: brief.value
  }
})

const skuShow = ref(false)
const commentShow = ref(false)
const showDiscountPopup = ref(false) // 促销弹窗显隐
const popupShow = ref(false)

watch([skuShow, commentShow, showDiscountPopup, popupShow], (newVal) => {
  if (newVal && isPlaying.value) {
    mediaRef.value.stopPlay()
  }
})

const dvyType = ref(1) // 配送类型 1:快递 2:自提 3：无需快递 4:同城配送
const dvyTypeChange = (dvyTypePra) => {
  dvyType.value = dvyTypePra
}

const tabShowHandler = () => {
  tabData.value.isTabShow = !tabData.value.isTabShow
  tabData.value.fixed = true
}

const scrolling = ref(false)
const topTabSts = ref(0) // 顶部 topTab 选中的项目

/**
 * 顶部Tab栏点击，页面滚动至指定位置
 * @param {number} type 0页面置顶; 1滚动至评价; 2滚动至详情
 */
const hanleTopTabClick = (type) => {
  if (type === 2 && !content.value) {
    return
  }
  topTabSts.value = type
  scrolling.value = true
  if (type === 0) {
    uni.pageScrollTo({
      scrollTop: 0,
      duration: 100
    })
  } else {
    let className = ''
    className = type === 1 ? 'evaluate' : 'prod-detail'
    uni
      .createSelectorQuery()
      .select('.' + className)
      .boundingClientRect((data) => {
        // 目标节点
        uni
          .createSelectorQuery()
          .select('.page-prod')
          .boundingClientRect((res) => {
            // 最外层盒子节点
            let scrollTop = 0
            if (data.top < 0) {
              scrollTop = Math.abs(res.top) - Math.abs(data.top) - 60
            } else {
              scrollTop = Math.abs(res.top) + Math.abs(data.top) - 60
            }
            uni.pageScrollTo({
              duration: 100,
              scrollTop
            })
          })
          .exec()
      })
      .exec()
  }
  setTimeout(() => {
    scrolling.value = false
  }, 500)
}

/**
 * 获取高度
 */
const heightList = ref([0])
const getHeightList = () => {
  const classNameListPra = ['.evaluate', '.prod-detail']
  const heightListPra = heightList.value
  for (let i = 0; i < classNameListPra.length; i++) {
    uni
      .createSelectorQuery()
      .select(classNameListPra[i])
      .boundingClientRect((data) => {
        // 目标节点
        uni
          .createSelectorQuery()
          .select('.page-prod')
          .boundingClientRect((res) => {
            // 最外层盒子节点
            let scrollTop = 0
            if (data.top < 0) {
              scrollTop = Math.abs(res.top) - Math.abs(data.top) - 60
            } else {
              scrollTop = Math.abs(res.top) + Math.abs(data.top) - 60
            }
            heightListPra.push(scrollTop)
          })
          .exec()
      })
      .exec()
  }
  heightList.value = heightListPra
}

const userInfo = ref({}) // 会员信息
/**
 * 获取用户信息
 */
const getUserInfo = () => {
  http.request({
    url: '/p/user/userInfo',
    method: 'GET',
    data: {}
  }).then(({ data }) => {
    userInfo.value = data
  })
}

const shopProdList = ref([]) // 店铺商品
// 获取店铺商品
const getShopProdList = () => {
  http.request({
    url: '/search/page',
    method: 'GET',
    data: {
      current: 1,
      size: 7,
      sort: 2,
      isActive: 1 // 过滤掉活动商品
    }
  }).then(({ data }) => {
    if (data && data.records && data.records.length && data.records[0]) {
      const shopProdListPar = data.records[0].products || []
      shopProdList.value = shopProdListPar.filter(el => el.prodId !== prodId.value)
    }
  })
}

let isToDeliveryAddress = false
/**
 * 可用地址弹窗显示
 */
const addressListPop = () => {
  isToDeliveryAddress = true
  uni.navigateTo({
    url: '/package-user/pages/delivery-address/delivery-address?selectAddress=1'
  })
}

const isPlaying = ref(false)
const videoSts = (sts) => {
  isPlaying.value = sts
}

const findSku = ref(true)
const defaultSku = ref({})
const defaultActivitySku = ref('')
/**
 * 修改商品的sku
 */
const setSku = (skuPra, findSkuPra) => {
  findSku.value = findSkuPra
  skuId.value = skuPra.skuId
  defaultSku.value = skuPra || {}
}

const prodNum = ref(1)
/**
 * 修改商品数量
 */
const setProdNum = (prodNumPra) => {
  prodNum.value = prodNumPra
}

const totalCartNum = ref(0)
/**
 * 获取购物车
 */
const getCartCount = () => {
  http.request({
    url: '/p/shopCart/prodCount',
    method: 'GET',
    dontTrunLogin: true
  }).then(({ data }) => {
    totalCartNum.value = data
  })
}

/**
 * 执行函数
 */
const executionFunction = () => {
  // 加载商品数据
  getProdInfo()

  if (uni.getStorageSync('cbecB2cToken')) {
    // 获取商品是否被收藏信息
    getCollection()
  }
}

const isCollection = ref(false)
/**
 * 获取商品是否被收藏信息
 */
const getCollection = () => {
  http.request({
    url: '/p/user/collection/isCollection',
    method: 'GET',
    dontTrunLogin: true,
    data: {
      prodId: prodId.value
    }
  }).then(({ data }) => {
    isCollection.value = data
  })
}

/**
 * 添加或者取消收藏商品
 */
const addOrCannelCollection = () => {
  util.checkAuthInfo(() => {
    http.request({
      url: '/p/user/collection/addOrCancel',
      method: 'POST',
      data: prodId.value
    }).then(({ data }) => {
      isCollection.value = !isCollection.value
      uni.showToast({
        title: data ? $t('collectionAdded') : $t('collectionCancelled'),
        duration: 1200,
        icon: 'none'
      })
    })
  })
}

const briefUnfold = ref(true) // 卖点展开
const briefUnfoldUnfoldShow = ref(true)
const virtualInfo = ref({})
const prodSkuSelectRef = ref()
// 是否已请求过商品详情
const hadOnloaded = ref(false)
const prodInfo = ref({})
// 获取商品信息
const getProdInfo = () => {
  let userId = null
  if (uni.getStorageSync('cbecB2cToken') && uni.getStorageSync('cbecB2cUserInfo')) {
    userId = uni.getStorageSync('cbecB2cUserInfo').userId
  }
  http.request({
    url: '/prod/prodInfo',
    method: 'GET',
    hasCatch: true,
    data: {
      prodId: prodId.value,
      addrId: addrId || 0,
      userId,
      dvyType: dvyType.value
    }
  }).then(({ data }) => {
    prodInfo.value = data
    const imgStrsPar = data.imgs
    let imgsPar = imgStrsPar?.split(',')
    if (!imgsPar) {
      imgsPar = ['/static/images/icon/def.png']
    }
    const contentPar = util.formatHtml(data.content)
    let prodTypePra = data.prodType
    if (prodTypePra === 1 && !data.groupActivityVO) { // 拼团平台下架判断
      prodTypePra = 0
    }
    prodChange(data, imgsPar, contentPar, prodTypePra)

    // 买点是否超过一行
    if (data.prodName && data.brief) {
      nextTick(() => {
        const query = uni.createSelectorQuery()
        query.select('.prod-brief').boundingClientRect(data => {
          uni.getSystemInfo({
            success: function (res) {
              // px转换到rpx的比例
              const pxToRpxScale = 750 / res.windowWidth
              const FONT_SIZE = 24 // 字体大小
              briefUnfold.value = false
              briefUnfoldUnfoldShow.value = !(pxToRpxScale * data.height <= FONT_SIZE * 2)
            }
          })
        }).exec()
      })
    }
    getShopProdList()
    if (data.mold === 1) {
      virtualInfo.value = {
        virtualRemarks: data.virtualRemark ? JSON.parse(data.virtualRemark) : [], // 留言
        isRefund: data.isRefund, // 0不支持退款 1支持退款
        writeOffTime: data.writeOffTime, // 核销有效期 -1.长期有效 0.自定义 x.x天内有效
        writeOffNum: data.writeOffNum, // 核销次数 -1.多次核销 0.无需核销 1.单次核销
        writeOffStart: data.writeOffStart, // 核销有效期开始时间
        writeOffEnd: data.writeOffEnd, // 核销有效期结束时间
        prodParameterList: data.prodParameterList
      }
    }
    // 把访问的商品分类写进缓存,用作推荐列表的参数
    uni.setStorageSync('cbecB2cBrowseCategoryId', data.categoryId)
    hadOnloaded.value = true
    nextTick(() => {
      prodSkuSelectRef.value.groupSku(true)
      setTimeout(() => {
        // eslint-disable-next-line max-statements-per-line
        try { getHeightList() } catch (err) {}
      }, 500)
    })
  }).catch(err => {
    catchFunction(err)
  })
}

const catchFunction = (err) => {
  if (err.code === 'A00001') {
    uni.showModal({
      title: $t('tips'),
      content: err.msg,
      showCancel: false,
      cancelText: $t('confirm'),
      confirmText: $t('confirm'),
      success: (res) => {
        if (res.confirm) {
          getCurrentPages().length > 1 ? uni.navigateBack() : util.toHomePage()
        }
      }
    })
  }

  if (err.code === 'A04001' || err.code === 'A00012' || err.code === 'A00006') {
    uni.showToast({ title: err.msg || err.data || 'Error', icon: 'none' })
  }

  hadOnloaded.value = true
}

const shopLevel = ref(null) // 店铺会员等级
const defaultPrice = ref(0)
let shopId = 1
const pic = ref('')
const imgs = ref('')
const prodName = ref('')
const price = ref(0)
const content = ref('')
const prodId = ref(0)
const brief = ref('')
const skuId = ref(0)
const skuList = ref([])
const skuLoad = ref(false)
const prodType = ref(0) // 商品类型(0普通商品 1拼团 2秒杀 3积分 5活动商品)
// 活动剩余库存
const totalStocks = ref(0)
const deliveryModeVO = ref({}) // 配送方式

// 虚拟商品
const mold = ref('') // 1虚拟商品
const prodParameterList = ref([]) // 商品参数
const totalTransFee = ref(null) // 运费
const addrInfo = ref(null) // 地址

const curShopId = ref(0)
const prodChange = (res, imgsPar, contentPar, prodTypePra) => {
  curShopId.value = res.shopId
  imgs.value = imgsPar
  content.value = contentPar
  price.value = res.price
  prodName.value = res.prodName
  prodId.value = res.prodId
  brief.value = res.brief
  totalStocks.value = res.totalStocks
  skuList.value = res.skuList
  skuLoad.value = true
  pic.value = res.pic
  shopLevel.value = res.level
  shopId = res.shopId
  prodType.value = prodTypePra // 商品类型(0普通商品 1拼团 2秒杀 3积分 5活动商品)
  mold.value = res.mold // 1虚拟商品
  deliveryModeVO.value = res.deliveryModeVO || {} // 配送方式
  prodParameterList.value = res.prodParameterList
  defaultPrice.value = res.price
  totalTransFee.value = res.userDeliveryInfo && res.userDeliveryInfo.totalTransFee
  addrInfo.value = res.userDeliveryInfo && res.userDeliveryInfo.userAddr
  // 默认sku为有库存且价格最低的
  const tempSkuList = res.skuList
  const priceField = 'price'
  const stockField = 'stocks'
  let lowPricePat = Number.MAX_VALUE
  let tempSkuId = tempSkuList[0].skuId
  for (const skuItem of tempSkuList) {
    if (skuItem[stockField] && lowPricePat > skuItem[priceField]) {
      lowPricePat = skuItem[priceField]
      tempSkuId = skuItem.skuId
    }
  }
  skuId.value = tempSkuId
}

const isLoading = ref(false) // 是否正在请求门店数据
// 获取定位信息
const getCurrentLocation = () => {
  isLoading.value = true
  // #ifdef H5
  onGetLocation()
  // #endif
}

const onGetLocation = () => {
  isLoading.value = true
  util.getLocation(confirmLocation, () => {
    isLoading.value = false
    // #ifdef H5 || APP-PLUS
    getUserStation()
    uni.showToast({
      title: $t('getLocationFailed'),
      icon: 'none',
      duration: 1500
    })
    // #endif
  })
  showLocationAuthTips.value = ''
}

let lat = null
let lng = null
/**
 * 转换经纬度请求自提点列表
 * @param {Object} locationRes 当前定位信息
 */
const confirmLocation = (locationRes) => {
  lat = locationRes.latitude
  lng = locationRes.longitude
  getUserStation()
}

const selStationItem = ref({})
const getUserStation = () => {
  http.request({
    url: '/p/station/userstation',
    method: 'POST',
    data: {
      page: {
        current: 1,
        size: 1
      },
      lat: lat || 39.9, // 纬度 '23.151021',
      lng: lng || 116.48, // 经度 '113.326621',
      stationName: '',
      shopId,
      stationProdList: []
    }
  }).then(({ data }) => {
    isLoading.value = false
    selStationItem.value = (data.records && data.records[0]) || ''
    if (!selStationItem.value) return
    if (selStationItem.value.distance > 1000) {
      selStationItem.value.distance = new Big(selStationItem.value.distance)
      // 保留两位小数
      Big.DP = 2
      // 向下取整
      // big.RM = 3
      selStationItem.value.distance = selStationItem.value.distance.div(1000).valueOf()
      selStationItem.value.distanceCont = selStationItem.value.distance + 'km'
    } else {
      selStationItem.value.distanceCont = selStationItem.value.distance + 'm'
    }

    uni.setStorageSync('cbecB2cSelStationItem', {
      selStationItem: selStationItem.value,
      userInfo: {
        name: '',
        userMobile: ''
      }
    })
  }).catch(() => {
    isLoading.value = false
    uni.showToast({
      title: $t('serverWrong'),
      icon: 'none'
    })
  })
}

/**
 * 跳转到购物车
 */
const toCartPage = () => {
  uni.switchTab({ url: '/pages/basket/basket' })
}

const cartOrBuy = ref(2) // 1购物车 2 立即购买 3购物车 立即购买 4 预售立即购买商品
const popupShowHiden = ref(false) // 所有弹窗的显隐判断
/**
 * 加入购物车
 */
const addToCart = () => {
  if (cartOrBuy.value !== 3) {
    cartOrBuy.value = 1
  }

  if (!skuShow.value) {
    skuShow.value = true
    popupShowHiden.value = true
    return
  }

  if (!findSku.value) {
    return
  }
  // 查看是否授权
  util.checkAuthInfo(callChangeItem)
}

const isChangedItem = ref(false) // 加入购物车请求是否完成
const callChangeItem = () => {
  if (isChangedItem.value) {
    return
  }
  isChangedItem.value = true
  if (prodNum.value < 1) {
    uni.showToast({
      title: $t('leastTips'),
      icon: 'none'
    })
    prodNum.value = 1
    return
  }
  http.request({
    url: '/p/shopCart/changeItem',
    method: 'POST',
    hasCatch: true,
    data: {
      basketId: 0,
      count: prodNum.value,
      prodId: prodId.value,
      shopId,
      skuId: defaultSku.value.skuId
    }
  }).then(({ data }) => {
    if (data) {
      isChangedItem.value = false
      totalCartNum.value = data
      skuShow.value = false
      popupShowHiden.value = false
      uni.showToast({
        title: $t('successfullyAddedCart'),
        icon: 'none'
      })
    }
  }).catch((err) => {
    isChangedItem.value = false
    if (err.code === 'A00001') {
      uni.showModal({
        title: $t('tips'),
        content: err.msg,
        showCancel: false,
        cancelText: $t('confirm'),
        confirmText: $t('confirm'),
        success: (res) => {
          if (res.confirm) {
            getCurrentPages().length > 1 ? uni.navigateBack() : util.toHomePage()
          }
        }
      })
    }

    if (err.code === 'A04001' || err.code === 'A00012' || err.code === 'A00006') {
      uni.showToast({ title: err.msg || err.data || 'Error', icon: 'none' })
    }
  })
}

/**
 * 立即购买
 * @param {Number} orderType 订单类型  0普通 1团购 2秒杀 3积分
 */
const buyNow = (orderType) => {
  uni.removeStorageSync('cbecB2cSelStationItem')
  cartOrBuy.value = 2
  // 单规格积分商品可以直接购买
  if (!skuShow.value && !(prodType.value === 3 && !(skuList.value.length > 1))) {
    skuShow.value = true
    popupShowHiden.value = true
    return
  }
  // 查看是否授权
  util.checkAuthInfo(() => {
    if (prodNum.value < 1) {
      return
    }
    const orderItem = {
      prodId: prodId.value,
      skuId: defaultSku.value.skuId,
      prodCount: prodNum.value,
      shopId
    }
    let url = `/package-prod/pages/submit-order/submit-order?orderEntry=1&orderType=${orderType}&mold=${mold.value}${mold.value === 1 ? '&shopId=' + shopId : ''}`
    let dvyTypeVal = 1
    if (orderType === 3) {
      url += '&dvyType=1'
    } else {
      dvyTypeVal = dvyType.value
      url += '&dvyType=' + dvyType.value
    }
    uni.removeStorageSync('orderData')
    if (orderType == 1) {
      // 团购订单
      orderItem.groupSkuId = defaultActivitySku.value.groupSkuId
      orderItem.groupTeamId = 0
    } else if (orderType == 2) {
      // 秒杀订单
      orderItem.seckillSkuId = defaultSku.value.seckillSkuId
      // 请求秒杀订单路径
      http.request({
        url: '/p/seckill/orderPath',
        method: 'GET',
        data: {
          prodId: prodId.value
        }
      }).then(({ data }) => {
        if (addrInfo.value) {
          uni.setStorageSync('orderData', {
            dvyType: dvyTypeVal,
            userAddr: addrInfo.value
          })
        }
        url = `/package-prod/pages/submit-order/submit-order?orderEntry=1&orderType=${orderType}&orderPath=${data}&mold=${mold.value}&shopId=${shopId}&dvyType=${dvyType.value}`
        toSubmitOrder(orderItem, url)
      })
      return
    }
    // 将使用的地址与配送方式传递过去
    if (addrInfo.value) {
      uni.setStorageSync('orderData', {
        dvyType: dvyTypeVal,
        userAddr: addrInfo.value
      })
    }
    // 积分商品需跳转支付页面
    toSubmitOrder(orderItem, url)
  })
}

/* 保存图片 */
const saveImg = () => {
  // #ifndef H5
  uni.saveImageToPhotosAlbum({
    filePath: picture.value,
    success: () => {
      uni.showToast({
        icon: 'none',
        mask: true,
        title: $t('savedSuccessfully')
      })
    },
    fail: () => {
      uni.showToast({
        icon: 'none',
        mask: true,
        title: $t('savedFailfully')
      })
    }
  })
  // #endif
}

/**
 * 跳转提交订单页
 */
const toSubmitOrder = (orderItem, url) => {
  uni.setStorageSync('cbecB2cOrderItem', Object.assign({}, orderItem))
  uni.navigateTo({
    url
  })
}

// sku的显示类型 0普通sku 1拼团sku
const skuShowType = ref(0)
const showSku = (alonebuyPar, cartOrBuyPar, skupopPar) => {
  if ((prodType.value === 3 && !(skuList.value.length > 1))) {
    return
  }
  if (skupopPar) {
    cartOrBuy.value = 2
  }

  if (cartOrBuyPar) {
    cartOrBuy.value = cartOrBuyPar
  }
  skuShow.value = true
  popupShowHiden.value = true
  skuShowType.value = alonebuyPar == 1 ? 0 : 0
}

const joinPopupShow = ref(false)
const parameterPopupShow = ref(false)
const sharePopupShow = ref(false)
const closePopup = () => {
  popupShow.value = false
  skuShow.value = false
  commentShow.value = false
  popupShowHiden.value = false
  joinPopupShow.value = false
  parameterPopupShow.value = false
  sharePopupShow.value = false
}

const shareType = ref(1) // 1 普通分享 2 分销员分享
const shareShow = ref(false)
// 分销海报
const showShareBtn = ref(false) // 分享栏
const showCanvasImg = ref(true) // 海报图片（小程序不显示）
const showLocationAuthTips = ref('')
const onShowShare = () => {
  picture.value = ''
  sharePopupShow.value = false

  shareShow.value = true
  popupShowHiden.value = true

  // #ifndef MP-WEIXIN
  showShareBtn.value = true
  // #endif
  getShareQRCode()
}

const closeEarn = () => {
  shareShow.value = false
  popupShowHiden.value = false
}

// 分享二维码图片路径
const shareWxCode = ref('')
/**
 * 生成分销商品二维码
 * 小程序端 生成小程序菊花码  否则生成普通二维码
 */
const getShareQRCode = () => {
  uni.showToast({
    icon: 'loading',
    mask: true,
    duration: 10000,
    title: $t('posterInDrawing')
  })
  const cardNo = ''
  // #ifdef H5 || APP-PLUS
  let code = ''
  if (shareType.value === 1) {
    code = `${import.meta.env.VITE_APP_H5_DOMAIN}/package-prod/pages/prod/prod?prodId=${prodId.value}&isShare=1`
  } else {
    code = `${import.meta.env.VITE_APP_H5_DOMAIN}/package-prod/pages/prod/prod?prodId=${prodId.value}&isShare=1&cardno=${cardNo}`
  }
  shareWxCode.value = createQrCodeImg(code, { size: 250 })
  poster.value = util.getPoster(pic.value, price.value, prodName.value, shareWxCode.value)

  // dp.drawPosters(this)
  // #endif
}

/**
 * 关闭二维码弹窗
 */
const closeCodePopup = () => {
  wxCodeShow.value = false
  popupShowHiden.value = false
}

// 是否显示二维码弹层
const wxCodeShow = ref(false)
/**
 * 保存图片至相册
 */
const downloadImg = () => {
  uni.showLoading({
    // #ifndef MP-TOUTIAO
    mask: true
    // #endif
  })
  // #ifdef APP-PLUS
  const bitmap = new plus.nativeObj.Bitmap('test')
  bitmap.loadBase64Data(
    shareWxCode.value,
    () => {
      const url = '_doc/' + new Date() + '.png' // url建议用时间戳命名方式
      bitmap.save(
        url,
        {
          overwrite: true
        },
        () => {
          uni.saveImageToPhotosAlbum({
            filePath: url,
            success: () => {
              uni.hideLoading()
              wxCodeShow.value = false
              popupShowHiden.value = false
              uni.showToast({
                title: $t('downloadComplete')
              })
              bitmap.clear()
            }
          })
        },
        () => {
          uni.hideLoading()
          // alert('保存失败1111', JSON.stringify(e))
          bitmap.clear()
        }
      )
    },
    () => {
      // alert('保存失败2222', JSON.stringify(e))
      uni.hideLoading()
      bitmap.clear()
    }
  )
  // #endif
}

const guideShare = ref(false) // 引导蒙版

/**
 * 回到页面顶部
 */
const handleScorllTop = () => {
  uni.pageScrollTo({
    duration: 100,
    scrollTop: 0
  })
}

const back = () => {
  // #ifdef H5
  history.back()
  // #endif
  // #ifndef H5
  uni.navigateBack()
  // #endif
}

const toIndexPage = () => {
  uni.switchTab({
    url: '/pages/index/index'
  })
}

const copylink = () => {
  if (shareType.value === 1) { // 普通分享
    const code = `${import.meta.env.VITE_APP_H5_DOMAIN}/package-prod/pages/prod/prod?prodId=${prodId.value}&isShare=1`
    uni.setClipboardData({
      data: code,
      showToast: false,
      success () {
        uni.showToast({
          title: $t('copySucceeded'),
          icon: 'none'
        })
        sharePopupShow.value = false
      }
    })
  } else { // 分销分享
    const cardNo = uni.getStorageSync('cbecB2cDistCardNo')
    if (cardNo) { // 有分销码
      const code = `${import.meta.env.VITE_APP_H5_DOMAIN}/package-prod/pages/prod/prod?prodId=${prodId.value}&cardno=${cardNo}`
      uni.setClipboardData({
        data: code,
        showToast: false,
        success () {
          uni.showToast({
            title: $t('copySucceeded'),
            icon: 'none'
          })
          sharePopupShow.value = false
        }
      })
    } else { // 无分销码
      http.request({
        url: '/p/distribution/user/distributionUserInfo',
        method: 'GET',
        selfLoading: true
      }).then(({ data }) => {
        uni.setStorageSync('o2oDistCardNo', data.cardNo)
        const code = `${import.meta.env.VITE_APP_H5_DOMAIN}/package-prod/pages/prod/prod?prodId=${prodId.value}&cardno=${data.cardNo}`
        uni.setClipboardData({
          data: code,
          showToast: false,
          success () {
            uni.showToast({
              title: $t('copySucceeded'),
              icon: 'none'
            })
            sharePopupShow.value = false
          }
        })
      })
    }
  }
}

const poster = ref({})
const picture = ref(null)
let painterPathType = 'url'
// #ifdef H5
painterPathType = 'base64'
// #endif
const pictureSuccess = (e) => {
  uni.hideToast()
  picture.value = e
}

// 海报绘制完成
const completeDraw = () => {

}

// 使用防抖函数，防止点击过快造成页面样式紊乱
const onOpenDrief = util.debounce(() => {
  briefUnfold.value = !briefUnfold.value
}, 100)

</script>

<style lang="scss" scoped>
@use "prod";
</style>
