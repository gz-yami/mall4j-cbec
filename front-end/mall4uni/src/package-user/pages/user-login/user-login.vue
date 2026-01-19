<!--
  Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.

  https://www.mall4j.com/

  未经允许，不可做商业用途！

  版权所有，侵权必究！
-->

<template>
  <view class="Mall4j page-user-login con">
    <view class="logo">
      <view class="logo-con">
        <image
          :src="util.checkFileUrl(uniWebConfig.uniLoginLogoImg)"
          mode="heightFix"
          @tap="onToIndex"
        />
      </view>
    </view>
    <!-- 账号登录 -->
    <view
      v-if="!isForgetPassword && loginStatus === 0"
      class="login-form"
    >
      <!-- 账号 -->
      <view :class="['item', errorTips === 1 ? 'error' : '']">
        <view class="account ellipsis">
          <input
            v-model="principal"
            data-type="account"
            placeholder-class="inp-palcehoder"
            :placeholder="$t('enterEmail')"
            @blur="onPrincipalInput"
            @confirm="onLogin"
          >
        </view>
        <view
          v-if="errorTips === 1"
          class="error-text"
        >
          <text class="warning-icon">
            !
          </text>
          {{ $t('emailWarn') }}
        </view>
      </view>
      <!-- 密码 -->
      <view :class="['item', errorTips === 2 ? 'error' : '']">
        <view class="account">
          <input
            v-model="credentials"
            type="text"
            :password="passwordType"
            data-type="password"
            placeholder-class="inp-palcehoder"
            :placeholder="$t('enterPassword')"
            @blur="onPrincipalInput"
            @confirm="onLogin"
          >
          <div class="image-box">
            <image
              :src="passwordImage"
              style="width:26rpx;height:18rpx"
              mode="heightFix"
              @tap="onShowPassword"
            />
          </div>
        </view>
        <view
          v-if="errorTips === 2"
          class="error-text"
        >
          <text class="warning-icon">
            !
          </text>
          {{ $t('enterPassword') }}
        </view>
      </view>
      <!-- 按钮 -->
      <view>
        <button
          class="authorized-btn"
          @tap="onLogin"
        >
          {{ $t('login') }}
        </button>
      </view>
      <!-- 服务条款 -->
      <view
        v-if="serviceTermsSwitch || privacyPolicySwitch"
        class="item statement"
      >
        <label
          class="statement-label"
          @tap.stop="onHandlePrivacyClick"
        >
          <checkbox
            class="check-box"
            :checked="isPrivacy === 1"
            color="#fff"
            @tap.stop="onHandlePrivacyClick"
          />
          <view style="color: #999999">
            {{ $t('loginAgree') }}
            <text
              v-if="serviceTermsSwitch"
              @tap.stop="onToTermsOfService('serviceTerms')"
            >《{{ $t('termsOfService') }}》
            </text>
            <text
              v-if="privacyPolicySwitch && serviceTermsSwitch"
              style="color: #999999"
            >{{ $t('and') }}
            </text>
            <text
              v-if="privacyPolicySwitch"
              @tap.stop="onToTermsOfService('servicePolicy')"
            >《{{ $t('privacyPolicy') }}》
            </text>
          </view>
        </label>
      </view>
    </view>
    <!-- 邮箱注册 -->
    <view
      v-if="!isForgetPassword && loginStatus === 1"
      class="login-form"
    >
      <!-- 邮箱 -->
      <view :class="['item', errorTips ? 'error' : '']">
        <view
          class="account"
          :style="{justifyContent:'unset',background: errorTips === 1 ? '#FBEFEF' : '#fff'}"
        >
          <input
            v-model="principal"
            data-type="account"
            placeholder-class="inp-palcehoder"
            :placeholder="$t('enterEmail')"
            @blur="onPrincipalInput"
            @confirm="onRegister"
          >
        </view>
        <view
          v-if="errorTips === 1"
          class="error-text"
        >
          <text class="warning-icon">
            !
          </text>
          {{ $t('emailWarn') }}
        </view>
        <view
          v-if="!principal && errorTips === 8"
          class="error-text"
        >
          <text class="warning-icon">
            !
          </text>
          {{ $t('enterEmail') }}
        </view>
      </view>
      <!-- 密码 -->
      <view :class="['item', errorTips ? 'error' : '']">
        <view class="account">
          <input
            v-model="credentials"
            type="text"
            :password="passwordType"
            data-type="password"
            placeholder-class="inp-palcehoder"
            :placeholder="$t('enterPassword')"
            @blur="onPrincipalInput"
            @confirm="onRegister"
          >
          <div class="image-box">
            <image
              :src="passwordImage"
              style="width:26rpx;height:18rpx"
              mode="heightFix"
              @tap="onShowPassword"
            />
          </div>
        </view>
        <view
          v-if="errorTips === 2"
          class="error-text"
        >
          <text class="warning-icon">
            !
          </text>
          {{ $t('enterPassword') }}
        </view>
        <view
          v-if="errorTips === 9"
          class="error-text"
        >
          <text class="warning-icon">
            !
          </text>
          {{ $t('passwordVerification') }}
        </view>
      </view>
      <!-- 按钮 -->
      <view>
        <button
          class="authorized-btn"
          @tap="onRegister"
        >
          {{ $t('register') }}
        </button>
      </view>
      <!-- 服务条款 -->
      <view
        v-if="serviceTermsSwitch || privacyPolicySwitch"
        class="item statement"
      >
        <label
          class="statement-label"
          @tap.stop="onHandlePrivacyClick"
        >
          <checkbox
            class="check-box"
            color="#fff"
            :checked="isPrivacy === 1"
            @tap.stop="onHandlePrivacyClick"
          />
          <view style="color: #999999">
            {{ $t('regAgree') }}
            <text
              v-if="serviceTermsSwitch"
              @tap.stop="onToTermsOfService('serviceTerms')"
            >《{{ $t('termsOfService') }}》
            </text>
            <text
              v-if="privacyPolicySwitch && serviceTermsSwitch"
              style="color: #999999"
            >{{ $t('and') }}
            </text>
            <text
              v-if="privacyPolicySwitch"
              @tap.stop="onToTermsOfService('servicePolicy')"
            >《{{ $t('privacyPolicy') }}》
            </text>
          </view>
        </label>
      </view>
    </view>
    <!-- 修改密码 -->
    <view
      v-if="isForgetPassword"
      class="login-form"
    >
      <!-- 邮箱 -->
      <view
        v-if="!nextBtn"
        :class="['item', errorTips ? 'error' : '']"
      >
        <view class="account">
          <input
            v-if="!isPersonalCenter"
            v-model="principal"
            placeholder-class="inp-palcehoder"
            :placeholder="$t('enterEmail')"
            @confirm="onNextStep"
          >
          <view v-else>
            {{ privacyNumber }}
          </view>
        </view>
        <view
          v-if="errorTips === 1"
          class="error-text"
        >
          <text class="warning-icon">
            !
          </text>
          {{ $t('emailWarn') }}
        </view>
        <view
          v-if="!principal && errorTips === 8"
          class="error-text"
        >
          <text class="warning-icon">
            !
          </text>
          {{ $t('enterEmail') }}
        </view>
      </view>
      <!-- 验证码 -->
      <view
        v-if="!nextBtn"
        :class="['item', errorTips ? 'error' : '']"
      >
        <view class="account">
          <input
            v-model="validCode"
            type="text"
            class="int-yzm"
            maxlength="6"
            placeholder-class="inp-palcehoder"
            :placeholder="$t('enterCode')"
            @confirm="onNextStep"
          >
          <text
            v-if="show"
            class="login-code"
            @tap="onGetCode"
          >
            {{
              $t('getCode')
            }}
          </text>
          <text
            v-if="!show"
            class="input-btn"
          >
            {{ count }} s
          </text>
        </view>
        <view
          v-if="errorTips === 3"
          class="error-text"
        >
          <text class="warning-icon">
            !
          </text>
          {{ $t('enterCode') }}
        </view>
        <view
          v-if="errorTips === 6"
          class="error-text"
        >
          <text class="warning-icon">
            !
          </text>
          {{ $t('enterCodeFirst') }}
        </view>
      </view>
      <!-- 新密码 -->
      <view
        v-if="nextBtn"
        :class="['item', errorTips ? 'error' : '']"
      >
        <view class="account">
          <input
            type="text"
            :password="newPasswordType"
            data-type="0"
            :value="newPassWord"
            placeholder-class="inp-palcehoder"
            maxlength="20"
            :placeholder="$t('enterNew')"
            @input="onGetPassInputVal"
            @confirm="onChangePassWord"
          >
          <div class="image-box">
            <image
              :src="newPasswordImage"
              style="width:26rpx;height:18rpx"
              mode="heightFix"
              @tap="onShowNewPassword"
            />
          </div>
        </view>
        <view
          v-if="errorTips === 4"
          class="error-text"
        >
          <text class="warning-icon">
            !
          </text>
          {{ $t('enterNew') }}
        </view>
        <view
          v-if="errorTips === 9"
          class="error-text"
        >
          <text class="warning-icon">
            !
          </text>
          {{ $t('passwordVerification') }}
        </view>
        <view
          v-if="errorTips === 10"
          class="error-text"
        >
          <text class="warning-icon">
            !
          </text>
          {{ $t('pwdCantBeEmpty') }}
        </view>
      </view>
      <!-- 确认密码 -->
      <view
        v-if="nextBtn"
        :class="['item', errorTips ? 'error' : '']"
      >
        <view class="account">
          <input
            type="text"
            :password="conNewPasswordType"
            data-type="1"
            maxlength="20"
            :value="comNewPassWord"
            placeholder-class="inp-palcehoder"
            :placeholder="$t('enterNewAgain')"
            @input="onGetPassInputVal"
            @confirm="onChangePassWord"
          >
          <div class="image-box">
            <image
              :src="conNewPasswordImage"
              style="width:26rpx;height:18rpx"
              mode="heightFix"
              @tap="showConNewPassword"
            />
          </div>
        </view>
        <view
          v-if="errorTips === 5"
          class="error-text"
        >
          <text class="warning-icon">
            !
          </text>
          {{ $t('enterNewAgain') }}
        </view>
        <view
          v-if="errorTips === 7 && !identical"
          class="error-text"
        >
          <text class="warning-icon">
            !
          </text>
          {{ $t('comparedPassword') }}
        </view>
      </view>

      <view
        v-if="isForgetPassword && !nextBtn"
        style="text-align: center;"
      >
        <button
          class="authorized-btn"
          @tap="onNextStep"
        >
          {{ $t('nextStep') }}
        </button>
        <view
          v-if="!isPersonalCenter"
          class="back-login"
          @tap="onGoBack"
        >
          {{ $t('BackLogin') }}
        </view>
      </view>
      <view
        v-if="isForgetPassword && nextBtn"
        style="text-align: center;"
      >
        <button
          class="authorized-btn"
          @tap="onChangePassWord"
        >
          {{ $t('confirmChanges') }}
        </button>
        <view
          v-if="!isPersonalCenter"
          class="back-login"
          @tap="onGoBack"
        >
          {{ $t('BackLogin') }}
        </view>
      </view>
    </view>
    <!-- 其他登录方式 -->
    <view
      v-if="!isForgetPassword"
      class="footer"
      :style="{top: regLocation}"
    >
      <view class="other-login-text">
        {{ $t('otherLogMethods') }}
      </view>
      <view class="other-login">
        <view
          class="other-login-two"
          @tap="otherLogin(2)"
        >
          <image
            :src="loginStatus === 1 || loginStatus === 2 ? passwordImg : mobilePhoneImg"
            mode="heightFix"
          />
          <view>{{ loginSwitchTips }}</view>
        </view>
      </view>
    </view>
    <!-- 隐私协议弹窗 -->
    <view
      v-if="showAgreementPop"
      class="agreement-pop"
    >
      <view class="mask" />
      <view class="main">
        <view class="title">
          {{ $t('agreementTipsTitle') }}
        </view>
        <view class="content">
          {{ $t('agreementContent') }}
          <text @tap.stop="onToTermsOfService('serviceTerms')">
            《{{ $t('termsOfService') }}》
          </text>
          {{ $t('agreementAnd') }}
          <text @tap.stop="onToTermsOfService('servicePolicy')">
            《{{ $t('privacyPolicy') }}》
          </text>
        </view>
        <view class="btn-con">
          <view
            class="btn plain"
            @tap="showAgreementPop = false"
          >
            {{ $t('cancel') }}
          </view>
          <view
            class="btn"
            @tap="onHandlePrivacyClick"
          >
            {{ $t('agreementConfirm') }}
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { encrypt } from '@/utils/crypto'
import { AppType } from '@/utils/constant'
import util from '@/utils/util'
import passwordImg from '@/package-user/static/images/icon/password.png'
import mobilePhoneImg from '@/package-user/static/images/icon/mobile-phone.png'
import iconViewImg from '@/package-user/static/images/icon/icon-view.png'
import iconHideImg from '@/package-user/static/images/icon/icon-hide.png'

