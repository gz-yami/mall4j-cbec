<template>
  <el-dialog
    v-model="visible"
    :title="
      !dataForm.transportId
        ? $t('crud.addTitle')
        : $t('groups.edit')
    "
    :close-on-click-modal="false"
    width="72%"
    class="component-transport-add-or-update"
    @close="close"
  >
    <el-form
      ref="dataFormRef"
      v-loading="loading"
      :model="dataForm"
      :label-width="$t('language') !== 'zh_CN'?'auto':'130px'"
      @submit.prevent
      @keyup.enter="onSubmit()"
    >
      <el-form-item
        :label="$t('transport.name')"
        prop="transName"
        :rules="[
          { required: true, message: $t('shop.templateNameCannotBeEmpty') },
          validateName
        ]"
      >
        <el-input
          v-model="dataForm.transName"
          maxlength="36"
          show-word-limit
          :placeholder="$t('transport.name')"
        />
      </el-form-item>
      <el-form-item
        :label="$t('transport.chargePrice')"
        prop="chargeType"
      >
        <el-radio-group
          v-model="dataForm.chargeType"
          :disabled="dataForm.isFreeFee == 1"
          @change="changeChargeType"
        >
          <el-radio :label="0">
            {{ $t("transport.byCount") }}
          </el-radio>
          <el-radio :label="1">
            {{ $t("transport.byWeight") }}
          </el-radio>
          <el-radio :label="2">
            {{ $t("transport.byVolume") }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <!-- 添加可配送的区域和邮费 START -->
      <div class="area-transfee-table">
        <el-table
          :empty-text="$t('shop.noData')"
          class="transfee-table"
          :data="dataForm.transfees"
          header-cell-class-name="table-header"
          row-class-name="table-row-low"
          style="width: 100%"
        >
          <!-- 显示动态显示省市区 -->
          <el-table-column
            align="center"
            :label="$t('transport.distributableArea')"
          >
            <template #default="scope">
              <span v-if="scope.$index === 0 && dataForm.isFreeFee===1">{{
                $t("transport.allRegions")
              }}</span>
              <template v-if="dataForm.isFreeFee===0">
                <span
                  v-if="
                    (!scope.row.addrNameList || !scope.row.addrNameList.length)
                  "
                  class="city-text"
                >{{ $t("shop.pleaseSelectADeliveryArea") }}</span>
                <span>
                  <span
                    v-for="(city, index) in scope.row.addrNameList"
                    :key="index"
                    class="city-text"
                  >
                    {{ city }}
                  </span>
                </span>
                <span
                  class="default-btn text-btn"
                  @click="onAddOrUpdate(`${scope.$index}`)"
                >
                  {{ $t('crud.editBtn') }}
                </span>
                <span
                  v-if="scope.$index > 0"
                  class="default-btn text-btn"
                  @click="onDelete(`${scope.$index}`)"
                >
                  {{ $t('crud.delBtn') }}
                </span>
              </template>
            </template>
          </el-table-column>
          <!-- 首件（个） -->
          <el-table-column
            align="center"
            :label="tableTitle[0]"
            :class="[lang !== 'zh_CN'? 'errorTipsEn':'errorTips']"
          >
            <template #default="scope">
              <el-form-item
                :prop="`transfees.${scope.$index}.firstPiece`"
                label-width="0px"
                :class="[lang !== 'zh_CN'? 'errorTipsEn':'errorTips']"
                :rules="[
                  {
                    required: true,
                    message: `${tableTitle[0]}${$t('publics.noNull')}`,
                  },
                ]"
              >
                <el-input
                  v-model="scope.row.firstPiece"
                  oninput="if(value.length>10)value=value.slice(0,10)"
                  :disabled="!scope.row.status && scope.$index === 0"
                  :min="1"
                  :max="9999999999"
                  @change="checkChargeType(scope.row,'firstPiece',dataForm.chargeType)"
                />
              </el-form-item>
            </template>
          </el-table-column>
          <!-- 运费（元） -->
          <el-table-column
            align="center"
            :label="tableTitle[1]"
          >
            <template #default="scope">
              <el-form-item
                :prop="`transfees.${scope.$index}.firstFee`"
                label-width="0px"
                :class="[lang !== 'zh_CN'? 'errorTipsEn':'errorTips']"
                :rules="[
                  {
                    required: true,
                    message: `${tableTitle[1]}${$t('publics.noNull')}`,
                  },
                ]"
              >
                <el-input-number
                  v-model="scope.row.firstFee"
                  :disabled="!scope.row.status && scope.$index === 0"
                  :precision="2"
                  :min="0"
                  :max="100000000"
                />
              </el-form-item>
            </template>
          </el-table-column>
          <!-- 续件（个） -->
          <el-table-column
            align="center"
            :label="tableTitle[2]"
            width="220"
          >
            <template #default="scope">
              <el-form-item
                :prop="`transfees.${scope.$index}.continuousPiece`"
                label-width="0px"
                :class="[lang !== 'zh_CN'? 'errorTipsEn':'errorTips']"
                :rules="[
                  {
                    required: true,
                    message: `${tableTitle[2]}${$t('publics.noNull')}`,
                  },
                ]"
              >
                <el-input
                  v-model="scope.row.continuousPiece"
                  oninput="if(value.length>10)value=value.slice(0,10)"
                  :disabled="!scope.row.status && scope.$index === 0"
                  :max="100000000"
                  :min="0"
                  @change="checkChargeType(scope.row,'continuousPiece',dataForm.chargeType)"
                />
              </el-form-item>
            </template>
          </el-table-column>
          <!-- 续费（元） -->
          <el-table-column
            align="center"
            :label="tableTitle[3]"
            width="220"
          >
            <template #default="scope">
              <el-form-item
                :prop="`transfees.${scope.$index}.continuousFee`"
                label-width="0px"
                :class="[lang !== 'zh_CN'? 'errorTipsEn':'errorTips']"
                :rules="[
                  {
                    required: true,
                    message: `${tableTitle[3]}${$t('publics.noNull')}`,
                  },
                ]"
              >
                <el-input-number
                  v-model="scope.row.continuousFee"
                  :precision="2"
                  :min="0"
                  :max="100000000"
                  :disabled="!scope.row.status && scope.$index === 0"
                />
              </el-form-item>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div
        v-if="dataForm.isFreeFee === 0"
        style="margin-top: 20px"
      >
        <div
          class="default-btn primary-btn"
          @click="addTransfee()"
        >
          {{ $t("shop.addAreaAndShipping") }}
        </div>
      </div>
      <!-- 添加可配送的区域和邮费 END -->
      <!-- 指定条件包邮 START -->
      <el-checkbox
        v-if="!dataForm.isFreeFee"
        v-model="dataForm.hasFreeCondition"
        style="margin-top: 10px; font-size: 50px"
      >
        {{ $t("shop.freeShippingOnSpecifiedConditions") }}
      </el-checkbox>
      <el-table
        v-if="dataForm.hasFreeCondition && !dataForm.isFreeFee"
        :empty-text="$t('shop.noData')"
        header-cell-class-name="table-header"
        row-class-name="table-row-low"
        :data="dataForm.transfeeFrees"
        style="width: 100%"
      >
        <!-- 显示动态显示省市区 -->
        <el-table-column
          align="left"
          width="400"
          fixed
          :label="$t('transport.selArea')"
        >
          <template #default="scope">
            <span
              v-if="
                !scope.row.freeAddrNameList ||
                  !scope.row.freeAddrNameList.length
              "
              class="city-text"
            >{{ $t("transport.selCity") }}</span>
            <span
              v-for="(city, index) in scope.row.freeAddrNameList"
              :key="index"
              class="city-text"
            >
              {{ city }}
            </span>
            <span
              class="default-btn text-btn"
              @click="addOrUpdateTransfeeFree(`${scope.$index}`)"
            >
              {{ $t('crud.editBtn') }}
            </span>
            <span
              v-if="scope.$index > 0"
              class="default-btn text-btn"
              @click="deleteTransfeeFree(`${scope.$index}`)"
            >
              {{ $t('crud.delBtn') }}
            </span>
          </template>
        </el-table-column>
        <!-- 设置包邮条件 -->
        <el-table-column
          align="left"
          :label="$t('transport.setShopPrice')"
          min-width="500"
        >
          <template #default="scope">
            <el-radio-group v-model="scope.row.freeType">
              <el-radio :label="0">
                {{ [$t("transport.byCount"),$t("transport.byWeight"),$t("transport.byVolume")][dataForm.chargeType] }}
              </el-radio>
              <el-radio :label="1">
                {{
                  $t("product.freeShiullAmount")
                }}
              </el-radio>
              <el-radio
                :label="2"
              >
                {{ [$t("transport.fullCount"),$t("transport.fullWeight"),$t("transport.fullVolume")][dataForm.chargeType] }}{{ $t("transport.fullAmount1") }}{{ $t("transport.shippingIncluded") }}
              </el-radio>
            </el-radio-group>
          </template>
        </el-table-column>
        <!-- 设置条件 -->
        <el-table-column
          width="400"
          align="right"
        >
          <template #default="scope">
            <el-form-item
              v-if="scope.row.freeType === 1 || scope.row.freeType === 2"
              :prop="`transfeeFrees.${scope.$index}.amount`"
              label-width="0px"
              :rules="[{ required: true, message: `${$t('publics.noNull')}` }]"
            >
              <el-input
                v-model="scope.row.amount"
                width="200"
                oninput="if(value.length>10)value=value.slice(0,10)"
                :min="0"
                @change="convertPrice(scope.row,'amount')"
              >
                <template #prepend>
                  {{ $t("marketing.full") }}
                </template>
                <template #append>
                  {{ $t("product.freeShipping") }}
                </template>
              </el-input>
            </el-form-item>
            <el-form-item
              v-if="scope.row.freeType === 0 || scope.row.freeType === 2"
              :prop="`transfeeFrees.${scope.$index}.piece`"
              label-width="0px"
              :rules="[{ required: true, message: `${$t('publics.noNull')}` }]"
            >
              <el-input
                v-model="scope.row.piece"
                width="200"
                oninput="if(value.length>10)value=value.slice(0,10)"
                @change="checkChargeType(scope.row,'piece',dataForm.chargeType)"
              >
                <template #prepend>
                  {{ $t("marketing.full") }}
                </template>
                <template #append>
                  {{ [$t("transport.pieces"),'kg','m³'][dataForm.chargeType] }}{{ $t("product.freeShipping") }}
                </template>
              </el-input>
            </el-form-item>
          </template>
        </el-table-column>
      </el-table>
      <div
        v-if="dataForm.isFreeFee === 0"
        style="margin-top: 20px"
      >
        <div
          :class="['default-btn primary-btn icon-btn', !dataForm.hasFreeCondition ? 'disabled-btn': '']"
          @click="addTransfeeFree()"
        >
          {{ $t("shop.clickToAddTheSpecifiedShippingConditions") }}
        </div>
      </div>
      <!-- 指定条件包邮 END -->
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <div
          class="default-btn"
          @click="close"
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
    <!-- 弹窗, 新增 / 修改 -->
    <transcity-select
      v-if="transcitySelectVisible"
      ref="transcitySelectRef"
      @refresh-data-list="getDataList"
    />
  </el-dialog>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { areaNameList } from '@/utils/addr.js'
