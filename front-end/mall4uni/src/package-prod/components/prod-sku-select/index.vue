<!--
  Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.

  https://www.mall4j.com/

  未经允许，不可做商业用途！

  版权所有，侵权必究！
-->

<template>
  <view class="Mall4j component-prod-sku-select">
    <!-- 已选规格 -->
    <view
      v-if="pageType"
      class="sku"
      @tap="showSku"
    >
      <view
        class="sku-select"
        :class="{skuSelectEn: $t('lang') !== 'zh_CN' && $t('lang') !== 'zh_HK'}"
      >
        {{ $t('select') }}
      </view>
      <view class="content">
        <view class="sku-select-con">
          <view class="sku-tit">
            {{ $t('selected') }}：
          </view>
          <view class="sku-con">
            <block
              v-for="(skuItem, index) in selectedPropShowList"
              :key="index"
            >
              <text decode>
                {{ index < selectedPropShowList.length - 1 ? skuItem + '，' : skuItem + '&nbsp; &nbsp;' }}
              </text>
            </block>
            <text>{{ prodNum }}&nbsp;{{ $t('piece') }}</text>
          </view>
          <view
            v-if="!(prodType === 3 && !(skuList.length > 1))"
            class="cmt-more"
          />
        </view>
        <view
          v-if="skuImgList && skuImgList.length > 0"
          class="sku-imgs"
        >
          <view
            v-for="(el, i) in ImgList"
            v-show="i < 4 && pic"
            :key="i"
            class="img"
          >
            <img-show
              class-list="['img-item']"
              img-mode="aspectFill"
              :src="setPicURL(el.pic)"
            />
          </view>
          <view
            v-if="skuList.length > 1"
            class="sku-tips"
          >
            <view class="content">
              {{ $t('share') }} {{ skuNum }} {{ $t('seed') }} {{ skuList[0].properties.split(':')[0] }}&nbsp;
            </view>
            <view class="choosable">
              {{ $t('choosable') }}
            </view>
          </view>
        </view>
      </view>
    </view>
    <!-- 已选规格end -->

    <!-- 规格弹窗 -->
    <view
      v-if="skuShow || (!skuShow && !pageType)"
      class="pup-sku"
      @touchmove.stop.prevent="preventHandler"
      @tap="closePopup"
    >
      <view
        class="pup-sku-main"
        @tap.stop
      >
        <view class="pup-sku-header">
          <view
            class="close"
            @tap="closePopup"
          />
          <view class="pup-sku-img">
            <image
              v-if="setPicURL(defaultSku.pic, pic) && !isPicError"
              :src="setPicURL(defaultSku.pic, pic)"
              mode="aspectFill"
              @tap="onPreviewPic"
              @error="handlePicError"
            />
            <image
              v-else
              src="/static/images/icon/def.png"
              mode="aspectFill"
            />
          </view>
          <view class="pup-sku-prod">
            <!-- 秒杀商品价格 -->
            <view
              v-if="findSku && prodType === 2 && activityInfo.activityStatus === 2"
              class="pup-sku-price"
            >
              <text class="yuan">
                {{ useCurrencyStore().defMark }}
              </text>
              <text class="pup-sku-price-int">
                {{ wxs.parsePrice(defaultSku.seckillPrice)[0] }}
              </text>
              .{{ wxs.parsePrice(defaultSku.seckillPrice)[1] }}
            </view>
            <!-- 商品价格 -->
            <view
              v-if="findSku && skuShowType === 0 && prodType === 0 || prodType === 3 || (prodType === 1 && skuShowType === 0) || (prodType === 2 && activityInfo.activityStatus === 1 || activityInfo.activityStatus === undefined)"
              class="pup-sku-price"
            >
              <block v-if="prodType !== 3 && (defaultSku.matchingPrice || defaultSku.price)">
                <text
                  v-if="defaultSku.matchingPrice || defaultSku.price"
                  class="pup-sku-price-int"
                >
                  <text class="yuan">
                    {{ useCurrencyStore().defMark }}
                  </text>{{ wxs.parsePrice(defaultSku.matchingPrice || defaultSku.price)[0] }}
                </text>
                <text
                  v-else-if="findSku"
                  class="pup-sku-price-int"
                >
                  <text class="yuan">
                    {{ useCurrencyStore().defMark }}
                  </text>0
                </text>
                <text v-if="defaultSku.matchingPrice || defaultSku.price">
                  .{{ wxs.parsePrice(defaultSku.matchingPrice || defaultSku.price)[1] }}
                </text>
                <text v-else-if="findSku">
                  .00
                </text>
              </block>
              <view v-if="prodType == 3">
                <text class="price">
                  <text class="sku-score">
                    {{ wxs.parsePrice(defaultSku.skuScore)[0] }}{{ $t('integral') }}
                  </text>
                  <text
                    v-if="prodType == 3 && defaultSku.price"
                    class="sku-score"
                  >
                    +
                  </text>
                  <text
                    v-if="prodType == 3 && defaultSku.price"
                    class="sku-score"
                  >
                    {{ useCurrencyStore().defMark + defaultSku.price.toFixed(2) }}
                  </text>
                </text>
              </view>
            </view>
            <!-- 拼团商品价格 -->
            <view
              v-if="prodType === 1 && findSku && skuShowType === 1"
              class="pup-sku-price"
            >
              <text class="yuan">
                {{ useCurrencyStore().defMark }}
              </text>
              <text class="pup-sku-price-int">
                {{ wxs.parsePrice(defaultGroupSku.actPrice || defaultSku.actPrice)[0] }}
              </text>
              .{{ wxs.parsePrice(defaultGroupSku.actPrice || defaultSku.actPrice)[1] }}
            </view>
            <view
              v-if="!findSku"
              class="pup-sku-price"
            >
              {{ $t('outOfStock') }}
            </view>
            <view class="pup-sku-prop">
              <view class="prop-item prop-sku-name">
                <text class="selected">
                  {{ $t('selected') }}
                </text>
                <view
                  decode="true"
                  class="default-sku"
                >
                  <view
                    v-if="defaultSku.skuName"
                    class="skuName"
                  >
                    {{ '&nbsp;' + defaultSku.skuName }}
                  </view>
                  <view
                    v-else
                    class="skuName"
                  >
                    {{ '&nbsp;' + selectedPropShowList.join('，') }}
                  </view>
                  <view
                    v-if="leastNum && comboId"
                    class="prodNum"
                  >
                    {{ '&nbsp;' + leastNum + '&nbsp;' }}{{ $t('piece') }}
                  </view>
                  <view
                    v-else
                    class="prodNum"
                  >
                    {{ '&nbsp;' + prodNum }}&nbsp;{{ $t('piece') }}
                  </view>
                </view>
              </view>
              <view
                v-if="findSku"
                class="prop-item"
              >
                <text>{{ $t('inventory') }}</text>
                <text decode>
                  {{ '&nbsp;' + (prodType === 2 && (defaultSku.seckillStocks === 0 || defaultSku.seckillStocks) ?
                    defaultSku.seckillStocks : defaultSku.stocks) }}
                </text>
                <text
                  v-if="(prodType === 2 || prodType === 1) && activityInfo.maxNum >= 1 && skuShowType === 1"
                  style="margin-left: 20rpx;"
                >
                  {{ $t('purchaseLimit') }}
                </text>
                <text
                  v-if="(prodType === 2 || prodType === 1) && activityInfo.maxNum >= 1 && skuShowType === 1"
                  decode
                >
                  {{ '&nbsp;' + activityInfo.maxNum }}
                </text>
                {{ $t('piece') }}
              </view>
            </view>
          </view>
        </view>
        <view class="pup-sku-body">
          <view
            v-if="isInputing"
            class="input-mask"
            @tap="handleSetVirtualInputInfo"
          />
          <!-- 说明（虚拟商品） -->
          <view
            v-if="mold === 1 && (virtualInfo.writeOffNum !== 0 || (virtualInfo.writeOffNum === 0 && virtualInfo.isRefund === 0))"
            class="virtual-goods-tips"
          >
            <text class="vi-t">
              {{ $t('usageInstructions') }}：
            </text>
            <!-- writeOffNum 0无需核销 1单次核销 -1多次核销 -->
            <block v-if="virtualInfo.writeOffNum !== 0">
              <!-- writeOffTime核销有效期 -1.长期有效 0.自定义 x.x天内有效 -->
              <text v-if="virtualInfo.writeOffTime === -1">
                {{ $t('longTermValidity') }}
              </text>
              <text v-else-if="virtualInfo.writeOffTime === 0">
                {{ $t('afterPurchase') }} {{ virtualInfo.writeOffStart }} {{ $t('to') }} {{ virtualInfo.writeOffEnd }}
                <text v-if="!isEn">
                  {{ $t('effective') }}
                </text>
              </text>
              <text v-else-if="virtualInfo.writeOffTime === 1">
                {{ $t('validOnTheSameDay') }}
              </text>
              <text v-else>
                {{ $t('purchase') }}{{ virtualInfo.writeOffTime }}{{ $t('validDay') }}
              </text>
            </block>
            <!-- isRefund 0不支持退款 1支持退款 -->
            <text v-if="virtualInfo.isRefund === 0">
              <text v-if="virtualInfo.writeOffNum !== 0">
                ，
              </text>{{ $t('refundsAreNotAllowed') }}
            </text>
          </view>

          <!-- 规格 -->
          <view
            :class="['pup-sku-area', mold === 2 ? 'combo-area' : '']"
          >
            <scroll-view
              :scroll-y="true"
              class="sku-box-scroll"
            >
              <view
                v-if="skuList.length"
                class="sku-box"
              >
                <view
                  v-for="(skuGroupItem, skuGroupItemIndex) in skuGroupList"
                  :key="skuGroupItemIndex"
                >
                  <view
                    v-for="(skuLine, key) in skuGroupItem"
                    :key="key"
                    class="items sku-text"
                  >
                    <view class="sku-name">
                      <text class="sku-kind">
                        {{ key }}{{ skuGroupItemIndex === 0 && skuLine.length ? '(' + skuLine.length + ')' : '' }}
                      </text>
                      <view
                        v-if="skuGroupItemIndex === 0 && hfweihuo"
                        class="img-index"
                        @tap="imgIndexChange"
                      >
                        <image
                          v-if="imgIndex === 1"
                          class="img"
                          src="/static/images/icon/Large.png"
                          mode="aspectFill"
                        />
                        <image
                          v-else
                          class="img"
                          src="/static/images/icon/list.png"
                          mode="aspectFill"
                        />
                        <text>{{ imgIndex === 1 ? $t('bigPic') : $t('list') }}</text>
                      </view>
                    </view>
                    <view class="con">
                      <view
                        v-for="(skuLineItem, index) in skuLine"
                        :key="index"
                        class="sku-choose-item"
                        :class="['item-item', selectedPropList.indexOf(key + ':' + skuLineItem) !== -1 ? 'active' : '',
                                 isSkuLineItemNotOptional(allProperties, selectedPropObj, key, skuLineItem, propKeys) ? 'dashed' : '', imgIndex === 1 && skuGroupItemIndex === 0 ? 'item-comm-img' : imgIndex === 2 && skuGroupItemIndex === 0 ? 'item-ckg' : 'nowarp']"
                        @click="toChooseItem(skuGroupItemIndex, skuLineItem, key)"
                      >
                        <image
                          v-if="skuGroupItemIndex === 0 && hfweihuo && !errSkuLineItems.some(el => el === skuLineItem)"
                          mode="aspectFill"
                          class="img"
                          :src="setPicURL(getSkuImg(key, skuLineItem), pic)"
                          @error="imgerror(skuLineItem)"
                        />
                        <image
                          v-else-if="skuGroupItemIndex === 0 && hfweihuo"
                          mode="aspectFill"
                          class="img"
                          :src="'/static/images/icon/def.png'"
                        />

                        <view class="item-name">
                          {{ skuLineItem }}
                        </view><text
                          v-if="stockout(skuGroupItemIndex, key, skuLineItem)"
                          class="stockout"
                        >
                          {{ $t('unavailableGoods') }}
                        </text>
                      </view>
                    </view>
                  </view>
                  <view class="division" />
                </view>
              </view>
              <!--（自提/同城相关 --- 勿动）-->
              <!-- 选择配送方式 -->
              <!--              <view-->
              <!--                v-if="(mold===0 || mold===2)&&deliveryModeVO&&cartOrBuy!==1&&prodType!==3"-->
              <!--                class="distribution-mode"-->
              <!--              >-->
              <!--                <view class="distribution-mode-tit">-->
              <!--                  {{ $t('deliveryMethod') }}-->
              <!--                </view>-->
              <!--                <view class="distribution-mode-content">-->
              <!--                  <view-->
              <!--                    v-if="deliveryModeVO.hasShopDelivery"-->
              <!--                    :class="['content-item', dvyType === 1 ? 'content-item-active' : '', isDelivery ? '' : 'disabled']"-->
              <!--                    @tap="dvyTypeChange(1)"-->
              <!--                  >-->
              <!--                    {{ $t('expressDelivery') }}-->
              <!--                  </view>-->
              <!--                  <view-->
              <!--                    v-if="deliveryModeVO.hasUserPickUp"-->
              <!--                    :class="['content-item', dvyType === 2 ? 'content-item-active' : '']"-->
              <!--                    @tap="dvyTypeChange(2)"-->
              <!--                  >-->
              <!--                    {{ $t('pickStore') }}-->
              <!--                  </view>-->
              <!--                  <view-->
              <!--                    v-if="deliveryModeVO.hasCityDelivery"-->
              <!--                    :class="['content-item', dvyType === 4 ? 'content-item-active' : '']"-->
              <!--                    @tap="dvyTypeChange(4)"-->
              <!--                  >-->
              <!--                    {{ $t('sameDelivery') }}-->
              <!--                  </view>-->
              <!--                </view>-->
              <!--              </view>-->
            </scroll-view>
          </view>

          <!-- 数量 -->
          <view
            v-if="pageType || isJionGroup"
            class="pup-sku-count"
          >
            <view class="count-name">
              {{ $t('quantity') }}
            </view>
            <view class="m-numSelector">
              <view
                :class="['minus', (prodNumSelf <= 1 ? 'btn-disabled m-p-btn' : '')]"
                @tap="onCountMinus"
              />
              <input
                ref="prodNumSelfRef"
                :key="prodNumSelfKey"
                type="number"
                :value="prodNumSelf"
                @input="changeNUm"
                @blur="prodNumInp()"
              >
              <view
                class="plus"
                @tap="onCountPlus"
              />
            </view>
          </view>
        </view>
        <!-- 当前地区无货提示 -->
        <!-- 当前规格存在库存，并且选择的规格在当前地址无货，并且非自提 -->
        <view
          v-if="prodType !== 3 && (pageType || isJionGroup) && defaultSku.isHasStock === false && dvyType !== 2 && cartOrBuy!==1"
          class="delivery-tip"
        >
          <view class="text">
            {{ $t('prodAreaNotStockTip') }}
          </view>
          <view
            class="btn"
            @click="addressListPop"
          >
            {{ $t('changeTheAddress') }}
          </view>
        </view>
        <!-- 底部按钮: 普通/秒杀/拼团  -->
        <view
          v-if="(pageType || isJionGroup) && pageType != 3"
          :class="'pup-sku-footer ' + (isJionGroup ? '' : findSku && !prodNumBiggerThanStock() ? '' : 'gray ') + ((prohibitive() && cartOrBuy !== 1) || isStockout ? 'disabled' : '')"
        >
          <!-- 秒杀按钮 -->
          <view
            v-if="prodType === 2 && activityInfo.activityStatus === 2"
            class="btn buy"
            :class="{'gray-btn': !defaultSku.isHasStock && dvyType!==2}"
            @tap="handleBuyNow(2)"
          >
            {{ $t('immediatelyBuy') }}
          </view>
          <!-- 非秒杀、非预售商品 -->
          <block v-else-if="skuShowType == 0 && preSellStatus != 1 || cartOrBuy === 3">
            <view
              v-if="cartOrBuy === 3"
              class="cart-and-buy"
            >
              <view
                v-if="skuShowType == 0 && mold !== 1"
                class="btn cart"
                @tap="handleAddToCart"
              >
                {{ $t('addShoppingCart') }}
              </view>
              <view
                v-if="skuShowType == 0"
                :class="{'gray-btn': !defaultSku.isHasStock && dvyType!==2}"
                class="btn buy"
                @tap="handleBuyNow(0)"
              >
                {{ $t('buyNow') }}
              </view>
            </view>
            <view
              v-else
              class="confirm"
              :class="{'gray-btn': !defaultSku.isHasStock && dvyType!==2 && cartOrBuy!==1}"
              @tap="confirm"
            >
              {{ $t('confirm') }}
            </view>
          </block>
          <!-- 预售 -->
          <view
            v-if="(preSellStatus == 1 && !(prodType === 2 && activityInfo.activityStatus === 2) && !(prodType === 1 && activityInfo.activityStatus === 2)) || cartOrBuy === 4"
            class="btn pre-sale-buy"
            :class="{'gray-btn': !defaultSku.isHasStock && dvyType!==2 && cartOrBuy!==1}"
            @tap="handleBuyNow(0)"
          >
            {{ $t('buyNow') }}
          </view>
          <!-- 开团按钮 -->
          <view
            v-if="skuShowType == 1 && !isJionGroup && prodType === 1"
            :class="['btn', (activityInfo.activityStatus === 1 || (!defaultSku.isHasStock && dvyType!==2)) ? 'gray-btn' : 'buy']"
            @tap="handleBuyNow(1)"
          >
            {{ $t('startAGroup') }}
          </view>
          <!-- 参团按钮 -->
          <view
            v-if="isJionGroup"
            class="btn"
            :class="[defaultSku.stocks === 0 ? 'gray-btn' : 'buy']"
            @tap="handleJionGroup"
          >
            {{ $t('JoinGroupNow') }}
          </view>
        </view>
        <!-- 积分商品: 兑换按钮 -->
        <view
          v-if="prodType == 3"
          :class="'pup-sku-footer ' + (findSku && !prodNumBiggerThanStock() ? '' : 'gray ') + (prohibitive() || isStockout ? 'disabled' : '')"
        >
          <view
            class="btn buy"
            @tap="handleBuyNow(3)"
          >
            {{ $t('redeemNow') }}
          </view>
        </view>
        <!-- 活动商品: 仅作为套餐(附属商品)或赠品 不可单独购买 -->
        <view
          v-if="prodType == 5 && pageType"
          class="pup-sku-footer gray"
        >
          <view class="btn buy">
            {{ $t('notAvailableForPurchase') }}
          </view>
        </view>
        <!-- 购物车: 切换sku弹窗 -->
        <view
          v-if="!pageType && !isJionGroup"
          class="pup-sku-footer"
          :class="{ gray: !findSku || prodNumBiggerThanStock(), disabled: prohibitive() }"
        >
          <view
            class="btn buy"
            @tap="submit"
          >
            {{ $t('confirm') }}
          </view>
        </view>
      </view>
    </view>
    <!-- 规格弹窗 end -->
  </view>
