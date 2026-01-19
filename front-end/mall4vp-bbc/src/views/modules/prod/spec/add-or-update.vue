<template>
  <el-dialog
    v-model="visible"
    :title="!dataForm.propId ? $t('crud.addTitle') : $t('temp.modify')"
    destroy-on-close
    :close-on-click-modal="false"
    style="min-width: 700px;"
    top="50px"
  >
    <el-form
      :model="form"
      :label-width="$t('language') !== 'zh_CN' ? 'auto' : '80px'"
      :class="{'en-form':$t('language') !== 'zh_CN'}"
      class="page-spec"
      @submit.prevent
      @keyup.enter="onSubmit()"
    >
      <el-form-item
        v-if="langItemList.length > 1"
        :label="$t('product.chooseLanguage')"
      >
        <el-select
          v-model="curLang"
          multiple
          :placeholder="$t('tip.select')"
          style="width: 450px;"
          @change="selectLang"
        >
          <el-option
            v-for="item in langItemList"
            :key="item.lang"
            :label="item.name"
            :value="item.lang"
          />
        </el-select>
        <p style="font-size:12px;color:#999999;line-height:20px;">
          {{ $t('product.skuLangTips') }}
        </p>
      </el-form-item>

      <!-- 规格名称 -->
      <el-form-item
        :label="$t('product.attributeName')"
        :required="true"
      >
        <el-form
          v-for="(item,inx) in prodPropLangList"
          :key="inx"
          :class="['attr-name',{'isError': isError}]"
          :rules="proNamesRules"
          :model="item"
          @submit.prevent
        >
          <el-form-item
            prop="propName"
          >
            <el-input
              v-model.trim="item.propName"
              :placeholder="$t('product.attributeName') + (langItemList.length === 1 ? '' : `(${item.langName})`)"
              maxlength="10"
              show-word-limit
              clearable
              @blur="item.propName = handleInputSpaces(item.propName, 0)"
            />
          </el-form-item>
        </el-form>
        <p style="font-size:12px;color:#999999;line-height:20px;">
          {{ $t('product.attributeTips') }}
        </p>
      </el-form-item>

      <el-form-item
        :label="$t('product.attributeValue')"
        :required="true"
      >
        <div
          class="default-btn primary-btn"
          @click="add"
        >
          {{ $t('admin.add') }}
        </div>
        <p style="font-size:12px;color:#999999;line-height:20px;">
          {{ $t('product.attributeValueTips') }}
        </p>
      </el-form-item>

      <!-- 规格值 -->
      <el-form-item
        label=" "
        prop="prodPropValues"
      >
        <el-form
          v-for="(item,index) in form.prodPropValues"
          :key="index"
          class="attr-value-box"
          :model="item"
          @submit.prevent
          @keyup.enter="onSubmit()"
        >
          <div class="left-box">
            <el-form-item prop="prodPropValueLangList">
              <el-form
                v-for="(prop,propInx) in item.prodPropValueLangList"
                :key="propInx"
                class="attr-value"
                :rules="dataItemRules"
                :model="prop"
                @submit.prevent
              >
                <!--规格值-->
                <el-form-item prop="propValue">
                  <el-input
                    v-model.trim="prop.propValue"
                    :class="{'isValError': validateWord3(prop.propValue)}"
                    maxlength="20"
                    clearable
                    :placeholder="$t('product.attributeValue') + (langItemList.length === 1 ? '' : `(${prop.langName})`)"
                  />
                </el-form-item>
              </el-form>
            </el-form-item>
          </div>
          <div
            class="value-del-btn"
            @click="deleteRow(index)"
          >
            {{ $t('resource.Delete') }}
          </div>
        </el-form>
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <div
          class="default-btn"
          @click="visible = false"
        >
          {{ $t("crud.filter.cancelBtn") }}
        </div>
        <div
          class="default-btn primary-btn"
          @click="onSubmit()"
        >
          {{ $t("crud.filter.submitBtn") }}
        </div>
      </div>
    </template>
  </el-dialog>
