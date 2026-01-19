import { defineStore } from 'pinia'
export const useProdStore = defineStore('prod', {
  state: () => {
    return {
      id: 0,
      skuTags: [],
      prodCategory: {},
      platProdCategory: {},
      defalutSku: {
        price: 0.01, // 销售价
        oriPrice: 0.01, // 市场价
        stocks: 0, // 库存
        properties: '', // 销售属性组合字符串
        skuName: '', // sku名称
        prodName: '', // 商品名称
        partyCode: '', // 商品编码
        weight: 0, // 商品重量
        volume: 0, // 商品体积
        status: 1, // 0 禁用 1 启用
        stockWarning: 0, // 库存预警,
        skuComboList: [], // 组合单品列表
        stockPointList: [], // 库存点库存
        filterList: [], // 过滤的门店列表
        filterStock: 0, // 过滤的门店库存
        skuId: ''
      }
    }
  },
  actions: {
    updateSkuTags (skuTags) {
      this.skuTags = skuTags
    },
    updateSkuTag ({ skuTag, index }) {
      index = parseInt(index)
      if (skuTag && typeof index === 'number') {
        this.skuTags.splice(index, 1, skuTag)
      }
    },
    addSkuTag (skuTag) {
      this.skuTags.push(skuTag)
    },
    addPlatProdCategory (platProdCategory) {
      this.platProdCategory = platProdCategory
    },
    updatePlatProdCategory ({ platProdCategory, selectUpdate }) {
      if (selectUpdate) {
        // 选择更新
        if (platProdCategory.firstCat) {
          this.platProdCategory.firstCat = platProdCategory.firstCat
        }
        if (platProdCategory.secondCat) {
          this.platProdCategory.secondCat = platProdCategory.secondCat
        }
        if (platProdCategory.threeCat) {
          this.platProdCategory.threeCat = platProdCategory.threeCat
        }
        if (platProdCategory.composeType) {
          this.platProdCategory.composeType = platProdCategory.composeType
        }
      } else {
        // 全部更新
        this.platProdCategory = platProdCategory
      }
    },
    removeSkuTag (tagIndex) {
      this.skuTags.splice(tagIndex, 1)
    },
    removeSkuTagItem ({ tagIndex, tagItemIndex }) {
      this.skuTags[tagIndex].tagItems.splice(tagItemIndex, 1)
    },
    addSkuTagItem ({ tagIndex, tagItem }) {
      this.skuTags[tagIndex].tagItems.push(tagItem)
    },
    updateSkuTagItem ({ skuTagItem, tagIndex, tagItemIndex }) {
      if (skuTagItem) {
        this.skuTags[tagIndex].tagItems.splice(tagItemIndex, 1, skuTagItem)
      }
    },
    setStockPointList (defaultStockPointId) {
      this.defalutSku.stockPointList = [{
        stock: 0,
        stockPointId: defaultStockPointId,
        stockPointType: 1,
        type: 0
      }]
    }
  }
})
