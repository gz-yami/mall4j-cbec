<template>
  <div class="app-container">
    <div class="main-content">
      <el-tabs
        v-model="activeName"
        class="tabs"
      >
        <el-tab-pane
          :label="$t('webInfoConfig.platformSideConfiguration')"
          name="webConfigBs"
        >
          <add-or-update
            v-if="activeName === 'webConfigBs'"
            param-key="PLATFROM_WEBSITE_CONFIG"
            remark="后台端网站配置"
          />
        </el-tab-pane>
        <el-tab-pane
          :label="$t('webInfoConfig.h5Configuration')"
          name="webConfigUni"
        >
          <add-or-update
            v-if="activeName === 'webConfigUni'"
            param-key="H5_WEBSITE_CONFIG"
            remark="H5端网站配置"
          />
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import AddOrUpdate from './add-or-update.vue'

const activeName = ref('webConfigBs')

</script>
<style lang="scss" scoped>
</style>