</template>

<script setup>
import { computed } from 'vue'
import { useCurrencyStore } from '@/stores/modules/currency'

const wxs = number()

const emit = defineEmits([
  'dvyTypeChange',
  'closeSkuPop',
  'setSku',
  'setSku',
  'showSku',
  'setProdNum',
  'setSku',
  'setSku',
  'setSku',
  'setProdNum',
  'setProdNum',
  'addToCart',
  'buyNow',
  'groupConfirmOrder',
  'setVirtualInputInfo',
  'setProdNum',
  'setSku',
  'addressListPop'
])

const props = defineProps({
  pic: {
    type: String,
    default: ''
  },
  isMain: {
    type: Boolean,
    default: false
  },
  isDelivery: {
    type: Boolean,
    default: true
  },
  leastNum: {
    type: Number,
    default: 0
  },
  dvyType: {
    type: Number,
    default: 1
  },
  deliveryModeVO: {
    type: Object,
    default: () => { }
  },
  skuName: {
    type: String,
    default: ''
  },
  skuList: {
    type: Array,
    default () {
      return []
    }
  },
  prodId: {
    type: [String, Number],
    default: 0
  },
  distributionCardNo: {
    type: [String, Number],
    default: 0
  },
  skuId: {
    type: Number,
    default: 0
  },
  comboId: {
    type: Number,
    default: 0
  },
  defaultPrice: {
    type: Number,
    default: 0
  },
  mold: {
    type: [Number, String],
    default: 0
  },
  virtualInfo: {
    type: Object,
    default: () => { }
  },
  // sku的显示类型 0普通sku 1拼团sku
  skuShowType: {
    type: Number,
    default: 0
  },
  prodType: {
    type: Number,
    default: 0
  },
  preSellStatus: {
    type: Number,
    default: 0
  },
  prodNum: {
    type: Number,
    default: 1
  },
  // 团购/秒杀  活动信息
  activityInfo: {
    type: Object,
    default: () => { }
  },
  // 地区信息
  deliveryAddrInfo: {
    type: Object,
    default: null
  },
  // 页面类型: 0非商品详情页(套餐/购物车/团购详情:参团页面)  1普通商品(团购)  2秒杀商品  3积分商品  5活动商品
  pageType: {
    type: Number,
    default: 0
  },
  skuShow: {
    type: Boolean,
    default: false
  },
  // 参团页面
  isJionGroup: {
    type: Boolean,
    default: false
  },
  cartOrBuy: { // 1购物车 2 立即购买 3 购物车||立即购买
    type: Number,
    default: 2
  },
  userInfoFlag: { // 是否有用户信息
    type: Boolean,
    default: false
  }
})

