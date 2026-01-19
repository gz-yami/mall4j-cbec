<template>
  <el-dialog
    v-model="visible"
    :title="!dataForm.id ? $t('sysManagement.add') : $t('sysManagement.modify')"
    :close-on-click-modal="false"
  >
    <el-form
      ref="dataFormRef"
      :model="dataForm"
      :rules="dataRule"
      :label-width="$t('language')!=='zh_CN'? 'auto' : '120px'"
      @submit.prevent
      @keyup.enter="onSubmit()"
    >
      <el-form-item
        :label="$t('sys.roleName')"
        prop="roleName"
      >
        <el-input
          v-model.trim="dataForm.roleName"
          :placeholder="$t('sys.roleName')"
          maxlength="20"
          show-word-limit
        />
      </el-form-item>
      <el-form-item
        :label="$t('publics.remark')"
        prop="remark"
      >
        <el-input
          v-model="dataForm.remark"
          type="textarea"
          :placeholder="$t('publics.remark')"
          maxlength="100"
          show-word-limit
        />
      </el-form-item>
      <el-form-item
        :label="$t('sys.authorize')"
        required
      >
        <el-tree
          ref="menuListTreeRef"
          :data="menuList"
          style="flex:1"
          :props="menuListTreeProps"
          node-key="menuId"
          show-checkbox
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
import { treeDataTranslate } from '@/utils'
import { ElMessage } from 'element-plus'

const emit = defineEmits(['refreshDataList'])
const dataFormRef = ref(null)
const menuListTreeRef = ref(null)
const visible = ref(false)
const menuList = ref([])
const menuListTreeProps = reactive({
  label: 'name',
  children: 'children'
})
const dataForm = reactive({
  id: 0,
  roleName: '',
  remark: ''
})
const isSubmit = ref(false)
const dataRule = reactive({
  roleName: [
    { required: true, message: $t('sys.roleNameNoNull'), trigger: 'blur' }
  ]
})

const init = (id) => {
  dataForm.id = id || 0
  isSubmit.value = false
  let nodeList = []
  http({
    url: http.adornUrl('/sys/menu/table'),
    method: 'get',
    params: http.adornParams()
  }).then(({ data }) => {
    menuList.value = treeDataTranslate(data, 'menuId', 'parentId')
    if (dataForm.id) {
      nodeList = getNode(menuList.value)
    }
  }).then(() => {
    visible.value = true
    nextTick(() => {
      dataFormRef.value?.resetFields()
      menuListTreeRef.value?.setCheckedKeys([])
    })
  }).then(() => {
    if (dataForm.id) {
      http({
        url: http.adornUrl(`/sys/role/info/${dataForm.id}`),
        method: 'get',
        params: http.adornParams()
      }).then(({ data }) => {
        dataForm.roleName = data.roleName
        dataForm.remark = data.remark
        const selectMenuIdList = data.menuIdList.filter(x => !nodeList.includes(x))
        menuListTreeRef.value?.setCheckedKeys(selectMenuIdList)
      })
    }
  })
}

// 查找菜单节点id
const getNode = (list) => {
  const res = []
  list.forEach(item => {
    if (item.children) {
      res.push(item.menuId, ...getNode(item.children))
    }
  })
  return res
}

// 表单提交
const onSubmit = () => {
  const menuIdList = [].concat(menuListTreeRef.value?.getCheckedKeys(), menuListTreeRef.value?.getHalfCheckedKeys())
  if (!menuIdList.length) {
    return ElMessage({
      message: $t('sys.pleaseSelectAuth'),
      type: 'warning',
      duration: 1500
    })
  }
  dataFormRef.value?.validate((valid) => {
    if (valid) {
      if (isSubmit.value) {
        return false
      }
      isSubmit.value = true
      http({
        url: http.adornUrl('/sys/role'),
        method: dataForm.id ? 'put' : 'post',
        data: http.adornData({
          roleId: dataForm.id || undefined,
          roleName: dataForm.roleName,
          remark: dataForm.remark,
          menuIdList
        })
      }).then(() => {
        ElMessage({
          message: $t('publics.operation'),
          type: 'success',
          duration: 1500,
          onClose: () => {
            visible.value = false
            emit('refreshDataList')
          }
        })
      }).catch((e) => {
        isSubmit.value = false
      })
    }
  })
}
defineExpose({ init })
</script>
<style scoped lang="scss">
:deep(.el-form-item__label) {
  min-width: 160px;
}
</style>
