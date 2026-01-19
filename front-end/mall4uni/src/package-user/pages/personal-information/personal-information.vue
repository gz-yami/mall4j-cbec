<!-- 个人资料 -->
<!-- images/personal-information/personal-information.wxml -->
<!--
  Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.

  https://www.mall4j.com/

  未经允许，不可做商业用途！

  版权所有，侵权必究！
-->

<template>
  <view class="Mall4j page-personal-information">
    <view class="item-wrap">
      <view
        class="cloumn-item"
        @tap="getUploadImg"
      >
        <view class="txt-wrap">
          {{ $t('avatar') }}
        </view>
        <view class="infor-right-wrap">
          <image
            class="avatar-picture"
            :src="photoFiles ? photoFiles : (pic ? pic : head04Png)"
            mode="aspectFill"
          />
          <image
            class="right-img"
            src="@/static/images/icon/arrow-right.png"
            mode="scaleToFill"
          />
        </view>
      </view>
    </view>
    <view class="item-wrap">
      <view
        v-if="username"
        class="cloumn-item"
      >
        <view class="txt-wrap">
          {{ $t('userName') }}
        </view>
        <view class="right-wrap">
          <input
            type="text"
            class="txt-infor"
            :value="username"
            disabled
          >
        </view>
      </view>
      <view
        class="cloumn-item"
        @tap="none"
      >
        <view class="txt-wrap">
          {{ $t('nickname') }}
        </view>
        <view class="right-wrap">
          <input
            type="text"
            class="txt-infor nickname-input"
            :value="nickName"
            :placeholder="$t('maximumCharacters')"
            maxlength="15"
            @input="getNickNameInt"
          >
        </view>
      </view>
      <view
        class="cloumn-item"
        @tap="translate"
      >
        <view class="txt-wrap">
          {{ $t('gender') }}
        </view>
        <view class="right-wrap">
          <view
            v-if="sex===1 || sex===0"
            class="txt-infor"
          >
            {{ genderArray[sex] }}
          </view>
          <view
            v-else
            class="sex-defaul-value"
          >
            {{ $t('pleaseChoose') }}
          </view>
          <image
            class="right-img"
            src="@/static/images/icon/arrow-right.png"
            mode="scaleToFill"
          />
        </view>
      </view>
      <picker
        class="birthday-picker birthday-wrap"
        mode="date"
        :value="date"
        fields="day"
        :end="endDate"
        @change="bindDateChange"
      >
        <view class="txt-wrap">
          {{ $t('birthday') }}
        </view>
        <view class="right-wrap">
          <view
            v-if="date"
            class="choose-value"
          >
            {{ date }}
          </view>
          <view
            v-else
            class="choose-value"
            style="color: #5B5B5B;"
          >
            {{ $t('pleaseChoose') }}
          </view>
          <image
            class="right-img"
            src="@/static/images/icon/arrow-right.png"
            mode="scaleToFill"
          />
        </view>
      </picker>
    </view>
    <!-- 确认按钮 -->
    <view
      v-if="isSexChange || isChange"
      class="confirm"
      @tap="setUserInfo"
    >
      {{
        $t('confirm')
      }}
    </view>
    <!-- 确认按钮 end -->

    <!-- 性别选择 弹框 -->
    <view
      v-if="showPicker"
      class="animation-element-wrapper"
      :animation="animation"
      :style="'visibility:' + (show ? 'visible' : 'hidden')"
      @tap.stop="hiddenFloatView"
    >
      <view
        class="animation-element"
        @tap.stop="nono"
      >
        <text
          class="right-bt"
          @tap.stop="hiddenFloatView"
        >
          {{
            $t('confirm')
          }}
        </text>
        <view class="line" />
        <picker-view
          indicator-style="height: 50px;"
          @change="bindChange"
          @tap.stop="nono"
        >
          <picker-view-column>
            <view
              v-for="(item, index) in genderArray"
              :key="index"
              style="line-height:50px;"
            >
              <span>{{ item }}</span>
            </view>
          </picker-view-column>
        </picker-view>
      </view>
    </view>
    <!-- 性别选择 end -->
    <helang-compress ref="hCompressRef" />
  </view>
</template>

<script setup>
import head04Png from '@/static/images/icon/head04.png'
import util from '@/utils/util'

const endDate = computed(() => {
  return getDate()
})

// 不能直接这样的，要读取后保存进来
const genderArray = ref([])
onLoad(() => {
  // 加载会员信息
  genderArray.value.push($t('male'))
  genderArray.value.push($t('female'))
  queryUserInfo()
})

onShow(() => {
  // 头部导航标题
  uni.setNavigationBarTitle({
    title: $t('personalInfor')
  })
})