// 是否非中文
const isEn = ref(uni.getStorageSync('cbecB2cLang') !== 'zh_CN')
const prodNumSelfKey = ref(1)

const { proxy } = getCurrentInstance()
// 团购sku
const defaultGroupSku = ref({})
const isPicError = ref(false)

const prodNumSelf = computed({
  get () {
    return props.prodNum
  },
  set () {
    prodNumSelfKey.value = Math.random() * 1000000
    proxy.$forceUpdate()
  }
})

const isStockout = computed(() => {
  if (defaultSku.value.seckillStocks === 0 || defaultSku.value.seckillStocks) {
    return !defaultSku.value.seckillStocks
  } else {
    return !defaultSku.value.stocks
  }
})
onLoad(() => {
  // groupSku()
})

// 自提/同城相关 --- 勿动
// const dvyTypeChange = (type) => {
//   if (type === 1 && !props.isDelivery) {
//     return
//   }
//   emit('dvyTypeChange', type)
// }

// 禁用购买按钮
const prohibitive = () => {
  return !props.isDelivery && props.dvyType === 1 && !props.deliveryModeVO.hasUserPickUp && !props.deliveryModeVO.hasCityDelivery
}

const preventHandler = () => {

}

const confirm = () => {
  if (props.cartOrBuy === 1) {
    handleAddToCart()
  } else {
    if (!defaultSku.value.isHasStock && props.dvyType !== 2) {
      return
    }
    handleBuyNow(0)
  }
}

