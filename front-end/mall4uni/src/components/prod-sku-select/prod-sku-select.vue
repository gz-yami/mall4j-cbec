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
      <view class="sku-tit">
        {{ $t('selected') }}
      </view>
      <view class="sku-con">
        <block
          v-for="(skuItem,index) in selectedPropShowList"
          :key="index"
        >
          <text decode>
            {{ index < selectedPropShowList.length-1 ? skuItem +'，' : skuItem + '&nbsp; &nbsp;' }}
          </text>
        </block>
        <text>{{ prodNum }}&nbsp;{{ $t('piece') }}</text>
      </view>
      <view class="more">
        ...
      </view>
    </view>
    <!-- 已选规格end -->

    <!-- 规格弹窗 -->
    <view
      v-if="skuShow || (!skuShow && !pageType)"
      class="pup-sku"
      @tap="onClosePopup"
    >
      <view
        class="pup-sku-main"
        @tap.stop
      >
        <view class="pup-sku-header">
          <view
            class="close"
            @tap="onClosePopup"
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
              {{ useCurrencyStore().defMark }}
              <text class="pup-sku-price-int">
                {{ wxs.parsePrice(defaultSku.seckillPrice)[0] }}
              </text>
              .{{ wxs.parsePrice(defaultSku.seckillPrice)[1] }}
            </view>
            <!-- 商品价格 -->
            <view
              v-if="findSku && skuShowType === 0 && prodType === 0 || prodType === 3 || (prodType === 1 && skuShowType === 0 ) || (prodType === 2 && activityInfo.activityStatus === 1 || activityInfo.activityStatus === undefined)"
              class="pup-sku-price"
            >
              <block v-if="prodType!==3 || defaultSku.matchingPrice || defaultSku.price">
                <text
                  v-if="defaultSku.matchingPrice || defaultSku.price"
                  class="pup-sku-price-int"
                >
                  {{ useCurrencyStore().defMark }}{{ wxs.parsePrice(defaultSku.matchingPrice || defaultSku.price)[0] }}
                </text>
                <text
                  v-else-if="findSku"
                  class="pup-sku-price-int"
                >
                  {{ useCurrencyStore().defMark }}0
                </text>
                <text v-if="defaultSku.matchingPrice || defaultSku.price">
                  .{{ wxs.parsePrice(defaultSku.matchingPrice || defaultSku.price)[1] }}
                </text>
                <text v-else-if="findSku">
                  .00
                </text>
              </block>
              <text v-if="(defaultSku.matchingPrice || defaultSku.price) && defaultSku.skuScore">
                +
              </text>
              <text
                v-if="defaultSku.skuScore && prodType===3"
                class="pup-sku-price-int"
              >
                {{ defaultSku.skuScore }}{{ $t('integral') }}
              </text>
            </view>
            <!-- 拼团商品价格 -->
            <view
              v-if="prodType === 1 && findSku && skuShowType === 1"
              class="pup-sku-price"
            >
              {{ useCurrencyStore().defMark }}
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
              <view>
                <text>{{ $t('selected') }}</text>
                <text decode>
                  <text v-if="defaultSku.skuName">
                    {{ '&nbsp;' + defaultSku.skuName }}
                  </text>
                  <text v-else>
                    {{ '&nbsp;' + selectedPropShowList.join('，') }}
                  </text>
                  <text v-if="leastNum && comboId">
                    {{ '&nbsp;'+leastNum+'&nbsp;' }}{{ $t('piece') }}
                  </text>
                  <text v-else>
                    {{ '&nbsp;' + prodNum }}&nbsp;{{ $t('piece') }}
                  </text>
                </text>
              </view>
              <view v-if="findSku">
                <text>{{ $t('inventory') }}</text>
                <text decode>
                  {{ '&nbsp;'+ (prodType === 2 && (defaultSku.seckillStocks === 0 || defaultSku.seckillStocks) ? defaultSku.seckillStocks : defaultSku.stocks) }}
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
                  {{ '&nbsp;'+ activityInfo.maxNum }}
                </text>
              </view>
            </view>
          </view>
        </view>
        <view class="pup-sku-body">
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
                {{ $t('afterPurchase') }} {{ virtualInfo.writeOffStart }} {{ $t('to') }} {{ virtualInfo.writeOffEnd }} <text v-if="!isEn">
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
              <text v-if="virtualInfo.writeOffNum !== 0 ">
                ，
              </text>{{ $t('refundsAreNotAllowed') }}
            </text>
          </view>

          <!-- 规格 -->
          <view class="pup-sku-area">
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
                  <text class="sku-kind">
                    {{ key }}
                  </text>
                  <view class="con">
                    <text
                      v-for="skuLineItem in skuLine"
                      :key="skuLineItem"
                      class="sku-choose-item"
                      :class="['item-item',selectedPropList.indexOf(key + ':' + skuLineItem) !== -1?'active':'',
                               isSkuLineItemNotOptional(allProperties,selectedPropObj,key,skuLineItem,propKeys)? 'dashed' : '']"
                      @click="toChooseItem(skuGroupItemIndex, skuLineItem, key)"
                    >
                      {{ skuLineItem }}
                    </text>
                  </view>
                </view>
              </view>
            </view>
          </view>
          <!-- 数量 -->
          <view
            v-if="pageType || isJionGroup"
            class="pup-sku-count"
          >
            <view class="m-numSelector">
              <view
                class="minus"
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
            <view class="count-name">
              {{ $t('quantity') }}
            </view>
          </view>
        </view>
        <!-- 底部按钮: 普通/秒杀/拼团  -->
        <view
          v-if="(pageType || isJionGroup) && +pageType !== 3"
          :class="'pup-sku-footer ' + ( isJionGroup ? '' : findSku && !prodNumBiggerThanStock() ? '':'gray')"
        >
          <!-- 秒杀按钮 -->
          <view
            v-if="prodType===2 && activityInfo.activityStatus === 2"
            class="btn buy"
            @tap="handleBuyNow(2)"
          >
            {{ $t('immediatelyBuy') }}
          </view>
          <!-- 非秒杀、非预售商品 -->
          <block v-else-if="+skuShowType===0 && +preSellStatus !== 1">
            <view
              v-if="+skuShowType === 0 && mold !== 1"
              class="btn cart"
              @tap="handleAddToCart"
            >
              {{ $t('addShoppingCart') }}
            </view>
            <view
              v-if="+skuShowType===0"
              class="btn buy"
              @tap="handleBuyNow(0)"
            >
              {{ $t('buyNow') }}
            </view>
          </block>
          <!-- 预售 -->
          <view
            v-if="+preSellStatus === 1 && !(prodType===2 && activityInfo.activityStatus === 2)"
            class="btn pre-sale-buy"
            @tap="handleBuyNow(0)"
          >
            {{ $t('buyNow') }}
          </view>
          <!-- 开团按钮 -->
          <view
            v-if="+skuShowType===1 && !isJionGroup && prodType === 1"
            :class="['btn', activityInfo.activityStatus === 1 ? 'gray-btn' : 'buy']"
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
          v-if="+prodType === 3"
          :class="'pup-sku-footer ' + (findSku && !prodNumBiggerThanStock() ? '':'gray')"
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
          v-if="+prodType === 5 && pageType"
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
          :class="{gray: !findSku || prodNumBiggerThanStock()}"
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
import { useCurrencyStore } from '@/stores/modules/currency'

