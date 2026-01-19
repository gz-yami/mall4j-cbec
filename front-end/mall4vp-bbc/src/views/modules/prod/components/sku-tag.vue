<template>
  <div class="component-sku-tag">
    <el-form-item
      :label="$t('product.productSpecifi')"
      :label-width="$t('language') !== 'zh_CN'?'auto':'130px'"
    >
      <div class="sku-border">
        <!-- 新增的商品sku tag -->
        <div
          v-for="(tag, tagIndex) in skuTags"
          :key="tag.tagName"
          class="tagTree"
        >
          <div class="tag">
            <!-- 规格名 -->
            <div class="sku-background spec-name-box">
              <div class="spec-name">
                <div class="tag-item-tit">
                  {{ $t('group.skuName') }}
                </div>
                <div>
                  <div>
                    <el-select
                      :model-value="tag.tagName"
                      filterable
                      default-first-option
                      allow-create
                      :placeholder="$t('tip.select')"
                      @change="changeTagName($event, tagIndex)"
                    >
                      <el-option
                        v-for="item in unUseTags"
                        :key="item.propId"
                        :label="item.propName"
                        :value="item.propName"
                      />
                    </el-select>
                    <!-- 添加图片 -->
                    <span
                      v-if="tagIndex === 0"
                      class="add-img"
                    >
                      <el-checkbox
                        v-model="addImgchecked"
                        @change="handleAddImgchecked(tagIndex)"
                      >{{ $t('product.addSpecPic') }}</el-checkbox>
                    </span>
                    <div class="del-spec-btn">
                      <el-icon @click="removeTag(tagIndex)">
                        <CircleCloseFilled />
                      </el-icon>
                    </div>
                  </div>

                  <div
                    v-if="tagIndex === 0"
                    class="first-tag-tips"
                  >
                    {{ $t('product.createNewSpecNameManually') }}
                  </div>
                </div>
              </div>
            </div>
            <!-- 规格值 -->
            <div
              v-if="tag.tagName"
              class="spec-val"
            >
              <div class="tag-item-tit">
                {{ $t('product.specificationValue') }}
              </div>
              <div class="tag-item-box">
                <div
                  class="item-list"
                  :class="{ 'ft-spec-val': tagIndex === 0 }"
                >
                  <div
                    v-for="(tagItem, tagItemIndex) in tag.tagItems"
                    :key="tagItem.valueId"
                    class="spec-val-item"
                    :class="{ 'ft-spec-val-item': tagIndex === 0 && !tagItem.creating && addImgchecked }"
                  >
                    <div class="item">
                      <div class="prop-value">
                        <div class="text-hid">
                          {{ tagItem.propValue }}
                        </div>
                        <!-- 第一个规格允许上传图片 -->
                        <div
                          v-if="tagIndex === 0 && !tagItem.creating && addImgchecked"
                          class="up-box"
                        >
                          <div class="img-upload">
                            <pic-upload
                              v-model="tagItem.pic"
                              @update:model-value="changeTagItemPic($event, tagItem, tagIndex, tagItemIndex)"
                            />
                          </div>
                          <div
                            v-if="tagItem.pic"
                            class="default-btn text-btn"
                            @click="delTagItemPic(tagIndex, tagItemIndex)"
                          >
                            {{ $t('text.delBtn') }}
                          </div>
                        </div>
                      </div>
                      <div
                        class="del-btn"
                        @click="handleTagClose(tagIndex, tagItemIndex)"
                      >
                        <el-icon><Close /></el-icon>
                      </div>
                    </div>
                  </div>

                  <!-- 添加规格值 -->
                  <div class="add-item">
                    <el-select
                      v-if="tagItemInputs[tagIndex] && tagItemInputs[tagIndex].visible"
                      :ref="`saveTagInput${tagIndex}`"
                      v-model="tagItemInputs[tagIndex].value"
                      :teleported="false"
                      popper-class="tagItem-select"
                      filterable
                      allow-create
                      default-first-option
                      :placeholder="$t('tip.select')"
                      class="input-new-tag"
                      @visible-change="addSkuTagItem($event, tagIndex)"
                    >
                      <el-option
                        v-for="item in getTagValues(tag.tagName, tag.tagItems)"
                        :key="item.valueId"
                        :label="item.propValue"
                        :value="item"
                      />
                    </el-select>
                    <el-icon
                      v-else
                      class="add-btn"
                      :class="{'disable': !tag.showAddTagBtn}"
                      @click="showTagInput(tagIndex)"
                    >
                      <CirclePlus />
                    </el-icon>
                  </div>
                  <!-- 添加规格值 / -->
                </div>
                <div
                  v-if="tagIndex === 0"
                  class="img-tips"
                >
                  {{ $t('product.postProductTips12') }}
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 添加商品规格按钮 -->
        <div class="sku-background">
          <div
            class="default-btn add-sku-btn"
            @click="addskuTag()"
          >
            {{ $t("product.addSpecifications") }}
          </div>
          <span
            v-if="!skuTags.length"
            class="tips"
          >{{ $t("product.postProductTips13") }}</span>
          <span
            v-if="skuTags.length"
            class="tips"
          >({{ $t('shopProcess.mostAdd') + maxNumOfCombo + $t('shopProcess.group') }})</span>
        </div>
      </div>
    </el-form-item>
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { validNoEmptySpace } from '@/utils/validate'
import { getUUID } from '@/utils'

