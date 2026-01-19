<template>
  <div class="mod-hotSearch-add-or-update">
    <el-dialog
      v-model="visible"
      :title="
        !dataForm.hotCityId ? $t('brand.add') : $t('sysManagement.modify')
      "
      :close-on-click-modal="false"
    >
      <el-form
        ref="dataFormRef"
        :model="dataForm"
        :rules="dataRule"
        :label-width="$t('language')!=='zh_CN'?'200px': 'auto'"
        @submit.prevent
        @keyup.enter="onSubmit()"
      >
        <!-- 热门城市 -->
        <el-form-item
          :label="$t('hotCity.hotCountries')"
          prop="areaId"
        >
          <el-select
            v-model="selectCity"
            filterable
            clearable
            style="width: 260px"
            value-key="areaId"
            :placeholder="$t('hotCity.selectHotCountries')"
            @change="selectCityChange"
          >
            <el-option
              v-for="item in cityList"
              :key="item.areaId"
              :label="item.areaNameEn"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item
          :label="$t('brand.sequence')"
          prop="seq"
        >
          <el-input-number
            v-model="dataForm.seq"
            controls-position="right"
            :min="0"
            :max="9999999"
            :precision="0"
            :label="$t('hotSearch.seq')"
            @keydown="channelInputLimit"
            @blur="changeSeq"
          />
        </el-form-item>
        <el-form-item
          :label="$t('brand.status')"
          size="small"
          prop="status"
        >
          <el-radio-group v-model="dataForm.status">
            <el-radio :label="0">
              {{ $t('groups.notEnabled') }}
            </el-radio>
            <el-radio :label="1">
              {{ $t('brand.enAble') }}
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
            {{ $t("remindPop.cancel") }}
          </div>
          <div
            class="default-btn primary-btn"
            @click="onSubmit()"
          >
            {{ $t("remindPop.confirm") }}
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'

const emit = defineEmits(['refreshDataList'])

const dataForm = ref({
  hotCityId: 0,
  areaId: '',
  areaNameEn: '',
  seq: 0,
  status: 0
})
const page = reactive({
  total: 0, // 总页数
  currentPage: 1, // 当前页数
  pageSize: 10 // 每页显示多少条
})

const dataRule = {
  areaId: [
    { required: true, message: $t('hotCity.hotCountriesNoNull'), trigger: 'blur' }
  ]
}

const selectCityChange = (e) => {
  dataForm.value.areaId = ''
  dataForm.value.areaNameEn = ''
  if (!e) return
  dataForm.value.areaId = e.areaId
  dataForm.value.areaNameEn = e.areaNameEn
}

const visible = ref(false)
let isSubmit = false
const selectCity = ref('')
const dataFormRef = ref(null)
const init = (data) => {
  getCityList()
  visible.value = true
  isSubmit = false
  if (data) {
    dataForm.value = Object.assign({}, data)
    selectCity.value = dataForm.value
  } else {
    nextTick(() => {
      dataFormRef.value?.resetFields()
      selectCity.value = ''
      dataForm.value = {
        hotCityId: 0,
        areaId: '',
        areaNameEn: '',
        seq: 0,
        status: 0
      }
    })
  }
}

// 表单提交
const onSubmit = () => {
  dataFormRef.value?.validate(valid => {
    if (valid) {
      const param = dataForm.value
      if (isSubmit) {
        return false
      }
      isSubmit = true
      http({
        url: http.adornUrl('/platform/hotCity'),
        method: param.hotCityId ? 'put' : 'post',
        data: http.adornData(param)
      }).then(() => {
        ElMessage({
          message: $t('remindPop.success'),
          type: 'success',
          duration: 1500,
          onClose: () => {
            visible.value = false
            emit('refreshDataList', page)
          }
        })
      }).catch(() => {
        isSubmit = false
      })
    }
  })
}

// 城市参数
const cityList = ref([])
/**
 * 获取城市列表
 */
const getCityList = () => {
  http({
    url: http.adornUrl('/platform/hotCity/listCityInfo'),
    method: 'get',
    params: http.adornParams()
  }).then(({ data }) => {
    if (!data) return
    cityList.value = data
  })
}

const channelInputLimit = (e) => {
  const key = e.key
  // 不允许输入'e'
  if (key === 'e' || key === '.' || key === '+' || key === '-') {
    e.returnValue = false
    return false
  }
  return true
}

const changeSeq = () => {
  if (!dataForm.value.seq) {
    dataForm.value.seq = 0
  }
}

defineExpose({
  init
})

</script>