</template>
<script setup>
import { ElMessage } from 'element-plus'
const emit = defineEmits(['refreshDataList'])

// 表单
const form = ref({
  prodPropValues: [
    {
      prodPropLangList: [
        { propValue: '' }
      ]
    }
  ]
})

/**
 * 检查 是否包含特殊字符 :;
 * @param rule
 * @param value
 * @param callback
 */
const validateWord2 = (rule, value, callback) => {
  const reg = /[\\;\\:\\；\\：]/g
  if (reg.test(value)) {
    callback(new Error($t('product.specName') + $t('product.specialWordSymbolTips') + ':;'))
    return
  }
  callback()
}
/**
 * 规格名称 校验
 */
const proNamesRules = reactive({
  propName: [
    { validator: validateWord2, trigger: 'blur' }
  ]
})

const validateWord3 = (value) => {
  const regVal = /[\\;\\；]/g
  const negativeNumStr = '-'
  if (regVal.test(value)) {
    return true
  }
  if (value?.search(negativeNumStr) !== -1) {
    return true
  }
  return false
}

/**
 * 检查 是否包含特殊字符 ;；-
 * @param rule
 * @param value
 * @param callback
 */
const validateWord1 = (rule, value, callback) => {
  const regVal = /[\\;\\；]/g
  const negativeNumStr = '-'
  if (regVal.test(value)) {
    callback(new Error($t('product.specValue') + $t('product.specialWordSymbolTips') + ';'))
    return true
  }
  if (value?.search(negativeNumStr) !== -1) {
    callback(new Error($t('product.specValue') + $t('product.specialWordSymbolTips') + '-'))
    return true
  }
  callback()
}
/**
 * 规格值 校验
 */
const dataItemRules = reactive({
  propValue: [
    { validator: validateWord1, trigger: 'blur' }
  ]
})

const page = reactive({
  total: 0, // 总页数
  currentPage: 1, // 当前页数
  pageSize: 10 // 每页显示多少条
})
const visible = ref(false)
let isSubmit = false
const dataForm = ref({ // 表单数据
  propId: 0
})
const prodPropLangList = ref([]) // 语言
const init = (val) => {
  isSubmit = false
  if (val) {
    dataForm.value = JSON.parse(JSON.stringify(val))
    prodPropLangList.value = dataForm.value.prodPropLangList
    form.value.prodPropValues = dataForm.value.prodPropValues
  } else {
    dataForm.value = {
      propId: 0
    }
    prodPropLangList.value = []
    form.value.prodPropValues = [{
      propId: dataForm.value.propId,
      prodPropValueLangList: [],
      propValue: '',
      propValueEn: ''
    }]
  }
  // 获取国际化配置语言
  getLangList()
  visible.value = true
}

const langItemList = ref([]) // 语言列表
const masterLangInfo = reactive({ name: '', lang: '' })
const curLang = ref([]) // 当前所选语言
const getLangList = () => {
  http({
    url: http.adornUrl('/platform/lang/default'),
    method: 'get',
    params: http.adornParams()
  })
    .then(({ data }) => {
      if (data) {
        const info = data
        masterLangInfo.name = info.name
        masterLangInfo.lang = info.lang
        langItemList.value = info.langItemList

        // 设置默认主语言
        if (!prodPropLangList.value.length) {
          selectLang([info.lang])
        } else {
          const curLang = []
          // 初始化所选语言
          const temPprodPropLangList = []
          for (const item of langItemList.value) {
            const fd = prodPropLangList.value.find(it => it.lang === item.lang)
            if (fd) {
              fd.langName = item.name
              temPprodPropLangList.push(fd)
              curLang.push(item.lang)
            }
          }
          prodPropLangList.value = temPprodPropLangList
          // 初始化规格值
          form.value.prodPropValues?.forEach((item, index) => {
            form.value.prodPropValues[index] = handlerListSort(form.value.prodPropValues[index])
          })
          for (const item of form.value.prodPropValues) {
            for (const prop of item.prodPropValueLangList) {
              const fd = langItemList.value.find(it => it.lang === prop.lang)
              if (fd) {
                prop.langName = fd.name
              }
            }
          }
          selectLang(curLang)
        }
      }
    })
}