const imgIndex = ref(1) // 0 无图 1有图 2 大图
const imgIndexChange = () => {
  imgIndex.value = imgIndex.value === 1 ? 2 : 1
}
// 判断是否缺货
const stockout = (skuGroupItemIndex, key, val) => {
  const selectedPropListPar = JSON.parse(JSON.stringify(selectedPropList.value))
  selectedPropListPar[skuGroupItemIndex] = key + ':' + val
  // 找到匹配规格并判断库存
  return props.skuList.some(el => {
    return el.properties === selectedPropListPar.join(';') && ((el.stocks === 0 && props.prodType !== 2) || (el.seckillSkuItem?.seckillStocks === 0 && props.prodType === 2))
  })
}

// 获取pic
const getSkuImg = (key, val) => {
  let pic = ''
  props.skuList.forEach(el => {
    if (el.properties.split(';')[0] === key + ':' + val && el.pic) {
      pic = el.pic
    }
  })
  return pic
}

/**
 * 图片路径处理
 */
const setPicURL = (pic, pic1) => {
  const url = pic || pic1
  return util.checkFileUrl(url)
}
const closePopup = () => {
  emit('closeSkuPop')
}

const skuNum = ref(0)
const skuImgList = ref([]) // 规格图片列表
const ImgList = ref([])
const hfweihuo = ref(false)
const defaultSku = ref('')
const propKeys = ref([])
const selectedPropObj = ref({})
const skuGroup = ref({})
const allProperties = ref([])
const skuGroupList = ref([])
const selectedPropObjList = ref([])
const findSku = ref(false)
const groupSku = () => {
  const skuList = props.skuList
  if (skuList.length == 1 && !skuList[0].properties) {
    defaultSku.value = skuList[0]
    findSku.value = true
    if (props.pageType || props.isJionGroup) {
      emit('setSku', defaultSku.value, findSku.value)
    }
    if (props.activityInfo && props.prodType === 1) {
      setDefaultGroupSku()
    }
    return
  }
  const skuGroupListPar = []
  const skuGroupPar = {}
  const allPropertiesPar = []
  const propKeysPar = []
  const selectedPropObjPar = {}
  const selectedPropObjListPar = []
  const skuImgListPar = []
  const ImgListPar = []
  let skuNumPar = 0

  let defaultSkuPar = null
  const arr = JSON.parse(JSON.stringify(skuList))
  arr.sort((a, b) => {
    return a.seckillPrice - b.seckillPrice
  })
  for (let i = 0; i < skuList.length; i++) {
    if (skuList[i].pic) {
      hfweihuo.value = true
    }
    let isDefault = false
    if ((!defaultSkuPar && skuList[i].skuId == props.skuId)) {
      defaultSkuPar = skuList[i]
      isDefault = true
    }
    if (defaultSkuPar === null && (i === skuList.length - 1)) {
      defaultSkuPar = arr[0]
      isDefault = true
    }
    const properties = skuList[i].properties // 版本:公开版;颜色:金色;内存:64GB
    allPropertiesPar.push(properties)
    const propList = properties.split(';') // ["版本:公开版","颜色:金色","内存:64GB"]
    if (skuImgListPar.length === 0 || !(skuImgListPar.some(el => el.name === propList[0]))) {
      skuNumPar++
      skuImgListPar.push({
        name: propList[0],
        pic: skuList[i].pic || ''
      })
      if (skuList[i].pic) {
        ImgListPar.push({
          pic: skuList[i].pic || ''
        })
      }
    }

    for (let j = 0; j < propList.length; j++) {
      const index = propList[j].indexOf(':')
      const propval = [propList[j].slice(0, index), propList[j].substring(index + 1)] // ["版本","公开版"]
      let props = skuGroupPar[propval[0]] // 先取出 版本对应的值数组

      // 如果当前是默认选中的sku，把对应的属性值 组装到selectedProp
      if (isDefault) {
        propKeysPar.push(propval[0])
        selectedPropObjPar[propval[0]] = propval[1]
        const selectedPropObjItem = {}
        selectedPropObjItem[propval[0]] = propval[1]
        selectedPropObjListPar.push(selectedPropObjItem)
      }

      if (props == undefined) {
        props = [] // 假设还没有版本，新建个新的空数组
        props.push(propval[1]) // 把 "公开版" 放进空数组
      } else {
        if (props.indexOf(propval[1]) === -1) { // 如果数组里面没有"公开版"
          props.push(propval[1]) // 把 "公开版" 放进数组
        }
      }
      skuGroupPar[propval[0]] = props // 最后把数据 放回版本对应的值
      const propListItem = {}
      propListItem[propval[0]] = props
      skuGroupListPar.push(propListItem)
    }
  }
  skuNum.value = skuNumPar

  skuImgList.value = skuImgListPar
  ImgList.value = ImgListPar
  defaultSku.value = defaultSkuPar || {}
  propKeys.value = propKeysPar
  selectedPropObj.value = selectedPropObjPar
  skuGroup.value = skuGroupPar
  selectedPropObjList.value = selectedPropObjListPar
  skuGroupList.value = unique(skuGroupListPar)

  allProperties.value = allPropertiesPar
  parseSelectedObjToVals(skuList)
  proxy.$forceUpdate()
  if (props.activityInfo && props.prodType === 1) {
    setDefaultGroupSku()
  }
}

