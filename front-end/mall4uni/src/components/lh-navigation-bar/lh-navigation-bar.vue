<!--
  Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.

  https://www.mall4j.com/

  未经允许，不可做商业用途！

  版权所有，侵权必究！
-->

<template>
  <view
    class="Mall4 component-lh-navigation-bar bar-sticky"
    :style="{ background: !isBgImg ? navigationBarStyle.background : '', height: isBgImg ? navigationBarStyle.picHeight + 'px' : '88px'}"
  >
    <!-- 背景图片 -->
    <image
      v-if="isBgImgShow || isBgImg"
      :style="[
        {bottom: navigationBarStyle.bottom || 0},
        {top: navigationBarStyle.top || 0},
        {height: navigationBarStyle.picHeight + 'px'}
      ]"
      class="top-bg-img"
      :src="bgImgSrc || navigationBarStyle.backgroundImage"
      :mode="bgImgMode"
    />
    <!-- 内容 -->
    <view class="content-wrap">
      <view
        class="status-line"
        :style="{
          height: lineHeight,
        }"
      />
      <view class="bar-line">
        <view class="bar-line container-in">
          <view
            v-if="!custom"
            class="bar-font bar-content"
            :class="{ 'left-text': isLeft }"
          >
            <image
              v-if="showBack && isWhiteBack"
              class="back-img"
              src="../../static/images/icon/back-white.png"
              mode=""
              @click="onTurnPage('1', 'navigateBack')"
            />
            <image
              v-if="showBack && !isWhiteBack"
              class="back-img"
              src="../../static/images/icon/back.png"
              mode=""
              @click="onTurnPage('1', 'navigateBack')"
            />
            <view
              v-if="showTitle"
              class="bar-title"
              :style="{ color: navigationBarStyle.fontColor || normal.fontColor }"
            >
              {{ title }}
            </view>
          </view>
          <view
            v-else
            class="bar-font container-in"
          >
            <slot />
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
const props = defineProps({
  // 自定义头部，否则沿用原生的头部样式
  custom: {
    type: Boolean,
    default: false
  },
  // 原生头部自定义样式
  navigationBarStyle: {
    type: Object,
    default: function () {
      return {
        top: '0',
        bottom: '',
        background: '#F81A1A',
        fontColor: '#000000',
        iconColor: '#000000',
        picHeight: '88px'
      }
    }
  },
  // 是否显示回退图标，默认显示
  showBack: {
    type: Boolean,
    default: false
  },
  isBgImg: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: ''
  },
  showTitle: {
    type: Boolean,
    default: true
  },
  url: {
    type: String,
    default: ''
  },
  isLeft: {
    type: Boolean,
    default: false
  },
  isWhiteBack: {
    type: Boolean,
    default: false
  },
  // 是否使用背景图片
  isBgImgShow: {
    type: Boolean,
    default: false
  },
  // 背景图路径
  bgImgSrc: {
    type: String,
    default: ''
  },
  // 背景图图片组件的mode
  bgImgMode: {
    type: String,
    default: 'widthFix'
  }
})

const normal = reactive({
  backgroundImage: '',
  background: '#F81A1A',
  fontColor: '#000000',
  iconColor: '#000000'
}) // 公用样式,个性化样式可通过传值实现
const lineHeight = ref('') // 状态栏高度
const $system = inject('$system')
onMounted(() => {
  getApp()
  lineHeight.value = $system.ktxStatusHeight + 'rpx'
})

const onTurnPage = () => {
  if (props.url) {
    uni.navigateTo({
      url: props.url
    })
  } else {
    uni.navigateBack(-1)
  }
}

</script>

<style lang="scss" scoped>
/* 导航栏吸顶效果 */
.component-lh-navigation-bar.bar-sticky {
  position: sticky;
  position: -webkit-sticky;
  top: 0;
  z-index: 101;
  background-repeat: no-repeat;
  background-size: cover;
  -moz-background-size: 100% 100%;
  overflow: hidden;
  .top-bg-img {
    top: 0;
    width: 100%;
    overflow: hidden;
    position: absolute;
  }
  .content-wrap {
    width: 100%;
    height: 100%;
    position: relative;
    .bar-line {
      height: 44px;
      /*正中*/
      .bar-content {
        width: 100%;
        display: flex;
        /* flex-direction: column; */
        align-items: center;
        justify-content: center;
      }
      /* 导航栏高度 */
      .bar-title {
        max-width: 80%;
        font-size: 30rpx;
        /* font-weight: bold; */
        line-height: 44rpx;
        text-overflow: ellipsis;
        overflow: hidden;
        line-break: anywhere;
        white-space: nowrap;
      }
      .left-text {
        justify-content: flex-start;
        margin-left: 30rpx;
      }
      .back-img {
        width: 34rpx;
        height: 34rpx;
        position: absolute;
        left: 30rpx;
      }
    }
    /*垂直居中*/
    .container-in {
      display: flex;
      -webkit-align-items: center;
      align-items: center;
      width: 100%;
      height: 44px;
    }
  }
}
</style>
