<template>
  <div class="component-add-or-update">
    <el-dialog
      v-model="visible"
      :close-on-click-modal="false"
    >
      <el-form
        :inline="true"
        class="demo-form-inline"
      >
        <div class="header-wrap">
          <el-icon
            size="24"
            color="#e6a23c"
          >
            <WarningFilled />
          </el-icon>
          <div class="title">
            {{ $t('internalization.question') }}
          </div>
        </div>
        <div class="content-box">
          <div class="account-txt">
            {{ $t('internalization.defaultCurrencyTip') }}
          </div>
          <div class="select-box">
            <el-form-item :label="$t('internalization.ChangeDelCurrency')">
              <el-select
                v-model="selectCurrency"
                v-el-select-loadmore="loadmore"
                value-key="id"
                :teleported="false"
                popper-class="single-select-loadmore"
                clearable
                collapse-tags
                :placeholder="$t('internalization.selectTxt')"
                :style="{
                  'min-width': $t('language')!=='zh_CN'?'280px': 'auto'
                }"
              >
                <el-option
                  v-for="item in currencyList"
                  :key="item.id"
                  :label="item.currencyName"
                  :value="item"
                />
              </el-select>
            </el-form-item>
          </div>
        </div>
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
import { Debounce } from '@/utils/debounce.js'
const vElSelectLoadmore = {
  mounted: (el, binding) => {
    // 获取element-ui定义好的scroll盒子（single-select-loadmore为自定义的类名）
    const SELECTWRAP_DOM = el.querySelector('.single-select-loadmore .el-select-dropdown__wrap')
    SELECTWRAP_DOM?.addEventListener('scroll', function () {
      /**
       * scrollHeight 获取元素内容高度(只读)
       * scrollTop 获取或者设置元素的偏移值,常用于, 计算滚动条的位置, 当一个元素的容器没有产生垂直方向的滚动条, 那它的scrollTop的值默认为0.
       * clientHeight 读取元素的可见高度(只读)
       * 如果元素滚动到底, 下面等式返回true, 没有则返回false:
       * ele.scrollHeight - ele.scrollTop === ele.clientHeight;
       */
      const condition = this.scrollHeight - this.scrollTop <= this.clientHeight
      if (condition) {
        binding.value()
      }
    })
  }
}
const emit = defineEmits(['refreshDataList'])
const selectCurrency = ref('') // 选择的id

const visible = ref(false)
const init = () => {
  getCurrencyList()
  visible.value = true
}

const currencyList = ref([]) // 货币列表
let dataTotal = 0
const tagSearchParam = {
  size: 10,
  current: 1,
  status: 1
}
/**
 * 获取货币汇率表列表
 */
const getCurrencyList = () => {
  http({
    url: http.adornUrl('/platform/currency/page'),
    method: 'get',
    params: http.adornParams(
      tagSearchParam
    )
  })
    .then(({ data }) => {
      if (!data) return
      dataTotal = data.total
      currencyList.value = [...currencyList.value, ...data.records]
    })
}
const loadmore = () => {
  tagSearchParam.current++
  if (currencyList.value.length < dataTotal) {
    getCurrencyList()
  }
}
const currencyStore = useCurrencyStore()
/**
 * 表单提交
 */
const onSubmit = Debounce(() => {
  if (!selectCurrency.value) {
    return ElMessage({
      message: $t('internalization.selectDelTxt'),
      type: 'error',
      duration: 1500
    })
  }
  const selectCurrencyForm = Object.assign({}, selectCurrency.value)
  selectCurrencyForm.defaultCurrency = 1
  http({
    url: http.adornUrl('/platform/currency/enableCurrency'),
    method: 'put',
    data: http.adornData(selectCurrencyForm)
  })
    .then(() => {
      ElMessage({
        message: $t('remindPop.succeeded'),
        type: 'success',
        duration: 1500,
        onClose: () => {
          visible.value = false
          emit('refreshDataList')
        }
      })
      // 更新缓存
      currencyStore.UPDATE_CURRENCY(selectCurrency.value.currencyCode)
    })
}, 1000)

defineExpose({
  init
})

</script>
<style scoped>
:deep(.el-dialog__header){
  border-bottom: 1px solid transparent;
}
:deep(.el-dialog__footer){
  border-top: 1px solid transparent;
}
.component-add-or-update {
  .header-wrap {
    margin-bottom: 10px;
    display: flex;
    align-items: center;
    .title {
      color: #303133;
      margin-left: 12px;
      font-weight: bold;
      font-size: 16px;
    }
  }
  .content-box {
    padding-left: 36px;
    line-height: 24px;
    .select-box {
      margin-top: 20px;
    }
  }
}
</style>