const selectedProperties = ref('')
const selectedPropList = ref([])
const selectedPropShowList = ref([])
/**
 * 将已选的 {key:val,key2:val2}转换成 [val,val2]
 */
const parseSelectedObjToVals = (skuList) => {
  const selectedPropObjListPar = selectedPropObjList.value
  let selectedPropertiesPar = ''
  const selectedPropListPar = []
  const selectedPropShowListPar = []
  for (let i = 0; i < selectedPropObjListPar.length; i++) {
    const selectedPropObjItem = selectedPropObjListPar[i]
    for (const key in selectedPropObjItem) {
      if (Object.hasOwnProperty.call(selectedPropObjItem, key)) {
        selectedPropListPar.push(key + ':' + selectedPropObjItem[key])
        selectedPropShowListPar.push(selectedPropObjItem[key])
        selectedPropertiesPar += key + ':' + selectedPropObjItem[key] + ';'
      }
    }
  }
  selectedPropertiesPar = selectedPropertiesPar.substring(0, selectedPropertiesPar.length - 1)
  selectedPropList.value = selectedPropListPar
  selectedPropShowList.value = selectedPropShowListPar
  selectedProperties.value = selectedPropertiesPar
  selectedPropObjList.value = selectedPropObjListPar
  let findSkuPar = false
  for (let i = 0; i < skuList.length; i++) {
    if (skuList[i].properties == selectedPropertiesPar) {
      findSkuPar = true
      defaultSku.value = skuList[i]
      break
    }
  }
  if (findSkuPar === false) {
    defaultSku.value = {
      pic: setSkuImg() || defaultSku.value.pic
    }
  }
  findSku.value = findSkuPar
  if (props.pageType || props.isJionGroup) {
    emit('setSku', defaultSku.value, findSku.value)
  }
  if (props.activityInfo && props.prodType === 1) {
    setDefaultGroupSku()
  }
}