const principal = ref('')
const credentials = ref('')
const isForgetPassword = ref(false) // 是否修改密码

// 验证码相关
const validCode = ref('') // 验证码
const count = ref(null)
let timer = null

const newPassWord = ref('') // 新密码
const comNewPassWord = ref('') // 确认新密码

const checkRegisterSmsFlag = ref('') // 校验验证码成功的标识
const errorTips = ref(0) // 错误提示
const identical = ref(false) // 是否一致

const isPrivacy = ref(uni.getStorageSync('cbecB2cAppType') === AppType.MINI ? 0 : (uni.getStorageSync('cbecB2cIsPrivacy') || 0)) // 小程序默认不勾选    其他使用第一次登录需要勾选
const passwordType = ref(true)
const passwordRule = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*(\W|_))[A-Za-z\d(\W|_)]{8,20}$/

const loginStatus = ref(0) // 选择的登录方式 0密码、1注册、2微信

const privacyNumber = ref('')

watch(loginStatus, () => {
  errorTips.value = 0
  validCode.value = ''
  principal.value = ''
  credentials.value = ''
  const email = uni.getStorageSync('cbecB2cUserInfo')?.userMail || ''
  if (isForgetPassword.value) {
    const pre = email.substr(0, 3)
    const next = email.substr(7, email.length - 1)
    privacyNumber.value = pre + '****' + next
    principal.value = email
  }
})

