<template>
  <view class="Mall4j component-compress">
    <canvas
      :style="{ width: canvasSize.width, height: canvasSize.height }"
      canvas-id="myCanvas"
    />
  </view>
</template>

<script setup>
const canvasSize = ref({
  width: 0,
  height: 0
})

// 获取图片信息
const getImageInfo = (src) => {
  return new Promise((resolve, reject) => {
    uni.getImageInfo({
      src,
      success: (info) => {
        resolve(info)
      },
      fail: () => {
        reject(new Error(null))
      }
    })
  })
}
// 批量压缩
const batchCompress = (params) => {
  // index:进度，done:成功，fail:失败
  let [index, done, fail] = [0, 0, 0]
  // 压缩完成的路径集合
  const paths = []
  // 批量压缩方法
  const batch = () => {
    return new Promise((resolve) => {
      // 开始
      const start = async () => {
        params.progress &&
        params.progress({
          done,
          fail,
          count: params.batchSrc.length
        })
        // 等待图片压缩方法返回
        const path = await next()
        if (path) {
          done++
          paths.push(path)
        } else {
          fail++
        }

        index++
        // 压缩完成
        if (index >= params.batchSrc.length) {
          resolve(true)
        } else {
          start()
        }
      }
      start()
    })
  }
  // 依次调用压缩方法
  const next = () => {
    return compress({
      src: params.batchSrc[index],
      maxSize: params.maxSize,
      fileType: params.fileType,
      quality: params.quality,
      minSize: params.minSize
    })
  }

  // 全部压缩完成后调用
  // eslint-disable-next-line no-async-promise-executor
  return new Promise(async (resolve, reject) => {
    // 批量压缩方法回调
    const res = await batch()
    if (res) {
      resolve(paths)
    } else {
      reject(new Error(null))
    }
  })
}
// 压缩
const compress = async (params, tempFile, maxSizeMB) => {
  // eslint-disable-next-line no-async-promise-executor
  return new Promise(async (resolve, reject) => {
    maxSizeMB = maxSizeMB || 2
    if (tempFile.size < 1024 * 1024 * maxSizeMB) {
      resolve(params.src)
      return
    }
    // 等待图片信息
    const info = await getImageInfo(params.src)
      .then((info) => info)
      .catch((err) => err)

    if (!info) return reject(new Error($t('GetImageInformationException')))

    // 设置最大 & 最小 尺寸
    const maxSize = params.maxSize || 1080
    const minSize = params.minSize || 640

    // 当前图片尺寸
    let { width, height } = info

    // 非 H5 平台进行最小尺寸校验
    // #ifndef H5
    if (width <= minSize && height <= minSize) {
      resolve(params.src)
      return
    }
    // #endif

    // 最大尺寸计算
    if (width > maxSize || height > maxSize) {
      if (width > height) {
        height = Math.floor(height / (width / maxSize))
        width = maxSize
      } else {
        width = Math.floor(width / (height / maxSize))
        height = maxSize
      }
    }

    // 设置画布尺寸
    canvasSize.value = {
      width: `${width}rpx`,
      height: `${height}rpx`
    }

    // Vue.nextTick 回调在 App 有异常，则使用 setTimeout 等待DOM更新
    setTimeout(() => {
      const ctx = uni.createCanvasContext('myCanvas', this)
      ctx.clearRect(0, 0, width, height)
      ctx.drawImage(info.path, 0, 0, uni.upx2px(width), uni.upx2px(height))
      ctx.draw(false, () => {
        uni.canvasToTempFilePath(
          {
            x: 0,
            y: 0,
            width: uni.upx2px(width),
            height: uni.upx2px(height),
            destWidth: width,
            destHeight: height,
            canvasId: 'myCanvas',
            fileType: params.fileType || 'png',
            quality: params.quality || 0.9,
            success: (res) => {
              // 在H5平台下，tempFilePath 为 base64
              // 压缩到2m以下为止
              uni.getFileInfo({
                filePath: res.tempFilePath,
                success: (res2) => {
                  params.src = res.tempFilePath
                  resolve(compress(params, res2))
                }
              })
            },
            fail: () => {
              reject(new Error(null))
            }
          },
          this
        )
      })
    }, 300)
  })
}
defineExpose({ batchCompress, compress })
</script>

<style scoped lang="scss">
.component-compress {
  position: fixed;
  overflow: hidden;
  top: -99999px;
  left: 0;
}
</style>
