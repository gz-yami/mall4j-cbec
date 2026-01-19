<template>
  <div class="mod-index-img">
    <el-dialog
      v-model="visible"
      :title="!dataForm.imgId ? $t('brand.add') : $t('coupon.edit')"
      :close-on-click-modal="false"
      @close="closeDialogHandle"
    >
      <el-form
        ref="dataFormRef"
        :model="dataForm"
        :rules="dataRule"
        :label-width="$t('language')==='zh_CN'?'100px': '160px'"
        @submit.prevent
      >
        <el-form-item
          :label="$t('platform.carouselImg')"
          prop="imgUrl"
        >
          <pic-upload v-model="dataForm.imgUrl" />
          <div style="width: 100%;">
            <span v-if="dataForm.imgType === 0">{{ $t('platform.recommImgSize') }}750*400</span>
            <span v-if="dataForm.imgType === 1">{{ $t('platform.recommImgSize') }}1920*450</span>
          </div>
        </el-form-item>
        <el-form-item
          :label="$t('brand.sequence')"
          prop="seq"
        >
          <el-input
            v-model="dataForm.seq"
            min="0"
            max="2147483640"
            @change="seqCheck"
          />
        </el-form-item>
        <el-form-item
          :label="$t('brand.status')"
          prop="status"
        >
          <el-radio-group v-model="dataForm.status">
            <el-radio :label="0">
              {{ $t('publics.disable') }}
            </el-radio>
            <el-radio :label="1">
              {{ $t('publics.normal') }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item
          :label="$t('platform.platform')"
          prop="imgType"
        >
          <el-radio-group v-model="dataForm.imgType">
            <el-radio :label="0">
              {{ $t('platform.mobile') }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div>
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
  status: 1,
  des: '',
  imgUrl: '',
  seq: 0,
  imgType: 0,
  imgId: 0,
  type: -1,
  relation: null
})
// 关联数据
const card = ref({
  id: 0,
  pic: '',
  name: '',
  realData: {
    prod: [],
    shop: [],
    activity: []
  }
})

const page = reactive({
  total: 0, // 总页数
  currentPage: 1, // 当前页数
  pageSize: 10 // 每页显示多少条
})
const dataRule = reactive({
  imgUrl: [
    { required: true, message: $t('platform.carouselImgNoNull'), trigger: 'blur' }
  ],
  seq: [
    { required: true, message: $t('platform.seqNONull'), trigger: 'blur' }
  ]
})

const visible = ref(false)
let isSubmit = false
const prodsSelectVisible = ref(false)
const dataFormRef = ref(null)
// 获取分类数据
const init = (id) => {
  visible.value = true
  isSubmit = false
  dataForm.value.imgId = id || 0
  nextTick(() => {
    prodsSelectVisible.value = false
    dataFormRef.value?.resetFields()
  })
  if (dataForm.value.imgId) {
    // 获取产品数据
    http({
      url: http.adornUrl(`/platform/indexImg/info/${dataForm.value.imgId}`),
      method: 'get',
      params: http.adornParams()
    }).then(({ data }) => {
      dataForm.value = data
      if (data.relation) {
        card.value.pic = data.pic
        card.value.name = data.prodName
        card.value.id = data.relation
      }
    })
  } else {
    nextTick(() => {
      dataFormRef.value?.resetFields()
      dataForm.value.imgUrl = ''
    })
  }
}

// 检验输入顺序值
const seqCheck = () => {
  let maxNum = Math.round(dataForm.value.seq)
  if (!maxNum) {
    maxNum = 0
  } else if (maxNum < 0) {
    maxNum = 0
  } else if (maxNum > 2147483640) {
    maxNum = 2147483640
  }
  dataForm.value.seq = maxNum
}

// 表单提交
const onSubmit = () => {
  if (dataForm.value.type === 0 && dataForm.value.relation == null) {
    ElMessage({
      message: $t('marketing.pleaseSelectAProduct'),
      type: 'error',
      duration: 1500
    })
    return
  }
  dataFormRef.value?.validate(() => {
    if (!dataForm.value.imgUrl) {
      return
    }
    const param = dataForm.value
    if (isSubmit) {
      return false
    }
    isSubmit = true
    http({
      url: http.adornUrl('/platform/indexImg'),
      method: param.imgId ? 'put' : 'post',
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
  })
}

// 关闭对话框回调
const closeDialogHandle = () => {
  prodsSelectVisible.value = false
  dataFormRef.value?.resetFields()
  dataForm.value.relation = null
  card.value = {
    id: 0,
    pic: '',
    name: '',
    realData: {
      prod: [],
      shop: [],
      activity: []
    }
  }
}

defineExpose({
  init
})

</script>
<style lang="scss" scoped>
.mod-index-img {
  &:deep(.whether){
    padding-top: 10px;
    width: 100%;
    display: flex;
    justify-content:flex-end;
    border-top: 1px solid #eeeeee;

  }
}
</style>
