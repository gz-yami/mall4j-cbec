<template>
  <div class="component-category-selector">
    <el-dialog
      v-model="visible"
      :title="$t('category.categorySelector')"
      :append-to-body="visible"
      width="1000px"
    >
      <div class="prod-category clearfix">
        <div
          ref="categoryBoxRef"
          class="category"
        >
          <!-- 分类 -->
          <div class="category-box">
            <el-input
              v-model="firstCategorys.categoryName"
              :placeholder="$t('category.chooseProdCateg')"
              :disabled="true"
            />
            <ul class="category-list">
              <li
                v-for="(item,index) in firstCategorys.dataList"
                :key="item.categoryId"
                class="category-item"
                :class="item.categoryId==firstCategorys.id?'active':''"
                @click="selectFirstCategorys(item.categoryId, index)"
              >
                {{ item.categoryName }}
              </li>
            </ul>
          </div>
          <!-- 分类 -->
          <div class="category-box">
            <el-input
              v-model="secondCategorys.categoryName"
              :placeholder="$t('category.chooseProdCateg')"
              :disabled="true"
            />
            <ul class="category-list">
              <li
                v-for="(item,index) in secondCategorys.dataList"
                :key="item.categoryId"
                class="category-item"
                :class="item.categoryId==secondCategorys.id?'active':''"
                @click="selectSecondCategorys(item.categoryId,index)"
              >
                {{ item.categoryName }}
              </li>
            </ul>
          </div>
          <!-- 分类 -->
          <div class="category-box">
            <el-input
              v-model="threeCategorys.categoryName"
              :placeholder="$t('category.chooseProdCateg')"
              :disabled="true"
            />
            <ul class="category-list">
              <li
                v-for="(item,index) in threeCategorys.dataList"
                :key="item.categoryId"
                class="category-item"
                :class="[isCreateCategory?'prohibit-sel':item.categoryId==threeCategorys.id?'active':'']"
                @click="selectThreeCategorys(item.categoryId,index)"
              >
                {{ item.categoryName }}
              </li>
            </ul>
          </div>
        </div>
        <!-- 当前选择 -->
        <div class="current-selected">
          <span class="blod">{{ $t("category.currCho") }}：</span>
          <span class="select-item">{{ firstCategorys.categoryName }}</span>
          <span
            v-if="secondCategorys.id"
            class="select-item"
          >&nbsp;>&nbsp;&nbsp;{{ secondCategorys.categoryName }}</span>
          <span
            v-if="threeCategorys.id"
            class="select-item"
          >&nbsp;>&nbsp;&nbsp;{{ threeCategorys.categoryName }}</span>
        </div>
        <!-- 确认 -->
        <div class="read-rule">
          <div
            class="read-rule-txt"
            :class="(!isCreateCategory && threeCategorys.id !== 0) || (isCreateCategory && firstCategorys.id !== 0)?'todo':''"
            @click="optionsConfirm"
          >
            {{ $t("category.haveReadFol") }}
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ElLoading } from 'element-plus'
import { treeToArray } from '@/utils/index'

const emit = defineEmits(['getCategorySelected'])

const visible = ref(false)
const allDataList = ref([])
// 第一个分类
const firstCategorys = reactive({
  id: 0,
  categoryName: '',
  dataList: []
})
// 第二个分类
const secondCategorys = reactive({
  id: 0,
  categoryName: '',
  dataList: []
})
// 第三个分类
const threeCategorys = reactive({
  id: 0,
  categoryName: '',
  dataList: []
})
let parentId = 0
const isCreateCategory = ref(false) // 是否创建（平台）分类选择

/**
 * 初始化
 */
const init = (type, selectedCategories) => {
  visible.value = true
  const loadingInstance = ElLoading.service({
    target: '.component-category-selector',
    text: $t('components.loading')
  })
  http({
    url: http.adornUrl('/prod/category/listCategory'),
    method: 'get'
  }).then(({ data }) => {
    data = treeDataTranslate(data, 'categoryId')
    allDataList.value = treeToArray(data)
    firstCategorys.dataList = allDataList.value.filter(item => item.grade === 0 || item.grade === '0')
    loadingInstance.close()
    if (selectedCategories && selectedCategories.length === 2) {
      secondCategorys.dataList = allDataList.value.filter(item => item.parentId === firstCategorys.id)
    }
    // 回显
    BackToShow(selectedCategories)
  })

  isCreateCategory.value = !!(type && type === 1)
}

const hide = () => {
  visible.value = false
}