const loginSwitchTips = computed(() => {
  return [1, 2].includes(loginStatus.value) ? $t('accountLogin') : $t('registerAccount')
})

const isPersonalCenter = ref(false) // 是否从个人中心页面跳转过来
/**
 * 生命周期函数--监听页面加载
 */
onLoad((options) => {
  if (options?.isWebview) {
    // 是否为web-view跳转登录
    const pages = getCurrentPages()
    // 登录后的回跳地址
    uni.setStorageSync('cbecB2cRouteUrlAfterLogin', pages[pages.length - 2].$page.fullPath)
  }
  if (options && options.isForgetPassword) {
    isForgetPassword.value = options.isForgetPassword
    uni.setNavigationBarTitle({
      title: $t('changePassword')
    })
  }
  if (options && options.isPersonalCenter) {
    isPersonalCenter.value = options.isPersonalCenter
    uni.setNavigationBarTitle({
      title: $t('changePassword')
    })
  } else {
    // 头部导航标题
    uni.setNavigationBarTitle({
      title: $t('userLogin')
    })
  }
  getSwitch()
})

const webConfigStore = useWebConfigStore()
const uniWebConfig = computed(() => webConfigStore.webConfig)

const regLocation = ref('')
/**
 * 生命周期函数--监听页面初次渲染完成
 */
