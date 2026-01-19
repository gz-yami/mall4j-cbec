<template>
  <!-- 发布商品：分类选择组件 -->
  <div class="category-selection-component">
    <div class="category-select-box">
      <!-- 第一级 -->
      <div class="category-select-item">
        <div class="int-box">
          <input
            disabled
            class="se-int"
            :placeholder="$t('product.selectFirstLevelCategory')"
          >
        </div>
        <ul class="category-list">
          <li
            v-for="(item, index) in firstCat.dataList"
            v-show="showFirstCat(item.categoryId)"
            :key="item.categoryId"
            class="category-item"
            :class="item.categoryId === firstCat.id ? 'active' : ''"
            @click="selectFirstCat(item.categoryId, index)"
          >
            {{ item.categoryName }}
            <span
              class="el-icon-arrow-right"
              style="float:right"
            />
          </li>
          <div
            v-if="!initCategoryIng && !allDataList.length"
            class="category-empty-tips"
          >
            {{ $t('product.noContractedCategories') }}
            <span
              class="default-btn text-btn"
              @click="toShopInfo"
            >{{ $t('product.shopInfo') }}</span>
            {{ $t('product.applyContracting') }}
          </div>
        </ul>
      </div>
      <!-- 第二级 v-if="firstCat.id !== 0 && secondCat.dataList.length" -->
      <div
        v-if="firstCat.dataList.length"
        class="category-select-item"
      >
        <div class="int-box">
          <input
            disabled
            class="se-int"
            :placeholder="$t('product.selectSecondLevelCategory')"
          >
        </div>
        <ul class="category-list">
          <li
            v-for="(item, index) in secondCat.dataList"
            v-show="showSecondCat(item.categoryId)"
            :key="item.categoryId"
            class="category-item"
            :class="item.categoryId === secondCat.id ? 'active' : ''"
            @click="selectSecondCat(item.categoryId, index)"
          >
            {{ item.categoryName }}
            <span
              class="el-icon-arrow-right"
              style="float:right"
            />
          </li>
          <li
            v-if="!secondCat.dataList.length"
            class="cate-empty"
          >
            {{ $t('product.selectFirstCategory') }}
          </li>
        </ul>
      </div>
      <div
        v-if="firstCat.dataList.length"
        class="category-select-item"
      >
        <div class="int-box">
          <input
            disabled
            class="se-int"
            :placeholder="$t('product.selectThirdLevelCategory')"
          >
        </div>
        <ul class="category-list">
          <li
            v-for="(item, index) in threeCat.dataList"
            :key="item.categoryId"
            class="category-item"
            :class="item.categoryId === threeCat.id ? 'active' : ''"
            @click="selectThreeCat(item.categoryId, index)"
          >
            {{ item.categoryName }}
          </li>
          <li
            v-if="!threeCat.dataList.length"
            class="cate-empty"
          >
            {{ $t('product.selectSecondCategory') }}
          </li>
        </ul>
      </div>
    </div>

    <!-- 已选 -->
    <div class="selected-results">
      <div
        class="sel-con"
      >
        {{ $t("product.selectedCategories") }}：
        <span class="cur-cate">{{ firstCat.categoryName }}</span>
        <span
          v-if="secondCat.id"
          class="cur-cate"
        >&nbsp;>&nbsp;&nbsp;{{ secondCat.categoryName }}</span>
        <span
          v-if="threeCat.id"
          class="cur-cate"
        >&nbsp;>&nbsp;&nbsp;{{ threeCat.categoryName }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useProdStore } from '@/stores/prod.js'
import { useRouter } from 'vue-router'

const router = useRouter()
const prodStore = useProdStore()

const emit = defineEmits(['reSelection', 'close'])

const props = defineProps({
  categoryId: {
    type: [String, Number],
    default: null
  }
})

// 第一个分类
const firstCat = reactive({
  id: 0,
  categoryName: '',
  dataList: []
})
// 第二个分类
const secondCat = reactive({
  id: 0,
  categoryName: '',
  dataList: []
})
// 第三个分类
const threeCat = reactive({
  id: 0,
  categoryName: '',
  dataList: []
})

const composeType = 0 // 是否是组合商品
const initCategoryIng = ref(true)
onMounted(() => {
  initCategoryIng.value = true
  clearAllCategories()
  // 请求分类列表
  getDataList()
})

const showSecondCat = (categoryId) => {
  const dataList = allDataList.value.filter(item => item.parentId === categoryId)
  return dataList.length
}
const showFirstCat = (categoryId) => {
  let flag = 0
  const dataList = allDataList.value.filter(item => item.parentId === categoryId)
  dataList.forEach(i => {
    if (allDataList.value.filter(item => item.parentId === i.categoryId).length) {
      flag++
    }
  })
  return flag
}
/**
 * 获取分类数据
 */
const allDataList = ref([])
const getDataList = () => {
  const url = '/prod/category/listCategory'
  http({
    url: http.adornUrl(url),
    method: 'get',
    params: http.adornParams()
  }).then(({ data }) => {
    allDataList.value = data
    firstCat.dataList = data.filter(item => item.grade === 0 && item.status)
    if (threeCat.id && !data.find(el => el.categoryId === threeCat.id)) {
      clearAllCategories()
    }
    // 存在传入的categoryId且最新分类列表中包含categoryId
    if (props.categoryId && allDataList.value.find(el => el.categoryId === props.categoryId)) {
      categoryBackDisplay(data)
    } else {
      // 有传入categoryId但是最新分类列表中无对应categoryId（例：已选categoryId被删除），则必须重选
      resetCategory(true)
      categoryBackDisplay(data)
    }
    initCategoryIng.value = false
  }).catch(() => {
    initCategoryIng.value = false
  })
}

