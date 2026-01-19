<template>
  <view class="component-empty-all-tips">
    <!-- 列表为空 -->
    <view
      v-if="isEmpty"
      class="empty"
    >
      <view
        v-if="!emptyImg"
        class="empty-icon"
      >
        <image src="@/static/images/icon/empty.png" />
      </view>
      <view
        v-else
        class="not-found"
      >
        <image
          :style="imgSty"
          :src="emptyImg"
        />
      </view>
      <view class="empty-text">
        {{ emptyTips || $t('noData') }}
      </view>
    </view>
    <!-- 已加载全部数据 -->
    <view
      v-if="!isEmpty && isAll"
      :class="['load-all',m0?'m0':'']"
    >
      {{ allTips || $t('endTips') }}
    </view>
  </view>
</template>

<script setup>
defineProps({
  isEmpty: {
    type: Boolean,
    default: false
  },
  m0: {
    type: Boolean,
    default: false
  },
  emptyTips: {
    type: String,
    default: ''
  },
  isAll: {
    type: Boolean,
    default: false
  },
  allTips: {
    type: String,
    default: ''
  },
  emptyImg: {
    type: String,
    default: ''
  },
  imgSty: {
    type: Object,
    default: () => ({})
  }
})
</script>

<style lang="scss" scoped>
@use "empty-all-tips";
</style>
