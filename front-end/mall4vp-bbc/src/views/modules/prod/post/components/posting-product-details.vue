<template>
  <!-- 发布商品：03、编辑商品详情 -->
  <div class="posting-product-details">
    <el-tabs>
      <el-tab-pane
        v-for="(item,index) in prodLangList"
        :key="index"
        :label="`${item.langName}${$t('shop.withdrawalDetail')}`"
      >
        <div class="prod-det-box">
          <div class="prod-content">
            <tiny-mce
              v-if="!loading"
              v-model="item.content"
              :height="600"
            />
          </div>
          <!-- 详情预览 -->
          <div class="details-preview">
            <div class="preview-box-title">
              {{ $t('product.detailPagePreviewImage') }}
            </div>
            <div
              v-rich="item.content"
              class="preview-con"
            />
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
const emit = defineEmits(['updateContent'])

const props = defineProps({
  modelValue: {
    type: Object,
    default: () => ({})
  }
})

const prodLangList = ref([])
watch(() => prodLangList.value, (val) => {
  emit('updateContent', val)
}, { deep: true })

const loading = ref(true)
onMounted(() => {
  prodLangList.value = JSON.parse(JSON.stringify(props.modelValue.prodLangList))
  // 等待数据接受完毕，再渲染富文本组件
  nextTick(() => {
    loading.value = false
  })
})

</script>

<style lang="scss" scoped>
.posting-product-details {
  .prod-det-box {
    display: flex;
    justify-content: space-between;
    --details-preview-width:397px;

    .prod-content {
      width: 60%;
      width: calc(100% - var(--details-preview-width) - 30px);
      margin-right: 30px;
    }

    // 详情预览
    .details-preview {
      width: 40%;
      width: var(--details-preview-width);
      border: 1px solid #DCDCDC;
      box-sizing: border-box;
      .preview-box-title {
        height: 45px;
        line-height: 45px;
        background: #f9f9f9;
        text-align: center;
        border-bottom: 1px solid #DCDCDC;
      }
      :deep(.preview-con){
        img{
          vertical-align: initial;
          font-size: initial;
        }
      }

      .preview-con {
        &::-webkit-scrollbar {
          display: none;
        }
        scrollbar-width:none;
        height: 540px;
        overflow-y: auto;
        padding: 0 10px;
        box-sizing: border-box;
        word-wrap: break-word;
      }
    }
  }
}
</style>