const wxs = number()

const emit = defineEmits([
  'closeSkuPop',
  'setSku',
  'showSku',
  'setProdNum',
  'addToCart',
  'buyNow',
  'groupConfirmOrder',
  'setVirtualInputInfo'
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
  leastNum: {
    type: Number,
    default: 0
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
    default: () => {}
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
    default: () => {}
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
  prodId: {
    type: Number || String,
    default: 0
  }
})

// 是否非中文
const isEn = uni.getStorageSync('cbecB2cLang') !== 'zh_CN'

const prodNumSelfKey = ref(1)
const prodNumSelf = computed({
  get () {
    return props.prodNum
  },
  set () {
    prodNumSelfKey.value = Math.random() * 1000000
  }
})

onMounted(() => {
  onGroupSku()
})

/**
 * 图片路径处理
 */
const setPicURL = (pic, pic1) => {
  const url = pic || pic1
  if (url) {
    return util.checkFileUrl(url)
  }
  return false
}
const onClosePopup = () => {
  emit('closeSkuPop')
}

const defaultSku = ref('')
const propKeys = ref([])
const selectedPropObj = ref({})
const selectedPropObjList = ref([])
const skuGroupList = ref([])
const allProperties = ref([])
const findSku = ref(false)
const onGroupSku = () => {
  const tempSkuList = +props.activityInfo.activityStatus === 2 && props.activityInfo.seckillId ? props.activityInfo.seckillSkuList : props.skuList
  if (tempSkuList.length === 1 && !tempSkuList[0].properties) {
    defaultSku.value = tempSkuList[0]
    findSku.value = true
    if (props.pageType || props.isJionGroup) {
      emit('setSku', defaultSku.value, findSku.value)
    }
    if (props.activityInfo && props.prodType === 1) {
      setDefaultGroupSku()
    }
    return
  }
  const tempSkuGroupList = []
  const skuGroup = {}
  const tempAllProperties = []
  const tempPropKeys = []
  const tempSelectedPropObj = {}
  const tempSelectedPropObjList = []

  let tempDefaultSku = null
  const arr = JSON.parse(JSON.stringify(tempSkuList))
  arr.sort((a, b) => a.seckillPrice - b.seckillPrice)
  for (let i = 0; i < tempSkuList.length; i++) {
    let isDefault = false
    if ((!tempDefaultSku && +tempSkuList[i].skuId === +props.skuId)) {
      tempDefaultSku = tempSkuList[i]
      isDefault = true
    }
    if (tempDefaultSku === null && (i === tempSkuList.length - 1)) {
      tempDefaultSku = arr[0]
      isDefault = true
    }
    const properties = tempSkuList[i].properties // 版本:公开版;颜色:金色;内存:64GB
    tempAllProperties.push(properties)
    const propList = properties.split(';') // ["版本:公开版","颜色:金色","内存:64GB"]

    for (let j = 0; j < propList.length; j++) {
      const index = propList[j].indexOf(':')
      const propval = [propList[j].slice(0, index), propList[j].substring(index + 1)] // ["版本","公开版"]
      let props = skuGroup[propval[0]] // 先取出 版本对应的值数组

      // 如果当前是默认选中的sku，把对应的属性值 组装到selectedProp
      if (isDefault) {
        tempPropKeys.push(propval[0])
        tempSelectedPropObj[propval[0]] = propval[1]
        const selectedPropObjItem = {}
        selectedPropObjItem[propval[0]] = propval[1]
        tempSelectedPropObjList.push(selectedPropObjItem)
      }

      if (!props) {
        props = [] // 假设还没有版本，新建个新的空数组
        props.push(propval[1]) // 把 "公开版" 放进空数组
      } else {
        if (props.indexOf(propval[1]) === -1) { // 如果数组里面没有"公开版"
          props.push(propval[1]) // 把 "公开版" 放进数组
        }
      }
      skuGroup[propval[0]] = props // 最后把数据 放回版本对应的值
      const propListItem = {}
      propListItem[propval[0]] = props
      tempSkuGroupList.push(propListItem)
    }
  }
  defaultSku.value = tempDefaultSku || {}
  propKeys.value = tempPropKeys
  selectedPropObj.value = tempSelectedPropObj
  selectedPropObjList.value = tempSelectedPropObjList
  skuGroupList.value = unique(tempSkuGroupList)
  allProperties.value = tempAllProperties
  parseSelectedObjToVals(tempSkuList)
  if (props.activityInfo && props.prodType === 1) {
    setDefaultGroupSku()
  }
}

/**
 * 将已选的 {key:val,key2:val2}转换成 [val,val2]
 */
const selectedPropList = ref([])
const selectedPropShowList = ref([])
let selectedProperties = ''
const parseSelectedObjToVals = (paramSkuList) => {
  const tempSelectedPropObjList = selectedPropObjList.value
  let tempSelectedProperties = ''
  const tempSelectedPropList = []
  const tempSelectedPropShowList = []
  for (let i = 0; i < tempSelectedPropObjList.length; i++) {
    const selectedPropObjItem = tempSelectedPropObjList[i]
    for (const key in selectedPropObjItem) {
      if (Object.hasOwnProperty.call(selectedPropObjItem, key)) {
        tempSelectedPropList.push(key + ':' + selectedPropObjItem[key])
        tempSelectedPropShowList.push(selectedPropObjItem[key])
        tempSelectedProperties += key + ':' + selectedPropObjItem[key] + ';'
      }
    }
  }
  selectedProperties = tempSelectedProperties.substring(0, tempSelectedProperties.length - 1)
  selectedPropList.value = tempSelectedPropList
  selectedPropShowList.value = tempSelectedPropShowList
  selectedPropObjList.value = tempSelectedPropObjList
  let temFindSku = false
  for (let i = 0; i < paramSkuList.length; i++) {
    if (paramSkuList[i].properties === selectedProperties) {
      temFindSku = true
      defaultSku.value = paramSkuList[i]
      break
    }
  }
  if (temFindSku === false) {
    defaultSku.value = {
      pic: setSkuImg() || defaultSku.value.pic
    }
  }
  findSku.value = temFindSku
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

const showSku = () => {
  emit('showSku')
}

/**
 * 判断当前的规格值 是否可以选
 */
const isSkuLineItemNotOptional = (parAllProperties, propObj, key, item, parPropKeys) => {
  const temSelectedPropObj = Object.assign({}, propObj)
  let properties = ''
  temSelectedPropObj[key] = item
  for (let j = 0; j < parPropKeys.length; j++) {
    properties += parPropKeys[j] + ':' + temSelectedPropObj[parPropKeys[j]] + ';'
  }
  properties = properties.substring(0, properties.length - 1)
  for (let i = 0; i < parAllProperties.length; i++) {
    if (properties === parAllProperties[i]) {
      return false
    }
  }
  for (let i = 0; i < parAllProperties.length; i++) {
    if (parAllProperties[i].indexOf(item) >= 0) {
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
  const tempSkuList = props.activityInfo?.activityStatus == 2 && props.activityInfo.seckillId ? props.activityInfo.seckillSkuList : props.skuList
  parseSelectedObjToVals(tempSkuList)
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
const defaultGroupSku = ref({})
const setDefaultGroupSku = () => {
  const groupSkuList = props.activityInfo.groupSkuList || props.activityInfo.seckillSkuList
  if (groupSkuList) {
    if (groupSkuList.length === 1 && !groupSkuList[0].properties) {
      defaultGroupSku.value = groupSkuList[0]
      emit('setSku', defaultGroupSku.value, findSku.value)
      return
    }
    for (let i = 0; i < groupSkuList.length; i++) {
      if (groupSkuList[i].properties === selectedProperties) {
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
  if (!findSku.value || prodNumBiggerThanStock()) {
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
  const defaultSkuStock = +props.pageType === 2 ? defaultSku.value.seckillStocks : defaultSku.value.stocks
  return defaultSku.value.stocks === 0 ? true : (defaultSkuStock === 0 ? true : prodNumSelf.value > defaultSkuStock)
}

/**
 * 输入商品数量
 */
const prodNumInp = () => {
  let temNum = Number(num.value.replace(/[^\d]/g, ''))
  const max = props.prodType === 2 && (defaultSku.value.seckillStocks === 0 || defaultSku.value.seckillStocks) ? defaultSku.value.seckillStocks : defaultSku.value.stocks
  if (temNum > max) {
    temNum = max
  } else if (temNum < 1) {
    temNum = 1
  }
  emit('setProdNum', temNum)
  prodNumSelf.value = props.prodNum
}

const num = ref('')
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
const isPicError = ref(false)
const handlePicError = () => {
  isPicError.value = true
}

/**
 * 图片预览
 */
const onPreviewPic = () => {
  uni.previewImage({
    urls: [util.checkFileUrl(defaultSku.value.pic || props.pic)]
  })
}

</script>

<style lang="scss" scoped>
@use './prod-sku-select.scss';
</style>
