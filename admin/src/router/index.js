import { createRouter, createWebHistory } from 'vue-router'
import store from "@/store";
import {notification} from "ant-design-vue";

const routes = [
// {
//     path: '/login',
//     name: 'login',
//     component: () => import('../views/LoginView.vue')
//   },
    {
    path: '/',
    name: 'main',
    component: () => import('../views/main.vue'),
    //  在Vue的路由中，meta是一个用于存储额外信息的对象。它可以在路由配置中的meta字段中定义，并且可以在路由导航过程中访问。
    //  meta对象可以包含任意的自定义属性，用于描述路由的特性或其他相关信息。常见的用法是在路由中定义权限信息，以便在导航守卫中进行权限验证。
    // meta: {
    //     loginRequire: true
    // },
    children: [{
//  二级路由或者说是子路由。拼接上一个路由的路径。比如上一级是/main，这一级在它后面拼接/main/welcome
        path: 'welcome',
        component: () => import('../views/main/welcome.vue'),
    },{
        path: 'about',
        component: () => import('../views/main/about.vue'),
    },{
        path: 'station',
        component: () => import('../views/main/business/station.vue'),
    },{
        path: 'train',
        component: () => import('../views/main/business/train.vue'),
    },{
        path: 'train-station',
        component: () => import('../views/main/business/train-station.vue'),
    },{
        path: 'train-carriage',
        component: () => import('../views/main/business/train-carriage.vue'),
    },{
        path: 'train-seat',
        component: () => import('../views/main/business/train-seat.vue'),
    }]
   },
    {
//  访问定向 ： 默认打开welcome页面
        path: '',
        redirect: '/welcome'
    }
]
// 配置了基本路由信息
const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
