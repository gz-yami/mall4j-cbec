<!--
  Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.

  https://www.mall4j.com/

  未经允许，不可做商业用途！

  版权所有，侵权必究！
-->

<template>
  <view class="Mall4j component-prod-list prod-list">
    <!-- 一行两列 -->
    <view
      v-if="showType === 2"
      class="goods-box"
    >
      <view
        v-for="(showProd, idx) in showProdList"
        :key="idx"
        class="list"
      >
        <view
          v-for="(item, indexItem) in showProd"
          :key="item.prodId"
          class="prod-items"
          @tap="onToProdPage(idx, indexItem)"
        >
          <view class="hot-imagecont">
            <view class="hotsaleimg">
              <img-show :src="item.pic" />
            </view>
          </view>
          <view class="hot-text">
            <view :class="prodNameType === 1 ? 'hotprod-text1' : 'hotprod-text'">
              <view class="activity-box">
                <block v-if="item.activityInProgress && item.prodType != 0">
                  <view class="activity">
                    {{ prodTypeArr[item.prodType] }}
                  </view>
                </block>
              </view>
              <span>{{ item.prodName || '' }}</span>
            </view>
            <view
              v-if="couponShow && item.couponDtos && item.couponDtos.length"
              class="coupon"
            >
              <view
                v-for="coupon in item.couponDtos.slice(0, 3)"
                :key="coupon.couponId"
                class="coupon-item"
              >
                <text
                  v-if="coupon.couponType === 1"
                  class="right"
                >
                  {{ $t('available') }}{{ coupon.cashCondition }}{{ $t('reduce') }}{{ coupon.reduceAmount }}
                </text>
                <text
                  v-else
                  class="right"
                >
                  {{ $t('available') }}
                  {{ useCurrencyStore().defMark }}{{ coupon.cashCondition }}
                  {{ $t('enjoy') }}
                  {{ util.countDiscountRatio(coupon.couponDiscount) }}
                  {{ $t('discount') }}
                </text>
              </view>
            </view>
            <view
              v-if="showBrief && item.brief"
              class="pro-tag"
            >
              {{ item.brief || '' }}
            </view>

            <view class="prod-text-info">
              <view class="price">
                <text class="symbol">
                  {{ useCurrencyStore().defMark }}
                </text>
                <text class="big-num">
                  {{ parsePrice(item.activityPrice && item.activityPrice < item.price ? item.activityPrice : item.price
                  )[0] }}
                </text>
                <text class="symbol">
                  .{{ parsePrice(item.activityPrice && item.activityPrice < item.price ? item.activityPrice : item.price)[1]
                  }}
                </text>
              </view>
              <view
                v-if="item.soldNum || isSoldNumShow"
                class="sold-num"
              >
                {{ payNumFilter(item.soldNum || 0) }} {{ $t('payment') }}
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 一行一列 -->
    <view
      v-else
      class="goods-box-one"
    >
      <view
        v-for="(item, idx) in showProdOneList"
        :key="idx"
        class="list"
        @tap.stop="onToProdPage(idx)"
      >
        <view class="prod-img">
          <img-show :src="item.pic" />
        </view>
        <view class="prod-info">
          <view>
            <view class="title">
              <view class="activity-box">
                <block v-if="item.activityInProgress && item.prodType != 0">
                  <view class="activity">
                    {{ prodTypeArr[item.prodType] }}
                  </view>
                </block>
              </view>
              {{ item.prodName || '' }}
            </view>

            <view
              v-if="item.couponDtos && item.couponDtos.length"
              class="coupon"
            >
              <view
                v-for="coupon in item.couponDtos.slice(0, 4)"
                :key="coupon.couponId"
                class="coupon-item"
              >
                <text
                  v-if="coupon.couponType === 1"
                  class="right"
                >
                  {{ $t('available') }}{{ coupon.cashCondition }}{{ $t('reduce') }}{{ coupon.reduceAmount }}
                </text>
                <text
                  v-else
                  class="right"
                >
                  {{ $t('available') }}
                  {{ useCurrencyStore().defMark }}{{ coupon.cashCondition }}
                  {{ $t('enjoy') }}
                  {{ util.countDiscountRatio(coupon.couponDiscount) }}
                  {{ $t('discount') }}
                </text>
              </view>
            </view>
          </view>

          <view class="pay">
            <view class="left">
              <!-- 价格 -->
              <view class="price">
                {{ useCurrencyStore().defMark }}<text class="big">
                  {{ dealPrice(item.activityPrice && item.activityPrice < item.price ? item.activityPrice : item.price)[0] }}
                </text><text class="small">
                  .{{ dealPrice(item.activityPrice && item.activityPrice < item.price ? item.activityPrice : item.price)[1] }}
                </text>
              </view>
            </view>
            <view class="right">
              {{ payNumFilter(item.soldNum) }} {{ $t('payment') }}
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import util from '@/utils/util'
import { useCurrencyStore } from '@/stores/modules/currency'

