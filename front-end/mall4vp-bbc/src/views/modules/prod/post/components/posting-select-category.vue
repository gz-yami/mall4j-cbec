<template>
  <!-- 发布商品：01、选择商品类目 -->
  <div class="posting-select-category">
    <!-- 商品类型 -->
    <div class="prod-type-box">
      <div class="prod-title-row">
        <span>{{ $t("product.prodMold") }}</span>
      </div>
      <div class="type-select">
        <!-- 实物商品（物流发货） -->
        <div
          class="type-item"
          :class="{'active': dataForm.mold === 0}"
          @click="switchProdType(0)"
        >
          <div class="big-txt">
            {{ $t("product.physicalGoods") }}
          </div>
          <div class="weak-txt">
            {{ $t("product.logisticsDelivery") }}
          </div>
          <i class="active-icon" />
        </div>
        <!-- 虚拟商品（无需物流） -->
        <div
          class="type-item"
          :class="{'active': dataForm.mold === 1}"
          @click="switchProdType(1)"
        >
          <div class="big-txt">
            {{ $t("product.virtualGoods") }}
          </div>
          <div class="weak-txt">
            {{ $t("product.noLogisticsRequired") }}
          </div>
          <i class="active-icon" />
        </div>
      </div>
    </div>

    <!-- 商品分类 -->
    <div class="prod-category-box">
      <div class="prod-title-row">
        <span>{{ $t("product.productCategories") }}</span>
      </div>
      <div class="select-category-containers">
        <!-- 选择店铺分类 -->
        <div class="category-com">
          <div class="title">
            <span class="stress">*</span>{{ $t("product.chooseProdCateg") }}：
          </div>
          <categorySelection
            :category-id="dataForm.categoryId"
            :category-type="'platform'"
            @close="getSelectedCategory"
            @re-selection="reSelectionCategory"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import categorySelection from './category-selection.vue'

const emit = defineEmits(['selectedCategoryName', 'update:modelValue', 'clearStock'])

const props = defineProps({
  modelValue: {
    type: Object,
    default: () => ({})
  }
})

const dataForm = computed(() => props.modelValue)

watch(() => dataForm.value, (val) => {
  emit('update:modelValue', val)
}, { deep: true })

// 分类名称组合
const categoryName = ref('')
watch(categoryName, (newVal) => {
  emit('selectedCategoryName', newVal)
})

/**
 * 切换商品类型
 */
const switchProdType = (type) => {
  if (dataForm.value.prodId) {
    if (type !== dataForm.value.mold) {
      ElMessage({
        message: $t('product.cannotModifyProdType'),
        type: 'error',
        duration: 1500,
        onClose: () => {
        }
      })
      return
    }
  }
  dataForm.value.mold = type
  if (dataForm.value.mold !== 0) dataForm.value.prodType = 0
  emit('clearStock')
}

/**
 * 获取选定的类别
 */
const getSelectedCategory = (...flag) => {
  // 选择的末级分类id
  dataForm.value.categoryId = flag[0]
  // 分类名称展示
  categoryName.value = flag[4] && flag[3] && flag[2] ? flag[2] + ' > ' + flag[3] + ' > ' + flag[4] : flag[3] && flag[2] ? flag[2] + ' > ' + flag[3] : flag[2] ? flag[2] : ''
}
/**
 * 重选分类
 */
const reSelectionCategory = () => {
  if (dataForm.value.categoryId) {
    dataForm.value.categoryId = null
    categoryName.value = ''
  }
}

</script>

<style lang="scss" scoped>
.posting-select-category {
  // 商品类型
  .prod-type-box {
    .type-select {
      display: flex;
      align-items: center;
      justify-content: flex-start;
      .type-item:not(:last-child) {
        margin-right: 10px;
      }
      .type-item {
        position: relative;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 14px;
        padding: 16px;
        border: 1px solid #DCDCDC;
        cursor: pointer;
        .weak-txt {
          font-size: 12px;
          color: #999;
        }
        &.active {
          border-color: #155BD4;
          .big-txt {
            color: #155BD4;
          }
          .active-icon {
            position: absolute;
            right: 0;
            bottom: 0;
            width: 0;
            height: 0;
            border-bottom: 20px solid #155BD4;
            border-left: 20px solid transparent;
            &::after {
              content: "";
              position: absolute;
              right: 2px;
              bottom: -16px;
              width: 10px;
              height: 5px;
              border: 1px solid #fff;
              border-radius: 1px;
              border-top: none;
              border-right: none;
              background: transparent;
              transform: rotate(-45deg);
            }
          }
        }
      }
      .type-item:last-child {
        &.active {
          .active-icon {
            right: -1px;
          }
        }
      }
    }
  }

  // 商品分类
  .prod-category-box {
    margin-top: 30px;
    .select-category-containers {
      .category-com {
        display: inline-block;
        margin-right: 50px;
        .title {
          font-size: 12px;
          margin-bottom: 10px;
          .stress {
            font-size: 14px;
            color: #D40000;
            padding-right: 4px;
          }
        }
      }
    }
  }
}
</style>
