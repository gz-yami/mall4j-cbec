<template>
  <view
    v-if="!isHide"
    class="component-pop-comm"
  >
    <view
      class="modals-cancel"
      @tap="hideModal"
    />
    <view
      id="modals"
      class="modals-content bottom-pos radius"
      :style="{bottom:popBottom+'rpx'}"
      :animation="animationData"
    >
      <!-- 头部标题 -->
      <slot name="header">
        <view
          class="modals-title radius"
          :style="{backgroundColor:tilBg , paddingBottom:padB+'rpx'}"
        >
          <text>{{ title }}</text>
          <view
            class="close-icon"
            @tap="hideModal"
          >
            <image src="@/static/images/icon/close_pop.png" />
          </view>
        </view>
      </slot>
      <!-- 内容区域 -->
      <slot />
      <!-- 底部按钮 -->
      <slot name="footer">
        <view
          v-if="isBtn"
          class="confirm-box"
        >
          <view
            class="confirm-btn"
            @tap="confirm"
          >
            {{ $t('confirm') }}
          </view>
        </view>
      </slot>
    </view>
  </view>
</template>

<script setup>
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: ''
  },
  tilBg: {
    type: String,
    default: '#ffffff'
  },
  padB: {
    type: Number,
    default: 32
  },
  isBtn: {
    type: Boolean,
    default: true
  },
  popBottom: {
    type: Number,
    default: 0
  }
})
const emit = defineEmits(['confirm', 'update:modelValue'])

const animationData = ref({})
const isHide = ref(true)
let fadeInH = 0
let animation = null

watch(() => props.modelValue, (show) => {
  const duration = show ? 500 : 800
  animation = uni.createAnimation({
    duration,
    timingFunction: 'ease'
  })
  if (show) {
    isHide.value = false
    fadeIn()
    nextTick(() => {
      fadeInH <= 0 && getRect()
    })
  } else {
    fadeDown()
    setTimeout(() => {
      isHide.value = true
    }, 500)
  }
})

const getRect = () => {
  let query
  // #ifdef H5 || APP-PLUS
  query = uni.createSelectorQuery().in()
  // #endif


  query.select('#modals').boundingClientRect(data => {
    fadeInH = Math.floor((data && data.height) || 0)
  }).exec()
}

const hideModal = () => {
  emit('update:modelValue', false)
}

// 动画集
const fadeIn = () => {
  animation.translateY(0).step()
  animationData.value = animation.export() // 动画实例的export方法导出动画数据传递给组件的animation属性
}

const fadeDown = () => {
  animation.translateY(fadeInH || 350).step()
  animationData.value = animation.export()
}

const confirm = () => {
  emit('confirm')
}

</script>

<style lang="scss" scoped>
.component-pop-comm {
  position: fixed;
  z-index: 101;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  .modals-cancel {
    position: absolute;
    z-index: 1000;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.5);
  }

  .bottom-pos {
    -webkit-transform: translateY(100%);
    transform: translateY(100%);
  }
  .radius {
    border-radius: 20rpx 20rpx 0 0;
  }
  .modals-content {
    position: absolute;
    z-index: 10001;
    left: 0;
    right: 0;
    background-color: #fff;
  }

  .modals-title {
    font-size: 36rpx;
    padding-top: 32rpx;
    text-align: center;
    position: relative;
    font-weight: bold;
    line-height: 42rpx;
    font-family: 'PingFang SC-Bold, PingFang SC';
    .close-icon{
      width: 44rpx;
      height: 44rpx;
      position: absolute;
      top: 33rpx;
      right: 32rpx;
      image{
        width: 100%;
        height: 100%;
      }
    }
  }

  .confirm-box{
    padding: 12rpx 24rpx;
    .confirm-btn{
      width: 100%;
      height: 75rpx;
      line-height: 75rpx;
      text-align: center;
      border-radius: 75rpx;
      color: #fff;
      font-size: 32rpx;
      font-weight: bold;
      background-color: #F81A1A;
    }
  }
}
</style>