onReady(() => {
  uni.getSystemInfo({
    success: res => {
      // 根据屏幕高度设置 top 值
      regLocation.value = res.windowHeight - 150 + 'px'
    }
  })
})

/**
 * 生命周期函数--监听页面显示
 */
onShow(() => {
  // 头部导航标题
  uni.setNavigationBarTitle({
    title: isForgetPassword.value ? $t('changePassword') : $t('userLogin')
  })
})

const showAgreementPop = ref(false)

/**
 * 条款点击事件(勾选/取选)
 */
const onHandlePrivacyClick = () => {
  isPrivacy.value = isPrivacy.value === 1 ? 0 : 1
  showAgreementPop.value = false
}

const onPrincipalInput = () => {
  principal.value = principal.value.replace(/\s/g, '')
  credentials.value = credentials.value.replace(/\s/g, '')
}

const serviceTermsSwitch = ref(false)
const privacyPolicySwitch = ref(false)
/**
 * 获取服务条款或隐私策略开关相关配置
 */
const getSwitch = () => {
  const params = {
    url: '/sys/config/info/getSysServiceSwitch',
    method: 'GET'
  }
  http.request(params).then(({ data }) => {
    serviceTermsSwitch.value = data.serviceTermsSwitch
    privacyPolicySwitch.value = data.privacyPolicySwitch
  })
}

