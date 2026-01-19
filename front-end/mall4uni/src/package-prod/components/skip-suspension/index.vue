<!--
  Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.

  https://www.mall4j.com/

  未经允许，不可做商业用途！

  版权所有，侵权必究！
-->

<template>
  <view class="Mall4j component-skip-suspension">
    <!-- 悬浮按钮 -->
    <view
      class="suspension-def"
      @tap="tabShowHandler()"
    >
      <image src="@/static/images/icon/suspension_icon_right.png" />
    </view>
    <!-- 弹框 -->
    <view
      v-if="tabData.isTabShow"
      :class="['bullet-frame',tabData.fixed?'bullet-frame-fixed':'']"
    >
      <view
        class="item-wrap"
        @tap="toPage('/pages/index/index')"
      >
        <image src="@/package-prod/static/images/icon/icon_home_skip.png" />
        <text>{{ $t('homepage') }}</text>
      </view>
      <view
        class="item-wrap"
        @tap="toPage('/pages/category/category')"
      >
        <image src="@/package-prod/static/images/icon/icon_classify_skip.png" />
        <text>{{ $t('categorySearch') }}</text>
      </view>
      <view
        class="item-wrap"
        @tap="toPage('/pages/user/user')"
      >
        <image src="@/package-prod/static/images/icon/icon_center_skip.png" />
        <text>{{ $t('personalCenter') }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>

const emit = defineEmits(['tabData-update'])

const props = defineProps({
  tabData: {
    type: Object,
    default: null
  }
})

const setTabData = computed({
  get: () => props.tabData,
  set: (val) => emit('tabData-update', val)
})

/**
 * 跳转页面
 */
const toPage = (url) => {
  if (!url) return
  uni.switchTab({
    url
  })
}
/**
 * 弹框显隐
 */
const tabShowHandler = () => {
  setTabData.value.isTabShow = !(props.tabData.isTabShow)
  setTabData.value.fixed = false
}

</script>

<style lang="scss" scoped>
.component-skip-suspension {
  // 悬浮按钮
  .suspension-def {
    position: absolute;
    top: 32rpx;
    right: 32rpx;
    image {
      width: 56rpx;
      height: 56rpx;
    }
  }

  // 弹框
  .bullet-frame {
    margin-top: 104rpx;
    margin-right: 32rpx;
    border-radius: 6rpx;
    padding-left: 24rpx;
    background: rgba(0,0,0,0.6);
    position: relative;
    &::before {
      content: '';
      width: 0;
      height: 0;
      display: block;
      border: 12rpx solid rgba(0,0,0,0.6);
      border-top: 12rpx solid transparent;
      border-left: 12rpx solid transparent;
      border-right: 12rpx solid transparent;
      position: absolute;
      top: -24rpx;
      right: 14rpx;
    }
    .item-wrap {
      display: flex;
      display: -webkit-flex;
      align-items: center;
      &:nth-child(2) > text {
        border-top: 1px solid rgba(255,255,255,0.2);
        border-bottom: 1px solid rgba(255,255,255,0.2);
      }
      image {
        width: 32rpx;
        height: 32rpx;
        margin-right: 8rpx;
      }
      text {
        padding-right: 24rpx;
        font-size: 28rpx;
        height: 76rpx;
        line-height: 76rpx;
        color: #FFF;
        display: inline-block;
        font-weight: 400;
      }
    }
  }

  .bullet-frame-fixed{
    top: 112rpx;
    right: 24rpx;
    position: fixed;
    margin: 0;
    background: rgba(0,0,0,0.6);
    z-index: 99;
  }
}
</style>