// 回显
const BackToShow = (selectedCategories) => {
  if (selectedCategories && selectedCategories.length > 0) {
    if (allDataList.value.find(el => el.categoryId === selectedCategories[0].id)) {
      firstCategorys.id = selectedCategories[0].id || firstCategorys.id
      selectFirstCategorys(firstCategorys.id, queryIndex(firstCategorys))
    }
    if (allDataList.value.find(el => el.categoryId === selectedCategories[1].id)) {
      secondCategorys.id = selectedCategories[1].id || secondCategorys.id
      selectSecondCategorys(secondCategorys.id, queryIndex(secondCategorys))
    }
    if (allDataList.value.find(el => el.categoryId === selectedCategories[2].id)) {
      threeCategorys.id = selectedCategories[2].id || threeCategorys.id
      selectThreeCategorys(threeCategorys.id, queryIndex(threeCategorys))
    }
  }
}

const queryIndex = (list) => {
  let index = null
  list.dataList.forEach((el, idx) => {
    if (el.categoryId === list.id) {
      index = idx
    }
  })
  return index
}

// 选中第一个分类
const selectFirstCategorys = (categoryId, index) => {
  secondCategorys.dataList = allDataList.value.filter(item => item.parentId === categoryId)
  firstCategorys.categoryName = index >= 0 ? firstCategorys.dataList[index].categoryName : firstCategorys.categoryName
  parentId = firstCategorys.id = categoryId
  secondCategorys.id = 0
  threeCategorys.id = 0
  threeCategorys.dataList = []
  threeCategorys.categoryName = ''
}
// 选中第二个分类
const selectSecondCategorys = (categoryId, index) => {
  threeCategorys.dataList = allDataList.value.filter(item => item.parentId === categoryId)
  parentId = secondCategorys.id = categoryId
  secondCategorys.categoryName = index >= 0 ? secondCategorys.dataList[index].categoryName : secondCategorys.categoryName
  threeCategorys.id = 0
}
// 选中第三个分类
const selectThreeCategorys = (categoryId, index) => {
  if (isCreateCategory.value) {
    return
  }
  parentId = threeCategorys.id = categoryId
  threeCategorys.categoryName = index >= 0 ? threeCategorys.dataList[index].categoryName : threeCategorys.categoryName
}
// 新增 / 修改
const optionsConfirm = () => {
  if (!isCreateCategory.value && !threeCategorys.id) {
    return
  }
  if (isCreateCategory.value && !firstCategorys.id) {
    return
  }
  const selectedCategories = []
  if (firstCategorys.id) {
    selectedCategories.push(firstCategorys)
  }
  if (secondCategorys.id) {
    selectedCategories.push(secondCategorys)
  }
  if (!isCreateCategory.value && threeCategorys.id) {
    selectedCategories.push(threeCategorys)
  }
  emit('getCategorySelected', selectedCategories, parentId)
  hide()
}

defineExpose({
  init
})

</script>

<style lang="scss" scoped>
.prod-category {
  width: 90%;
  margin: 0 auto;
  .clearfix::after {
    display: block;
    content: "";
    visibility: hidden;
    height: 0;
    clear: both;
  }
  ul,
  li {
    margin: 0;
    padding: 0;
    list-style: none;
  }

  .category {
    display: flex;
  }
  .category-box {
    box-sizing: border-box;
    flex: 1;
    border: 1px solid #e4e4e4;
    padding: 20px;
    background: #fff;
    box-shadow: 0 5px 8px -4px #e2e2e2;
  }
  .category-box:not(:last-child) {
    margin-right: 10px;
  }

  .category-list {
    margin-top: 10px;
    height: 250px;
    margin-left: 10px;
    overflow-y: auto;
  }
  .category-item {
    display: -webkit-box;
    max-width: 100%;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    overflow: hidden;
    word-break: break-word;
    word-wrap: break-word;
    padding: 6px;
    box-sizing: border-box;
    cursor: pointer;
  }
  .category-item:hover,
  .category-item.active {
    background: #f5f5f5;
    border-radius: 3px;
  }
  .current-selected {
    display: block;
    width: 100%;
    margin-top: 20px;
    border: 2px solid #fedbab;
    padding: 6px;
    background: #fffaf2;
    line-height: 1.5em;
    word-break: break-word;
    word-wrap: break-word;
    overflow: hidden;
  }
  .read-rule {
    width: 100%;
    margin-top: 20px;
  }
  .read-rule-txt {
    text-align: center;
    color: #fff;
    background: #999999;
    line-height: 3em;
    width: 50%;
    margin: 0 auto;
  }

  .blod {
    display: inline-block;
    font-weight: bold;
  }
  .todo {
    background: #155bd4;
    cursor: pointer;
  }
  .prohibit-sel {
    cursor: not-allowed;
  }
}
</style>
