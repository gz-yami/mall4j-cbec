<!--
  Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.

  https://www.mall4j.com/

  未经允许，不可做商业用途！

  版权所有，侵权必究！
-->

<template>
  <!-- 物流信息 -->
  <view class="Mall4j page-logistics-info">
    <view
      v-if="deliveryList.length>1"
      class="delivery"
    >
      <view class="title">
        {{ $t('multiplePackagesSent') }}
      </view>

      <view
        class="deliveryList"
      >
        <block
          v-for="(item, index) in deliveryList"
          :key="index"
        >
          <view
            class="deliveryList-item"
            :data-sts="index"
            @tap="navChange"
          >
            <view
              :class="'nav-txt ' + (isCurrent === index ? 'active' : '')"
            >
              <view class="deliveryList-item-info">
                <view class="deliveryInx">
                  {{ $t('package') }}{{ index + 1 }}
                </view>
                <view class="prodNum">
                  {{ $t('inTotal') }} {{ item.orderItems.length }} {{ $t('piece') }}
                </view>
              </view>
              <view class="prod-img">
                <img-show :src="item.orderItems[0].pic" />
              </view>
            </view>
          </view>
        </block>
      </view>

      <!--deliveryInfo.deliveryType:deliveryType  发货方式(1.快递 3.无需物流 4.同城配送)  -->
      <view
        v-if="deliveryInfo&&deliveryInfo.deliveryDto&&(deliveryInfo.deliveryType===1||deliveryInfo.deliveryType===0)"
        class="delivery-info"
      >
        <view class="dvyFlowId">
          <image
            v-if="deliveryInfo.deliveryDto.logo"
            class="company-pic"
            mode="aspectFill"
            :src="util.checkFileUrl(deliveryInfo.deliveryDto.logo)"
          />
          <view
            v-else
            class="company-pic"
          >
            <image
              class="company-def-pic"
              src="@/package-user/static/images/icon/icon_pack.png"
            />
          </view>
          <view>
            {{
              deliveryInfo.deliveryDto.companyName
            }}:{{
              deliveryInfo.deliveryDto.dvyFlowId
            }}
          </view>
        </view>
        <view
          v-if="deliveryInfo&&deliveryInfo.deliveryDto && (deliveryInfo.deliveryType===1||deliveryInfo.deliveryType===0)"
          class="copy-btn"
          @tap="copyBtn"
        >
          {{
            $t('copy')
          }}
        </view>
      </view>
      <view
        v-if="deliveryInfo && deliveryInfo.deliveryType===3"
        class="delivery-info"
      >
        <view class="dvyFlowId">
          <view class="company-pic">
            <image
              class="company-def-pic"
              src="@/package-user/static/images/icon/icon_pack.png"
            />
          </view>
          <view>{{ $t('noNeedDelivery') }}</view>
        </view>
      </view>
    </view>
    <view
      v-if="deliveryList.length"
      class="logistics-box"
    >
      <!-- 物流信息 -->
      <view
        v-if="deliveryInfo&&deliveryInfo.deliveryDto"
        class="logistics-info"
      >
        <view
          v-if="deliveryList.length<=1"
          class="delivery-info"
        >
          <view class="dvyFlowId">
            <image
              v-if="deliveryInfo.deliveryDto.logo"
              class="company-pic"
              mode="aspectFill"
              :src="util.checkFileUrl(deliveryInfo.deliveryDto.logo)"
            />
            <view
              v-else
              class="company-pic"
            >
              <image
                class="company-def-pic"
                src="@/package-user/static/images/icon/icon_pack.png"
              />
            </view>
            <view>
              {{
                deliveryInfo.deliveryDto.companyName
              }}:{{
                deliveryInfo.deliveryDto.dvyFlowId
              }}
            </view>
          </view>
          <view
            v-if="deliveryInfo&&deliveryInfo.deliveryDto"
            class="copy-btn"
            @tap="copyBtn"
          >
            {{
              $t('copy')
            }}
          </view>
        </view>

        <view
          class="logistics-box"
        >
          <view
            class="logistics"
            :class="{isopen:isopen}"
          >
            <view
              class="item"
              :class="{'logistics-active':(deliveryInfo.deliveryDto && deliveryInfo.deliveryDto.state === 0) &&
                orderInfo.status === 5 &&
                orderInfo.finallyTime !== null}"
            >
              <text
                v-if="orderInfo.userAddrDto"
                class="des"
              >
                <!--买家已收货-->

                {{ orderInfo.userAddrDto.province }}
                <span v-if="orderInfo.userAddrDto.city">
                  / {{ orderInfo.userAddrDto.city }}
                </span>
                <span v-if="orderInfo.userAddrDto.area">
                  / {{ orderInfo.userAddrDto.area }}
                </span>
                &nbsp;&nbsp;{{ orderInfo.userAddrDto.receiver }}&nbsp;&nbsp;{{ phoneDesensitization(orderInfo.userAddrDto.mobile) }}
              </text>
            </view>
            <block v-if="deliveryInfo.deliveryDto && deliveryInfo.deliveryDto.traces">
              <block
                v-for="(tracesItem, index) in deliveryInfo.deliveryDto.traces"
                :key="index"
              >
                <view
                  class="item"
                  :class="{'logistics-active':index===0 &&!((deliveryInfo.deliveryDto && deliveryInfo.deliveryDto.state === 0) &&
                    orderInfo.status === 5 &&
                    orderInfo.finallyTime !== null)}"
                >
                  <view class="des">
                    {{ tracesItem.acceptStation }}
                  </view>
                  <view class="time">
                    {{ tracesItem.acceptTime }}
                  </view>
                </view>
              </block>
            </block>
            <view
              v-if="orderInfo.status >= 3&&(!deliveryInfo.deliveryDto || !(deliveryInfo.deliveryDto.traces&&deliveryInfo.deliveryDto.traces.length))"
              class="item"
            >
              <view
                v-if="orderInfo.status >= 3"
                class="des"
              >
                {{ $t('noDeliveryInformation') }}
              </view>
            </view>
            <view
              v-if="(orderInfo.status >= 3||(deliveryInfo?.deliveryDto && orderInfo.status == 2)) && deliveryInfo.createTime"
              class="item"
              :class="{'logistics-active':(orderInfo.status === 3||(deliveryInfo?.deliveryDto && orderInfo.status == 2))&&!(deliveryInfo.deliveryDto && deliveryInfo.deliveryDto.traces&&deliveryInfo.deliveryDto.traces.length)}"
            >
              <view class="des">
                {{ $t('merchantHasShippedWa') }}
              </view>
              <view class="time">
                {{ deliveryInfo.createTime }}
              </view>
            </view>
            <view
              v-if="orderInfo.status >= 2 && orderInfo.payTime !== null"
              class="item"
              :class="{'logistics-active':orderInfo.status === 2 && !deliveryInfo?.deliveryDto }"
            >
              <view class="des">
                {{ $t('buyerHasPaidWa') }}
              </view>
              <view class="time">
                {{ orderInfo.payTime }}
              </view>
            </view>
            <view
              v-if="orderInfo.status >= 1"
              class="item create-order"
              :class="{'logistics-active':orderInfo.status === 1}"
            >
              <view class="des logistics-stauts">
                {{ $t('alreadyOrdered') }}
              </view>
              <view class="time">
                {{ $t('buyerSubmittedAnOrder') }}
              </view>
              <view class="time">
                {{ orderInfo.createTime }}
              </view>
            </view>
          </view>

          <view
            v-if="orderInfo.status >= 1 && !isopen"
            class="open"
            @tap.stop="open"
          >
            {{ $t('open') }}
            <view class="open-icon" />
          </view>
        </view>
      </view>
    </view>

    <!-- 包裹多件商品商品信息 -->
    <view
      v-if="deliveryInfo&&deliveryInfo.orderItems&&deliveryInfo.orderItems.length&&orderInfo"
      class="prod-details"
    >
      <view
        v-for="(prod, index) in (isProdOpen ? deliveryInfo.orderItems : [deliveryInfo.orderItems[0]])"
        :key="index"
        class="good-box"
      >
        <view
          v-if="prod.preSaleTime"
          class="pre-sale"
        >
          <view
            v-if="prod.preSaleTime"
            class="a-icon preSaleTime-icon"
          >
            <!--预售-->
            {{ $t('preSale') }}
          </view>
          <view class="sku-name preSaleTime">
            {{ $t('expect') }}&nbsp;{{ tsToDate(prod.preSaleTime) }}&nbsp;{{ $t('startDelivery') }}
          </view>
        </view>
        <view class="goods">
          <view class="prod-pic">
            <img-show :src="prod.pic" />
          </view>
          <view class="info">
            <view class="info-box">
              <view class="info-left prod-name-box">
                <view
                  v-if="prod.activityType===5"
                  class="sku-box gift"
                >
                  <!--赠品-->
                  {{ $t('gift') }}
                </view>
                <text class="prod-name">
                  {{ prod.prodName }}
                </text>
              </view>
            </view>
            <view class="info-box">
              <view class="prod-sku-name">
                {{ prod.properties }}
              </view>
            </view>
            <view class="info-box">
              <view class="info-left">
                <view
                  v-if="prod.orderType"
                  class="sku-box"
                >
                  {{ ['', $t('aGroup'), $t('spike'), $t('integral')][prod.orderType] }}
                </view>
              </view>
            </view>
            <!-- 价格/数量 -->
            <view class="info-box prodprice">
              <view class="info-left">
                <view class="price">
                  <text class="price-symbol">
                    {{ useCurrencyStore().defMark }}
                  </text>
                  {{ wxs.parsePrice(((prod.type === 1 || prod.activityType===5) ? 0 : prod.price) || 0)[0] }}
                  <text class="price-symbol">
                    .{{ wxs.parsePrice(((prod.type === 1 || prod.activityType===5) ? 0 : prod.price) || 0)[1] }}
                  </text>
                </view>
              </view>
              <view class="info-right">
                <view class="num">
                  ×{{ prod.prodCount }}
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>
      <view
        v-if="deliveryInfo.orderItems && deliveryInfo.orderItems.length > 1 && !isProdOpen"
        class="openProdList"
        @tap.stop="openProdList"
      >
        {{ $t('viewAll') + $t('invoice.orderInfo') }}
        <view class="open-icon" />
      </view>
    </view>

    <!-- 为你推荐 -->
    <template v-if="showRecommend">
      <view
        v-if="hotSalesList&&hotSalesList.length"
        class="goods-list"
      >
        <view class="title">
          <image
            src="@/package-user/static/images/icon/recommend.png"
            class="title-img title-img-left"
          />
          <text>{{ $t('recommendedForYou') }}</text>
          <image
            src="@/package-user/static/images/icon/recommend1.png"
            class="title-img"
          />
        </view>
        <view class="goods-item-cont">
          <prod-list
            :prods="hotSalesList"
            :show-vip="true"
          />
        </view>
      </view>
    </template>
  </view>