/**
 * 修改排序，把列表中的默认数据项，移动到开头
 * @param list
 * @returns {*}
 */
const handlerListSort = (list) => {
  const { prodPropValueLangList } = list
  if (prodPropValueLangList.length) {
    const idx = prodPropValueLangList.findIndex(item => (item.lang === masterLangInfo.lang))
    const topItem = prodPropValueLangList[idx]
    prodPropValueLangList.splice(idx, 1)
    prodPropValueLangList.unshift(topItem)
    list.prodPropValueLangList = prodPropValueLangList
  }
  return list
}

/**
 * 输入框纯空格处理
 */
const handleInputSpaces = (value, type) => {
  if (!value) return
  value = specInputLimit(value, type)
  return value.trim()
}

const isError = ref(false) // 规格名称校错标记
/**
 * 输入特殊字符处理
 */
const specInputLimit = (value, type = 0) => {
  const reg = /[\\;\\:\\；\\：]/g
  const regVal = /[\\;\\；]/g
  const negativeNumStr = '-'
  if (type === 0) { // 规格值
    if (reg.test(value)) {
      isError.value = true
      // ElMessage.error($t('product.specName') + $t('product.specialWordSymbolTips') + ':;')
      return value
    }
    // 规格名称校验
    for (const item of prodPropLangList.value) {
      // 特殊字符
      const regVal = /[\\;\\:\\；\\：]/g
      if (regVal.test(item.propName)) {
        isError.value = true
        return value
      }
    }
  } else { // 规格名称
    if (regVal.test(value)) {
      // ElMessage.error($t('product.specValue') + $t('product.specialWordSymbolTips') + ';')
      return value
    }
    if (value.search(negativeNumStr) !== -1) {
      // ElMessage.error($t('product.specValue') + $t('product.specialWordSymbolTips') + '-')
      return value
    }
  }

  isError.value = false
  return value
}

/**
 * 多选框默认选择中文
 * @param value
 */
const selectLang = (value) => {
  curLang.value = value
  // 设置主语言不可删除
  if (!curLang.value.includes(masterLangInfo.lang)) {
    curLang.value.unshift(masterLangInfo.lang)
  }
  // 规格名
  const tempArr = prodPropLangList.value.filter(item => curLang.value.includes(item.lang))
  curLang.value.forEach((item, index) => {
    if (!tempArr.find(f => f.lang == item)) {
      const fd = langItemList.value.find(it => it.lang === item)
      if (fd) {
        tempArr.splice(index, 0, { langName: fd.name, lang: item, propId: dataForm.value.propId, propName: '' })
      }
    }
  })
  prodPropLangList.value = tempArr

  // 规格值
  form.value.prodPropValues.forEach(prop => {
    const flList = prop.prodPropValueLangList.filter(item => curLang.value.includes(item.lang))
    curLang.value.forEach((item, index) => {
      if (!flList.find(f => f.lang == item)) {
        const fd = langItemList.value.find(it => it.lang === item)
        if (fd) {
          flList.splice(index, 0, { langName: fd.name, lang: item, propValue: '' })
        }
      }
    })
    prop.prodPropValueLangList = flList
  })
}

/**
 * 表单提交
 */