const props = defineProps({
  prods: {
    type: Array,
    default: () => {
      return []
    }
  },
  showType: {
    type: Number,
    default: 2 // 1：一行，2：两列
  },
  prodNameType: {
    type: Number,
    default: 2 // 两列商品名称行数 1：一行，2：两行
  },
  showVip: { // 是否显示会员价格
    type: Boolean,
    default: false
  },
  showBrief: { // 是否显示商品卖点
    type: Boolean,
    default: false
  },
  couponShow: {
    type: Boolean,
    default: true
  },
  newData: {
    type: Boolean,
    default: false
  },
  // 销量为0，也显示销量
  isSoldNumShow: {
    type: Boolean,
    default: false
  }
})
const emit = defineEmits(['loadFirstPage'])

const parsePrice = util.parsePrice
const showProdList = reactive([[], []])
const showProdOneList = ref([])
let index = 0

const setIndex = (value) => {
  index = value
}

defineExpose({ setIndex, showProdList })
const prodTypeArr = computed(() => {
  return [
    $t('prodType1'),
    $t('prodType2'),
    $t('prodType3'),
    $t('prodType4')
  ]
})

watch(() => props.prods, (newVal) => {
  delProdList(newVal)
}, {
  immediate: false,
  deep: true
})
onMounted(() => {
  delProdList(props.prods)
})

const onToProdPage = (index, indexItem) => {
  uni.navigateTo({ url: '/package-prod/pages/prod/prod?prodId=' + (props.showType === 2 ? showProdList[index][indexItem].prodId : showProdOneList.value[index].prodId) })
}

const dealPrice = (val) => {
  if (val.toString().indexOf('.') > -1) {
    return val.toString().split('.')
  } else {
    return [val, '00']
  }
}

const delProdList = (data) => {
  let list = JSON.parse(JSON.stringify(data))
  const leftList = []
  const rightList = []
  showProdOneList.value = list
  if (!props.newData) {
    list = list.slice(index, list.length)
  } else {
    index = 0
  }
  list.forEach(item => {
    if (item.couponList) {
      item.couponList = item.couponList.slice(0, 2)
    } else {
      item.couponList = []
    }
    if ((index) % 2 === 0) {
      leftList.push(item)
    } else {
      rightList.push(item)
    }
    index++
  })
  if (props.newData) {
    showProdList[0] = leftList
    showProdList[1] = rightList
    emit('loadFirstPage')
  } else {
    showProdList[0].push(...leftList)
    showProdList[1].push(...rightList)
  }
}
/**
 *  付款人数过滤器
 * @param {*} num 付款人数
 */
const payNumFilter = (num) => {
  if (num <= 500) {
    return num
  } else {
    num = num.toString()
    let a = num[0]
    for (let i = 1; i < num.length; i++) {
      a += '0'
    }
    return a + '+'
  }
}

</script>
<style lang="scss" scoped>
@use "./prod-list.scss";
</style>