const showPicker = ref(false)
onHide(() => {
  showPicker.value = false
})

const animation = ref('')
onReady(() => {
  // #ifdef H5 || APP-PLUS
  animation.value = uni.createAnimation({
    transformOrigin: '50% 50%',
    duration: 0,
    timingFunction: 'ease',
    delay: 0
  })
  // #endif
  animation.value.translateY(200 + 'vh').step()

  animation.value = animation.value.export()
  show.value = false
})

/**
 * 用户点击右上角分享 (勿删)
 */
onShareAppMessage(() => {})

/**
 * none
 */
const none = () => {}

const getDate = () => {
  const date = new Date()
  const year = date.getFullYear()
  let month = date.getMonth() + 1
  let day = date.getDate()
  month = month > 9 ? month : '0' + month
  day = day > 9 ? day : '0' + day
  return `${year}-${month}-${day}`
}

const photoFiles = ref('')
const isChange = ref(false) // 是否修改用户信息
const hCompressRef = ref(null)
/**
 * 上传图片
 */
const getUploadImg = () => {
  const count = 1
  util.chooseImage({ compress: hCompressRef.value.compress, count }).then((data) => {
    photoFiles.value = util.checkFileUrl(data[0].filePath)
    isChange.value = true
  })
}

const date = ref('') // 用户生日
const nickName = ref('') // 用户昵称
const sex = ref('') // 性别
const oldSex = ref('') // 原先性别
const pic = ref('')
const username = ref('')
// 获取用户信息
const queryUserInfo = () => {
  const params = {
    url: '/p/user/userInfo',
    method: 'GET',
    data: {}
  }
  http.request(params).then((res) => {
    date.value = res.data.birthDate// 用户生日
    nickName.value = res.data.nickName// 用户昵称
    if (res.data.sex === '1' || res.data.sex === '0') {
      sex.value = Number(res.data.sex)
      oldSex.value = Number(res.data.sex) // 原本的性别
    }
    pic.value = res.data.pic
    username.value = res.data.username
  })
}

let t = 0
let moveY = 200
const show = ref('')
// 移动按钮点击事件
const translate = () => {
  if (t === 0) {
    moveY = 0
    show.value = false
    t = 1
  } else {
    moveY = 200
    show.value = true
    t = 0
  }
  show.value = true
  showPicker.value = true
  animationEvents(moveY)
}

const sexNumber = ref(0) // 判断有无滑动
const isSexChange = ref(false) // 是否修改性别
// 隐藏弹窗浮层
const hiddenFloatView = () => {
  if (!sexNumber.value) {
    sex.value = 0
    if (sex.value !== oldSex.value) {
      isSexChange.value = true
    }
  }
  moveY = 200
  show.value = true
  t = 0
  animationEvents(moveY)
}

// 动画事件
const animationEvents = (moveY) => {
  // #ifdef H5 || APP-PLUS
  animation.value = uni.createAnimation({
    transformOrigin: '50% 50%',
    duration: 400,
    timingFunction: 'ease',
    delay: 0
  })
  // #endif
  animation.value.translateY(moveY + 'vh').step()

  animation.value = animation.value.export()
}

// 用户昵称
const getNickNameInt = (e) => {
  nickName.value = e.detail.value
  isChange.value = true
}

// 选择性别
const bindChange = (e) => {
  sex.value = e.detail.value[0]
  sexNumber.value = (sexNumber.value += 1)
  isSexChange.value = sex.value !== oldSex.value
}

// 选择日期
const bindDateChange = (e) => {
  date.value = e.detail.value
  isChange.value = true
}

/**
 * 设置用户信息
 */
const setUserInfo = () => {
  if (!nickName.value || !nickName.value.trim()) {
    uni.showToast({
      title: $t('nicknameEmptyTips'),
      icon: 'none'
    })
    return
  }

  if (isChange.value || isSexChange.value) {
    const params = {
      url: '/p/user/setUserInfo',
      method: 'PUT',
      data: {
        avatarUrl: photoFiles.value,
        birthDate: date.value,
        sex: sex.value,
        nickName: nickName.value
      }
    }
    http.request(params).then(() => {
      uni.showToast({
        title: $t('successfullyModified'),
        icon: 'none',
        duration: 1000
      })
      setTimeout(() => {
        queryUserInfo()
        uni.switchTab({
          url: '/pages/user/user'
        })
      }, 1000)
    })
  }
}

const nono = () => {}

/**
 * 页面上拉触底事件的处理函数
 */
onShareAppMessage(() => {
  return {}
})

</script>
<style lang="scss" scoped>
@use "personal-information";
</style>