const onSubmit = () => {
  // 是否存在未填的规格（规格值）
  for (const item of form.value.prodPropValues) {
    for (const propItem of item.prodPropValueLangList) {
      // propItem.propValue = propItem.propValue.trim()
      // 判空
      if (!propItem.propValue) {
        ElMessage.error($t('product.specValueCannotBeEmpty'))
        return
      }
      // 特殊字符
      const regVal = /[\\;\\；]/g
      if (regVal.test(propItem.propValue) || propItem.propValue.search('-') !== -1) {
        return
      }
    }
  }

  // 判断是否有相同的属性值
  for (let i = 0; i < form.value.prodPropValues.length; i++) {
    for (let j = i + 1; j < form.value.prodPropValues.length; j++) {
      // 规格
      for (let k = 0; k < form.value.prodPropValues[i].prodPropValueLangList.length; k++) {
        if (form.value.prodPropValues[i].prodPropValueLangList[k].propValue === form.value.prodPropValues[j].prodPropValueLangList[k].propValue) {
          ElMessage.error($t('product.same'))
          return
        }
      }
    }
  }
  // 规格名称校验
  for (const item of prodPropLangList.value) {
    if (!item.propName) {
      ElMessage.error($t('product.attributeNameNoNull'))
      return
    }
    // 特殊字符
    const regVal = /[\\;\\:\\；\\：]/g
    if (regVal.test(item.propName)) {
      return
    }
  }
  if (isSubmit) {
    return false
  }
  isSubmit = true

  const params = {
    propId: dataForm.value.propId || 0,
    propName: prodPropLangList.value[0].propName,
    prodPropLangList: prodPropLangList.value,
    prodPropValues: form.value.prodPropValues
  }

  http({
    url: http.adornUrl('/prod/spec'),
    method: dataForm.value.propId ? 'put' : 'post',
    data: http.adornData(params)
  })
    .then(() => {
      ElMessage({
        message: $t('publics.operation'),
        type: 'success',
        duration: 1500,
        onClose: () => {
          visible.value = false
          emit('refreshDataList', page)
        }
      })
    })
    .catch(() => {
      isSubmit = false
    })
}
/**
 * 添加一行规格
 */
const add = () => {
  for (const item of form.value.prodPropValues) {
    for (const prop of item.prodPropValueLangList) {
      // 判空
      if (!prop.propValue) {
        ElMessage.error($t('product.specValueCannotBeEmpty'))
        return
      }
      // 特殊字符
      const regVal = /[\\;\\；]/g
      if (regVal.test(prop.propValue) || prop.propValue.search('-') !== -1) {
        return
      }
    }
  }
  // 添加行
  const prodPropValueLangList = []
  curLang.value.forEach(item => {
    const fd = langItemList.value.find(it => it.lang === item)
    prodPropValueLangList.push({ langName: fd.name, lang: item, propValue: '' })
  })
  form.value.prodPropValues.unshift({
    propId: dataForm.value.propId,
    propValue: '',
    propValueEn: '',
    prodPropValueLangList
  })
}
/**
 * 删除一行规格
 */
const deleteRow = (index) => {
  if (form.value.prodPropValues.length <= 1) {
    ElMessage.error($t('product.attributeNotDel'))
    return
  }
  form.value.prodPropValues.splice(index, 1)
}

defineExpose({
  init
})

</script>

<style lang="scss" scoped>
.page-spec{
  :deep(.el-form-item__content) {
    display: block;
  }
  .attr-name{
    width: 230px;
    display: inline-block;
    padding: 6px 12px;
    border: 1px solid #e8eef5;

  }
  // 出现提示
  .isError {
    padding-bottom: 16px;
  }
}

.attr-value-box{
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #e8eef5;
  .left-box{
    max-width: 500px;
    .attr-value{
      width: 230px;
      display: inline-block;
      padding-right: 12px;
    }
  }
  :deep(.left-box:has(.isValError)) {
    .attr-value{
      padding-bottom: 4px;
    }
    .attr-value:nth-last-child(n+3){
      padding-bottom: 16px;
    }
  }
  .value-del-btn{
    color: #447cdd;
    cursor: pointer;
  }
}
.en-form :deep(.el-form-item__label){
 word-break: break-word;
}
</style>
