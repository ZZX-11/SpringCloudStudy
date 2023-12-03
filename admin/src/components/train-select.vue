<template>
  <a-select v-model:value="trainCode" show-search allowClear
            :filterOption="filterTrainCodeOption"
            @change="onChange" placeholder="请选择车次"
            :style="'width: ' + localWidth">
    <a-select-option v-for="item in trains" :key="item.code" :value="item.code" :label="item.code + item.start + item.end">
      {{item.code}} {{item.start}} ~ {{item.end}}
    </a-select-option>
  </a-select>
  <!--总而言之，这段代码通过循环遍历trains数组，为每个数组项生成一个a-select-option选项。选项的值是item.code，显示标签是item.code + item.start + item.end。-->
  <!--value与label均是自定义值，用来传给filterTrainCodeOption做过滤，目的是不显示一些值-->
</template>

<script>

import {defineComponent, onMounted, ref, watch} from 'vue';
import axios from "axios";
import {notification} from "ant-design-vue";

export default defineComponent({
  name: "train-select-view",
  props: ["modelValue", "width"],
  // 定义事件 其中change是自定义事件
  emits: ['update:modelValue', 'change'],
  setup(props, {emit}) {
    const trainCode = ref();
    // 车次下拉框
    const trains = ref([]);
    const localWidth = ref(props.width);
    if (Tool.isEmpty(props.width)) {
      localWidth.value = "100%";
    }

    // 利用watch，动态获取父组件的值，当传入的父组件值发生改变就立马更新
    watch(() => props.modelValue, ()=>{
      console.log("props.modelValue", props.modelValue);
      trainCode.value = props.modelValue;
    }, {immediate: true});

    /**
     * 查询所有的车次，用于车次下拉框
     */
    const queryAllTrain = () => {
      axios.get("/business/admin/train/query-all").then((response) => {
        let data = response.data;
        if (data.success) {
          trains.value = data.content;
        } else {
          notification.error({description: data.message});
        }
      });
    };

    /**
     * 车次下拉框筛选
     */
    const filterTrainCodeOption = (input, option) => {
      console.log(input, option);
      return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    };

    /**
     * 将当前组件的值响应给父组件
     * @param value
     */
    const onChange = (value) => {
      // 这是一个固定写法 ”update:modelValue“ ，让父组件拿到子组件的值。 这个函数将自组件拿到的值传给父组件 传给 v-model
      // 当我们在自定义组件中使用v-model时，需要同时提供一个value属性和一个名为'update:modelValue'的自定义事件。
      // 这样，v-model指令就会将数据属性与value属性进行双向绑定，并在用户输入或选择时触发'update:modelValue'事件，将新的值传递给父组件。
      emit('update:modelValue', value);
      let train = trains.value.filter(item => item.code === value)[0];
      if (Tool.isEmpty(train)) {
        train = {};
      }
      // 为功能扩展做准备
      // 为父组件的onChange函数服务，将train可以返回给父组件 onChange 定义的函数的地方，传入该函数
      emit('change', train);
    };

    onMounted(() => {
      queryAllTrain();
    });

    return {
      trainCode,
      trains,
      filterTrainCodeOption,
      onChange,
      localWidth
    };
  },
});
</script>
