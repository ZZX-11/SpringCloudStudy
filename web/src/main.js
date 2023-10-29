import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/reset.css';
import * as Icons from "@ant-design/icons-vue";

// createApp(App).use(Antd).use(store).use(router).mount('#app')
const app = createApp(App);
app.use(Antd).use(store).use(router).mount('#app')

// 导入图标
// app.component(i, icons[i]) 是一个 Vue 3 的方法调用，用于注册全局组件。
// 在这段代码中，i 是一个变量，代表图标的名称，icons[i] 则是对应图标的组件。
// 通过循环遍历 icons 对象的属性，将每个图标组件注册为全局组件。
// 这样，在应用程序中的任何地方都可以直接使用这些图标组件，而无需在每个组件中单独导入和注册。
const icons = Icons;
for(const i in icons){
    app.component(i,icons[i]);
}