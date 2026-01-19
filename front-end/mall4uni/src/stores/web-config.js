import { defineStore } from 'pinia'

export const useWebConfigStore = defineStore('webConfigStore', {
  state: () => {
    return {
      webConfig: {
        // 登录logo
        uniLoginLogoImg: '',
        // 标题
        uniTitleContent: '',
        // 欢迎语
        h5Welcome: '',
        // 国际化配置列表
        configLangList: []
      }
    }
  },
  actions: {
    getUniWebConfig () {
      http.request({
        url: '/webConfig/getUniWebConfig',
        method: 'GET'
      }).then(({ data }) => {
        this.webConfig.uniLoginLogoImg = data.uniLoginLogoImg
        this.webConfig.configLangList = data.webConfigLangList
        this.formatConfigInfo()
      })
    },
    // 格式化配置信息
    formatConfigInfo () {
      let langKey = Number(uni.getStorageSync('cbecB2cLangKey'))
      if (!langKey && langKey !== 0) langKey = 2
      const webConfig = this.webConfig.configLangList?.find((element) => element.lang === langKey)
      if (webConfig) {
        this.webConfig.uniTitleContent = webConfig.uniTitleContent
        this.webConfig.h5Welcome = webConfig.h5Welcome
      }
    }
  }
})