import transcitySelect from './transcity-select.vue'
import { Debounce } from '@/utils/debounce.js'

const emit = defineEmits(['refreshDataList', 'close'])

const dataForm = ref({
  hasFreeCondition: false,
  transName: '',
  createTime: '',
  chargeType: 0,
  transportId: 0,
  isFreeFee: 0,
  transfees: [{ cityList: [], addrNameList: [], status: 1 }],
  transfeeFrees: [{ freeCityList: [], freeAddrNameList: [] }]
})
const validateNameRule = (rule, value, callback) => {
  if (!value.trim()) {
    callback(new Error($t('shop.templateNameCannotBeEmpty')))
  } else if (value.trim() === '包邮' || value.trim() === '固定运费') {
    callback(new Error($t('components.templateRestrictionTips')))
  } else {
    callback()
  }
}
const validateName = reactive({ validator: validateNameRule, trigger: 'blur' })

const page = reactive({
  total: 0, // 总页数
  currentPage: 1, // 当前页数
  pageSize: 10 // 每页显示多少条
})
const lang = window.localStorage.getItem('cbecB2cLang')

const tableTitle = computed(() => {
  const titles = [
    [
      $t('transport.firstPiece'),
      $t('transport.transportationCost') + '(' + useCurrencyStore().defMark + ')',
      $t('transport.continuationPiece'),
      $t('transport.continuationCost') + '(' + useCurrencyStore().defMark + ')'
    ],
    [
      $t('transport.firstWeight'),
      $t('transport.transportationCost') + '(' + useCurrencyStore().defMark + ')',
      $t('transport.continuedWeight'),
      $t('transport.continuationCost') + '(' + useCurrencyStore().defMark + ')'
    ],
    [
      $t('transport.firstVolume'),
      $t('transport.transportationCost') + '(' + useCurrencyStore().defMark + ')',
      $t('transport.continuedVolume'),
      $t('transport.continuationCost') + '(' + useCurrencyStore().defMark + ')'
    ]
  ]
  if (dataForm.value.chargeType) {
    return titles[dataForm.value.chargeType]
  }
  return titles[0]
})