import { useProdStore } from '@/stores/prod.js'

const prodStore = useProdStore()

const emit = defineEmits(['change', 'resetInit', 'clearSkuImg', 'changeSkuImg'])

const props = defineProps({
  skuList: {
    type: Array,
    default: () => ([])
  }
})

let type = 0
let tagItemName = ''
let tagName = ''
let tagNameIndex = 0

const tagItemInputs = reactive([])
// 数据库中的规格
const dbTags = ref([])
// 根据选定的规格所查询出来的规格值
const dbTagValuesMap = {}
let initing = false

const addImgchecked = ref(true)

// 最大规格组合数量
const maxNumOfCombo = ref(100)
const masterLangInfo = reactive({ name: '', lang: 0 })

// 未使用的规格
const unUseTags = computed(() => {
  const res = []
  for (let i = 0; i < dbTags.value.length; i++) {
    const dbTag = dbTags.value[i]
    const specIndex = skuTags.value.findIndex(tag => tag.tagName === dbTag.propName)
    if (specIndex === -1) {
      res.push(dbTag)
    }
  }
  return res
})
const skuTags = computed({
  get () {
    return prodStore.skuTags
  },
  set (val) {
    prodStore.updateSkuTags(val)
  }
})
const defalutSku = computed(() => {
  return prodStore.defalutSku
})

// 计算规格组合数量
const countSkuTagCombo = computed(() => {
  // 计算当前规格组合数量
  const TempSkuTags = skuTags.value.filter(el => el.tagItems && el.tagItems.length)
  return TempSkuTags.length ? TempSkuTags.map(tag => (tag.tagItems && tag.tagItems.length)).reduce((a, b) => a * b) : 0
})

watch(() => skuTags.value, (val) => {
  handlerTagLength(val)
  if (initing) {
    initing = false
    return
  }
  let skuList = []
  if (type === 4) {
    // 删除规格值
    props.skuList.forEach((sku) => {
      const propertiesArray = sku.properties.split(';')
      if (propertiesArray[tagNameIndex] && tagItemName !== propertiesArray[tagNameIndex].substring(propertiesArray[tagNameIndex].indexOf(':') + 1)) {
        skuList.push(sku)
      }
    })
  } else if (type === 2) {
    // 添加规格值
    const properties = tagName + ':' + tagItemName
    // 增加或删除规格
    let tempSkuList = []
    val.forEach(tag => {
      if (skuList.length === 0) {
        if (tagName === tag.tagName) {
          const sku = Object.assign({}, defalutSku.value)
          sku.properties = properties // 销售属性组合字符串
          skuList.push(sku)
        } else {
          tag.tagItems.forEach(tagItem => {
            const sku = Object.assign({}, defalutSku.value)
            sku.properties = `${tag.tagName}:${tagItem.propValue}` // 销售属性组合字符串
            skuList.push(sku)
          })
        }
        if (val.length === 1) {
          skuList = props.skuList.concat(skuList)
        }
      } else {
        tempSkuList = []
        if (tagName === tag.tagName) {
          skuList.forEach(sku => {
            if (!slidingCheckTagName(sku.properties, tagName)) {
              const newSku = Object.assign({}, sku)
              newSku.properties = `${sku.properties};${properties}`
              tempSkuList.push(newSku)
            }
          })
        } else {
          tag.tagItems.forEach(tagItem => {
            skuList.forEach(sku => {
              if (!slidingCheckTagName(sku.properties, tag.tagName)) {
                const newSku = Object.assign({}, sku)
                newSku.properties = `${sku.properties};${tag.tagName}:${tagItem.propValue}`
                tempSkuList.push(newSku)
              }
            })
          })
        }
        skuList = props.skuList.concat(tempSkuList)
      }
    })
  } else if (type === 5) {
    // 新增/更改/删除图片
    return
  } else {
    // 增加或删除规格
    let tempSkuList = []
    val.forEach(tag => {
      if (skuList.length === 0) {
        tag.tagItems.forEach(tagItem => {
          const sku = Object.assign({}, defalutSku.value)
          sku.properties = `${tag.tagName}:${tagItem.propValue}` // 销售属性组合字符串
          if (tagItem.pic) {
            sku.pic = tagItem.pic
          }
          skuList.push(sku)
        })
      } else {
        tempSkuList = []
        skuList.forEach(sku => {
          tag.tagItems.forEach(tagItem => {
            const newSku = Object.assign({}, sku)
            newSku.properties = `${sku.properties};${tag.tagName}:${tagItem.propValue}`
            if (tagItem.pic) {
              newSku.pic = tagItem.pic
            }
            tempSkuList.push(newSku)
          })
        })
        skuList = tempSkuList
      }
    })
  }
  if (!skuList.length) {
    skuList.push(Object.assign({}, defalutSku.value))
  }
  emit('change', skuList, val, type)
}, { deep: true })

