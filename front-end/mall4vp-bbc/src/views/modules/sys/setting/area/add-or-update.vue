<template>
  <el-dialog
    v-model="visible"
    :title="!dataForm.areaId ? $t('sysManagement.add') : $t('sysManagement.modify')"
    :close-on-click-modal="false"
  >
    <el-form
      ref="dataFormRef"
      :model="dataForm"
      :rules="dataRule"
      :label-width="$t('language')==='zh_CN'?'100px': 'auto'"
      @submit.prevent
      @keyup.enter="onSubmit()"
    >
      <el-form-item
        :label="$t('sys.areaName')"
        prop="areaNameEn"
      >
        <el-input
          v-model.trim="dataForm.areaNameEn"
          maxlength="20"
        />
      </el-form-item>
      <el-form-item
        :label="$t('sys.superiorAreaL')"
        prop="parentId"
      >
        <el-cascader
          v-model="selectedOptions"
          expand-trigger="hover"
          :options="areaList"
          :props="categoryTreeProps"
          change-on-select
          filterable
          @change="onChange"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <div
          class="default-btn"
          @click="visible = false"
        >
          {{ $t('coupon.cancel') }}
        </div>
        <div
          class="default-btn primary-btn"
          @click="onSubmit(dataFormRef)"
        >
          {{ $t('coupon.confirm') }}
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { treeDataTranslate } from '@/utils/index.js'
import { ElMessage } from 'element-plus'

const visible = ref(false)
const dataForm = reactive({
  areaId: '',
  areaNameEn: null,
  parentId: null,
  level: null
})
const page = reactive({
  total: 0, // 总页数
  currentPage: 1, // 当前页数
  pageSize: 20 // 每页显示多少条
})
const dataRule = reactive({
  areaNameEn: [
    { required: true, message: $t('sysManagement.areaTips'), trigger: 'blur' }
  ]
})
const isSubmit = ref(false)
const areaList = ref([])
const categoryTreeProps = reactive({
  value: 'areaId',
  label: 'areaNameEn',
  checkStrictly: true
})
const selectedOptions = ref([])
const dataFormRef = ref(null)
const init = (areaId) => {
  dataForm.areaId = areaId || 0
  visible.value = true
  isSubmit.value = false
  selectedOptions.value = []
  nextTick(() => {
    dataFormRef.value?.resetFields()
    if (dataForm.areaId) {
      http({
        url: http.adornUrl('/admin/area/info/' + dataForm.areaId),
        method: 'get',
        params: http.adornParams()
      }).then(({ data }) => {
        dataForm.areaId = data.areaId
        dataForm.areaNameEn = data.areaNameEn
        dataForm.parentId = data.parentId
        dataForm.level = data.level
        selectedOptions.value = dataForm.parentId
        categoryTreeProps.areaId = dataForm.areaId
        categoryTreeProps.areaNameEn = dataForm.areaNameEn
      })
    }

    http({
      url: http.adornUrl('/admin/area/list'),
      method: 'get',
      params: http.adornParams({
        maxGrade: 3
      })
    }).then(({ data }) => {
      areaList.value = treeDataTranslate(data, 'areaId', 'parentId')
    })
  })
}

const emit = defineEmits(['refreshDataList'])
// 表单数据提交

const onSubmit = async formEl => {
  if (!formEl) return
  await formEl.validate(valid => {
    if (!valid) return
    if (!dataForm.parentId) {
      dataForm.parentId = 0
      dataForm.level = 0
    } else if (selectedOptions.value.length) {
      dataForm.level = selectedOptions.value.length
    }
    if (isSubmit.value) {
      return false
    }
    isSubmit.value = true
    http({
      url: http.adornUrl('/admin/area'),
      method: dataForm.areaId ? 'put' : 'post',
      data: http.adornData(dataForm)
    }).then(() => {
      ElMessage({
        message: $t('publics.operation'),
        type: 'success',
        duration: 1500,
        onClose: () => {
          visible.value = false
          emit('refreshDataList', page)
        }
      })
    }).catch(() => {
      isSubmit.value = false
    })
  })
}
const onChange = (val) => {
  dataForm.parentId = val && val[val.length - 1]
}
defineExpose({ init })
</script>