/**
 * 分类数据回显
 */
const categoryBackDisplay = (data) => {
  data.forEach(el => {
    // 根据第三级分类id回填第二级分类id（parentId）
    if (el.categoryId === props.categoryId) {
      secondCat.id = el.parentId
      threeCat.categoryName = el.categoryName
    }
    // 根据第二级分类id回填第一级分类id（parentId）
    if (el.categoryId === secondCat.id) {
      firstCat.id = el.parentId
      secondCat.categoryName = el.categoryName
    }
    // 第一级分类名称
    if (el.categoryId === firstCat.id) {
      firstCat.categoryName = el.categoryName
      selectFirstCat(firstCat.id, null)
      selectSecondCat(secondCat.id, null)
      selectThreeCat(props.categoryId, null)
    }
  })
}

// 重选
const resetCategory = (isRequired) => {
  threeCat.id = 0
  threeCat.categoryName = ''
  threeCat.dataList = []
  // 如果点击时已存在categoryId，表示正在重选分类
  if (props.categoryId || isRequired) {
    emit('reSelection')
  }
}

const selectFirstCat = (categoryId, index) => {
  firstCat.id = categoryId
  if (index !== null && index !== undefined) {
    resetCategory()
    firstCat.categoryName = firstCat.dataList[index].categoryName
    secondCat.id = 0
    threeCat.id = 0
  }
  secondCat.dataList = allDataList.value.filter(item => item.parentId === categoryId)
  emit('close')
}
const selectSecondCat = (categoryId, index) => {
  secondCat.id = categoryId
  if (index !== null && index !== undefined) {
    resetCategory()
    secondCat.categoryName = secondCat.dataList[index].categoryName
    threeCat.id = 0
  }
  threeCat.dataList = allDataList.value.filter(item => item.parentId === categoryId)
  emit('close')
}
const selectThreeCat = (categoryId, index) => {
  threeCat.id = categoryId
  if (index !== null && index !== undefined) {
    threeCat.categoryName = threeCat.dataList[index].categoryName
  }
  emit('close', threeCat.id, composeType, firstCat.categoryName,
    secondCat.categoryName, threeCat.categoryName)
  prodStore.addPlatProdCategory({
    firstCat: JSON.parse(JSON.stringify(firstCat)),
    secondCat: JSON.parse(JSON.stringify(secondCat)),
    threeCat: JSON.parse(JSON.stringify(threeCat)),
    composeType
  })
}
const toShopInfo = () => {
  emit('close', false)
  const newPage = router.resolve({
    path: '/shop/shop-info/index',
    query: {
      navStatus: 2
    }
  })
  window.open(newPage.href, '_blank', 'noopener,noreferrer')
}

/**
 * 清空已选分类
 */
const clearAllCategories = () => {
  // 一级
  firstCat.id = 0
  firstCat.categoryName = ''
  firstCat.dataList = []
  // 二级
  secondCat.id = 0
  secondCat.categoryName = ''
  secondCat.dataList = []
  // 三级
  threeCat.id = 0
  threeCat.categoryName = ''
  threeCat.dataList = []
}

</script>

<style lang="scss" scoped>
.category-selection-component {
  margin-bottom: 15px;
  ul,
  li {
    margin: 0;
    padding: 0;
    list-style: none;
  }
  .category-select-box {
    display: flex;
    align-items: flex-start;
    justify-content: flex-start;
    .category-select-item:not(:last-child) {
      margin-right: 10px;
    }
    .category-select-item {
      width: 240px;
      height: auto;
      box-sizing: border-box;
      border: 1px solid #DCDCDC;
      .int-box {
        width: 100%;
        padding: 10px;
        box-sizing: border-box;
        .se-int {
          width: 100%;
          height: 30px;
          line-height: 30px;
          &::placeholder {
            font-size: 12px;
            color: #999;
            padding: 6px;
            box-sizing: border-box;
          }
        }
      }
      .category-list {
        // margin: 10px;
        height: 240px;
        overflow-y: auto;
        .category-item {
          font-size: 12px;
          color: #333;
          padding: 8px;
          box-sizing: border-box;
          cursor: pointer;
          & :deep(.el-icon-arrow-right) {
            color: #999;
          }
        }
        .category-empty-tips {
          font-size: 12px;
          color: #333;
          padding: 0 10px;
          margin-top: 45px;
        }
        .category-item:hover {
          background: rgba(245,248,255, .6);
          border-radius: 2px;
        }
        .category-item.active {
          background: #e8effa;
          border-radius: 2px;
        }
      }

      // 空
      .cate-empty {
        margin-top: 50px;
        color: #bbb;
        text-align: center;
        font-size: 12px;
        padding: 0 10px;
        box-sizing: border-box;
        line-height: 1.5em;
      }
    }
  }

  // 已选中
  .selected-results {
    margin-top: 10px;
    .sel-con {
      display: block;
      background: #f9f9f9;
      font-size: 12px;
      padding: 10px;
      margin-bottom: 20px;
      .cur-cate {
        color: #155BD4;
      }
    }
  }
}
</style>
