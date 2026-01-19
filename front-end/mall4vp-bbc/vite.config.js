import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons'
import viteCompression from 'vite-plugin-compression'
import DefineOptions from 'unplugin-vue-define-options/vite'
import legacy from '@vitejs/plugin-legacy'
// eslint
import eslintPlugin from 'vite-plugin-eslint'
import esbuild from 'rollup-plugin-esbuild'

// https://vitejs.dev/config/
export default defineConfig(({ command }) => {
  return {
    plugins: [
      vue(),
      DefineOptions(),
      createSvgIconsPlugin({
        iconDirs: [path.resolve(process.cwd(), 'src/icons/svg')],
        symbolId: 'icon-[dir]-[name]'
      }),
      // 自动引入内容
      AutoImport({
        imports: [
          'vue',
          'vue-router'
        ],
        dirs: [
          'src/hooks/**',
          'src/stores/**',
          'src/utils/**'
        ],
        resolvers: command === 'build' ? [ElementPlusResolver()] : [],
        dts: 'src/auto-import/imports.d.ts',
        eslintrc: {
          enabled: true
        }
      }),
      // 自动引入组件
      Components({
        dirs: [
          'src/components'
        ],
        resolvers: command === 'build' ? [ElementPlusResolver()] : [],
        dts: 'src/auto-import/components.d.ts'
      }),
      // 对大于 1k 的文件进行压缩
      viteCompression({
        threshold: 10240
      })
    ].concat(getDynamicPlugins(command))
      .concat(
        // eslint
        command !== 'build' ? [eslintPlugin({ include: ['src/**/*.js', 'src/**/*.vue', 'src/*.js', 'src/*.vue'] })] : []
      ),
    server: {
      host: true,
      port: 9527,
      open: true
    },
    resolve: {
      alias: {
        '@': path.resolve(__dirname, 'src'),
        'vue-i18n': 'vue-i18n/dist/vue-i18n.cjs.js'
      }
    },
    build: {
      base: './',
      rollupOptions: {
        // 静态资源分类打包
        output: {
          chunkFileNames: 'static/js/[name]-[hash].js',
          entryFileNames: 'static/js/[name]-[hash].js',
          assetFileNames: 'static/[ext]/[name]-[hash].[ext]',
          // 静态资源分拆打包
          manualChunks (id) {
            if (id.includes('node_modules')) {
              if (id.toString().indexOf('.pnpm/') !== -1) {
                return id.toString().split('.pnpm/')[1].split('/')[0].toString()
              } else if (id.toString().indexOf('node_modules/') !== -1) {
                return id.toString().split('node_modules/')[1].split('/')[0].toString()
              }
            }
          }
        }
      },
      sourcemap: false,
      minify: 'terser',
      reportCompressedSize: false
    }
  }
})

/**
 * 根据运行环境加载动态插件
 * @param {String} command  serve: 开发环境 || 预览环境；build： 构建环境
 */
function getDynamicPlugins (command) {
  const dynamicPlugins = [
    command === 'serve' ?
      // esbuild 在开发环境兼容低版本浏览器
      {
        ...esbuild({
          target: 'chrome64',
          include: /\.vue|.ts|.js$/,
          loaders: {
            '.vue': 'js'
          }
        }),
        enforce: 'post'
      } :
      // 在构建时使用 legacy 处理兼容
      legacy({
        // 兼容浏览器列表
        targets: ['chrome >= 51', 'firefox >= 54', 'safari >= 12', 'Android >= 7']
      })
  ]
  return dynamicPlugins
}
