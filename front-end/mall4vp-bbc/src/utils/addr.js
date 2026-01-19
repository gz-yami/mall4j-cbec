/**
 * 通过地址id获取省市区|国省市的地址名
 * @param {*} s
 */
export function areaNameList (areaData, cityList) {
  // 插入数据
  HandlerAreaList(areaData, cityList)

  // 获取区域名
  let addrNameList = []
  // 省名
  let cityNameList = []
  // 城市名
  let areaNameList = []
  areaData.forEach(province => {
    const cityList = []
    let cityAll = true
    // 省
    province.areas.forEach(city => {
      const areaList = []
      let areaAll = true
      // 城市
      city.areas.forEach(area => {
        if (area.check) {
          areaList.push(area.areaNameEn)
        } else if (!areaAll) { /* empty */ } else {
          areaAll = false
          cityAll = false
        }
      })
      // 城市 END
      if (areaAll && city.areas.length > 0) {
        // 该市下的区已全部勾选，则添加城市名
        cityList.push(city.areaNameEn)
        city.check = true
      } else if (areaList.length > 0) {
        // 仅勾选部分区，把勾选的区添加到地区名名的数组中
        areaNameList = areaNameList.concat(areaList)
      }
      if (!city.check) {
        cityAll = false
      } else {
        // 没有城市数据的省
        if (city.areas.length === 0) {
          cityList.push(city.areaNameEn)
        }
      }
    })
    // 省 END
    if (cityAll && province.areas.length > 0) {
      // 所有的城市都已勾选，则添加省名
      addrNameList.push(province.areaNameEn)
      province.check = true
    } else if (cityList.length > 0) {
      // 只勾选部分城市，把勾选的城市添加到城市名的数组中
      cityNameList = cityNameList.concat(cityList)
    }
    // 没有省数据的国家
    if (province.check && province.areas.length === 0) {
      addrNameList.push(province.areaNameEn)
    }
  })

  // 拼接省->市->区 || 国家-省-市
  if (cityNameList.length > 0) {
    addrNameList = addrNameList.concat(cityNameList)
  }
  if (areaNameList.length > 0) {
    addrNameList = addrNameList.concat(areaNameList)
  }
  return addrNameList
}

/**
 * 地址数组中的勾选项增加(check:true)标记
 * @param areaData 作为参照的地址数组
 * @param cityList 已勾选的项
 * @constructor
 */

function HandlerAreaList (areaData, cityList) {
  cityList.forEach(element => {
    const city = element
    // 国家
    for (let i = 0; i < areaData.length; i++) {
      if (areaData[i].level === null && areaData[i].areaId === element.areaId) {
        // 标记已选国家(原数据层数只到 国家)
        areaData[i].check = true
        return
      } else {
        // 省
        if (element.level === 2) {
          const cityIndex = areaData[i].areaIds?.indexOf(city.areaId)
          if (cityIndex !== -1) {
            // 标记已选省(原数据层数只到 省)
            areaData[i].areas[cityIndex].check = true
            return
          }
        } else {
          const cityIndex = areaData[i].areaIds.indexOf(city.parentId)
          if (cityIndex > -1) {
            // 城市
            const areaIndex = areaData[i].areas[cityIndex].areaIds.indexOf(city.areaId)
            if (areaIndex > -1) {
              areaData[i].areas[cityIndex].areas[areaIndex].check = true
            }
          }
        }
      }
    }
  })
}