const visible = ref(false)
const transcitySelectVisible = ref(false)
watch(() => visible.value, (val) => {
  // 如果当前对话框不可见，则关闭选择城市的对话框
  if (!val) {
    transcitySelectVisible.value = false
  }
})

const dataFormRef = ref(null)
let transfeesBin = [] // 可配送区域运费，禁止选中列表
let transfeeFreesBin = [] // 指定条件包邮，禁止选中列表
const init = (id) => {
  dataForm.value.transportId = id || 0
  visible.value = true
  nextTick(() => {
    dataFormRef.value?.resetFields()
    dataForm.value = {
      hasFreeCondition: false,
      transName: '',
      createTime: '',
      chargeType: 0,
      transportId: id || 0,
      isFreeFee: 0,
      transfees: [{ cityList: [], addrNameList: [], status: 1 }],
      transfeeFrees: [{ freeCityList: [], freeAddrNameList: [] }]
    }
  })
  transfeesBin = []
  transfeeFreesBin = []
  getTransport()
}
// 收费方式切换
const changeChargeType = (type) => {
  dataForm.value.transfees.forEach(item => {
    item.firstPiece && checkChargeType(item, 'firstPiece', type)
    item.continuousPiece && checkChargeType(item, 'continuousPiece', type)
  })
  dataForm.value.transfeeFrees.forEach(item => {
    item.piece && checkChargeType(item, 'piece', type)
  })
}