const onGetPassInputVal = (e) => {
  const type = Number(e.currentTarget.dataset.type)
  if (type === 0) {
    newPassWord.value = e.detail.value
    const result = passwordRule.test(newPassWord.value)
    if (!result) {
      errorTips.value = 9
    } else {
      errorTips.value = 0
    }
  } else if (type === 1) {
    if (errorTips.value === 9) {
      // errorTips.value = 10
      return
    }
    comNewPassWord.value = e.detail.value
    setTimeout(() => {
      if (errorTips.value === 9 || errorTips.value === 10) {
        return
      }
      if (
        comNewPassWord.value &&
        newPassWord.value &&
        comNewPassWord.value != newPassWord.value
      ) {
        errorTips.value = 7
        identical.value = false
      } else if (!comNewPassWord.value) {
        errorTips.value = 5
      } else {
        errorTips.value = 0
        identical.value = true
      }
    }, 1000)
  }
}

/**
 * 登录
 */
const onLogin = util.debounce(() => {
  // 密码账号登录
  errorTips.value = (!principal.value.trim() || !util.checkEmail(principal.value)) ? 1 : !credentials.value.trim() ? 2 : 0
  if (errorTips.value) return
  if (isPrivacy.value !== 1) {
    showAgreementPop.value = true
    return
  }
  const url = uni.getStorageSync('cbecB2cAppType') > AppType.MP ? '/login' : '/wx/login'
  const params = {
    url,
    method: 'POST',
    data: {
      passWord: encrypt(credentials.value),
      socialType: uni.getStorageSync('cbecB2cAppType'),
      userName: principal.value
    },
    hasCatch: true
  }
  http.request(params).then(({ data: res }) => {
    login.loginSuccess(res.tokenInfo ? res.tokenInfo : res)
  }).catch(err => {
    loginErrHandle(err)
  })
}, 600)

/**
 * 注册
 */
