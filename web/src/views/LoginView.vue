<template>
  <a-form
      :model="formState"
      name="basic"
      :label-col="{ span: 8 }"
      :wrapper-col="{ span: 16 }"
      autocomplete="off"
      @finish="onFinish"
      @finishFailed="onFinishFailed"
  >
    <a-form-item
        label="Username"
        name="username"
        :rules="[{ required: true, message: 'Please input your username!' }]"
    >
      <a-input v-model:value="formState.username" />
    </a-form-item>

    <a-form-item
        label="Password"
        name="password"
        :rules="[{ required: true, message: 'Please input your password!' }]"
    >
      <a-input-password v-model:value="formState.password" />
    </a-form-item>
  <!--v-model 创造一个响应式的连接-->
    <a-form-item name="remember" :wrapper-col="{ offset: 8, span: 16 }">
      <a-checkbox v-model:checked="formState.remember">Remember me</a-checkbox>
    </a-form-item>

    <a-form-item :wrapper-col="{ offset: 8, span: 16 }">
      <a-button type="primary" html-type="submit">Submit</a-button>
    </a-form-item>
  </a-form>
</template>

<script>

import { reactive,defineComponent } from 'vue';
export default defineComponent({
  setup(){
// 这里的方法给html调用
//  给html绑定
    const formState = reactive({
    username: '',
    password: '',
    remember: true,
    });

// values 是一个参数，它表示表单提交成功后的回调函数 onFinish 的参数。values 参数是一个对象，包含了表单中所有字段的值。
// 当用户在表单中填写完数据并点击提交按钮时，Vue.js 会自动收集表单中的数据，并将其作为参数传递给 onFinish 回调函数。
// 这样，你就可以在 onFinish 函数中通过 values 参数来访问表单中各个字段的值。
    const onFinish = values => {
      console.log('Success:', values);
    };

    const onFinishFailed = errorInfo => {
      console.log('Failed:', errorInfo);
    };
    return {
      formState,
      onFinish,
      onFinishFailed
    }
  }
})

</script>