/**
 * 价格格式化
 * @param row
 * @param attr
 */
const convertPrice = (row, attr) => {
  const price = +checkAmount(row[attr])
  row[attr] = price.toFixed(2)
}

/**
 * 根据收费方式格式化输入的值
 * @param row
 * @param attr
 * @param chargeType
 */
const checkChargeType = (row, attr, chargeType) => {
  // 0：按件数  1：按重量  2：按体积
  if (chargeType === 0) {
    const number = getNumber(row[attr])
    row[attr] = number > 0 ? number : 1
  } else if (chargeType === 1 || chargeType === 2) {
    const price = +checkAmount(row[attr])
    row[attr] = price.toFixed(2)
  }
}

/**
 * 保留整数并小于零的数设为0
 */
const getNumber = (num) => {
  num = Math.round(num)
  return num < 0 ? 0 : num
}
const checkAmount = (data) => {
  const reg = /^[1-9]\d*$|^[1-9]\d*\.\d\d?$|^0\.\d\d?$/
  let num = data
  if (!reg.test(data)) {
    num = +Number(data).toFixed(2)
  }
  if (num < 0) {
    num = 1
  }
  return num
}
const loading = ref(false)
const getTransport = () => {
  if (dataForm.value.transportId) {
    loading.value = true
    http({
      // 获取运费模板数据
      url: http.adornUrl(`/platform/transport/info/${dataForm.value.transportId}`),
      method: 'get'
    }).then(({ data }) => {
      getAreaListInfo()
      if (data.isFreeFee) {
        data.transfees[0].status = 0
      } else {
        data.transfees[0].status = 1
      }
      dataForm.value = data
      if (!dataForm.value.transfeeFrees.length) {
        dataForm.value.transfeeFrees = [{ freeCityList: [], freeAddrNameList: [] }]
      }
      dataForm.value.hasFreeCondition = !!data.hasFreeCondition
      handleBinAreas()
      loading.value = false
    })
  } else {
    getAreaListInfo()
    handleBinAreas()
  }
}
const getDataList = (row, cityList, type) => {
  if (type === 0) {
    dataForm.value.transfees[row].cityList = cityList
    dataForm.value.transfees[row].addrNameList = setAddrInfoByAreaList(cityList)
  }
  if (type === 1) {
    dataForm.value.transfeeFrees[row].freeCityList = cityList
    dataForm.value.transfeeFrees[row].freeAddrNameList = setAddrInfoByAreaList(cityList)
  }
  handleBinAreas()
}
/**
 * 获取省市区信息
 */
