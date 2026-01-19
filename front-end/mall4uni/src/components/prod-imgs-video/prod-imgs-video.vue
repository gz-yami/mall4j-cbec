<!--
  Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.

  https://www.mall4j.com/

  未经允许，不可做商业用途！

  版权所有，侵权必究！
-->

<template>
  <!-- 轮播图 & 商品视频-->
  <view class="Mall4j component-prod-imgs-video">
    <view
      v-if="video && isPlaying"
      class="video-container"
    >
      <video
        id="myVideo"
        :autoplay="true"
        enable-progress-gesture="false"
        :src="util.checkFileUrl(video)"
        controls
        @error="onErrorVideo"
        @ended="playEnd"
      />
    </view>
    <view
      v-if="video&&errVideo"
      class="play-btn"
      @tap="videoOper"
    >
      <image
        v-if="!isPlaying"
        class="play-icon"
        src="/static/images/icon/play-red.png"
      />
      <text
        v-if="isPlaying"
        :class="'play-text ' + (isPlaying ? 'video-stop' : 'video-play')"
      >
        {{ isPlaying ? $t('quitPlaying') : "" }}
      </text>
    </view>
    <swiper
      v-if="!isPlaying"
      :circular="true"
      :indicator-dots="indicatorDots"
      :autoplay="video||imgs"
      :indicator-color="indicatorColor"
      :interval="interval"
      :current="currentPicIndex"
      :duration="duration"
      :indicator-active-color="indicatorActiveColor"
    >
      <block v-if="imgsList.length">
        <block
          v-for="(item, imgIndex) in imgsList"
          :key="imgIndex"
        >
          <swiper-item>
            <img-show
              :src="item"
              @handle-tap="()=>onPreviewImg(imgIndex)"
            />
          </swiper-item>
        </block>
      </block>
    </swiper>
  </view>
  <!-- 轮播图 & 商品视频end -->
</template>

<script setup>
import { watch } from 'vue'
import util from '@/utils/util.js'
const emit = defineEmits(['videoSts', 'videoSts'])
const props = defineProps({
  imgs: {
    type: String,
    default: ''
  },
  video: {
    type: String,
    default: ''
  }
})

const indicatorDots = ref(true)
const indicatorColor = ref('#f2f2f2')
const indicatorActiveColor = ref('#eb2444')
const interval = ref(3000)
const duration = ref(1000)
const isPlaying = ref(false)
// 当前显示商品图像的索引
const currentPicIndex = ref(0)
let videoContext = ''
const imgsList = ref([])

watch(() => props.imgs, (val) => {
  imgsList.value = val ? val.split(',').map(item => util.checkFileUrl(item)) : ['']
}, { immediate: true })

/**
 * 视频的方法
 */
const videoOper = () => {
  if (isPlaying.value) {
    stopPlay()
  } else {
    startPlay()
  }
}
/**
 * 播放结束
 */
const playEnd = () => {
  stopPlay()
}

/**
 * 开始播放
 */
const startPlay = () => {
  isPlaying.value = true
  emit('videoSts', isPlaying.value)

  if (!videoContext) {
    videoContext = uni.createVideoContext('myVideo')
  }
  setTimeout(() => {
    videoContext.seek(0)
    videoContext.play()
  }, 200)
}

/**
 * 停止播放
 */
const stopPlay = () => {
  isPlaying.value = false
  emit('videoSts', isPlaying.value)
  setTimeout(() => {
    videoContext.seek(0)
    videoContext.stop()
  }, 200)
}
const errVideo = ref(true)
const onErrorVideo = () => {
  errVideo.value = false
}

let isShowPreview = false
const onPreviewImg = (current) => {
  uni.previewImage({
    current,
    urls: imgsList.value,
    success: () => {
      isShowPreview = true
    }
  })
}
onUnmounted(() => {
  if (isShowPreview) {
    uni.closePreviewImage({
      success: () => {
        isShowPreview = false
      }
    })
  }
})

defineExpose({
  stopPlay
})
</script>

<style lang="scss" scoped>
.component-prod-imgs-video {
  position: relative;

  swiper {
    height: 750rpx;
    width: 100%;
  }

  swiper image {
    height: 750rpx;
    width: 100%;
  }

  .video-container {
    position: relative;
    display: flex;
    flex-direction: column;
    width: 100%;
    height: 750rpx;
    justify-content: center;
    align-items: center;
    background: #000;
    z-index: 10;
  }

  .video-container video {
    display: block;
    width: 100%;
    height: 100%;
  }

  .play-btn {
    position: absolute;
    left: 50%;
    bottom: 12%;
    padding: 2rpx;
    background: rgba(255, 255, 255, 0.75);
    border-radius: 50rpx;
    color: #000;
    font-size: 24rpx;
    text-align: center;
    transform: translateX(-50%);
    display: flex;
    justify-content: space-between;
    align-items: center;
    z-index: 15;
  }

  .play-icon {
    width: 50rpx;
    height: 50rpx;
  }

  .play-text {
    padding-right: 10rpx;
    margin: 0 10rpx;
  }

  .video-stop {
    padding: 2rpx 8rpx;
  }
}
</style>
