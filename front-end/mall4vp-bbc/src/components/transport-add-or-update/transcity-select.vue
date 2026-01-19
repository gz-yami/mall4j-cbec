<template>
  <div
    class="shop-transcity-add-or-update"
    @click="markClick($event)"
  >
    <el-dialog
      v-model="visible"
      :modal="false"
      :title="$t('shop.chooseDeliveryArea')"
      width="1100px"
      :close-on-click-modal="false"
    >
      <main
        v-loading="loading"
        :element-loading-text="$t('components.loading')"
        class="content-box"
      >
        <el-scrollbar style="height:100%;">
          <!-- 区域 -->
          <div
            v-for="region in menuList"
            :key="region.areaId"
            class="addr-region"
          >
            <div class="region-name">
              <el-checkbox
                v-model="region.checked"
                :disabled="region.disabled"
                :indeterminate="region.indeterminate"
                @change="checked=>regionChange(checked, region)"
              >
                {{ region.areaNameEn }}
              </el-checkbox>
            </div>
            <div class="province-box">
              <!-- 省 -->
              <div
                v-for="provinceItem in region.areas"
                :key="provinceItem.areaId"
                class="province-item"
              >
                <el-checkbox
                  v-model="provinceItem.checked"
                  :disabled="provinceItem.disabled"
                  :indeterminate="provinceItem.indeterminate"
                  @change="checked=>provinceChange(checked, provinceItem , region)"
                />
                <span
                  style="cursor: pointer;"
                  @click="handleProvinceClick(provinceItem)"
                >
                  <span class="check-label">{{ provinceItem.areaNameEn }}</span>
                  <span
                    v-show="provinceItem.show"
                    class="area-count"
                  >({{ provinceItem.areaCount }})</span>
                  <el-icon
                    v-if="provinceItem.areas&&provinceItem.areas.length"
                  ><ArrowDown />
                  </el-icon>
                </span>
                <!-- 市 -->
                <div
                  v-if="provinceItem.show"
                  class="city-box bor-style"
                >
                  <div
                    v-for="(cityItem,cityIndex) in provinceItem.areas"
                    :key="cityItem.areaId"
                    class="city-item"
                  >
                    <span ref="cityRef">
                      <el-checkbox
                        v-model="cityItem.checked"
                        :disabled="cityItem.disabled"
                        :indeterminate="cityItem.indeterminate"
                        @change="checked=>cityChange(checked, cityItem , provinceItem,region)"
                      />
                      <span
                        style="cursor: pointer;"
                        @click.stop="handleCityClick(provinceItem.areas,cityItem,cityIndex,$event)"
                      >
                        <span
                          class="check-label"
                          :data-inx="cityIndex"
                        >{{ cityItem.areaNameEn }}</span>
                        <span
                          v-show="provinceItem.show"
                          :data-inx="cityIndex"
                          class="area-count"
                        >({{ cityItem.areaCount }})</span>
                        <el-icon
                          v-if="cityItem.areas&&cityItem.areas.length"
                          :data-inx="cityIndex"
                        ><ArrowDown /></el-icon>
                      </span>
                    </span>
                    <!-- 区 -->
                    <div
                      v-show="cityItem.show"
                      ref="areaBoxRef"
                      :style="areaStyle"
                      class="area-box bor-style"
                    >
                      <div
                        v-for="areaItem in cityItem.areas"
                        :key="areaItem.areaId"
                      >
                        <el-checkbox
                          v-model="areaItem.checked"
                          :disabled="areaItem.disabled"
                          @change="checked=>areaChange(checked, cityItem ,provinceItem,region)"
                        >
                          {{ areaItem.areaNameEn }}
                        </el-checkbox>
                      </div>
                      <!-- 区小角标 -->
                      <span
                        :style="areaMarkStyle"
                        class="area-mark"
                      />
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div style="height:20px;" />
        </el-scrollbar>
      </main>
      <template #footer>
        <div class="dialog-footer">
          <el-checkbox
            v-model="checkedAllState"
            :indeterminate="checkedAllIndeterminate"
            :disabled="checkedAllDisabled"
            @change="checkedAll"
          >
            {{ $t('resource.selectAll') }}
          </el-checkbox>
          <span>
            <span class="area-num">{{ $t('shopProcess.chosen')+' '+checkedAreaNum+' '+$t('components.chosenTip') }}</span>
            <el-button
              type="primary"
              @click="onSubmit()"
            >{{ $t('shopProcess.submit') }}</el-button>
          </span>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
const emit = defineEmits(['refreshDataList'])

let type = 0
let rowIndex = 0
const menuList = ref([])
const checkedAreaNum = ref(0) // 所选区数量
const visible = ref(false)
let holdInitData = {}
let tempData = []
// 下标, obj数组, 0, id数组