const setSkuImg = () => {
  for (let i = 0; i < props.skuList.length; i++) {
    const skuObj = props.skuList[i].properties.split(';')[0]
    const index = skuObj.indexOf(':')
    if (skuObj.substring(index + 1) === selectedPropObjList.value[0][skuObj.slice(0, index)]) {
      return props.skuList[i].pic
    }
  }
}

const errSkuLineItems = ref([]) // 图片加载错误的sku
const imgerror = (skuLineItem) => {
  errSkuLineItems.value.push(skuLineItem)
}

const showSku = () => {
  emit('showSku', undefined, undefined, true)
}

/**
 * 判断当前的规格值 是否可以选
 */
const isSkuLineItemNotOptional = (allPropertiesPar, propObj, key, item, propKeysPar) => {
  const selectedPropObjPar = Object.assign({}, propObj)
  let properties = ''
  selectedPropObjPar[key] = item
  for (let j = 0; j < propKeysPar.length; j++) {
    properties += propKeysPar[j] + ':' + selectedPropObjPar[propKeysPar[j]] + ';'
  }
  properties = properties.substring(0, properties.length - 1)
  for (let i = 0; i < allPropertiesPar.length; i++) {
    if (properties == allPropertiesPar[i]) {
      return false
    }
  }
  for (let i = 0; i < allPropertiesPar.length; i++) {
    if (allPropertiesPar[i].indexOf(item) >= 0) {
      return true
    }
  }
  return false
}

