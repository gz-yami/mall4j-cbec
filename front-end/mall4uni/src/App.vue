<script setup>
import { useCurrencyStore } from '@/stores/modules/currency'
import { i18n } from '@/lang'

const currencyStore = useCurrencyStore()
const webConfigStore = useWebConfigStore()
onLaunch(() => {
  // eslint-disable-next-line no-console
  console.log('mall4j.v240506')
  // #ifdef H5
  uni.setStorageSync('cbecB2cAppType', AppType.H5)
  // #endif

  // 获取网站配置
  webConfigStore.getUniWebConfig()

  // 初始化语言环境
  setDefaultLang()

  util.transTabbar()

  // 初始化货币数据
  currencyStore.INIT_CURRENCY_DATA()

  // 获取系统支付设置
  util.getSysPaySwitch(true)

  // 获取网站配置
  useWebConfigStore().getUniWebConfig()

  // APP模式下保持竖屏
  // #ifdef APP-PLUS
  plus.screen.lockOrientation('portrait-primary')
  uni.getSystemInfo({
    success: (sysInfo) => {
      if (sysInfo.platform === 'android') {
        uni.setStorageSync('cbecB2cAppType', AppType.ANDROID)
      } else {
        uni.setStorageSync('cbecB2cAppType', AppType.IOS)
      }
    }
  })
  // #endif

  // #ifndef H5
  uni.getSystemInfo({
    success: function (res) {
      // px转换到rpx的比例
      const pxToRpxScale = 750 / res.windowWidth
      const systems = {
        ktxStatusHeight: res.statusBarHeight * pxToRpxScale, // 状态栏的高度
        navigationHeight: 44 * pxToRpxScale, // 导航栏的高度
        ktxWindowWidth: res.windowWidth * pxToRpxScale, // window的宽度
        ktxWindowHeight: res.windowHeight * pxToRpxScale, // window的高度
        ktxScreentHeight: res.screenHeight * pxToRpxScale // 屏幕的高度
      }
      const app = createApp()
      app.config.globalProperties.$system = systems
      app.config.globalProperties.$lineHeight = systems.ktxStatusHeight + systems.navigationHeight
      provide('$system', systems)
      provide('$lineHeight', systems.ktxStatusHeight + systems.navigationHeight)
    }
  })
  // #endif
  uni.removeStorageSync('cbecB2cCartSelectAddrInfo')
  // 全局变量
  nextTick(() => initGlobalData())
})

// 页面不存在时触发
onPageNotFound(() => {
  uni.reLaunch({
    url: '/pages/index/index'
  })
})

// 获取国际化语言列表
const setDefaultLang = () => {
  const param = {
    url: '/lang/default',
    method: 'GET'
  }
  http.request(param).then(({ data }) => {
    const langListPar = data.langItemList.filter(f => !f.hide) // 过滤掉隐藏的语言
    const defLangObj = data.langItemList.find(f => f.master === 1) || data.langItemList[0] // 获取系统默认语言
    uni.setStorageSync('cbecB2cDefLangObj', defLangObj)
    const b2cLang = uni.getStorageSync('cbecB2cLang')
    const b2cLangKey = uni.getStorageSync('cbecB2cLangKey')
    let curLangItem
    // 当前语言存在语言列表中
    let isExist = false
    if (b2cLang) {
      for (const it of langListPar) {
        if (it.language === b2cLang) {
          curLangItem = langListPar.find(f => f.language === b2cLang)
          isExist = true
          break
        }
      }
    }
    // 当前无缓存语言或当前缓存语言不在语言列表时
    if (!b2cLang || !isExist) {
      uni.setStorageSync('cbecB2cLang', data.language)
      uni.setStorageSync('cbecB2cLangKey', data.lang + '')
      i18n.global.locale.value = data.language
    } else if (!b2cLangKey || curLangItem.lang !== b2cLangKey) {
      uni.setStorageSync('cbecB2cLangKey', curLangItem.lang + '')
    }
  })
}

const initGlobalData = () => {
  // 全局请求队列
  getApp().globalData.requestQueue = []
  // 是否正在进行登陆
  getApp().globalData.isLanding = false
  // 购物车商品数量
  getApp().globalData.totalCartCount = 0
  // 当前请求数量
  getApp().globalData.currentReqCounts = 0
  // 是否重新加载页面数据
  getApp().globalData.isRefreshPage = false
  // 直播间socket任务
  getApp().globalData.imSocketTask = null
  /**
   * 由于浏览器的flash支持问题,h5暂不兼容rtmp
   * 小程序&h5支持flv和m3u8播放(小程序播放flv采用web-view方式引入，必须发布h5才可使用)，app3个都支持
   * 若想小程序支持3个类型播放，可申请live-player播放组件
   */
  // 直播播放类型 1.rtmp 2.flv 3.m3u8
  getApp().globalData.livePlayType = 3
  // 小程序是否使用live-player进行播放
  getApp().globalData.useLivePlayer = false // 使用live-player播放器
  getApp().globalData.reloadHomePopupAd = true // 是否重新加载首页弹窗
}

/**
 * 微信小程序检查升级
 */
const checkMiniUpdate = () => {
  const updateManager = wx.getUpdateManager()
  updateManager.onUpdateReady(function () {
    wx.showModal({
      title: '更新提示',
      content: '新版本已经准备好，是否重启应用？',
      success: function (res) {
        if (res.confirm) {
          // 新的版本已经下载好，调用 applyUpdate 应用新版本并重启
          updateManager.applyUpdate()
        }
      }
    })
  })
  updateManager.onUpdateFailed(function () {
    // 新版本下载失败
    uni.showToast({
      title: '新版本下载失败,请重试',
      duration: 2000,
      icon: 'none'
    })
  })
}

</script>

<!-- eslint-disable-next-line vue-scoped-css/enforce-style-type -->
<style>
/*每个页面公共样式 */
/* #ifndef APP-PLUS-NVUE */
@import url('./app.css');
/* #endif */
</style>