const onRegister = util.debounce(() => {
  if (!principal.value.trim()) {
    errorTips.value = 8
    return
  }
  if (!util.checkEmail(principal.value)) {
    errorTips.value = 1
    return
  }
  if (!credentials.value.trim()) {
    errorTips.value = 2
    return
  }
  if (!passwordRule.test(credentials.value)) {
    errorTips.value = 9
    return
  }
  errorTips.value = 0
  if (isPrivacy.value !== 1) {
    showAgreementPop.value = true
    return
  }
  const params = {
    url: '/user/register',
    method: 'POST',
    data: {
      mail: principal.value,
      password: encrypt(credentials.value)
    },
    hasCatch: true
  }
  http.request(params).then(({ data: res }) => {
    if (res?.accessToken) {
      login.loginSuccess(res)
      return
    }
    uni.showToast({
      title: $t('registerSuccess'),
      icon: 'none'
    })
    loginStatus.value = 0
    credentials.value = ''
  }).catch(err => {
    uni.showToast({
      title: err.msg || 'Error',
      icon: 'none'
    })
  })
}, 600)

/**
 * 登录异常
 */
const loginErrHandle = (err) => {
  if (err.code === 'A00001' || err.code === 'A00005' || err.code === 'A00006') {
    uni.showToast({
      title: err.msg || 'Error',
      icon: 'none'
    })
  }
  if (err.code === 'A04002') {
    // 弹窗
    uni.showModal({
      showCancel: false,
      title: $t('bindedTipsTit'),
      content: $t('bindedTipsCon'),
      confirmText: $t('bindedTipsBtn'),
      confirmColor: '#F81A1A'
    })
  }
}

/**
 * 回到首页
 */
const onToIndex = () => {
  util.toHomePage()
}

/**
 * 去条款页
 */
const onToTermsOfService = (key) => {
  uni.navigateTo({
    url: '/package-user/pages/terms-page/terms-page?sts=' + key
  })
}

const passwordImage = ref(iconHideImg)
// 控制密码输入框显隐
const onShowPassword = () => {
  passwordImage.value = passwordType.value ? iconViewImg : iconHideImg
  passwordType.value = !passwordType.value
}
// 控制新密码输入框显隐
const newPasswordImage = ref(iconHideImg)
const newPasswordType = ref(true)
const onShowNewPassword = () => {
  newPasswordImage.value = newPasswordType.value ? iconViewImg : iconHideImg
  newPasswordType.value = !newPasswordType.value
}

const conNewPasswordType = ref(true)
const conNewPasswordImage = ref(iconHideImg)
// 控制确认密码输入框显隐
const showConNewPassword = () => {
  conNewPasswordImage.value = conNewPasswordType.value ? iconViewImg : iconHideImg
  conNewPasswordType.value = !conNewPasswordType.value
}
// 其他登录方式
const otherLogin = (val) => {
  if (val === 1) {
    loginStatus.value = loginStatus.value === 0 || loginStatus.value === 1 ? 2 : 1
  } else {
    loginStatus.value = loginStatus.value === 1 || loginStatus.value === 2 ? 0 : 1
  }
}
/**
 * 修改密码（登录页修改密码按钮）
 */
const onForgotPassword = () => {
  isForgetPassword.value = true
  errorTips.value = 0
  principal.value = ''
  credentials.value = ''

  uni.setNavigationBarTitle({
    title: $t('changePassword')
  })
}
// 返回
const onGoBack = () => {
  principal.value = ''
  errorTips.value = null
  newPassWord.value = ''
  validCode.value = ''
  comNewPassWord.value = ''
  isForgetPassword.value = false
  nextBtn.value = false

  uni.setNavigationBarTitle({
    title: $t('userLogin')
  })
}

const show = ref(true) // 获取验证码按钮
/**
 * 获取验证码
 */
const onGetCode = () => {
  if (!principal.value.trim()) {
    errorTips.value = 8
    return
  }
  // 判断邮箱格式是否正确
  if (!util.checkEmail(principal.value)) {
    errorTips.value = 1

    return
  }
  errorTips.value = 0
  const params = {
    url: isForgetPassword.value ? '/sms/sendUpdatePwdCode' : '/sms/sendLoginCode',
    method: 'POST',
    selfLoading: true,
    data: {
      mail: principal.value
    }
  }
  uni.showLoading({
    mask: true
  })
  http.request(params).then(() => {
    startInterval()
    uni.hideLoading()
  })
}

