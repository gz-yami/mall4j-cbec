<template>
  <div>
    <el-select
      v-model="selDvyId"
      v-loadmore="loadmore"
      :style="selStyle"

      filterable
      remote
      :remote-method="remoteMethod"
      :placeholder="placeTips"
      :disabled="disabled"
      @change="change"
    >
      <el-option
        v-for="item in devList"
        :key="item.dvyId"
        :label="item.dvyName"
        :value="item.dvyId"
      />
    </el-select>
  </div>
</template>

<script setup>
import { watch } from 'vue'

const emit = defineEmits(['update:modelValue'])

const vLoadmore = {
  bind (el, binding) {
    // 获取element-ui定义好的scroll盒子
    const SELECTWRAP_DOM = el.querySelector('.el-select-dropdown .el-select-dropdown__wrap')
    SELECTWRAP_DOM.addEventListener('scroll', function () {
      const condition = this.scrollHeight - this.scrollTop <= this.clientHeight
      if (condition) {
        binding.value()
      }
    })
  }
}

const props = defineProps({
  modelValue: {
    type: [String, Number],
    default: ''
  },
  selStyle: {
    type: Object,
    default: () => {}
  },
  placeTips: {
    type: String,
    default: $t('prodSku.choose')
  },
  disabled: {
    type: Boolean,
    default: false
  }
})

const selDvyId = ref('') // 当前选择的值
const size = 20
let current = 1
let dvyName = ''
let pages = 0
const devList = ref([])
let isSearch = false

watch(() => props.modelValue, (val) => {
  selDvyId.value = val
}, {
  immediate: true
})

onMounted(() => {
  getDeliveryList()
})

const change = () => {
  emit('update:modelValue', selDvyId.value)
}
const remoteMethod = (name) => {
  isSearch = true
  current = 1
  dvyName = name
  getDeliveryList()
}
// 获取物流列表
const getDeliveryList = () => {
  http({
    url: http.adornUrl('/platform/delivery/page'),
    method: 'get',
    params: http.adornParams({
      size,
      current,
      dvyName
    })
  }).then(({ data }) => {
    current = data.current
    pages = data.pages
    if (data.current === 1) {
      devList.value = data.records
      if (selDvyId.value && !isSearch) {
        checkCurSel(devList.value, selDvyId.value)
      }
    } else {
      const fList = data.records.filter(r => r.dvyId !== selDvyId.value)
      devList.value.push(...fList)
    }
  })
}
// 获取当前id的物流
const getDeliveryById = (dvyId) => {
  http({
    url: http.adornUrl('/platform/delivery/page'),
    method: 'get',
    params: http.adornParams({
      dvyId
    })
  }).then(({ data }) => {
    devList.value.unshift(...data.records)
  })
}
// 分页加载
const loadmore = () => {
  if (current < pages) {
    current++
    getDeliveryList()
  }
}
// 筛查出当前所选的对象
const checkCurSel = (curList, curId) => {
  for (const item of curList) {
    if (item.dvyId === curId) {
      return
    }
  }
  getDeliveryById(curId)
}

</script>