/**
 * 规格点击事件
 */
const toChooseItem = (skuGroupItemIndex, skuLineItem, key) => {
  selectedPropObjList.value[skuGroupItemIndex] = selectedPropObjList.value[skuGroupItemIndex] || {}

  selectedPropObjList.value[skuGroupItemIndex][key] = skuLineItem
  selectedPropObj.value[key] = skuLineItem
  emit('setProdNum', 1)
  const skuList = props.activityInfo?.activityStatus == 2 && props.activityInfo.seckillId ? props.activityInfo.seckillSkuList : props.skuList
  parseSelectedObjToVals(skuList)
  if (props.pageType || props.isJionGroup) {
    emit('setSku', defaultSku.value, findSku.value)
  }
  if (props.activityInfo && props.prodType === 1) {
    setDefaultGroupSku()
  }
}

/**
 * 设置选中的拼团sku
 */
const setDefaultGroupSku = () => {
  const groupSkuList = props.activityInfo.groupSkuList || props.activityInfo.seckillSkuList
  if (groupSkuList) {
    if (groupSkuList.length === 1 && !groupSkuList[0].properties) {
      defaultGroupSku.value = groupSkuList[0]
      emit('setSku', defaultGroupSku.value, findSku.value)
      return
    }
    for (let i = 0; i < groupSkuList.length; i++) {
      if (groupSkuList[i].properties == selectedProperties.value) {
        defaultGroupSku.value = groupSkuList[i]
        emit('setSku', defaultGroupSku.value, findSku.value)
        break
      }
    }
  }
}

/**
 * 去重
 */
