import { defineStore } from 'pinia'
export const useRouterStore = defineStore('routerStore', {
  state: () => {
    return {
      includePageList: [],
      hisPageList: []
    }
  },
  actions: {
    updateIncludePageList (page) {
      this.includePageList = page
    },
    pushIncludePageList (page) {
      this.includePageList.push(page)
    },
    pushHisPageList (page) {
      this.hisPageList.push(page)
      this.hisPageList = [...new Set(this.hisPageList)]
    }
  }
})
