<template>
  <div class="page-member-manage-list mod-user">
    <div class="search-bar">
      <el-form
        ref="searchFormRef"
        :inline="true"
        class="search-form"
        :model="searchForm"
        @submit.prevent
      >
        <div class="input-row">
          <el-form-item
            prop="nickName"
            :label="$t('users.name') + ':'"
          >
            <el-input
              v-model="searchForm.nickName"
              type="text"
              clearable
              :placeholder="$t('users.name')"
            />
          </el-form-item>
          <el-form-item
            prop="userMail"
            :label="$t('sys.email') + ':'"
          >
            <el-input
              v-model="searchForm.userMail"
              type="text"
              clearable
              maxlength="320"
              :placeholder="$t('sys.email')"
            />
          </el-form-item>
          <el-form-item>
            <div
              class="default-btn primary-btn"
              @click="onSearch(true)"
            >
              {{ $t('crud.searchBtn') }}
            </div>
            <div
              class="default-btn"
              @click="resetForm()"
            >
              {{ $t('product.reset') }}
            </div>
          </el-form-item>
        </div>
      </el-form>
    </div>

    <div class="main-container">
      <div class="table-con">
        <div
          v-if="dataList.length === 0"
          class="empty-text"
        >
          {{ $t('user.noData') }}
        </div>
        <el-table
          :empty-text="$t('order.noData')"
          :data="dataList"
          header-cell-class-name="table-header"
          row-class-name="table-row-low"
          style="width: 100%"
        >
          <el-table-column
            fixed
            :label="$t('users.name')"
            prop="nickName"
          />
          <el-table-column
            fixed
            prop="pic"
            :label="$t('publics.profilePicture')"
          >
            <template #default="scope">
              <div class="table-cell-image">
                <img
                  v-if="!scope.row.pic"
                  src="~@/assets/img/userImg.jpg"
                  width="130px"
                >
                <img
                  v-else
                  :src="checkFileUrl(scope.row.pic)"
                  @error="scope.row.pic=''"
                >
              </div>
            </template>
          </el-table-column>
          <el-table-column
            prop="userMail"
            :label="$t('sys.email')"
          >
            <template #default="scope">
              <div>{{ scope.row.userMail }}</div>
            </template>
          </el-table-column>
          <el-table-column
            prop="userRegtime"
            :label="$t('user.registrationTime')"
          />
          <template #empty>
            <div>
              &nbsp;
            </div>
          </template>
        </el-table>
      </div>
      <el-pagination
        :current-page="page.currentPage"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="page.pageSize"
        :total="page.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="onPageSizeChange"
        @current-change="onPageChange"
      />
    </div>
  </div>
</template>

<script setup>
let tempSearchForm = null // 保存上次点击查询的请求条件

const dataList = ref([])
const createDateRange = ref([]) // 注册时间范围
const page = reactive({
  total: 0, // 总页数
  currentPage: 1, // 当前页数
  pageSize: 10 // 每页显示多少条
})
const searchForm = reactive({
  nickName: '',
  userMail: ''
})

onMounted(() => {
  getDataList(page)
})

// 获取数据列表  /admin/user/page
const getDataList = (pageParam, newData = false) => {
  if (newData || !tempSearchForm) {
    tempSearchForm = JSON.parse(JSON.stringify(searchForm))
  }
  tempSearchForm.userTag = null
  http({
    url: http.adornUrl('/admin/user/userPage'),
    method: 'get',
    params: http.adornParams(
      Object.assign(
        {
          current: pageParam == null ? page.currentPage : pageParam.currentPage,
          size: pageParam == null ? page.pageSize : pageParam.pageSize
        },
        tempSearchForm
      )
    )
  }).then(({ data }) => {
    getVipInternationalization(data.records)
    dataList.value = data.records
    page.total = data.total
  })
}

// 条件查询 JSON.stringify(arr)
const onSearch = (newData = false) => {
  page.currentPage = 1
  getDataList(page, newData)
}
const onPageSizeChange = (val) => {
  page.pageSize = val
  getDataList(page)
}
const onPageChange = (val) => {
  page.currentPage = val
  getDataList(page)
}
const searchFormRef = ref(null)
const resetForm = () => {
  searchFormRef.value.resetFields()
  searchForm.userTag = []
  searchForm.userRegStartTime = null
  searchForm.userRegEndTime = null
  createDateRange.value = []
}

const langStore = useLangStore()
// 国际化
const getVipInternationalization = (data) => {
  if (!data) return
  const lang = localStorage.getItem('cbecB2cLang')
  const langList = langStore.langList
  let obj = langList.find(i => i.language === lang)
  if (!obj) obj = langList[0]
  data.forEach(element => {
    if (!element.userLevelLangList) return element
    const nowLangVal = element.userLevelLangList.find(i => i.lang === obj.lang)
    if (nowLangVal && nowLangVal.levelName) {
      element.levelName = nowLangVal.levelName
    }
  })
}
</script>
<style lang="scss" scoped>
.page-member-manage-list.mod-user {
  :deep(.main-container){
    .el-table__fixed{
      height: auto !important;
      bottom: 16px !important;
      &::before {
        background-color: transparent !important;
      }
    }
    .el-table__fixed-right{
      height: auto !important;
      bottom: 16px !important;
      &::before {
        background-color: transparent !important;
      }
    }
   .export-load {
      top: -50% !important;
    }
    .el-select .el-select__tags > span > .el-tag.el-tag--info:nth-last-child(2) {
      max-width: 120px;
    }
  }

  .table-con {
    position: relative;
  }

  .empty-text {
    position: absolute;
    z-index: 4;
    top: 50%;
    left: 50%;
    color: #909399;
  }

}
</style>
