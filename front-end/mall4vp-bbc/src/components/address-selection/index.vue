<template>
  <el-cascader
    ref="cascaderAddrRef"
    v-model="currentAddr"
    style="width: 300px"
    :options="addrList"
    :props="addrListTreeProps"
    :placeholder="$t('shopProcess.addrInputTips')"
    @change="onHandleAddrChange"
  />
</template>

<script setup>
import { treeDataTranslate } from '@/utils/index.js'
const emit = defineEmits(['updateCurrentAddr', 'onAddrChange'])

const props = defineProps({
  // 默认选中地址 (id数组)
  currentAddrVal: {
    default () {
      return []
    },
    type: Array
  }
})
const currentAddr = computed({
  set (val) {
    // 监听currentAddrVal实时变化
    emit('updateCurrentAddr', val)
  },
  get () {
    const AddrVal = props.currentAddrVal
    // 过滤空项,防止选择栏不回显
    if (!AddrVal[2]) {
      AddrVal.pop()
      if (!AddrVal[1]) AddrVal.pop()
    }
    return AddrVal
  }
})

onMounted(() => {
  onGetAddrList()
})

const areaNameKey = $t('language') !== 'zh_CN' ? 'areaNameEn' : 'areaName'
const addrList = ref([]) // 地址-国省市区
const addrListTreeProps = reactive({
  value: 'areaId',
  label: areaNameKey
})

/**
 * 获取地址
 */
const onGetAddrList = () => {
  http({
    url: http.adornUrl('/admin/area/list'),
    method: 'get',
    params: http.adornParams({
      minGrade: 0
    })
  }).then(({ data }) => {
    addrList.value = treeDataTranslate(data, 'areaId', 'parentId')
  })
}

const cascaderAddrRef = ref(null)
const selectedAddrDataForm = reactive({
  // 国家
  province: '',
  provinceId: '',
  // 省
  city: '',
  cityId: '',
  // 城市
  area: '',
  areaId: ''
})
/**
 * 监听地址改变
 */
const onHandleAddrChange = (value) => {
  if (value.length > 0) {
    const labels = cascaderAddrRef.value.getCheckedNodes()[0]?.pathLabels
    selectedAddrDataForm.province = labels[0]
    selectedAddrDataForm.provinceId = value[0]
    selectedAddrDataForm.city = labels[1] || ''
    selectedAddrDataForm.cityId = value[1] || ''
    selectedAddrDataForm.area = labels[2] || ''
    selectedAddrDataForm.areaId = value[2] || ''
  }
  emit('onAddrChange', selectedAddrDataForm)
}
</script>
