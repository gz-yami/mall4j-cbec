/* eslint-disable no-console */
// 全局请求封装
import loginMethods from './login'

const http = {
  request: (params) => {
    // 请求参数处理
    if (Object.prototype.toString.call(params.data) == '[object Array]') {
      params.data = JSON.stringify(params.data)
    } else if (Object.prototype.toString.call(params.data) == '[object Number]') {
      params.data = params.data + ''
    }
    // 发起请求
    return new Promise((resolve, reject) => {
      uni.request({
        dataType: 'json',
        responseType: params.responseType == undefined ? 'text' : params.responseType,
        header: {
          Authorization: uni.getStorageSync('cbecB2cToken'),
          locale: uni.getStorageSync('cbecB2cLang') || 'en'
        },
        url: (params.domain ? params.domain : import.meta.env.VITE_APP_BASE_API) + params.url,
        data: params.data,
        method: params.method == undefined ? 'POST' : params.method,
        success: (res) => {
          const responseData = res.data

          // 请求小程序码
          if (params.responseType === 'arraybuffer' && res.statusCode === 200) {
            return resolve(responseData)
          }

          // 00000 请求成功
          // A00001 用于直接显示提示用户的错误，内容由输入内容决定
          // A00002 用于直接显示提示系统的成功,内容由输入决定
          if (responseData.code === '00000' || responseData.code === 'A00002') {
            resolve(responseData)
          }
          // A00004 未授权
          if (responseData.code === 'A00004') {
            // 重设登录后跳转地址
            loginMethods.setRouteUrlAfterLogin()
            uni.removeStorageSync('cbecB2cUserInfo')
            uni.removeStorageSync('cbecB2cLoginResult')
            uni.removeStorageSync('cbecB2cToken')
            uni.removeStorageSync('cbecB2cHadLogin')
            uni.removeStorageSync('cbecB2cHadBindUser')
            uni.removeStorageSync('cbecB2cCode')
            if (!params.dontTrunLogin) {
              uni.showModal({
                title: $t('tips'),
                content: uni.getStorageSync('cbecB2cHadLogin') ? $t('loginExpired') : $t('loginPlease'),
                cancelText: $t('cancel'),
                confirmText: $t('confirm'),
                success: res => {
                  if (res.confirm) {
                    uni.navigateTo({
                      url: '/package-user/pages/user-login/user-login'
                    })
                  } else {
                    const router = getCurrentPages()
                    if (router[0].route === 'pages/basket/basket') {
                      uni.switchTab({
                        url: '/pages/index/index'
                      })
                    } else {
                      uni.navigateBack(0)
                    }
                  }
                }
              })
            }
            uni.removeStorageSync('cbecB2cHadLogin')
            return
          }

          // A00005 服务器出了点小差
          if (responseData.code === 'A00005') {
            http.onRequestFail(params, responseData)
            uni.showToast({
              title: '服务器出了点小差~',
              icon: 'none'
            })
          }

          // 其他异常码
          // A04001 社交账号未绑定
          // A00006 验证码错误
          if (responseData.code === 'A04001' || responseData.code === 'A00001' || responseData.code === 'A00006') {
            if (!params.hasCatch) {
              uni.showToast({
                title: responseData.msg || responseData.data || 'Error',
                icon: 'none'
              })
            }
          }
          if (responseData.code !== '00000') {
            reject(responseData)
          }
        },
        fail: (err) => {
          uni.showToast({
            title: '请求失败'
          })
          reject(err)
        }
      })
    })
  },

  getCartCount: () => {
    if (!uni.getStorageSync('cbecB2cToken')) {
      util.removeTabBadge()
      return
    }
    http.request({
      url: '/p/shopCart/prodCount',
      method: 'GET',
      dontTrunLogin: true
    }).then(({ data }) => {
      if (data > 0) {
        uni.setTabBarBadge({
          index: 2,
          text: data > 99 ? '99+' : data + ''
        })
        getApp().globalData.totalCartCount = data
      } else {
        util.removeTabBadge()
        getApp().globalData.totalCartCount = 0
      }
    })
  },
  onRequestFail: (params, responseData) => {
    console.error('============== 请求异常 ==============')
    console.log('接口地址: ', params.url)
    console.log('异常信息: ', responseData)
    console.error('============== 请求异常 end ==========')
  },
  /**
   * 上传文件统一接口
   */
  upload: (params) => {
    uni.uploadFile({
      url: import.meta.env.VITE_APP_BASE_API + params.url,
      filePath: params.filePath,
      name: params.name || 'file',
      header: {
        Authorization: params.login ? undefined : uni.getStorageSync('cbecB2cToken')
      },
      dataType: 'json',
      responseType: params.responseType === undefined ? 'json' : params.responseType,
      success: (res) => {
        const responseData = JSON.parse(res.data)
        if (responseData.code === '00000') {
          if (params.callBack) {
            params.callBack(responseData.data)
          }
        } else {
          uni.showToast({
            title: $t('serverWrong'),
            icon: 'none'
          })
        }
      },
      fail: function () {
        uni.hideLoading()
      }
    })
  },

  mpAuthLogin: (page, needCode) => {
    // 在微信环境打开,请求公众号网页登陆
    let redirectUrl = null

    if (!page || page === import.meta.env.VITE_APP_H5_DOMAIN) {
      redirectUrl = window.location.href
    } else {
      redirectUrl = import.meta.env.VITE_APP_H5_DOMAIN + page
    }
    const scope = 'snsapi_userinfo'
    window.location.href = 'https://open.weixin.qq.com/connect/oauth2/authorize?appid=' + import.meta.env.VITE_APP_MP_APPID +
      '&redirect_uri=' +
      encodeURIComponent(redirectUrl) + '&response_type=code&scope=' + scope + '&state=' + (needCode ? 'needCode' : 'unNeedCode') +
      '#wechat_redirect'
  }
}

export default http