const unique = (arr) => {
  const map = {}
  arr.forEach(item => {
    const obj = {}
    Object.keys(item).sort().forEach(key => {
      obj[key] = item[key]
    })
    map[JSON.stringify(obj)] = item
  })
  return Object.keys(map).map(key => JSON.parse(key))
}

/**
 * 减数量
 */
const onCountMinus = () => {
  let prodNum = prodNumSelf.value
  if (prodNum > 1) {
    prodNum = prodNum - 1
  }
  emit('setProdNum', prodNum)
}

/**
 * 加数量
 */
const onCountPlus = () => {
  // 商品数量
  let prodNum = parseInt(prodNumSelf.value)
  // 默认库存数量
  const defaultSkuStock = parseInt(props.prodType === 2 && (defaultSku.value.seckillStocks === 0 || defaultSku.value.seckillStocks) ? defaultSku.value.seckillStocks : defaultSku.value.stocks)
  // 是否限购
  const isRestriction = props.activityInfo.maxNum && props.activityInfo.maxNum !== -1 && props.skuShowType === 1 && props.activityInfo.maxNum <= defaultSkuStock
  // limit 最大可购买数量: 活动商品不限购则为当前sku库存；活动商品限购时，当库存大于限购数量则为限购数量，否则为库存数量
  const limit = !isRestriction ? defaultSkuStock : defaultSkuStock > props.activityInfo.maxNum ? props.activityInfo.maxNum : defaultSkuStock
  if (isRestriction) {
    if (prodNum >= limit) {
      uni.showToast({
        title: isRestriction && limit ? `${$t('purchaseLimit')} ${props.activityInfo.maxNum} ${$t('piece')}` : $t('insufficientStock'),
        icon: 'none'
      })
    }
    prodNum = (prodNum >= limit ? limit : prodNum + 1) || 1
  } else {
    if (prodNum >= defaultSkuStock) {
      uni.showToast({
        title: $t('insufficientStock'),
        icon: 'none'
      })
      prodNum = defaultSkuStock || 1
    } else {
      prodNum += 1
    }
  }
  emit('setProdNum', prodNum)
}

/**
 * 加购
 */
const handleAddToCart = () => {
  if (!findSku.value || prodNumBiggerThanStock()) {
    return
  }
  emit('addToCart')
}

/**
 * 立即购买
 * @param {Number} orderType 订单类型  0普通 1团购 2秒杀 3积分
 */
const handleBuyNow = (orderType) => {
  // 不支持配送
  if (!props.isDelivery && props.dvyType === 1) {
    return
  }
  if (!findSku.value || prodNumBiggerThanStock()) {
    return
  }
  if (!defaultSku.value.isHasStock && props.dvyType !== 2) {
    return
  }
  emit('buyNow', orderType)
}

/**
 * 参团
 */
const handleJionGroup = () => {
  if (!findSku.value || prodNumBiggerThanStock()) {
    return
  }
  emit('groupConfirmOrder')
}

/**
 * 库存校验
 * @return {Boolean} 购买数量大于库存
 */
const prodNumBiggerThanStock = () => {
  if (!defaultSku.value) return true
  const defaultSkuStock = props.pageType == 2 ? defaultSku.value.seckillStocks : defaultSku.value.stocks
  return defaultSku.value.stocks === 0 ? true : (defaultSkuStock === 0 ? true : prodNumSelf.value > defaultSkuStock)
}

const isInputing = ref(false)

/**
 * 设置虚拟商品留言
 */
const handleSetVirtualInputInfo = () => {
  isInputing.value = false
  emit('setVirtualInputInfo', props.virtualInfo.virtualRemarks)
}

const num = ref('')
/**
 * 输入商品数量
 */
const prodNumInp = () => {
  let numPar = Number(num.value.replace(/[^\d]/g, ''))
  const max = props.prodType === 2 && (defaultSku.value.seckillStocks === 0 || defaultSku.value.seckillStocks) ? defaultSku.value.seckillStocks : defaultSku.value.stocks
  if (numPar > max) {
    numPar = max
  } else if (numPar < 1) {
    numPar = 1
  }
  // prodNumSelfRef.value?.valueSync = num
  emit('setProdNum', numPar)
  prodNumSelf.value = props.prodNum
}

const changeNUm = (e) => {
  num.value = e.detail.value
}

const submit = () => {
  if (findSku.value && !prodNumBiggerThanStock()) {
    emit('setSku', defaultSku.value, props.isMain, findSku.value)
  }
}
/**
 * 图片加载失败时显示默认图片
 */
const handlePicError = () => {
  isPicError.value = true
}

/**
 * 可用地址弹窗显示
 */
const addressListPop = () => {
  emit('addressListPop')
}

/**
 * 图片预览
 */
const onPreviewPic = () => {
  uni.previewImage({
    urls: [util.checkFileUrl(defaultSku.value.pic || props.pic)]
  })
}

defineExpose({
  groupSku
})
</script>

<style lang="scss" scoped>
@use "index";
</style>