const loading = ref(true)
const init = (rowIndexPar, cityList, typePar, banArr) => {
  visible.value = true
  loading.value = true

  holdInitData = {
    cityList,
    banArr
  }
  type = typePar
  rowIndex = rowIndexPar
  checkedAreaNum.value = cityList.length || 0
  if (tempData.length === 0) {
    // 获取所有省和市
    http({
      url: http.adornUrl('/admin/area/areaList'),
      method: 'get',
      params: http.adornParams()
    }).then(({ data }) => {
      tempData = JSON.parse(JSON.stringify(data))
      menuList.value = data
      setTimeout(() => {
        const { cityList, banArr } = holdInitData
        disabledNodes(cityList, banArr)
      }, 50)
    })
  } else {
    menuList.value = JSON.parse(JSON.stringify(tempData))
    setTimeout(() => {
      const { cityList, banArr } = holdInitData
      disabledNodes(cityList, banArr)
    }, 50)
  }
}

const markClick = (e) => {
  // 点击当前弹窗外的部分关闭展开的列表
  if (e.target.className === 'el-overlay-dialog') {
    handleCheckChange(menuList.value, { show: false })
  }
}

// 全选
const checkedAllIndeterminate = ref(false) // 全选按钮不确定状态
const checkedAll = (checked) => {
  if (loading.value) return
  checkedAllIndeterminate.value = false
  handleCheckChange(menuList.value, { checked, indeterminate: false })
  getAreaCheckedList()
}
// 区域复选框
const regionChange = (checked, region) => {
  region.indeterminate = false
  handleCheckChange(region.areas, { checked, indeterminate: false })
  getAreaCheckedList()
  checkedAllJudge() // 放最后
}
// 省复选框
const provinceChange = (checked, province, region) => {
  province.indeterminate = false
  handleCheckChange(province.areas, { checked, indeterminate: false })
  indeterJudge(region)
  getAreaCheckedList()
  checkedAllJudge() // 放最后
}
// 市复选框
const cityChange = (checked, city, province, region) => {
  city.indeterminate = false
  handleCheckChange(city.areas, { checked })
  indeterJudge(province)
  indeterJudge(region)
  getAreaCheckedList()
  checkedAllJudge() // 放最后
}
// 区复选框
// eslint-disable-next-line no-unused-vars
const areaChange = (checked, city, province, region) => {
  indeterJudge(city)
  indeterJudge(province)
  indeterJudge(region)
  getAreaCheckedList()
  checkedAllJudge() // 放最后
}
// 检测是否全选
const checkedAllState = ref(false) // 全选状态
const checkedAllDisabled = ref(false) // 全选按钮是否禁用
const checkedAllJudge = () => {
  let checkedNum = 0
  let disabledNum = 0
  const len = menuList.value.length
  let hasIndeterminate = false
  menuList.value?.forEach(region => {
    region.checked && checkedNum++
    region.disabled && disabledNum++
    region.indeterminate && (hasIndeterminate = true)
  })
  // 是否为全选状态
  checkedAllState.value = checkedNum === len
  // 是否为禁用状态
  checkedAllDisabled.value = disabledNum === len
  // 是否为不确定的状态
  checkedAllIndeterminate.value = !(checkedNum === len || checkedNum === 0) || hasIndeterminate
}
// 检测其孩子列表是否全选
const indeterJudge = (parent) => {
  // 选中的个数
  let tCount = 0
  let hasIndeterminate = false
  const len = parent.areas ? parent.areas.length : 0
  if (!len) return
  parent.areas.forEach(item => {
    item.checked && tCount++
    item.indeterminate && (hasIndeterminate = true)
  })
  // 是否选择状态
  parent.checked = tCount === len
  // 是否为不确定的状态
  parent.indeterminate = !(tCount === len || tCount === 0) || hasIndeterminate
}
const handleCheckChange = (list, status) => {
  const { checked, show, indeterminate } = status
  list?.forEach(item => {
    show !== undefined && (item.show = show)
    indeterminate !== undefined && (item.indeterminate = indeterminate)
    if (checked !== undefined && !item.disabled) {
      item.checked = checked
    }
    if (item.areas && item.areas.length > 0) {
      handleCheckChange(item.areas, { checked, show, indeterminate })
    }
  })
}
// 获取被选中的区列表
const getAreaCheckedList = () => {
  const resultList = []
  menuList.value.forEach(region => {
    region.areas?.forEach(province => {
      if (!province.areas && province.checked) {
        return resultList.push(province)
      }
      province.areas?.forEach(city => {
        if (!city.areas && city.checked) {
          return resultList.push(city)
        }
        city.areas?.forEach(area => {
          if (area.checked) {
            resultList.push(area)
          }
        })
      })
    })
  })
  checkedAreaNum.value = resultList.length
  return resultList
}
// 处理省点击
const handleProvinceClick = (province) => {
  if (!province.areas) return
  const flag = province.show
  handleCheckChange(menuList.value, { show: false })
  province.show = !flag
}
const handleCityClick = (cityList, curCity, cityIndex, e) => {
  if (!curCity.areas) return
  handleAreaBoxShow(e, cityIndex)
  const flag = curCity.show
  cityList.forEach(item => {
    item.show = false
  })
  curCity.show = !flag
}
// 处理区地址盒子显示
const areaStyle = reactive({ // 区盒定位样式
  top: '20px',
  left: '20px'
})
const areaMarkStyle = reactive({ // 角标样式
  top: '20px',
  left: '20px'
})
const cityRef = ref(null)
const areaBoxRef = ref(null)
/**
 * 处理区级盒子显示
 * @param {Node} e 当前激活节点信息
 * @param {Number} index 市列表索引
 */