</template>

<script setup>
import uniCopy from '@/js_sdk/copy/uni-copy.js'
import util from '@/utils/util'
import { useCurrencyStore } from '@/stores/modules/currency'

const wxs = number()

const phoneDesensitization = (val) => {
  if (!val) return val
  return val.replace(/(\d{3})\d*(\d{4})/, '$1****$2')
}

const showRecommend = ref(true)

// 包裹集合信息
const deliveryList = ref([])
// 展示的物流详情
const deliveryInfo = ref({})
/**
 * 生命周期函数--监听页面加载
 */
onLoad((options) => {
  const params = {
    url: '/p/myDelivery/orderInfo/' + options.orderNumber,
    method: 'GET'
  }
  http.request(params).then(({ data }) => {
    deliveryList.value = data
    if (data[0].deliveryDto && data[0].deliveryDto.traces) {
      data[0].deliveryDto.traces.reverse()
    }
    deliveryInfo.value = data[0]
  })
  getOrderInfo(options.orderNumber)
  getHotSalesProds()
})

/**
 * 生命周期函数--监听页面显示
 */
onShow(() => {
  // 头部导航标题
  uni.setNavigationBarTitle({
    title: $t('logisticsDetails')
  })
})

/**
 * 用户点击右上角分享 (勿删)
 */
