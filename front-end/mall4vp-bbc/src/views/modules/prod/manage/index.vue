<template>
  <div class="mod-prod prod-list-mod">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-form
        ref="searchFormRef"
        :inline="true"
        class="search-form"
        :model="searchForm"
        label-width="auto"

        @submit.prevent
      >
        <!-- 表单项 -->
        <div class="input-row">
          <el-form-item :label="$t('product.prodName')+':'">
            <el-input
              v-model="prodName"
              type="text"
              clearable
              :placeholder="$t('product.prodName')"
            />
          </el-form-item>
          <el-form-item
            prop="status"
            :label="$t('product.status')+':'"
          >
            <el-select
              v-model="searchForm.status"
              clearable
              :placeholder="$t('product.status')"
            >
              <el-option
                :label="$t('publics.UpperShelf')"
                :value="1"
              />
              <el-option
                :label="$t('publics.LowerShelf')"
                :value="0"
              />
            </el-select>
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
              @click="onResetSearch"
            >
              {{ $t('shop.resetMap') }}
            </div>
          </el-form-item>
        </div>
      </el-form>
    </div>

    <!-- 主体内容 -->
    <div class="main-container">
      <!-- 操作按钮 -->
      <div class="operation-bar">
        <el-checkbox
          v-model="selectAll"
          :disabled="dataList.length===0"
          class="all-check-btn"
          @change="onSelectAll"
        >
          {{ $t('publics.selectAll') }}
        </el-checkbox>
        <span
          v-if="dataListSelections.length"
          class="had-selected"
        >{{ $t('publics.selected') }} {{ dataListSelections.length }}</span>
        <div
          v-if="isAuth('prod:prod:batchStatus') || isAuth('prod:prod:batchDelete')"
          style="display:inline-block; margin-left: 20px"
        >
          <el-dropdown
            class="batch-setting-dropdown"
            :hide-on-click="true"
            trigger="click"
            @command="handleBatchSetting"
            @visible-change="handleVisibleChange"
          >
            <div
              :class="[showBatchSetting? 'active' : '','default-btn batch-setting-btn']"
            >
              {{ $t('publics.batchSetting') }}
              <i class="arrow" />
            </div>
            <!-- 批量设置功能列表 -->
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item
                  v-if="isAuth('prod:prod:batchStatus')"
                  command="1"
                >
                  {{ $t('product.batchUp') }}
                </el-dropdown-item>
                <el-dropdown-item
                  v-if="isAuth('prod:prod:batchStatus')"
                  command="0"
                >
                  {{ $t('product.batchDown') }}
                </el-dropdown-item>
                <el-dropdown-item
                  v-if="isAuth('prod:prod:batchDelete')"
                  command="-1"
                >
                  {{ $t('publics.batchDelete') }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
      <!-- 表格 -->
      <div class="table-con prod-table">
        <el-table
          ref="prodListTableRef"
          :data="dataList"
          header-cell-class-name="table-header"
          row-class-name="table-row"
          style="width: 100%"
          @sort-change="handleSortableChange"
          @selection-change="onSelectSome"
        >
          <el-table-column
            type="selection"
            width="55"
          />
          <el-table-column
            fixed
            prop="prodName"
            :label="$t('product.prodInfo')"
            width="340"
          >
            <template #default="scope">
              <div class="table-cell-con">
                <div class="table-cell-image">
                  <img-show :src="scope.row.pic" />
                </div>
                <span class="table-cell-text">{{ scope.row.prodName }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column
            align="left"
            prop="oriPrice"
            :label="$t('product.marketPrice') + '(' + useCurrencyStore().defMark + ')'"
            :width="$t('language') !== 'zh_CN'?160:140"
            sortable
          />
          <el-table-column
            align="left"
            prop="price"
            :label="$t('product.sellingPrice') + '(' + useCurrencyStore().defMark + ')'"
            :width="$t('language') !== 'zh_CN' ? 160 : 140"
            sortable
          />
          <el-table-column
            align="left"
            prop="totalStocks"
            :label="$t('product.availableInventory')"
            :width="$t('language') !== 'zh_CN'?200:150"
            sortable
          />
          <el-table-column
            align="left"
            prop="prodType"
            :label="$t('product.prodType')"
            width="130"
          >
            <template #default="scope">
              <!-- 商品类型(0普通商品 1拼团 2秒杀 3积分 5活动商品) -->
              <div class="tag-text">
                {{ [$t('product.ordProd'),$t('product.groupProd'),$t('product.limitedTimeProd'),$t('product.pointsProduct'),'', $t('product.activeProd')][scope.row.prodType] }}
              </div>
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="prodMold"
            :label="$t('product.prodMold')"
            width="150"
          >
            <template #default="scope">
              <!-- 虚拟商品(0否 1是) -->
              <div
                v-if="scope.row.mold === 0"
                class="tag-text"
              >
                {{ $t('product.physicalGoods') }}
              </div>
              <div
                v-if="scope.row.mold === 1"
                class="tag-text"
              >
                {{ $t('product.virtualGoods') }}
              </div>
              <div
                v-if="scope.row.mold === 2"
                class="tag-text"
              >
                {{ $t('product.comboGoods') }}
              </div>
            </template>
          </el-table-column>
          <el-table-column
            align="left"
            prop="status"
            :label="$t('product.status')"
          >
            <template #default="scope">
              <div
                v-if="scope.row.status === 1"
                class="tag-text"
              >
                {{ $t("publics.UpperShelf") }}
              </div>
              <div
                v-if="scope.row.status === 0"
                class="tag-text"
              >
                {{ $t("publics.LowerShelf") }}
              </div>
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="deliveryMode"
            :label="$t('product.delType')"
            width="150"
          >
            <template #default="scope">
              <div v-if="scope.row.prodType === 5">
                -
              </div>
              <div v-else>
                {{ onHandleDeliveryMode(scope.row.deliveryMode,scope.row.mold, scope.row.prodType) }}
              </div>
            </template>
          </el-table-column>
          <el-table-column
            align="left"
            prop="seq"
            :label="$t('publics.seq')"
            width="120"
            sortable
          />
          <el-table-column
            fixed="right"
            align="center"
            :label="$t('publics.operating')"
            width="180"
          >
            <template #default="scope">
              <div class="text-btn-con">
                <div
                  v-if="isAuth('prod:prod:update')"
                  class="default-btn text-btn"
                  @click="onAddOrUpdate(scope.row.prodId)"
                >
                  {{ $t('groups.edit') }}
                </div>
                <div
                  v-if="isAuth('prod:prod:status') && scope.row.status === 0"
                  class="default-btn text-btn"
                  @click="changeProdStatus(scope.row.prodId, 1)"
                >
                  {{ $t('publics.UpperShelf') }}
                </div>
                <div
                  v-if="isAuth('prod:prod:status') && scope.row.status === 1"
                  class="default-btn text-btn"
                  @click="changeProdStatus(scope.row.prodId, 0)"
                >
                  {{ $t('publics.LowerShelf') }}
                </div>
                <div
                  v-if="isAuth('prod:prod:delete')"
                  class="default-btn text-btn"
                  @click="onDelete(scope.row.prodId)"
                >
                  {{ $t("text.delBtn") }}
                </div>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <!-- 分页 -->
      <el-pagination
        v-if="dataList.length"
        :current-page="page.currentPage"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="page.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="page.total"
        @size-change="onPageSizeChange"
        @current-change="onPageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import { isAuth } from '@/utils'

let tempSearchForm = null // 保存上次点击查询的请求条件
let theParams = null // 保存上次点击查询的请求条件

const dataList = ref([])
const page = reactive({
  total: 0, // 总页数
  currentPage: 1, // 当前页数
  pageSize: 10 // 每页显示多少条
})
const searchForm = reactive({
  status: ''
})

let prodId = null

const prodName = ref('')
onMounted(() => {
  if (useRoute().query.prodName) {
    prodName.value = useRoute().query.prodName
  }
  prodId = useRoute().query.prodId || null
  const parPage = useRoute().query.page
  if (parPage) {
    const { total, currentPage, pageSize } = JSON.parse(decodeURI(parPage))
    page.total = total
    page.currentPage = currentPage
    page.pageSize = pageSize
  }
  getDataList(page)
})

// 获取数据列表
const getDataList = (pageParam, newData = false) => {
  if (page) {
    const size = Math.ceil(page.total / page.pageSize)
    page.currentPage = (page.currentPage > size ? size : page.currentPage) || 1
  }
  // mysql 请求接口
  if (newData || !tempSearchForm) {
    theParams = JSON.parse(JSON.stringify(searchForm))
    tempSearchForm = {
      current: pageParam == null ? page.currentPage : pageParam.currentPage,
      keyword: prodName.value,
      size: pageParam == null ? page.pageSize : pageParam.pageSize,
      prodId: prodId || null
    }
  } else {
    tempSearchForm.current = pageParam == null ? page.currentPage : pageParam.currentPage
    tempSearchForm.size = pageParam == null ? page.pageSize : pageParam.pageSize
  }
  theParams.mustNotProdTypes = [3]
  if (searchForm.deliveryMode && searchForm.deliveryMode === 5) {
    theParams.deliveryMode = null
    // 无需快递
    if (searchForm.mold == null || searchForm.mold === '') {
      theParams.mold = 1
    }
    if (searchForm.mold === 0) {
      // 实物商品
      dataList.value = []
      return
    }
  }
  if (searchForm.deliveryMode && searchForm.deliveryMode !== 5) {
    // 实物快递
    if (searchForm.mold === 1) {
      // 虚拟商品
      dataList.value = []
      return
    }
    theParams.mold = 0
    theParams.mustNotProdTypes = [3, 5]
  }
  http({
    url: http.adornUrl('/prod/prod/page'),
    method: 'get',
    params: http.adornParams(
      Object.assign(
        tempSearchForm,
        theParams
      )
    )
  }).then(({ data }) => {
    dataList.value = data.records
    for (const key in dataList.value) {
      if (Object.prototype.hasOwnProperty.call(dataList.value, key)) {
        const element = dataList.value[key]
        if (element.imgs != null && element.imgs !== '') {
          element.imgs = element.imgs.split(',')[0]
        }
      }
    }
    page.total = data.total
    // 末尾页数据为空重新请求
    if (!dataList.value.length && page.currentPage > 1) {
      page.currentPage = 1
      getDataList()
    }
  })
}
// 新增 / 修改
const router = useRouter()
const onAddOrUpdate = (id) => {
  if (!isAuth('prod:prod:save')) {
    ElMessage.info($t('homes.noPermissionAccessPage'))
    return
  }
  router.push({
    path: '/prod/post/index',
    query: { prodId: id, page: encodeURI(JSON.stringify(page)) }
  })
}
// 删除
const dataListSelections = ref([])
const onDelete = (id) => {
  const ids = id ? [id] : dataListSelections.value.map(item => {
    return item.prodId
  })
  ElMessageBox.confirm(`${$t('admin.isDeleOper')}?`, $t('text.tips'), {
    confirmButtonText: $t('crud.filter.submitBtn'),
    cancelButtonText: $t('crud.filter.cancelBtn'),
    type: 'warning'
  }).then(() => {
    http({
      url: http.adornUrl(`/prod/prod/${id}`),
      method: 'delete',
      data: http.adornData({})
    }).then(() => {
      page.total = page.total - ids.length
      ElMessage({
        message: $t('publics.operation'),
        type: 'success',
        duration: 1500,
        onClose: () => {
          getDataList(page)
        }
      })
    })
  })
    .catch(() => { })
}
// 排序
const handleSortableChange = (sort) => {
  switch (sort.prop) {
    // 市场价
    case 'oriPrice': searchForm.sort = sort.order === 'descending' ? 7 : 8
      break
    // 销售价
    case 'price': searchForm.sort = sort.order === 'descending' ? 4 : 5
      break
    // 库存
    case 'totalStocks': searchForm.sort = sort.order === 'descending' ? 10 : 11
      break
    // 商品序号
    case 'seq': searchForm.sort = sort.order === 'descending' ? 12 : 13
      break
    default: searchForm.sort = ''
      break
  }
  if (!sort.order) searchForm.sort = ''
  getDataList(page, true)
}

// 批量改变状态
const batchChangeProdStatus = (status) => {
  const FailList = []
  const prodList = dataListSelections.value.filter(item => {
    if (item.status !== 0 && item.status !== 1) {
      FailList.push(item)
    }
    if (status === 0 && item.status === 1) {
      // 进行下架商品操作，则只改变状态为上架的
      return true
    } else return status === 1 && item.status === 0
  })
  const ids = prodList.map(item => {
    return item.prodId
  })
  if (FailList.length > 0) {
    FailList.forEach(el => {
      setTimeout(() => {
        ElMessage.warning($t('home.product') + '（' + el.prodName + '）' + $t('product.cannotBeRemovedOrRemovedFromTheChassis'))
      })
    })
    return
  }
  ElMessageBox.confirm(`${$t('sys.makeSure')}【${status ? $t('product.batchUp') : $t('product.batchDown')}】${$t('publics.operating')}?`, $t('text.tips'), {
    confirmButtonText: $t('crud.filter.submitBtn'),
    cancelButtonText: $t('crud.filter.cancelBtn'),
    type: 'warning'
  }).then(() => {
    http({
      url: http.adornUrl('/prod/prod/batchProdStatus/' + status),
      method: 'put',
      data: http.adornData(ids, false)
    }).then(() => {
      ElMessage({
        message: $t('publics.operation'),
        type: 'success',
        duration: 1500,
        onClose: () => {
          refreshChange()
        }
      })
    })
  }).catch(() => { })
}
// 批量删除
const batchDeleteHandle = () => {
  const ids = dataListSelections.value.map(item => {
    return item.prodId
  })
  ElMessageBox.confirm(`${$t('sys.makeSure')}【${$t('sys.batchDelete')}】${$t('publics.operating')}?`, $t('text.tips'), {
    confirmButtonText: $t('crud.filter.submitBtn'),
    cancelButtonText: $t('crud.filter.cancelBtn'),
    type: 'warning'
  }).then(() => {
    http({
      url: http.adornUrl('/prod/prod'),
      method: 'delete',
      data: http.adornData(ids, false)
    }).then(() => {
      ElMessage({
        message: $t('publics.operation'),
        type: 'success',
        duration: 1500,
        onClose: () => {
          refreshChange()
        }
      })
    })
  }).catch(() => { })
}
const changeProdStatus = (id, status) => {
  ElMessageBox.confirm(`${$t('sys.makeSure')}【${status ? $t('publics.UpperShelf') : $t('publics.LowerShelf')}】${$t('text.menu')}?`, $t('text.tips'), {
    confirmButtonText: $t('crud.filter.submitBtn'),
    cancelButtonText: $t('crud.filter.cancelBtn'),
    type: 'warning'
  })
    .then(() => {
      http({
        url: http.adornUrl('/prod/prod/prodStatus'),
        method: 'put',
        data: http.adornData({
          status,
          prodId: id
        })
      }).then(() => {
        ElMessage({
          message: $t('publics.operation'),
          type: 'success',
          duration: 1500,
          onClose: () => {
            page.currentPage = 1
            getDataList(page)
          }
        })
      })
    })
}
// 条件查询
const onSearch = (newData = false) => {
  page.currentPage = 1
  getDataList(page, newData)
}

// 回调刷新
const refreshChange = () => {
  getDataList(page)
}

// 多选变化
const selectAll = ref(false) // 是否全选
const onSelectSome = (val) => {
  dataListSelections.value = val
  selectAll.value = val.length === dataList.value.length
  if (dataList.value.length === 0) {
    selectAll.value = false
  }
}

// 全选按钮
const prodListTableRef = ref(null)
const onSelectAll = () => {
  prodListTableRef.value?.toggleAllSelection()
}

/**
 * 批量设置菜单显隐
 */
// 显示批量操作
const showBatchSetting = ref(false)
const handleVisibleChange = (val) => {
  showBatchSetting.value = val
}

/**
 * 批量设置
 */
const handleBatchSetting = (command) => {
  const commands = Number(command)
  if (!dataListSelections.value.length) {
    ElMessage.warning($t('marketing.pleaseSelectAProduct'))
    return
  }
  if (commands !== -1) {
    batchChangeProdStatus(commands)
  } else {
    batchDeleteHandle()
  }
}

/**
 * 重置表单
 * @param {String} formName 表单名称
 */
const searchFormRef = ref(null)
const selectedPlatformCategories = ref([])
const onResetSearch = () => {
  searchFormRef.value.resetFields()
  prodName.value = ''
  prodId = null
  searchForm.mold = null
  searchForm.prodType = null
  searchForm.primaryCategoryId = ''
  searchForm.secondaryCategoryId = ''
  searchForm.categoryId = ''
  selectedPlatformCategories.value = []
  theParams.mustNotProdTypes = [3]
}

const onPageSizeChange = (val) => {
  page.pageSize = val
  getDataList()
}
const onPageChange = (val) => {
  page.currentPage = val
  getDataList()
}

const onHandleDeliveryMode = (value, mold, prodType) => {
  // mold 虚拟商品
  if (mold === 1 || prodType === 5) {
    return $t('order.noNeedRequired')
  }
  // 将商品的配送方式字符串转化为json
  const objValue = JSON.parse(value)
  // 声明返回结果 result
  const result = []
  if (objValue.hasCityDelivery === true) {
    result.push($t('product.sameCityDelivery'))
  }
  if (objValue.hasUserPickUp === true) {
    result.push($t('product.userPickUp'))
  }
  if (objValue.hasShopDelivery === true) {
    result.push($t('product.ExpressDistribution'))
  }
  return result.join(' / ')
}

</script>

<style lang="scss" scoped>
.prod-list-mod {
  .main-container {
    .table-con .table-cell-con .table-cell-text{
      word-break: break-all
    }
    .operation-bar {
      position: relative;
      .all-check-btn {
        margin-right: 10px;
      }
      .had-selected {
        font-size: 12px;
        margin-right: 10px;
      }
      .tag-text {
        font-size: 12px;
      }
      .batch-setting-dropdown {
         .batch-setting-btn {
           padding-right: 15px;
           margin-right: 0;
          &:hover {
            .arrow::after {
              border-top: 1px solid #155bd4;
              border-right: 1px solid #155bd4;
            }
          }
          &.active {
            color: #155bd4;
            border-color: #155bd4;
            .arrow::after {
              border-top: 1px solid #155bd4;
              border-right: 1px solid #155bd4;
              transform: rotate(-45deg);
              top: 0;
              right: 0;
              transition: all .2s linear;
            }
          }
          .arrow {
            position: relative;
            margin-left: 9px;
            &::after{
              position: relative;
              top: -3px;
              right: 0;
              transition: all .2s linear;
              content: '';
              display: inline-block;
              width: 6px;
              height: 6px;
              border-top: 1px solid #656565;
              border-right: 1px solid #656565;
              transform: rotate(135deg);
            }
          }
        }
      }
    }
    .text-btn-con {
      display: block;
      .text-btn {
        margin: 0;
        padding: 0 10px;
      }
    }
  }
  :deep(.export-load) {
    top: -50% !important;
  }
}
</style>