const handleAreaBoxShow = (e, index) => {
  // 当前点击的节点
  const nodeName = e.target.nodeName.toLowerCase()
  // 市单体所占宽度
  const offsetLeft = cityRef.value[index]?.offsetWidth
  let offsetVal = 0 // 用于计算点击箭头时区块显示的位置
  // 显示区列表的左偏移值
  if (nodeName === 'span') {
    areaStyle.left = e.clientX - e.offsetX + offsetLeft + 14 + 'px'
    areaMarkStyle.left = e.clientX - e.offsetX + offsetLeft + 9 + 'px'
  } else {
    areaStyle.left = e.clientX - e.offsetX + 28 + 'px'
    areaMarkStyle.left = e.clientX - e.offsetX + 23 + 'px'
    offsetVal = -8
  }
  nextTick(() => {
    // 加setTimeout防止有时获取到的高度为0
    setTimeout(() => {
      // 显示区列表盒子的高度
      const areaBoxH = areaBoxRef.value?.[index].offsetHeight
      // 显示区列表的上偏移值
      areaStyle.top = e.clientY - e.offsetY - areaBoxH / 2 + 18 + offsetVal + 'px'
      // 角标
      areaMarkStyle.top = e.clientY - e.offsetY + 12 + offsetVal + 'px'
    })
  })
}
const disabledNodes = (cityList, banArr) => {
  let newBanArr = []
  newBanArr = newBanArr.concat(banArr)
  if (newBanArr.length > 0) {
    for (let i = 0; i < newBanArr.length; i++) {
      if (cityList.length > 0) {
        cityList.forEach(item => {
          if (newBanArr[i] === item.areaId) {
            newBanArr.splice(i, 1)
          }
        })
      }
    }
  }

  for (let i = 0; i < menuList.value.length; i++) {
    let disabledNumOne = 0
    // 状态
    menuList.value[i].disabled = false
    menuList.value[i].checked = false
    menuList.value[i].indeterminate = false

    // 获取所有的省
    const childrenOne = menuList.value[i].areas
    for (let j = 0; j < childrenOne.length; j++) {
      // 省下区的个数
      let provinceAreaCount = 0
      // debugger
      let disabledNumTwo = 0
      // 状态
      menuList.value[i].areas[j].disabled = false
      menuList.value[i].areas[j].show = false
      menuList.value[i].areas[j].checked = false
      menuList.value[i].areas[j].indeterminate = false
      // 回显勾选 国家(国家下无省)
      const result = cityList.find(cityI => childrenOne[j].areaId === cityI.areaId)
      menuList.value[i].areas[j].checked = !!result
      // 回显勾选 end

      // 获取当前省下的所有的市
      const childrenTwo = menuList.value[i].areas[j].areas || []
      for (let k = 0; k < childrenTwo.length; k++) {
        // 市下区的个数
        let cityAreaCount = 0
        // 状态
        menuList.value[i].areas[j].areas[k].disabled = false
        menuList.value[i].areas[j].areas[k].show = false
        menuList.value[i].areas[j].areas[k].checked = false
        menuList.value[i].areas[j].areas[k].indeterminate = false
        // 回显勾选 省(省下无城市)
        const result = cityList.find(cityI => childrenTwo[k].areaId === cityI.areaId)
        menuList.value[i].areas[j].areas[k].checked = !!result
        // 回显勾选 end

        if (menuList.value[i].areas[j].areas[k].areas && menuList.value[i].areas[j].areas[k].areas.length > 0) {
          let disableChildCity = 0

          // 获取当前市下的所有的区
          const childArea = menuList.value[i].areas[j].areas[k].areas
          for (let l = 0; l < childArea.length; l++) {
            provinceAreaCount++
            cityAreaCount++
            // 状态
            menuList.value[i].areas[j].areas[k].areas[l].disabled = false
            const result = cityList.find(cityI => childArea[l].areaId === cityI.areaId)
            // eslint-disable-next-line max-depth
            menuList.value[i].areas[j].areas[k].areas[l].checked = !!result
            const banCity = newBanArr.findIndex((item) => item === childArea[l].areaId) > -1
            // eslint-disable-next-line max-depth
            if (banCity) {
              menuList.value[i].areas[j].areas[k].areas[l].disabled = true
              disableChildCity++
            }
          }
          if (disableChildCity === menuList.value[i].areas[j].areas[k].areas.length) {
            menuList.value[i].areas[j].areas[k].disabled = true
            disabledNumTwo++
          }
        }

        // 检测并标记国家下的城市的禁用状态
        const childCity = menuList.value[i].areas[j].areas[k]
        const banCity = newBanArr.findIndex((item) => item === childCity.areaId) > -1
        if (banCity) {
          menuList.value[i].areas[j].areas[k].disabled = true
          disabledNumTwo++
        }
        // end 检测并标记国家下的城市的禁用状态

        // 检测市下的区是否全选
        indeterJudge(menuList.value[i].areas[j].areas[k])
        // 区个数
        menuList.value[i].areas[j].areas[k].areaCount = cityAreaCount
      }

      // 检测并标记大洲下的国家的禁用状态
      const childState = menuList.value[i].areas[j]
      const banState = newBanArr.findIndex((item) => item === childState.areaId) > -1
      if (banState) {
        menuList.value[i].areas[j].disabled = true
        disabledNumOne++
      }
      // end 检测并标记大洲下的国家的禁用状态

      if (disabledNumTwo === menuList.value[i].areas[j].areas?.length) {
        menuList.value[i].areas[j].disabled = true
        disabledNumOne++
      }
      // 检测省下的市是否全选
      indeterJudge(menuList.value[i].areas[j])
      // 区个数
      menuList.value[i].areas[j].areaCount = provinceAreaCount
    }
    if (disabledNumOne === menuList.value[i].areas.length) {
      menuList.value[i].disabled = true
    }
    // 检测区域下的省是否全选
    indeterJudge(menuList.value[i])
  }
  // 判断是否全选
  checkedAllJudge()
  loading.value = false
}
// 表单提交
const onSubmit = () => {
  if (loading.value) return
  const checdNodes = getAreaCheckedList()
  emit('refreshDataList', rowIndex, checdNodes, type)
  visible.value = false
  handleCheckChange(menuList.value, { checked: false, show: false })
  // 重置全选按钮状态
  checkedAllState.value = false
  // 全选按钮不确定状态
  checkedAllIndeterminate.value = false
}

