import {ConfigEnv, defineConfig, loadEnv, UserConfigExport} from 'vite';
import vue from '@vitejs/plugin-vue';
import {resolve} from 'path';

// https://vitejs.dev/config/
export default ({mode, command}: ConfigEnv): UserConfigExport => {
  //获取当前项目路径
  const root = process.cwd();
  //  获取当前环境变量
  const env = loadEnv(mode, root);
  console.log('env:', env);
  const isBuild = command === 'build';
  console.log('isBuild:', isBuild);

// https://vitejs.dev/config/
  return defineConfig({
    // plugins是一个配置要使用插件的数组，vite插件是一个函数，所以使用时直接调用就行，不要用new 调用 。数组中也可以使用对象来添加一些属性，实现特定效果
    plugins: [
      vue()
      // createHtmlPlugin({
      //   inject: {
      //     data: {
      //       // 通过插件设置标题
      //       title: loadEnv(mode, root).VITE_APP_TITLE
      //     }
      //   }
      // })
    ],
    // ------------------------------------------------------------------------
    // 这里更改打包相对绝对路径, 使得资源可以在 Nginx 中被正确访问
    base: loadEnv(mode, root).VITE_APP_BASE_PATH,
    // 静态资源服务的文件夹。默认值为public
    // publicDir: './',
    // 模块解析时规则对象，可以在解析的时候替换指定内容，含有alias(别名)、extensions（扩展名）等属性。
    resolve: {
      alias: {
        '@': resolve(__dirname, '.', 'src')
      },
      extensions: ['.mjs', '.js', '.ts', '.jsx', '.tsx', '.json', '.vue']
    },
    //  开发服务器
    server: {
      // 服务端口
      port: Number(env.VITE_APP_BASE_PORT),
      // 启动成功后打开页面
      open: false,
      // 为开发服务器配置自定义代理规则。
      proxy:
        {
          // '/api':
          [`${loadEnv(mode, root).VITE_APP_BASE_API}`]:
            {
              // 这里代理到网关的端口
              target: loadEnv(mode, root).VITE_APP_BASE_HOST,
              changeOrigin: true,
              // 当请求地址中以"/api"开头的地址替换成空字符串
              rewrite: (h) => h.replace(/^\/api/, '')
            }
        }
    },
    // 添加一个 scss 的配置，告诉 vite 不要使用 legacy api
    css: {
      preprocessorOptions: {
        scss: {
          // or "modern", "legacy"
          api: 'modern-compiler'
        }
      }
    }
  });
}