onMounted(() => {
  masterLangInfo.name = 'Default'
  masterLangInfo.lang = 0
  // 获取规格信息
  http({
    url: http.adornUrl('/prod/spec/list'),
    method: 'get',
    params: http.adornParams()
  }).then(({ data }) => {
    dbTags.value = data

    if (props.skuList.length) {
      emit('resetInit', props.skuList)
    }
  })
})
onDeactivated(() => {
  // 离开发布商品页清理tag标签
  prodStore.removeSkuTag(-1)
})

const init = (skuList) => {
  if (!skuList || !skuList.length) {
    skuTags.value = []
    emit('change', [Object.assign({}, defalutSku.value)])
    return
  }
  initing = true
  const tempSkuTags = []
  let isIncludeImg = false
  for (let i = 0; i < skuList.length; i++) {
    const sku = skuList[i]
    if (!sku.properties) break
    const propertiesArray = sku.properties.split(';')
    const imgSrc = sku.pic
    isIncludeImg = (imgSrc && imgSrc !== '') || isIncludeImg
    for (const j in propertiesArray) {
      const cnProperties = [propertiesArray[j].slice(0, propertiesArray[j].indexOf(':')), propertiesArray[j].substring(propertiesArray[j].indexOf(':') + 1)]
      if (!tempSkuTags[j]) {
        tempSkuTags[j] = {
          tagName: cnProperties[0],
          tagItems: []
        }
      }
      const tagItemNameIndex = tempSkuTags[j].tagItems.findIndex((tagItemName) => tagItemName.propValue === cnProperties[1])
      if (tagItemNameIndex === -1) {
        const extarlInfo = +j === 0 ? { pic: imgSrc } : {}
        tempSkuTags[j].tagItems.push({
          propValue: cnProperties[1],
          ...extarlInfo
        })
      }
    }
  }
  addImgchecked.value = isIncludeImg
  tagItemInputs.push({ visible: false, value: '' })
  handlerTagLength(tempSkuTags)
  skuTags.value = tempSkuTags
}

/**
 * 计算规格组合数量，判断是否允许新增
 */
const handlerTagLength = (val) => {
  const tempSkuTags = val.filter(el => el.tagItems && el.tagItems.length)
  val.forEach((tag, idx) => {
    const ln = tempSkuTags.length ? tempSkuTags.map(tag => (tag.tagItems && tag.tagItems.length)) : []
    ln[idx] = ln[idx] ? ln[idx] + 1 : ''
    const count = ln.length ? ln.reduce((a, b) => a * b) : 0
    tag.showAddTagBtn = count <= maxNumOfCombo.value
  })
}

/**
 * 从properties属性中查找是否存在名称为参数tagName的规格名
 */