defineExpose({
  init
})

</script>

<style lang="scss" scoped>
.content-box{
  height: 500px;
  .check-label{
    padding-left: 8px;
    padding-right: 6px;
    cursor: pointer;
  }
  .addr-region{
    display: flex;
    line-height: 32px;
    margin-bottom: 20px;
    .region-name{
      width: 120px;
    }
    .province-box{
      flex: 1;
      display: flex;
      flex-wrap: wrap;
      .province-item{
        width: 33%;
        position: relative;
        .area-count{
          color: #155bd4;
          padding-right: 6px;
        }
        .bor-style{
          border: 1px solid #eee;
          background-color: #fff;
          border-radius: 2px;
          box-shadow: 0 0 3px #ccc;
        }
        .city-box{
          min-width: 70%;
          max-height: calc(32px * 10 + 6px);
          overflow-y: auto;
          overflow-x: hidden;
          padding:3px 12px;
          position: absolute;
          left: -13px;
          z-index: 9;
          .city-item{
            position: relative;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            .area-box{
              max-height: calc(32px * 8 + 8px);
              overflow-y: auto;
              overflow-x: hidden;
              padding: 3px 20px;
              line-height: 32px;
              position: fixed;
              background-color: #fff;
              z-index: 999;
            }
            .area-mark{
              background-color: #fff;
              box-shadow: 1px 1px 3px 0 #ccc;
              display: block;
              width: 10px;
              height: 10px;
              position: fixed;
              transform: rotate(135deg);
            }
          }
        }
      }
    }
  }
}
.dialog-footer{
  display: flex;
  justify-content: space-between;
  align-items: center;
  .area-num{
    margin-right: 12px;
  }
}
.shop-transcity-add-or-update :deep(.el-checkbox__input.is-indeterminate .el-checkbox__inner){
  background-color:#fff;
}
.shop-transcity-add-or-update :deep(.el-checkbox__input.is-indeterminate .el-checkbox__inner::before){
  background-color:#155bd4;
}
.shop-transcity-add-or-update {
  :deep(.el-scrollbar__wrap) {
    overflow-x: hidden;
  }
}
</style>
