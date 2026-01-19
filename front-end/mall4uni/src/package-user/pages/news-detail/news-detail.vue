<!--
  Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.

  https://www.mall4j.com/

  未经允许，不可做商业用途！

  版权所有，侵权必究！
-->

<template>
  <view class="Mall4j container component-news-detail">
    <view class="news-detail">
      <view
        v-if="news.msgTitle"
        class="news-detail-title"
      >
        {{ news.msgTitle }}
      </view>
      <view
        v-else
        class="news-detail-title"
      >
        {{ news.title }}
      </view>
      <uv-parse
        v-if="news.content"
        :content="news.content"
      />
    </view>
  </view>
</template>

<script setup>
import util from '@/utils/util.js'

const news = ref({
  title: '',
  // 标题
  msgTitle: '',
  // 分销通知标题
  content: '',
  id: 0
})

/**
 * 生命周期函数--监听页面加载
 */
onLoad(async (options) => {
  if (options.type === 'dist') {
    // 加载分销公告
    const params = {
      url: '/p/distribution/msg/info',
      method: 'GET',
      data: {
        msgId: options.id
      }
    }
    const { data } = await http.request(params)
    if (data.content) {
      data.content = util.formatHtml(data.content)
    }
    news.value = data
  } else {
    let path = '/notice/info/'
    if (uni.getStorageSync('cbecB2cToken')) {
      path = '/p/notice/info/'
    }
    // 加载公告详情
    const params = {
      url: path + options.id,
      method: 'GET'
    }
    const { data } = await http.request(params)
    if (data.content) {
      data.content = util.formatHtml(data.content)
    }
    news.value = data
  }
})

/**
 * 生命周期函数--监听页面显示
 */
onShow(() => {
  // 头部导航标题
  uni.setNavigationBarTitle({
    title: $t('latestAnnouncement')
  })
})

/**
 * 用户点击右上角分享 (勿删)
 */
onShareAppMessage(() => {})

</script>
<style lang="scss" scoped>
@use "news-detail";
</style>