const slidingCheckTagName = (properties, tagName) => {
  if (tagName === '' && (properties.indexOf(';:') !== -1)) {
    return true
  }
  if (!tagName || tagName === '' || !properties || properties === '') {
    return false
  }
  let leftIndex = 0
  for (let i = 0; i < properties.length; i++) {
    leftIndex = i
    while (i < properties.length && properties.charAt(i) !== ':' && properties.charAt(i) !== ';') {
      ++i
    }
    if (properties.charAt(i) === ';' || (i - leftIndex) !== tagName.length || i >= properties.length) {
      continue
    }
    let j = 0
    while (properties.charAt(leftIndex++) === tagName.charAt(j)) {
      if (j + 1 === tagName.length) {
        return true
      }
      ++j
    }
  }
  return false
}

const handleAddImgchecked = (tagIndexPar) => {
  if (!addImgchecked.value) {
    // 不添加规格图片时把图片属性置空
    if (skuTags.value[tagIndexPar] && skuTags.value[tagIndexPar].tagItems) {
      for (let i = 0; i < skuTags.value[tagIndexPar].tagItems.length; i++) {
        const newSkuTagItem = {
          ...skuTags.value[tagIndexPar].tagItems[i],
          pic: ''
        }
        prodStore.updateSkuTagItem({
          tagIndex: tagIndexPar, tagItemIndex: i, skuTagItem: newSkuTagItem
        })
      }
      for (let i = 0; i < props.skuList.length; i++) {
        // eslint-disable-next-line vue/no-mutating-props
        props.skuList[i].pic = ''
      }
      type = 5
      emit('change', props.skuList, skuTags.value, type)
      emit('clearSkuImg')
    }
  }
}
/**
 * 添加规格名
 */
const addskuTag = () => {
  if (skuTags.value.find(tag => !tag.tagName ||
      !tag.tagItems.length ||
        tag.tagItems.find(tagItem => !tagItem.propValue))) {
    ElMessage({
      message: $t('product.completeTheAddedSpec'),
      type: 'error',
      duration: 1000
    })
    return
  }
  prodStore.addSkuTag({
    tagName: '',
    tagItems: []
  })
  type = 1
}

/**
 * 修改规格名
 */
const changeTagName = (selVal, index) => {
  let useTag = unUseTags.value.find(el => selVal === el.propName || selVal === el.propNameEn)
  const is = Object.prototype.toString.call(useTag) === '[object Object]'
  const reg = /[\\;\\:\\；\\：]/g
  if (!is) {
    // 纯空格校验
    if (validNoEmptySpace(selVal)) {
      ElMessage.error($t('shopProcess.inputAllSpace'))
      return
    }
    if (reg.test(selVal)) {
      ElMessage.error($t('product.specName') + $t('product.specialWordSymbolTips') + ':;')
      return
    }
    // 替换:;字符
    // selVal = selVal.replace(/[\\;\\:]/g, '')
    // 判断规格名是否超长
    if (selVal.length > 10) {
      ElMessage.error($t('product.postProductTips11'))
      return
    }
    // 判断规格名是否重复
    if (skuTags.value.find(el => el.tagName === selVal)) {
      ElMessage.error($t('group.skuName') + '\'' + selVal + '\' ' + $t('product.isExistsPleaReEn'))
      return
    }
    useTag = {
      propName: selVal,
      propNameEn: selVal
    }
  }
  const oldSkuTag = skuTags.value[index]
  const oldValue = oldSkuTag.tagName
  if (selVal !== oldValue) {
    // 规格发生变化
    type = 3
  }
  if (useTag && oldSkuTag) {
    const newSkuTag = {
      tagId: useTag.propId,
      tagName: useTag.propName,
      tagItems: []
    }
    prodStore.updateSkuTag({
      index, skuTag: newSkuTag
    })
    if (is) {
      getDbTagValues(useTag.propId, useTag.propName)
    }
  }
}