let areaListDB = []
const getAreaListInfo = () => {
  http({
    url: http.adornUrl('/admin/area/areaListInfo'),
    method: 'get'
  }).then(({ data }) => {
    areaListDB = data
    if (dataForm.value.transfees.length > 0) {
      getAreaListByType(0)
    }
    if (dataForm.value.transfeeFrees.length > 0) {
      getAreaListByType(1)
    }
  })
}
const getAreaListByType = (type) => {
  if (type === 0) {
    for (let t = 0; t < dataForm.value.transfees.length; t++) {
      dataForm.value.transfees[t].addrNameList = setAddrInfoByAreaList(dataForm.value.transfees[t].cityList)
    }
  }
  if (type === 1) {
    for (let f = 0; f < dataForm.value.transfeeFrees.length; f++) {
      dataForm.value.transfeeFrees[f].freeAddrNameList = setAddrInfoByAreaList(dataForm.value.transfeeFrees[f].freeCityList)
    }
  }
}
/**
 * 将运费模板中的地址存入地址列表中
 */
const setAddrInfoByAreaList = (cityList) => {
  let areaList = []
  areaList = JSON.parse(JSON.stringify(areaListDB))
  return areaNameList(areaList, cityList)
}
// 添加运费项
const addTransfee = () => {
  dataForm.value.transfees.push({ cityList: [], status: 1 })
}
// 删除运费项
const onDelete = (rowIndex) => {
  dataForm.value.transfees.splice(rowIndex, 1)
  handleBinAreas()
}
// 可配送区域和运费编辑
const transcitySelectRef = ref(null)
const onAddOrUpdate = (rowIndex) => {
  transcitySelectVisible.value = true
  nextTick(() => {
    transcitySelectRef.value?.init(rowIndex, dataForm.value.transfees[rowIndex].cityList || [], 0, transfeesBin)
  })
}
// 添加指定包邮条件
const addTransfeeFree = () => {
  if (!dataForm.value.hasFreeCondition) {
    return
  }
  dataForm.value.transfeeFrees.push({ freeCityList: [] })
}
// 删除指定包邮条件
const deleteTransfeeFree = (rowIndex) => {
  dataForm.value.transfeeFrees.splice(rowIndex, 1)
  handleBinAreas()
}
// 指定包邮条件编辑
const addOrUpdateTransfeeFree = (rowIndex) => {
  transcitySelectVisible.value = true
  nextTick(() => {
    transcitySelectRef.value?.init(rowIndex, dataForm.value.transfeeFrees[rowIndex].freeCityList || [], 1, transfeeFreesBin)
  })
}

/**
 * 验证指定区域是否设置包邮条件
 */
