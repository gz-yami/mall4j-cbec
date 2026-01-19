import { defineStore } from 'pinia'
export const useWebConfigStore = defineStore('webConfig', {
  state: () => {
    return localStorage.getItem('cbecB2cWebConfigData') ? JSON.parse(localStorage.getItem('cbecB2cWebConfigData')) : {
      bsLoginLogoImg: null,
      bsLoginBgImg: null,
      bsCopyright: null,
      bsTitleContent: null,
      bsTitleImg: null,
      bsMenuTitleOpen: null,
      bsMenuTitleClose: null
    }
  },
  actions: {
    addData (webConfigDataForm) {
      this.$patch(() => {
        webConfigDataForm.bsTitleImg = checkFileUrl(webConfigDataForm.bsTitleImg)
        localStorage.setItem('cbecB2cWebConfigData', JSON.stringify(webConfigDataForm || '{}'))
        document.title = webConfigDataForm.bsTitleContent || ''
        let facicon = document.querySelector('link[rel="icon"]')
        if (facicon !== null) {
          facicon.href = checkFileUrl(webConfigDataForm.bsTitleImg)
        } else {
          facicon = document.createElement('link')
          facicon.rel = 'icon'
          facicon.href = checkFileUrl(webConfigDataForm.bsTitleImg)
          document.head.appendChild(facicon)
        }
      })
    }
  }
})
