<template>
  <a-row class="login">
    <a-col :span="8" :offset="8" class="login-main">
      <h1 style="text-align: center"><rocket-two-tone />&nbsp;甲蛙12306售票系统</h1>
      <a-form
          :model="loginForm"
          name="basic"
          autocomplete="off"
      >
        <a-form-item
            label=""
            name="mobile"
            :rules="[{ required: true, message: '请输入手机号!' }]"
        >
          <a-input v-model:value="loginForm.mobile" placeholder="手机号"/>
        </a-form-item>

        <a-form-item
            label=""
            name="code"
            :rules="[{ required: true, message: '请输入验证码!' }]"
        >
          <a-input v-model:value="loginForm.code">
            <template #addonAfter>
              <a @click="sendCode">获取验证码</a>
            </template>
          </a-input>
          <!--<a-input v-model:value="loginForm.code" placeholder="验证码"/>-->
        </a-form-item>

        <a-form-item>
          <a-button type="primary" block @click="login">登录</a-button>
        </a-form-item>

      </a-form>
    </a-col>
  </a-row>
</template>

<script>
import { defineComponent, reactive } from 'vue';
// import { useRouter } from 'vue-router'
import axios from "axios";
import {notification} from "ant-design-vue";
import {useRoute,useRouter} from "vue-router";
import store from "@/store";
// 花括号（{}）用于指定您要从"vue-router"模块中导入的具名导出项
export default defineComponent({
// 这里的方法给html调用
//  给html绑定
  setup() {
    const router = useRouter()

    const loginForm = reactive({
      mobile: '13000000000',
      code: '',
    });

// values 是一个参数，它表示表单提交成功后的回调函数 onFinish 的参数。values 参数是一个对象，包含了表单中所有字段的值。
// 当用户在表单中填写完数据并点击提交按钮时，Vue.js 会自动收集表单中的数据，并将其作为参数传递给 onFinish 回调函数。
// 这样，你就可以在 onFinish 函数中通过 values 参数来访问表单中各个字段的值。
    const sendCode = () => {
      axios.post("/member/member/sendCode", {
        mobile: loginForm.mobile
      }).then(response => {
        let data = response.data;
        if (data.success) {
          notification.success({ description: '发送验证码成功！' });
          loginForm.code = "8888";
        } else {
          notification.error({ description: data.message });
        }
      });
    };

    const login = () => {
      axios.post("/member/member/login", loginForm).then((response) => {
        let data = response.data;
        if (data.success) {
          notification.success({ description: '登录成功！' });
          // 登录成功，跳到控台主页
          router.push("/");
          // 通过vuex 存储这个对象，这个函数第一个参数指定要提交的函数，第二个参数指定传递的值
          console.log(data.content)

          store.commit("setMember", data.content);
        } else {
          notification.error({ description: data.message });
        }
      })
    };

    return {
      loginForm,
      sendCode,
      login
    };
  },
});
</script>

<style>
.login-main h1 {
  font-size: 25px;
  font-weight: bold;
}
.login-main {
  margin-top: 100px;
  padding: 30px 30px 20px;
  border: 2px solid grey;
  border-radius: 10px;
  background-color: #fcfcfc;
}
</style>
