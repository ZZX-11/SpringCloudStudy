<template>
  <a-layout-header class="header">

    <div class="logo">
      <router-link to="/welcome" style="color: white; font-size: 18px">
        zzx控台
      </router-link>
    </div>

    <div style="float: right; color: white;">
     欢迎使用管理控制台
    </div>
    <a-menu
        v-model:selectedKeys="selectedKeys"
        theme="dark"
        mode="horizontal"
        :style="{ lineHeight: '64px' }"
    >
      <a-menu-item key="/welcome">
        <router-link to="/welcome">
          <coffee-outlined /> &nbsp; 欢迎
        </router-link>
      </a-menu-item>

      <a-menu-item key="/about">
        <router-link to="/about">
          <user-outlined /> &nbsp; 关于
        </router-link>
      </a-menu-item>

<!--      <a-menu-item key="/ticket">-->
<!--        <router-link to="/ticket">-->
<!--          <user-outlined /> &nbsp; 余票查询-->
<!--        </router-link>-->
<!--      </a-menu-item>-->
    </a-menu>

  </a-layout-header>
</template>

<script>
import {defineComponent, ref, watch} from 'vue';
import store from "@/store";
import router from "@/router";

export default defineComponent({
  name: "the-header-view",
  //给组件命名.该组件可以在别的地方通过<the-header-view></the-header-view> 这样的方式使用
  setup() {
    const selectedKeys = ref([]);
    //  header部分监控；路由路径的改变，当发生改变时更改所选中的值。
    watch(() => router.currentRoute.value.path, (newValue) => {
      console.log('watch', newValue);
      selectedKeys.value = [];
      selectedKeys.value.push(newValue);
    }, {immediate: true});
    // 这段代码是使用Vue 3中的watch函数来监听router.currentRoute.value.path的变化，并在变化时执行回调函数。
    // 代码中的router.currentRoute.value.path表示当前路由的路径。当路径发生变化时，回调函数被调用。
    // 另外，通过在watch函数的选项参数中设置immediate: true，可以使回调函数在初始化时立即执行一次，以处理初始状态的路径值。
    return {
      selectedKeys,
    };
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}

.logo {
  float: left;
  height: 31px;
  width: 150px;
  color: white;
  font-size: 20px;
}
</style>