// 添加规格值
const addSkuTagItem = (event, tagIndexPar) => {
  const reg = /[\\;；]/g
  const itemValue = tagItemInputs[tagIndexPar].value
  if (!event && itemValue) {
    let tagItem = {}
    const index = skuTags.value[tagIndexPar].tagItems.length - 1
    tagName = skuTags.value[tagIndexPar].tagName
    const is = Object.prototype.toString.call(itemValue) === '[object Object]'
    if (is) {
      tagItemName = itemValue.propValue
      tagItem = {
        propId: index === -1 ? 0 : skuTags.value[tagIndexPar].tagItems[index].propId,
        propValue: itemValue.propValue,
        propValueEn: itemValue.propValueEn,
        valueId: itemValue.valueId
      }
    } else {
      // 纯空格校验
      if (validNoEmptySpace(itemValue)) {
        ElMessage.error($t('shopProcess.inputAllSpace'))
        tagItemInputs[tagIndexPar].value = ''
        tagItemInputs[tagIndexPar].visible = true
        return
      }
      if (reg.test(itemValue)) {
        ElMessage.error($t('product.specValue') + $t('product.specialWordSymbolTips') + ';')
        tagItemInputs[tagIndexPar].value = ''
        tagItemInputs[tagIndexPar].visible = true
        return
      }
      // 替换:;字符
      // itemValue = itemValue.replace(/[\\;\\:]/g, '')
      // 规格值是否超长
      if (itemValue.length > 20) {
        ElMessage.error($t('product.specValueCharacterLength'))
        tagItemInputs[tagIndexPar].value = ''
        tagItemInputs[tagIndexPar].visible = true
        return
      }
      // 校验规格值是否重复
      if (checkTagItem(skuTags.value[tagIndexPar], tagIndexPar)) {
        tagItemInputs[tagIndexPar].value = ''
        tagItemInputs[tagIndexPar].visible = true
        return
      }
      tagItemName = itemValue
      const maxValue = getMaxValueId(skuTags.value[tagIndexPar].tagItems)
      tagItem = {
        propId: index === -1 ? 0 : skuTags.value[tagIndexPar].tagItems[index].propId,
        propValue: itemValue,
        propValueEn: itemValue,
        valueId: index === -1 ? 0 : maxValue ? (maxValue + 1) : getUUID()
      }
    }
    if (tagItem) {
      prodStore.addSkuTagItem({ tagIndex: tagIndexPar, tagItem })
      tagItemInputs[tagIndexPar].visible = false
      tagItemInputs[tagIndexPar].value = ''
      type = 2
    }
  }
  if (!event && !itemValue && tagItemInputs[tagIndexPar].visible) {
    tagItemInputs[tagIndexPar].visible = false
    tagItemInputs[tagIndexPar].value = ''
  }
}

// 修改图片
const changeTagItemPic = (value, skuTagItem, tagIndexPar, tagItemIndex) => {
  const skuTag = skuTags.value[tagIndexPar]
  if (skuTag && skuTagItem) {
    const newSkuTagItem = {
      ...skuTagItem,
      pic: value
    }
    prodStore.updateSkuTagItem({
      tagIndex: tagIndexPar, tagItemIndex, skuTagItem: newSkuTagItem
    })
    type = 5
    // 把对应的sku图片修改
    for (let i = 0; i < props.skuList.length; i++) {
      if (!props.skuList[i].properties) {
        continue
      }
      const pName = (props.skuList[i].properties.split(';'))[0]
      const properties = pName.substring(pName.indexOf(':') + 1)
      if (properties === skuTagItem.propValue) {
        // eslint-disable-next-line vue/no-mutating-props
        props.skuList[i].pic = value
      }
    }
    emit('changeSkuImg', skuTagItem.propValue, value)
  }
}
const delTagItemPic = (tagIndexPar, tagItemIndex) => {
  const skuTagItem = Object.assign({}, skuTags.value[tagIndexPar].tagItems[tagItemIndex])
  skuTagItem.pic = ''
  changeTagItemPic('', skuTagItem, tagIndexPar, tagItemIndex)
  type = 5
}
/**
 * 获取对应规格名下的规格值列表
 */
const getTagValues = (key, usedItems) => {
  const fd = dbTags.value.find(fdItem => fdItem.propName === key)
  if (fd) {
    const result = fd.prodPropValues
    // 过滤出未被选择的规格值
    const propValues = usedItems.map(el => el.propValue)
    return result.filter(item => !propValues.includes(item.propValue))
  } else {
    return []
  }
}
/**
 * 获取listSpecValue并设置到dbTagValuesMap
 */
