<!--
  Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.

  https://www.mall4j.com/

  未经允许，不可做商业用途！

  版权所有，侵权必究！
-->

<template>
  <view
    class="Mall4j component-prod-items"
    @tap="toProdPage"
  >
    <view class="hot-imagecont">
      <img-show
        :src="item.pic"
        class="hotsaleimg"
      />
    </view>
    <view class="hot-text">
      <view>
        <view class="hotprod-text">
          <view class="activity-box">
            <block v-if="item.activityInProgress && item.prodType != 0">
              <view
                v-if="item.prodType != 1"
                class="activity"
              >
                {{ prodTypeArr[item.prodType] }}
              </view>
              <view
                v-else
                class="activity"
              >
                {{ item.groupActivitySearchVO.groupNumber }}&nbsp;{{ $t('join') }}
              </view>
            </block>
          </view>
          {{ item.prodName }}
        </view>
      </view>
      <view
        v-if="sts==6"
        class="prod-info"
      >
        {{ item.commentNum }}{{ $t('evaluation') }} {{ item.positiveRating }}%{{ $t('praise') }}
      </view>
      <view
        class="prod-text-info"
        :class="{'collection-row':showCancelCollect}"
      >
        <view class="price">
          <text
            v-if="sts==2"
            class="deadline-price"
          >
            {{ $t('timeLimitedPrice') }}
          </text>
          <text class="symbol">
            {{ useCurrencyStore().defMark }}
          </text>
          <text class="big-num">
            {{ wxs.parsePrice(item.price)[0] }}
          </text>
          <text class="small-num">
            .{{ wxs.parsePrice(item.price)[1] }}
          </text>
          <text
            v-if="(item.soldNum || item.soldNum === 0) && isShowSold"
            class="prod-info sold-num"
          >
            {{ $t('sold') }} {{ item.soldNum }}
          </text>
        </view>
        <view
          v-if="showCancelCollect"
          class="collection-btn price small-num"
          @tap.stop="handleCancelCollection"
        >
          {{ $t('delete') }}
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { useCurrencyStore } from '@/stores/modules/currency'

const wxs = number()
const emit = defineEmits(['getCollectionProd'])
const props = defineProps({
  item: {
    type: Object,
    default: () => {}
  },
  sts: {
    type: String,
    default: ''
  },
  showCancelCollect: {
    type: Boolean,
    default: false
  },
  // 是否显示销量
  isShowSold: {
    type: Boolean,
    default: true
  }
})

const prodTypeArr = ref([])

/**
 * 生命周期函数--监听页面加载
 */
onMounted(() => {
  prodTypeArr.value = [
    $t('prodType1'),
    $t('prodType2'),
    $t('prodType3'),
    $t('prodType4')
  ]
})

const toProdPage = () => {
  uni.navigateTo({
    url: '/package-prod/pages/prod/prod?prodId=' + props.item.prodId
  })
}

const handleCancelCollection = () => {
  http.request({
    url: '/p/user/collection/addOrCancel',
    method: 'POST',
    data: props.item.prodId
  })
    .then(({ data }) => {
      uni.showToast({
        title: data ? $t('collectionAdded') : $t('collectionCancelled'),
        duration: 1200,
        icon: 'none'
      })
      emit('getCollectionProd')
    })
}
</script>

<style src="../../app.css" scoped />
<style scoped lang="scss">
  @use './production.scss';
</style>