const checkTransfeeFrees = () => {
  let isTrue = true
  if (dataForm.value.transfeeFrees.length > 0) {
    dataForm.value.transfeeFrees.forEach(element => {
      if (!element.freeType && element.freeType !== 0) {
        isTrue = false
      }
    })
  }
  return isTrue
}
// 设置不可选中
const handleBinAreas = () => {
  transfeesBin = []
  transfeeFreesBin = []
  const transfees = dataForm.value.transfees
  const transfeeFrees = dataForm.value.transfeeFrees
  if (transfees.length > 0) {
    transfees.forEach(i => {
      if (i.cityList.length > 0) {
        i.cityList.forEach(j => {
          if (!judgeArrHaving(transfeesBin, j)) {
            transfeesBin.push(j.areaId)
          }
        })
      }
    })
  }
  if (transfeeFrees.length > 0) {
    transfeeFrees.forEach(i => {
      if (i.freeCityList.length > 0) {
        i.freeCityList.forEach(j => {
          if (!judgeArrHaving(transfeeFreesBin, j)) {
            transfeeFreesBin.push(j.areaId)
          }
        })
      }
    })
  }
}
const judgeArrHaving = (arr, data) => {
  let isExist = false
  if (arr.length < 1) {
    return isExist
  }
  if (arr.includes(data.areaId)) {
    isExist = true
  }
  return isExist
}
const emptyCityHint = () => {
  ElMessage({
    message: $t('shop.pleaseSelectADeliveryArea'),
    type: 'error',
    duration: 1500
  })
}
// 填写信息提示
const formWriteHint = () => {
  ElMessage({
    message: $t('shop.pleaseWriteInfo'),
    type: 'error',
    duration: 1500
  })
}
// 表单提交
let isSubmitting = false // 提交中
const onSubmit = Debounce(() => {
  if (loading.value) return
  dataFormRef.value?.validate((valid) => {
    if (!valid) {
      formWriteHint()
    }
    if (valid) {
      const transFeesLen = dataForm.value.transfees.length || 0
      if (!transFeesLen) {
        emptyCityHint()
        return
      }
      for (let i = 0; i < transFeesLen; i++) {
        const transfee = dataForm.value.transfees[i]
        if (transfee.cityList.length === 0 && dataForm.value.isFreeFee === 0) {
          emptyCityHint()
          return
        }
      }
      if (dataForm.value.isFreeFee === 0 && dataForm.value.hasFreeCondition && !checkTransfeeFrees()) {
        ElMessage.error($t('shop.setRegionalConditions'))
        return
      }
      if (dataForm.value.hasFreeCondition && (!dataForm.value.transfeeFrees || dataForm.value.transfeeFrees.length === 0)) {
        ElMessage({
          message: $t('shop.setRegionalConditions'),
          type: 'error',
          duration: 1500
        })
        return
      }
      let hasFreeCondition = 0
      if (dataForm.value.hasFreeCondition) {
        hasFreeCondition = 1
      } else {
        hasFreeCondition = 0
      }
      // 指定区域条件判断
      if (hasFreeCondition === 1 && dataForm.value.transfeeFrees.length) {
        let flag = null
        dataForm.value.transfeeFrees.some((item) => {
          if (!item.freeCityList[0]) {
            ElMessage({
              message: $t('shop.setRegionalConditions'),
              type: 'error',
              duration: 1500
            })
            flag = true
            return true
          }
          if (!Object.prototype.hasOwnProperty.call(item, 'freeType')) {
            ElMessage({
              message: $t('shop.pleaseAddTheSpecifiedShippingConditions'),
              type: 'error',
              duration: 1500
            })
            flag = true
            return true
          }
          return false
        })
        if (flag) return
      }
      if (isSubmitting) {
        return
      }
      isSubmitting = true
      loading.value = true
      http({
        url: http.adornUrl('/platform/transport'),
        method: dataForm.value.transportId ? 'put' : 'post',
        data: http.adornData({
          transportId: dataForm.value.transportId || undefined,
          transName: dataForm.value.transName,
          chargeType: dataForm.value.chargeType,
          isFreeFee: dataForm.value.isFreeFee,
          transfees: dataForm.value.transfees,
          transfeeFrees: dataForm.value.transfeeFrees,
          hasFreeCondition
        })
      }).then(() => {
        loading.value = false
        ElMessage({
          message: $t('publics.operation'),
          type: 'success',
          duration: 1500,
          onClose: () => {
            isSubmitting = false
            visible.value = false
            emit('refreshDataList', page)
            close()
          }
        })
      }).catch(() => {
        loading.value = false
        isSubmitting = false
      })
    }
  })
}, 1000)

const close = () => {
  emit('close', 'close')
}

defineExpose({
  init
})

</script>

<style lang="scss" scoped>
.component-transport-add-or-update{
  .city-text {
    display: inline-block;
    padding: 0 5px;
  }

  .area-transfee-table {
    :deep(.el-form-item){
      padding-top: 18px;
      .el-form-item__content{
        justify-content: center;
      }
    }
    :deep(.el-input) {
      width: 150px;
    }
    :deep(.el-input-number) {
      width: 172px;
      .el-input {
        width: 100%;
      }
    }
  }
}
.errorTips :deep(.el-form-item__error) {
  left: 26%
}
.errorTipsEn :deep(.el-form-item__error) {
  left: 7%;
  top: 100%;
}
:deep(.el-form-item__content):has(.el-form-item__error) {
  margin-bottom: 10px;
}
</style>
<!-- eslint-disable-next-line vue-scoped-css/enforce-style-type -->
<style lang="scss">
.component-transport-add-or-update {
  min-width: 1030px;
}
</style>
