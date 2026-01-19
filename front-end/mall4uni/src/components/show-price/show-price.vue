<!-- 价格显示组件--主要用于统一价格样式，控制换算价格显隐等功能 -->
<template>
  <view
    class="component-show-price"
    :class="[customClass]"
    :style="{margin: isAlignCenter ? '0 auto' : ''}"
  >
    <view
      class="price"
      :style="{color: color, display: isBySide ? 'flex' : 'block'}"
      :class="{'delete-line': isDeleteLine}"
    >
      <!-- 如果没有金额，只设置积分的情况下，不显示金额 -->
      <template v-if="!(!price && score)">
        <!-- 保持符号小于等于整数部分 -->
        <span
          :style="{fontSize: (markFontSize > bigFontSize ? bigFontSize : markFontSize)}"
        >
          {{ mark }}
        </span>

        <!-- 2-不填充小数样式 -->
        <template v-if="type === 2">
          <span
            class="big"
            :style="{fontSize: bigFontSize}"
          >{{ price }}</span>
        </template>

        <!-- 1-大小样式（默认填充小数.00，小数比整数矮） -->
        <template v-else-if="type === 1">
          <span
            class="big"
            :style="{fontSize: bigFontSize}"
          >{{ showPrice[0] }}</span>
          <span
            class="small"
            :style="{fontSize: smallFontSize}"
          >.{{ showPrice[1] }}</span>
        </template>

        <!-- 0-正常样式（默认填充小数.00） -->
        <template v-else>
          <span
            class="big"
            :style="{fontSize: bigFontSize}"
          >{{ showPrice[0] }}</span>
          <span
            class="small"
            :style="{fontSize: smallFontSize}"
          >.{{ showPrice[1] }}</span>
        </template>
      </template>

      <span
        v-if="score"
        class="big"
        :style="{fontSize: bigFontSize}"
      >
        <span v-if="price">
          +
        </span>
        {{ score }}{{ $t('integral') }}
      </span>
      <view
        v-if="price && ratePrice !== '' && isShowRatePrice"
        class="rate-price"
        :style="{fontSize: rateFontSize}"
      >
        (
        {{ ratePrice }}
        <span v-if="score">
          <span v-if="price">
            +
          </span>
          {{ score }}{{ $t('integral') }}
        </span>
        )
      </view>
    </view>
  </view>
</template>

<script setup>
import { useCurrencyStore } from '@/stores/modules/currency'

const props = defineProps({
  // 此处价格只做样式上的显示处理，传参前请确保价格已经处理过了
  price: {
    type: Number || String,
    default: 0
  },
  score: {
    type: Number || String,
    default: 0
  },
  /**
   * 价格显示类型：
   * 0-正常样式（默认填充小数.00）
   * 1-大小样式（默认填充小数.00，小数比整数矮）
   * 2-不填充小数样式
   */
  type: {
    type: Number,
    default: 0
  },
  // 组件自定义样式类
  customClass: {
    type: String,
    default: ''
  },
  color: {
    type: String,
    default: '#f81a1a'
  },
  // 是否加中划线（删除线）
  isDeleteLine: {
    type: Boolean,
    default: false
  },
  // 是否显示换算后的价格
  isShowRatePrice: {
    type: Boolean,
    default: true
  },
  // 标识符大小
  markFontSize: {
    type: String,
    default: '32rpx'
  },
  // 整数部分大小
  bigFontSize: {
    type: String,
    default: '32rpx'
  },
  // 小数部分大小
  smallFontSize: {
    type: String,
    default: '24rpx'
  },
  // 换算价格大小
  rateFontSize: {
    type: String,
    default: '24rpx'
  },
  isAlignCenter: {
    type: Boolean,
    default: true
  },
  // 原价与换算价是否并排显示
  isBySide: {
    type: Boolean,
    default: true
  }
})

const currencyStore = useCurrencyStore()
const mark = computed(() => {
  return currencyStore.defMark
})

const showPrice = computed(() => {
  return parsePrice(props.price)
})

// 获取汇率转换后的价格
const ratePrice = computed(() => {
  const exchangeRate = currencyStore.exchangeRate
  let price = (props.price * exchangeRate).toFixed(2)
  if (currencyStore.currency === currencyStore.defCurrency) {
    return ''
  } else {
    if (price === '0.00') price = 0.01
    return currencyStore.mark + price
  }
})

const parsePrice = (val) => {
  let num = Number(val)
  if (!num) {
    num = 0
  }
  // 截取小数点后两位，并以小数点为切割点将val转化为数组
  return num.toFixed(2).split('.')
}

</script>

<style lang="scss" scoped>
.component-show-price {
  display: inline-block;
  text-align: center;

  // 价格
  .price {
    align-items: baseline;
    font-family: PingFang SC-Bold, PingFang SC;
  }

  .rate-price {
    padding: 0 5rpx;
  }

  .delete-line {
    text-decoration: line-through;
  }
}
</style>