const getDbTagValues = (propId, tagName) => {
  if (dbTagValuesMap[tagName]) return
  http({
    url: http.adornUrl(`/prod/spec/listSpecValue/${propId}`),
    method: 'get',
    params: http.adornParams()
  }).then(({ data }) => {
    dbTagValuesMap[tagName] = data
  })
}
// 关闭标签 --删除
const handleTagClose = (tagIndexPar, tagItemIndex) => {
  tagName = skuTags.value[tagIndexPar].tagName
  tagNameIndex = tagIndexPar
  tagItemName = skuTags.value[tagIndexPar].tagItems[tagItemIndex].propValue
  type = 4
  prodStore.removeSkuTagItem({ tagIndex: tagIndexPar, tagItemIndex })
}
// 显示标签输入框
const showTagInput = (tagIndex) => {
  // 删除没有填写内容的规格输入框
  for (let i = 0; i < tagItemInputs.length; i++) {
    tagItemInputs[i].visible = false
  }
  // 判断是否允许添加
  if (!skuTags.value[tagIndex].showAddTagBtn) {
    ElMessage.error(
      `${$t('product.currentlyAdded') +
      countSkuTagCombo.value +
      $t('product.productSpecCount')}（${$t('shopProcess.mostAdd') +
      maxNumOfCombo.value +
      $t('product.groups')}），${$t('product.cannotAddUnderThisSpec')}`
    )
    return
  }
  tagItemInputs.push({ visible: false, value: '' })
  tagItemInputs[tagIndex] = { visible: true, value: '' }
  const useTag = dbTags.value.find(el => el.propName === skuTags.value[tagIndex].tagName)
  const is = Object.prototype.toString.call(useTag) === '[object Object]'
  if (is) {
    getDbTagValues(useTag.propId, useTag.propName)
  }
}
// 删除 规格
const removeTag = (tagIndex) => {
  type = 3
  prodStore.removeSkuTag(tagIndex)
}
// 获取数据集合所有对象中某个属性的最大值
const getMaxValueId = (list) => {
  return Math.max.apply(Math, list.map(item => item.valueId))
}
/**
 * 新增规格值时，判断是否存在同名的规格值
 */
const checkTagItem = (tag, tagIndex) => {
  const tagItem = tagItemInputs[tagIndex].value
  if (!tagItem) {
    tagItemInputs[tagIndex].visible = false
    tagItemInputs[tagIndex].value = ''
    return true
  }
  let isSame = false
  const arr = tag.tagItems.map((item) => {
    return $t('language') !== 'zh_CN' ? item.propValueEn : item.propValue
  })
  if (arr.indexOf(tagItem) !== -1) {
    isSame = true
    ElMessage.error($t('product.specificationValue') + '\'' + tagItem + '\' ' + $t('product.isExistsPleaReEn'))
  }
  return isSame
}

/**
 * 校验规格列表
 */
const vaildSkuTag = () => {
  let isValid = true
  if (countSkuTagCombo.value > maxNumOfCombo.value) {
    ElMessage.error(
      `${$t('shopProcess.currentlyAdded') +
      countSkuTagCombo.value +
      $t('shopProcess.productSpecCount')}，${$t('shopProcess.mostAdd') +
      maxNumOfCombo.value +
      $t('shopProcess.groups')}`
    )
    return false
  }
  if (skuTags.value && skuTags.value.length) {
    for (const sku of skuTags.value) {
      if (!sku.tagName) {
        // 规格名不能为空
        ElMessage({
          message: $t('product.specNameNotNull'),
          type: 'error',
          duration: 1000
        })
        isValid = false
        // 终止遍历
        break
      }
      if (sku.tagName && (!sku.tagItems.length || (sku.tagItems.length && sku.tagItems.find(item => !item.propValue)))) {
        // 规格值不能为空
        ElMessage({
          message: $t('product.specValueCannotBeEmpty'),
          type: 'error',
          duration: 1000
        })
        isValid = false
        // 终止遍历
        break
      }
    }
  }
  return isValid
}

defineExpose({
  init,
  vaildSkuTag
})

</script>

<style lang="scss" scoped>
.component-sku-tag {
  // 新增规格外部边框
  .sku-border{
    width:100%;
    border: 1px solid #DCDCDC;
  }
  .input-new-tag {
    width: 170px;
    margin-bottom: 10px;
  }
  .text-btn {
    display: inline-block;
    margin-left: 5px;
  }
}

