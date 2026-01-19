<template>
  <div class="dialog-container component-dialog-component">
    <el-dialog
      v-model="visible"
      :title="headerTitle"
      :width="dialogWidth"
      :close-on-click-modal="closeOnClickModal"
      :append-to-body="appendToBody"
      :before-close="handleClose"
      class="dialog-container component-dialog-component-box"
      :[$options?.__scopeId]="''"
      @closed="emit('closed')"
    >
      <template #header>
        <div>
          <slot name="title" />
        </div>
      </template>
      <div class="contents-body">
        <slot name="contents" />
      </div>
      <template #footer>
        <span
          class="dialog-footer"
        >
          <slot name="footers" />
        </span>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
const emit = defineEmits(['handleClose', 'closed'])
const props = defineProps({
  headerTitle: { // 头部标题
    type: String,
    default: () => ''
  },
  dialogVisible: { // 弹窗是否显示
    type: Boolean,
    default: () => false
  },
  dialogWidth: { // 弹窗的宽度
    type: String,
    default: () => '50%'
  },
  closeOnClickModal: { // 是否点击遮罩层关闭
    type: Boolean,
    default: () => false
  },
  appendToBody: {
    type: Boolean,
    default: () => true
  }
})
const visible = ref(props.dialogVisible)
watch(() => props.dialogVisible, (val) => {
  visible.value = val
}, { deep: true })
const handleClose = () => {
  emit('handleClose')
}
</script>
<style lang="scss" scoped>
.component-dialog-component-box {
  &:deep(.dialog-footers) {
    width: 100%;
    height: 56px;
    text-align: center;

    .el-button {
      padding: 9px 20px;
    }
  }

  &:deep(.el-dialog__header) {
    border: 0 !important;
  }
  &:deep(.el-dialog__body) {
    padding: 0 !important;
  }
}

</style>