onShareAppMessage(() => {})

let pages = ''
let current = 1
/**
 * 页面上拉触底事件的处理函数
 */
onReachBottom(() => {
  if (pages > current) {
    current = current + 1
    getHotSalesProds()
  }
})

// 订单数据
const orderInfo = ref({})
/**
 * 用户点击右上角分享
 */
// 获取订单数据
const getOrderInfo = (orderNum) => {
  const params = {
    url: '/p/myOrder/orderDetail',
    method: 'GET',
    data: {
      orderNumber: orderNum
    }
  }
  http.request(params).then(({ data }) => {
    orderInfo.value = data
  })
}

// 当前nav
const isCurrent = ref(0)
/**
 * 获取指定包裹的物流信息
 */
const getDeliveryDtoById = () => {
  const params = {
    url: '/p/myDelivery/deliveryOrder/' + deliveryList.value[isCurrent.value].orderDeliveryId,
    method: 'GET'
  }
  http.request(params).then(({ data }) => {
    if (data.deliveryDto && data.deliveryDto.traces) {
      data.deliveryDto.traces.reverse()
    }
    deliveryInfo.value = data
  })
}

const isopen = ref(false) // 是否展开
const isProdOpen = ref(false)
/**
 * nav切换
 */
const navChange = (e) => {
  const sts = e.currentTarget.dataset.sts
  if (sts === isCurrent.value) return
  isCurrent.value = sts
  isopen.value = false
  isProdOpen.value = false
  getDeliveryDtoById()
}

// 一键复制事件
const copyBtn = () => {
  uniCopy({
    content: deliveryList.value[isCurrent.value].dvyFlowId,
    success: (res) => {
      uni.showToast({
        title: res,
        icon: 'none'
      })
    },
    error: (e) => {
      uni.showToast({
        title: e,
        icon: 'none',
        duration: 3000
      })
    }
  })
}

// 展开
const open = () => {
  isopen.value = !isopen.value
}

// 展开包裹商品列表
const openProdList = () => {
  isProdOpen.value = true
}

const hotSalesList = ref([])
/**
 * 加载热销商品列表
 */
const getHotSalesProds = () => {
  let userId = null
  if (uni.getStorageSync('cbecB2cToken') && uni.getStorageSync('cbecB2cUserInfo')) {
    userId = uni.getStorageSync('cbecB2cUserInfo').userId
  }
  const param = {
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
  http.request(param).then(({ data }) => {
    if (current === 1) {
      hotSalesList.value = data.records[0].products
      pages = data.pages
    } else {
      hotSalesList.value.push(...data.records[0].products)
    }
  })
}

// 格式化时间
const tsToDate = (value) => {
  const arr = value.split(' ')
  return arr[0].split('-')[1] + '-' + arr[0].split('-')[2]
}

</script>

<style lang="scss" scoped>
@use "logistics-info";
</style>