.component-sku-tag {
  & :deep(.el-upload-list__item.is-success){
    line-height: 76px !important;
  }
  & :deep(img) {
    line-height: 60px;
    height: auto;
    vertical-align: middle;
  }

  & :deep(.sku-background){
    background-color: #f8f8f8;
    margin: 10px;
    padding: 7px 10px;
    .el-button{
      margin-left: 10px;
      span{
        color:#000 !important;
      }
    }
    .el-form-item__label{
      padding:0 24px 0 0
    }
    .add-sku-btn {
      height: 28px;
      line-height: 28px;
      // margin-left: 10px;
    }
    .tips {
      font-size: 12px;
      color: #999999;
      margin-left: 10px;
    }
  }
  .tag-item-tit {
    min-width: 45px;
    margin-right: 10px;
  }
  // 规格名
  .sku-background.spec-name-box {
    .spec-name {
      display: flex;
      position: relative;
      .first-tag-tips {
        color: #999;
        font-size: 12px;
        height: 1em;
        line-height: 1em;
        margin: 5px 0;
      }
    }
    // 提示
    .tips-word {
      margin-left: 10px;
      margin-right: 10px;
      & :deep(.el-icon-question::before) {
        color: #999;
      }
    }
    & :deep(.el-input__inner) {
      width: 150px;
    }
    .del-spec-btn {
      font-size: 20px;
      position: absolute;
      right: 10px;
      top: 0;
      bottom: 0;
      color: #BFBFBF;
      cursor: pointer;
    }
    .add-img {
      margin-left: 10px;
    }
  }
  // 规格值
  .spec-val {
    display: flex;
    margin: 0 10px;
    padding: 0 10px;
    .tag-item-tit {
      margin-right: 12px;
    }
    .spec-val-item {
      margin-bottom: 10px;
      position: relative;
      display: inline-block;
      width: 150px;
      line-height: 1.5em;
      padding: 0 10px;
      box-sizing: border-box;
      border: 1px solid #DCDCDC;
      & :deep(.el-input__inner) {
        width: 100%;
        padding-left: 0!important;
        padding-right: 5px;
        border: 0;
        overflow: hidden;
        text-overflow:ellipsis;
        white-space: nowrap;
      }
      & :deep(.el-input__suffix) {
        display: none;
      }
      .item {
        display: flex;
        justify-content: space-between;
        .prop-value {
          width: 90%;
          .text-hid {
            display: block;
            width: 100%;
            height: 32px;
            line-height: 32px;
            overflow: hidden;
            text-overflow:ellipsis;
            white-space: nowrap;
          }
          .up-box {
            display: flex;
            align-items: flex-end;
            .img-upload {
              & :deep(.plugin-images),
              & :deep(.plugin-images .el-upload--picture-card),
              & :deep(.el-upload-list),
              & :deep(.el-upload-list__item.is-success) {
                width: 80px;
                height: 80px;
                line-height: 80px;
                border-radius: 0;
              }
              & :deep(.el-upload-list__item.is-success) {
                margin: 0;
              }
              & :deep(.plugin-images .el-upload--picture-card) {
                border: 1px dashed #155BD4;
                background: transparent;
                & i {
                  font-size: 18px;
                  font-weight: bold;
                  color: #155BD4;
                }
              }
            }
            .text-btn {
              height: 20px;
              line-height: 20px;
            }
          }
        }
        .del-btn {
          font-size: 12px;
          line-height: 32px;
          color: #999;
          cursor: pointer;
        }
      }
    }
    .spec-val-item:not(:last-child) {
      margin-right: 10px;
    }
    .add-btn {
      font-size: 20px;
      line-height: 32px;
      vertical-align: middle;
      color: #155bd4;
      cursor: pointer;
    }
    .add-btn.disable {
      color: #DCDCDC;
    }

    // 第一个规格值
    .spec-val-item.ft-spec-val-item {
      padding: 0 10px 10px 10px;
      & :deep(.el-input__inner) {
        padding-left: 5px;
        padding-right: 5px;
        border: 0;
        height: 24px;
        line-height: 24px;
        margin-bottom: 5px;
      }
      .item {
        .del-btn {
          display: block;
          font-size: 12px;
          line-height: 28px;
          color: #999;
          cursor: pointer;
        }
      }
    }
    .tag-item-box {
      .item-list {
        display: flex;
        align-items: flex-start;
        flex-wrap: wrap;
      }
      .img-tips {
        height: 1em;
        line-height: 1em;
        margin-bottom: 10px;
        color: #999999;
        font-size: 12px;
      }
    }
  }

  .ft-spec-val {
    .add-btn {
      vertical-align: top;
    }
  }
}

:deep(.tagItem-select .el-select-dropdown__item.selected) {
  min-width: 160px;
  font-weight: revert;
  color: #606266;
}
</style>
