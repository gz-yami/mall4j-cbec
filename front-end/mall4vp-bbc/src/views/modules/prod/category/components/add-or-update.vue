<template>
  <el-dialog
    v-model="visible"
    class="component-category-add-or-update"
    :title="!dataForm.categoryId
      ? $t('brand.add')
      : $t('brand.revise')
    "
    :close-on-click-modal="false"
    width="600px"
    @close="close"
  >
    <el-form
      ref="dataFormRef"
      :model="dataForm"
      :rules="dataRule"
      :label-width="$t('language')==='zh_CN'?'180px': 'auto'"
      class="form-box"
      @submit.prevent
      @keyup.enter="dataFormSubmit()"
    >
      <!-- :inline="true" -->
      <el-form-item
        v-if="dataForm.type !== 2 && isSecondLevel === false"
        :label="$t('category.categoryPicture')"
        prop="pic"
        :required="isRequiredImg"
      >
        <pic-upload v-model="dataForm.pic" />
        <span v-if="dataForm.parentId === 0">{{ $t("category.recPicSize") }}532*160</span>
        <span v-if="dataForm.parentId !== 0">{{ $t("category.recPicSize") }}120*120</span>
      </el-form-item>
      <!-- 语言选择 -->
      <el-form-item
        v-if="langItemList.length > 1"
        :label="$t('product.selectLanguage')"
      >
        <el-select
          v-model="curLang"
          multiple
          :placeholder="$t('tip.select')"
          style="width: 100%;"
          @change="selectLang"
        >
          <el-option
            v-for="item in langItemList"
            :key="item.lang"
            :label="item.name"
            :value="item.lang"
          />
        </el-select>
      </el-form-item>
      <!-- 分类名称 -->
      <template v-if="dataForm.type !== 2">
        <div
          v-for="(item, index) in categoryLangList"
          :key="index"
        >
          <el-form-item
            :label="$t('publics.category') + (langItemList.length === 1 ? '' : `(${item.langName})`)"
            required
          >
            <el-input
              v-model="item.categoryName"
              maxlength="30"
              show-word-limit
            />
          </el-form-item>
        </div>
      </template>
      <!-- 选择上级分类 -->
      <el-form-item :label="$t('category.categoryParent')">
        <el-cascader
          :key="cascaderKey"
          v-model="selectedCategory"
          expand-trigger="hover"
          :options="categoryList"
          :props="categoryTreeProps"
          :clearable="true"
          style="width: 100%"
          @change="handleChange"
        />
      </el-form-item>
      <el-form-item
        v-if="dataForm.type !== 2"
        :label="$t('hotSearch.seq')"
        prop="seq"
      >
        <el-input-number
          v-model="dataForm.seq"
          class="input-num"
          controls-position="right"
          :min="0"
          :max="100000000"
          :label="$t('hotSearch.seq')"
        />
      </el-form-item>
      <el-form-item
        :label="$t('product.status')"
        prop="status"
      >
        <el-radio-group v-model="dataForm.status">
          <el-radio :label="0">
            {{ $t("brand.offline") }}
          </el-radio>
          <el-radio :label="1">
            {{ $t("brand.normal") }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <div
          class="default-btn"
          @click="visible = false"
        >
          {{
            $t("remindPop.cancel")
          }}
        </div>
        <div
          class="default-btn primary-btn"
          @click="dataFormSubmit()"
        >
          {{
            $t("remindPop.confirm")
          }}
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { watch } from 'vue'

const emit = defineEmits(['refreshDataList'])

// 一级分类图片不必传
const validNoEmptyImg = (rule, value, callback) => {
  if (Data.selectedCategory.length > 0 && !value) {
    callback(new Error($t('category.categoryPicNull')))
  } else {
    callback()
  }
}

const Data = reactive({
  visible: false,
  isSecondLevel: false,
  dataForm: {
    categoryId: 0,
    grade: 0,
    categoryName: '',
    categoryNameEn: '',
    seq: 1,
    status: 1,
    parentId: 0,
    pic: ''
  },
  isRequiredImg: false, // 图片分类一级不必填
  cascaderKey: 'cascaderKey',
  isSubmit: false,
  dataRule: {
    pic: [
      { validator: validNoEmptyImg, trigger: 'blur' }
    ]
  },
  categoryList: [],
  selectedCategory: [],
  categoryTreeProps: {
    value: 'categoryId',
    label: 'categoryName',
    checkStrictly: true
  },
  // 语言列表
  langItemList: [],
  masterLangInfo: { name: '', lang: '' },
  // 当前所选语言
  curLang: [],
  categoryLangList: []
})

const { visible, isSecondLevel, dataForm, isRequiredImg, cascaderKey, dataRule, categoryList, selectedCategory, categoryTreeProps, langItemList, curLang, categoryLangList } = toRefs(Data)

watch(() => Data.dataForm, () => {
  if (Data.dataForm.type !== 2 && Data.isSecondLevel) {
    Data.dataForm.pic = ''
  }
}, { deep: true })

watch(() => Data.selectedCategory, (list) => {
  Data.isRequiredImg = !!(list && list.length)
})

const dataFormRef = ref()
let brandIds = []
const init = (id) => {
  Data.isSecondLevel = false
  Data.dataForm.parentId = 0
  Data.dataForm.categoryId = id || 0
  Data.isSubmit = false
  Data.dataForm.categoryName = ''
  Data.dataForm.categoryNameEn = ''
  Data.selectedCategory = []
  http({
    url: http.adornUrl(
      '/prod/category/upAndCurrCategoryList/' + Data.dataForm.categoryId
    ),
    method: 'get',
    params: http.adornParams({
      maxGrade: 1,
      status: 1
    })
  }).then(({ data }) => {
    Data.categoryList = treeDataTranslate(data, 'categoryId', 'parentId')
    Data.visible = true
    nextTick(() => {
      dataFormRef.value.resetFields()
      Data.selectedCategory = []
    })
    if (Data.dataForm.categoryId) {
      // 修改
      http({
        url: http.adornUrl(
          `/prod/category/info/${Data.dataForm.categoryId}`
        ),
        method: 'get',
        params: http.adornParams()
      }).then(({ data }) => {
        brandIds = data.brandIds
        Data.isSecondLevel = data.grade === 1
        Data.dataForm.categoryId = data.categoryId
        Data.dataForm.categoryName = data.categoryName
        Data.dataForm.seq = data.seq
        Data.dataForm.pic = data.pic
        Data.dataForm.parentId = data.parentId
        Data.dataForm.status = data.status
        Data.selectedCategory = idList(
          Data.categoryList,
          data.parentId,
          'categoryId',
          'children'
        ).reverse()
        Data.categoryLangList = data.categoryLangList
        getLangList()
      })
    } else {
      getLangList()
      Data.categoryLangList = []
    }
  })
}

// 获取国际化语言列表
const getLangList = () => {
  http({
    url: http.adornUrl('/platform/lang/default'),
    method: 'get',
    params: http.adornParams()
  }).then(({ data }) => {
    if (data) {
      const info = data
      // 主语言信息
      Data.masterLangInfo.name = info.name
      Data.masterLangInfo.lang = info.lang
      Data.langItemList = info.langItemList

      if (Data.dataForm.categoryId) {
        // 设置当前选择的语言
        const curLang = []
        const categoryLangList = []
        for (const item of Data.langItemList) {
          const fd = Data.categoryLangList.find(it => it.lang === item.lang)
          if (fd) {
            fd.langName = item.name
            categoryLangList.push(fd)
            curLang.push(item.lang)
          }
        }
        Data.categoryLangList = categoryLangList
        selectLang(curLang)
      } else {
        // 设置默认主语言
        selectLang([info.lang])
      }
    }
  })
}

// 多选框默认选择主语言
const selectLang = (value) => {
  Data.curLang = value
  // 设置主语言不可删除
  if (!Data.curLang.includes(Data.masterLangInfo.lang)) {
    Data.curLang.unshift(Data.masterLangInfo.lang)
  }
  // 分类名称
  const tempArr = Data.categoryLangList.filter(item => Data.curLang.includes(item.lang))
  Data.curLang.forEach((item, index) => {
    if (!tempArr.find(f => f.lang == item)) {
      const fd = Data.langItemList.find(it => it.lang === item)
      if (fd) {
        tempArr.splice(index, 0, { langName: fd.name, lang: item, categoryId: Data.dataForm.categoryId, categoryName: '' })
      }
    }
  })
  Data.categoryLangList = tempArr
}

const handleChange = (val) => {
  Data.isSecondLevel = val ? val.length === 1 : false
  Data.dataForm.parentId = val ? val[val.length - 1] : 0
  if (!Data.dataForm.parentId && Data.dataForm.parentId !== 0) {
    Data.dataForm.parentId = 0
  }
  Data.cascaderKey = Math.random() * 10000000000000
}

// 表单提交
const dataFormSubmit = () => {
  if (!Data.selectedCategory) {
    Data.selectedCategory = []
  }
  if (Data.selectedCategory.length === 0) {
    Data.dataForm.grade = 0
  }
  if (Data.selectedCategory.length === 1) {
    Data.dataForm.grade = 1
  }
  if (Data.selectedCategory.length === 2) {
    Data.dataForm.grade = 2
  }
  for (const item of Data.categoryLangList) {
    if (!item.categoryName?.trim()) {
      ElMessage.error($t('publics.categoryNoNull'))
      return
    }
  }
  dataFormRef.value.validate((valid) => {
    if (valid) {
      if (Data.isSubmit) {
        return false
      }
      Data.isSubmit = true
      http({
        url: http.adornUrl('/prod/category'),
        method: Data.dataForm.categoryId ? 'put' : 'post',
        data: http.adornData({
          categoryId: Data.dataForm.categoryId || undefined,
          status: Data.dataForm.status,
          seq: Data.dataForm.seq,
          grade: Data.dataForm.grade,
          parentId: Data.dataForm.parentId,
          pic: Data.dataForm.pic,
          categoryLangList: Data.categoryLangList,
          brandIds
        })
      }).then(() => {
        ElMessage({
          message: $t('remindPop.success'),
          type: 'success',
          duration: 1500,
          onClose: () => {
            Data.visible = false
            emit('refreshDataList')
            setTimeout(() => {
              Data.dataForm.pic = ''
            }, 500)
          }
        })
      }).catch((e) => {
        Data.isSubmit = false
      })
    }
  })
}

const close = () => {
  Data.dataForm = {
    categoryId: 0,
    grade: 0,
    categoryName: '',
    categoryNameEn: '',
    seq: 1,
    status: 1,
    parentId: 0,
    pic: ''
  }
}

const onValidateField = (field) => {
  dataFormRef.value?.validateField(field)
}

defineExpose({
  init
})
</script>

<style lang="scss" scoped>
.component-category-add-or-update {

  :deep(.input-num) {
    .el-input__inner {
      padding-right: 40px !important;
    }
  }

  .form-box {
    :deep(.el-form-item__label) {
      text-align: right;
      text-overflow: ellipsis;
      -o-text-overflow: ellipsis;
      -webkit-text-overflow: ellipsis;
      -moz-text-overflow: ellipsis;
      word-break: break-word;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      line-height: 20px;
      padding-top: 6px;
    }
  }
}

:deep(.el-form-item__content) {
  display: block;
}
</style>