/**
 * 倒计时
 * timeVal 倒计的时间长度 单位：秒
 */
const startInterval = () => {
  const entryTime = parseInt(new Date().getTime() / 1000) // 进入页面得当前时间
  let currentTime = 0 // 当前时间
  const maxtime = 60 // 传入秒钟 = 最多走几秒
  count.value = maxtime
  show.value = false
  timer = setInterval(() => {
    currentTime = parseInt(new Date().getTime() / 1000) // 在定时器里面每隔一秒记录当前时间；
    const TimeDifference = (currentTime - entryTime) // 时间差
    const mytime = maxtime - TimeDifference // 传入的秒数 - 已经走掉的秒数  = 当前还剩多少秒数
    if (TimeDifference <= maxtime) { // 如果已经走掉的秒数 小于等于  传入的秒数
      count.value = Math.floor(mytime % 60)
    } else {
      clearInterval(timer)
      timer = null
      show.value = true
    }
  }, 1000)
}

const nextBtn = ref(false) // 修改密码下一步
/**
 *  修改密码（下一步）
 */
const onNextStep = () => {
  if (!principal.value.trim()) {
    errorTips.value = 8
    return
  }
  if (!util.checkEmail(principal.value)) {
    errorTips.value = 1

    return
  }
  if (!validCode.value.trim()) {
    // 请输入验证码

    errorTips.value = 3

    return
  }
  errorTips.value = 0
  // 校验验证码
  const params = {
    url: '/user/checkUpdatePwdSms',
    method: 'put',
    data: {
      mail: principal.value,
      validCode: validCode.value
    }
  }
  http.request(params).then(({ data: res }) => {
    checkRegisterSmsFlag.value = res
    nextBtn.value = !nextBtn.value
  })
}
/**
 * 修改密码(确认按钮)
 */
const onChangePassWord = util.debounce(() => {
  if (errorTips.value === 9 || errorTips.value === 10) {
    return
  }
  if (!newPassWord.value) {
    errorTips.value = 4

    return
  }
  if (!newPassWord.value.trim()) {
    // uni.showToast({
    //   title: $t('pwdCantBeEmpty'),
    //   icon: 'none',
    // })

    errorTips.value = 10

    return
  }
  if (!comNewPassWord.value) {
    errorTips.value = 5

    return
  }
  if (
    comNewPassWord.value &&
    newPassWord.value &&
    comNewPassWord.value != newPassWord.value
  ) {
    errorTips.value = 7
    identical.value = false

    return
  }
  errorTips.value = 0
  const params = {
    url: '/user/updatePwd',
    method: 'put',
    data: {
      appType: uni.getStorageSync('cbecB2cAppType'), // 应用类型
      checkRegisterSmsFlag: checkRegisterSmsFlag.value, // 校验登陆注册验证码成功的标识
      mail: principal.value,
      password: encrypt(newPassWord.value),
      validCode: validCode.value,
      validateType: 1 // 验证类型 1验证码验证 2 小程序encryptedData验证 3 密码验证
    }
  }
  http.request(params).then(() => {
    uni.showToast({
      title: $t('successfullyModified'),
      icon: 'none',
      duration: 1500
    })
    setTimeout(() => {
      isForgetPassword.value = false

      uni.removeStorageSync('cbecB2cLoginResult')
      uni.removeStorageSync('cbecB2cToken')
      uni.removeStorageSync('cbecB2cHadLogin')
      uni.removeStorageSync('cbecB2cRecentSearch')
      util.removeTabBadge()
      uni.redirectTo({
        url: '/package-user/pages/user-login/user-login'
      })
    }, 1500)
  })
}, 2000)

</script>
<style lang="scss" scoped>
@use "./user-login.scss";
</style>
