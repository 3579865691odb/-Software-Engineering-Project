<template>
  <div class="product-container">
    <!-- 顶部统计卡片 -->
    <div class="statistics-cards">
      <a-row :gutter="{ xs: 8, sm: 16 }">
        <a-col :xs="24" :sm="12" :md="12" :lg="6">
          <div class="stat-card card-primary">
            <div class="stat-icon">
              <shopping-outlined />
            </div>
            <div class="stat-content">
              <div class="stat-title">商品总数</div>
              <div class="stat-value">{{ totalProducts }}</div>
              <div class="stat-desc">所有商品数量</div>
            </div>
          </div>
        </a-col>
        <a-col :xs="24" :sm="12" :md="12" :lg="6">
          <div class="stat-card card-success">
            <div class="stat-icon">
              <check-circle-outlined />
            </div>
            <div class="stat-content">
              <div class="stat-title">已上架</div>
              <div class="stat-value">{{ getProductStatusCount('0') }}</div>
              <div class="stat-desc">可购买商品</div>
            </div>
          </div>
        </a-col>
        <a-col :xs="24" :sm="12" :md="12" :lg="6">
          <div class="stat-card card-warning">
            <div class="stat-icon">
              <stop-outlined />
            </div>
            <div class="stat-content">
              <div class="stat-title">已下架</div>
              <div class="stat-value">{{ getProductStatusCount('1') }}</div>
              <div class="stat-desc">不可购买商品</div>
            </div>
          </div>
        </a-col>
        <a-col :xs="24" :sm="12" :md="12" :lg="6">
          <div class="stat-card card-danger">
            <div class="stat-icon">
              <warning-outlined />
            </div>
            <div class="stat-content">
              <div class="stat-title">库存不足</div>
              <div class="stat-value">{{ getLowStockCount() }}</div>
              <div class="stat-desc">库存低于10件</div>
            </div>
          </div>
        </a-col>
      </a-row>
    </div>

    <!-- 搜索和筛选区域 -->
    <a-card :bordered="false" class="filter-card">
      <a-row :gutter="{ xs: 8, sm: 16 }" class="filter-row">
        <a-col :xs="24" :sm="12" :md="6" :lg="6" class="filter-col">
          <a-input
            v-model:value="data.keyword"
            placeholder="搜索商品名称"
            allowClear
            class="search-input"
            @change="onSearchChange"
            @pressEnter="onSearch"
          >
            <template #prefix>
              <search-outlined />
            </template>
          </a-input>
        </a-col>
        <a-col :xs="24" :sm="12" :md="6" :lg="4" class="filter-col">
          <a-select
            v-model:value="data.statusFilter"
            placeholder="商品状态"
            style="width: 100%"
            class="filter-select"
            allowClear
            @change="onSearch"
          >
            <a-select-option value="">全部状态</a-select-option>
            <a-select-option value="0">已上架</a-select-option>
            <a-select-option value="1">已下架</a-select-option>
          </a-select>
        </a-col>
        <a-col :xs="24" :sm="12" :md="6" :lg="4" class="filter-col">
          <a-select
            v-model:value="data.classFilter"
            placeholder="商品分类"
            style="width: 100%"
            class="filter-select"
            allowClear
            @change="onSearch"
          >
            <a-select-option value="">全部分类</a-select-option>
            <a-select-option v-for="item in modal.cData" :key="item.id" :value="item.id">{{ item.title }}</a-select-option>
          </a-select>
        </a-col>
        <a-col :xs="24" :sm="12" :md="6" :lg="10" class="filter-col">
          <div class="filter-btns">
            <a-button type="primary" @click="onSearch" class="btn-search">
              <search-outlined />搜索
            </a-button>
            <a-button @click="handleReset" class="btn-reset">
              <reload-outlined />重置
            </a-button>
            <a-button type="primary" @click="handleAdd" class="btn-add">
              <plus-outlined />新增商品
            </a-button>
            <a-button danger @click="handleBatchDelete" :disabled="data.selectedRowKeys.length === 0" class="btn-delete">
              <delete-outlined />批量删除
            </a-button>
          </div>
        </a-col>
      </a-row>
    </a-card>

    <!-- 表格区域 -->
    <a-card :bordered="false" class="table-card">
      <a-table
        size="middle"
        rowKey="id"
        :loading="data.loading"
        :columns="columns"
        :data-source="filteredProducts"
        :scroll="{ x: 1200 }"
        :row-selection="rowSelection"
        :pagination="{
          size: 'default',
          current: data.page,
          pageSize: data.pageSize,
          onChange: (current) => (data.page = current),
          showSizeChanger: true,
          pageSizeOptions: ['10', '20', '50', '100'],
          showTotal: (total) => `共${total}条数据`,
        }"
        :rowClassName="getRowClassName"
      >
        <template #bodyCell="{ text, record, index, column }">
          <!-- 商品名称列 -->
          <template v-if="column.key === 'title'">
            <div class="product-cell">
              <div class="product-image">
                <img v-if="record.cover" :src="getImageUrl(record.cover)" alt="商品图片" />
                <div v-else class="no-image">
                  <file-image-outlined />
                </div>
              </div>
              <div class="product-info">
                <div class="product-name">{{ text }}</div>
                <div class="product-category" v-if="getCategoryName(record.classificationId)">
                  <tag-outlined /> {{ getCategoryName(record.classificationId) }}
                </div>
              </div>
            </div>
          </template>

          <!-- 价格列 -->
          <template v-if="column.key === 'price'">
            <div class="price-tag">¥{{ text }}</div>
          </template>

          <!-- 状态列 -->
          <template v-if="column.key === 'status'">
            <a-tag :color="text === '0' ? 'success' : 'error'" class="status-tag">
              <template v-if="text === '0'">
                <check-circle-outlined /> 已上架
              </template>
              <template v-else>
                <stop-outlined /> 已下架
              </template>
            </a-tag>
          </template>

          <!-- 库存列 -->
          <template v-if="column.key === 'repertory'">
            <a-progress
              :percent="getStockPercent(text)"
              :status="getStockStatus(text)"
              :stroke-color="getStockColor(text)"
              size="small"
            />
            <div class="stock-count">
              <span :class="{ 'stock-warning': text < 10 }">{{ text }}</span> 件
            </div>
          </template>

          <!-- 操作列 -->
          <template v-if="column.key === 'operation'">
            <div class="action-btns">
              <a-button
                type="primary"
                size="small"
                @click="handleEdit(record)"
                class="btn-edit"
              >
                <edit-outlined />编辑
              </a-button>
              <a-button
                danger
                size="small"
                @click="showDeleteConfirm(record)"
                class="btn-delete"
              >
                <delete-outlined />删除
              </a-button>
            </div>
          </template>
        </template>
      </a-table>
    </a-card>

    <!--商品编辑弹窗-->
    <a-modal
      :visible="modal.visile"
      :forceRender="true"
      :title="modal.title"
      width="880px"
      ok-text="确认"
      cancel-text="取消"
      @cancel="handleCancel"
      @ok="handleOk"
      class="product-modal"
    >
      <div class="modal-content">
        <a-form ref="myform" :model="modal.form" :rules="modal.rules" layout="vertical">
          <a-tabs>
            <a-tab-pane key="1" tab="基本信息">
              <a-row :gutter="24">
                <a-col :span="24">
                  <a-form-item label="商品名称" name="title">
                    <a-input placeholder="请输入商品名称" v-model:value="modal.form.title" />
                  </a-form-item>
                </a-col>
                <a-col :span="12">
                  <a-form-item label="商品分类" name="classificationId">
                    <a-select
                      placeholder="请选择分类"
                      allowClear
                      :options="modal.cData"
                      :field-names="{ label: 'title', value: 'id' }"
                      v-model:value="modal.form.classificationId"
                    />
                  </a-form-item>
                </a-col>
                <a-col :span="12">
                  <a-form-item label="商品标签">
                    <a-select mode="multiple" placeholder="请选择标签" allowClear v-model:value="modal.form.tags">
                      <a-select-option v-for="item in modal.tagData" :key="item.id" :value="item.id">{{ item.title }}</a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>
                <a-col :span="12">
                  <a-form-item label="商品价格" name="price">
                    <a-input-number
                      placeholder="请输入价格"
                      :min="0"
                      :precision="2"
                      v-model:value="modal.form.price"
                      style="width: 100%"
                      addon-before="¥"
                    />
                  </a-form-item>
                </a-col>
                <a-col :span="12">
                  <a-form-item label="库存数量" name="repertory">
                    <a-input-number
                      placeholder="请输入库存"
                      :min="0"
                      v-model:value="modal.form.repertory"
                      style="width: 100%"
                      addon-after="件"
                    />
                  </a-form-item>
                </a-col>
                <a-col :span="12">
                  <a-form-item label="商品状态" name="status">
                    <a-radio-group v-model:value="modal.form.status" button-style="solid">
                      <a-radio-button value="0">
                        <check-circle-outlined /> 上架
                      </a-radio-button>
                      <a-radio-button value="1">
                        <stop-outlined /> 下架
                      </a-radio-button>
                    </a-radio-group>
                  </a-form-item>
                </a-col>
              </a-row>
            </a-tab-pane>
            <a-tab-pane key="2" tab="商品详情">
              <a-row :gutter="24">
                <a-col :span="12">
                  <a-form-item label="商品封面">
                    <div class="upload-container">
                      <a-upload-dragger
                        name="file"
                        accept="image/*"
                        :multiple="false"
                        :before-upload="beforeUpload"
                        v-model:file-list="fileList"
                        class="upload-area"
                      >
                        <div class="upload-content">
                          <template v-if="modal.form.coverUrl">
                            <img :src="modal.form.coverUrl" class="preview-image" />
                          </template>
                          <template v-else>
                            <p class="upload-icon">
                              <file-image-outlined />
                            </p>
                            <p class="upload-text">点击或拖拽图片上传商品封面</p>
                          </template>
                        </div>
                      </a-upload-dragger>
                    </div>
                  </a-form-item>
                </a-col>
                <a-col :span="12">
                  <a-form-item label="商品简介">
                    <a-textarea
                      placeholder="请输入商品简介"
                      v-model:value="modal.form.description"
                      :rows="6"
                      :maxlength="500"
                      show-count
                    />
                  </a-form-item>
                </a-col>
              </a-row>
            </a-tab-pane>
          </a-tabs>
        </a-form>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { FormInstance, message, Modal } from 'ant-design-vue';
import { createApi, listApi, updateApi, deleteApi } from '/@/api/thing';
import { listApi as listClassificationApi } from '/@/api/classification';
import { listApi as listTagApi } from '/@/api/tag';
import { BASE_URL } from "/@/store/constants";
import {
  FileImageOutlined,
  ShoppingOutlined,
  CheckCircleOutlined,
  StopOutlined,
  WarningOutlined,
  SearchOutlined,
  ReloadOutlined,
  PlusOutlined,
  DeleteOutlined,
  EditOutlined,
  TagOutlined
} from '@ant-design/icons-vue';
import { computed, onMounted, reactive, ref } from 'vue';

const columns = reactive([
  {
    title: '序号',
    dataIndex: 'index',
    key: 'index',
    width: 80,
    align: 'center',
  },
  {
    title: '商品信息',
    dataIndex: 'title',
    key: 'title',
    width: 300,
    ellipsis: true,
  },
  {
    title: '价格',
    dataIndex: 'price',
    key: 'price',
    width: 100,
    align: 'right',
    sorter: (a, b) => a.price - b.price,
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 120,
    align: 'center',
    filters: [
      { text: '已上架', value: '0' },
      { text: '已下架', value: '1' },
    ],
    onFilter: (value, record) => record.status === value,
  },
  {
    title: '库存',
    dataIndex: 'repertory',
    key: 'repertory',
    width: 150,
    align: 'center',
    sorter: (a, b) => a.repertory - b.repertory,
  },
  {
    title: '简介',
    dataIndex: 'description',
    key: 'description',
    width: 250,
    ellipsis: true,
    customRender: ({ text }) => text || '--',
  },
  {
    title: '操作',
    dataIndex: 'action',
    key: 'operation',
    align: 'center',
    fixed: 'right',
    width: 180,
  },
]);

const beforeUpload = (file: File) => {
  // 改文件名
  const fileName = new Date().getTime().toString() + '.' + file.type.substring(6);
  const copyFile = new File([file], fileName);
  console.log(copyFile);
  modal.form.imageFile = copyFile;

  // 创建预览URL
  const reader = new FileReader();
  reader.readAsDataURL(file);
  reader.onload = () => {
    modal.form.coverUrl = reader.result as string;
  };

  return false;
};

// 文件列表
const fileList = ref<any[]>([]);

// 页面数据
const data = reactive({
  dataList: [],
  loading: false,
  keyword: '',
  statusFilter: '',
  classFilter: '',
  selectedRowKeys: [] as any[],
  pageSize: 10,
  page: 1,
});

// 计算总商品数
const totalProducts = computed(() => {
  return data.dataList.length;
});

// 根据状态筛选商品
const filteredProducts = computed(() => {
  let result = [...data.dataList];

  // 根据关键字筛选
  if (data.keyword) {
    const keyword = data.keyword.toLowerCase();
    result = result.filter(item =>
      (item.title && item.title.toLowerCase().includes(keyword)) ||
      (item.description && item.description.toLowerCase().includes(keyword))
    );
  }

  // 根据状态筛选
  if (data.statusFilter) {
    result = result.filter(item => item.status === data.statusFilter);
  }

  // 根据分类筛选
  if (data.classFilter) {
    result = result.filter(item => item.classificationId === data.classFilter);
  }

  return result;
});

// 获取商品状态数量
const getProductStatusCount = (status) => {
  return data.dataList.filter(item => item.status === status).length;
};

// 获取库存不足商品数量
const getLowStockCount = () => {
  return data.dataList.filter(item => item.repertory < 10 && item.status === '0').length;
};

// 获取行样式
const getRowClassName = (record) => {
  if (record.status === '1') {
    return 'product-row-inactive';
  }
  if (record.repertory < 10) {
    return 'product-row-low-stock';
  }
  return '';
};

// 获取库存百分比
const getStockPercent = (stock) => {
  if (stock <= 0) return 0;
  if (stock >= 100) return 100;
  return stock;
};

// 获取库存状态
const getStockStatus = (stock) => {
  if (stock <= 0) return 'exception';
  if (stock < 10) return 'warning';
  return 'normal';
};

// 获取库存颜色
const getStockColor = (stock) => {
  if (stock <= 0) return '#ff4d4f';
  if (stock < 10) return '#faad14';
  if (stock < 50) return '#1890ff';
  return '#52c41a';
};

// 获取图片URL
const getImageUrl = (cover) => {
  return cover ? `${BASE_URL}/api/staticfiles/image/${cover}` : '';
};

// 获取分类名称
const getCategoryName = (id) => {
  const category = modal.cData.find(item => item.id === id);
  return category ? category.title : '';
};

// 弹窗数据源
const modal = reactive({
  visile: false,
  editFlag: false,
  title: '',
  cData: [],
  tagData: [{}],
  form: {
    id: undefined,
    title: undefined,
    classificationId: undefined,
    tags: [],
    repertory: undefined,
    price: undefined,
    status: undefined,
    cover: undefined,
    coverUrl: undefined,
    imageFile: undefined,
    description: undefined
  },
  rules: {
    title: [{ required: true, message: '请输入商品名称', trigger: 'change' }],
    classificationId: [{ required: true, message: '请选择商品分类', trigger: 'change' }],
    repertory: [{ required: true, message: '请输入库存数量', trigger: 'change' }],
    price: [{ required: true, message: '请输入商品价格', trigger: 'change' }],
    status: [{ required: true, message: '请选择商品状态', trigger: 'change' }]
  },
});

const myform = ref<FormInstance>();

onMounted(() => {
  getDataList();
  getCDataList();
  getTagDataList();
});

const getDataList = () => {
  data.loading = true;
  listApi({
    keyword: data.keyword,
  })
    .then((res) => {
      data.loading = false;
      console.log(res);
      res.data.forEach((item: any, index: any) => {
        item.index = index + 1;
      });
      data.dataList = res.data;
    })
    .catch((err) => {
      data.loading = false;
      console.log(err);
    });
};

const getCDataList = () => {
  listClassificationApi({}).then(res => {
    modal.cData = res.data;
  });
};

const getTagDataList = () => {
  listTagApi({}).then(res => {
    res.data.forEach((item, index) => {
      item.index = index + 1;
    });
    modal.tagData = res.data;
  });
};

const onSearchChange = (e: Event) => {
  data.keyword = e?.target?.value;
  console.log(data.keyword);
};

const onSearch = () => {
  getDataList();
};

const handleReset = () => {
  data.keyword = '';
  data.statusFilter = '';
  data.classFilter = '';
  data.page = 1;
  getDataList();
};

const rowSelection = ref({
  onChange: (selectedRowKeys: (string | number)[], selectedRows: any[]) => {
    console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
    data.selectedRowKeys = selectedRowKeys;
  },
});

const handleAdd = () => {
  resetModal();
  modal.visile = true;
  modal.editFlag = false;
  modal.title = '新增商品';
  // 重置
  for (const key in modal.form) {
    modal.form[key] = undefined;
  }
  modal.form.status = '0'; // 默认上架
  modal.form.cover = undefined;
};

const handleEdit = (record: any) => {
  resetModal();
  modal.visile = true;
  modal.editFlag = true;
  modal.title = '编辑商品';
  // 重置
  for (const key in modal.form) {
    modal.form[key] = undefined;
  }
  for (const key in record) {
    if (record[key]) {
      modal.form[key] = record[key];
    }
  }
  if (modal.form.cover) {
    modal.form.coverUrl = BASE_URL + '/api/staticfiles/image/' + modal.form.cover;
    modal.form.cover = undefined;
  }
};

const showDeleteConfirm = (record: any) => {
  Modal.confirm({
    title: '确定要删除此商品吗?',
    content: '删除后无法恢复，请谨慎操作',
    okText: '确认',
    okType: 'danger',
    cancelText: '取消',
    onOk() {
      confirmDelete(record);
    },
  });
};

const confirmDelete = (record: any) => {
  console.log('delete', record);
  deleteApi({ ids: record.id })
    .then((res) => {
      message.success('删除成功');
      getDataList();
    })
    .catch((err) => {
      message.error(err.msg || '操作失败');
    });
};

const handleBatchDelete = () => {
  console.log(data.selectedRowKeys);
  if (data.selectedRowKeys.length <= 0) {
    message.warn('请勾选要删除的商品');
    return;
  }

  Modal.confirm({
    title: `确定要删除选中的 ${data.selectedRowKeys.length} 个商品吗?`,
    content: '删除后无法恢复，请谨慎操作',
    okText: '确认',
    okType: 'danger',
    cancelText: '取消',
    onOk() {
      deleteApi({ ids: data.selectedRowKeys.join(',') })
        .then((res) => {
          message.success('批量删除成功');
          data.selectedRowKeys = [];
          getDataList();
        })
        .catch((err) => {
          message.error(err.msg || '操作失败');
        });
    },
  });
};

const handleOk = () => {
  myform.value
    ?.validate()
    .then(() => {
      const formData = new FormData();
      if (modal.editFlag) {
        formData.append('id', modal.form.id);
      }
      formData.append('title', modal.form.title);
      if (modal.form.classificationId) {
        formData.append('classificationId', modal.form.classificationId);
      }
      if (modal.form.tags) {
        modal.form.tags.forEach(function (value) {
          if (value) {
            formData.append('tags[]', value);
          }
        });
      }
      if (modal.form.imageFile) {
        formData.append('imageFile', modal.form.imageFile);
      }
      formData.append('description', modal.form.description || '');
      formData.append('price', modal.form.price || '');
      if (modal.form.repertory >= 0) {
        formData.append('repertory', modal.form.repertory);
      }
      if (modal.form.status) {
        formData.append('status', modal.form.status);
      }
      if (modal.editFlag) {
        updateApi(formData)
          .then((res) => {
            hideModal();
            message.success('更新商品成功');
            getDataList();
          })
          .catch((err) => {
            console.log(err);
            message.error(err.msg || '操作失败');
          });
      } else {
        createApi(formData)
          .then((res) => {
            hideModal();
            message.success('新增商品成功');
            getDataList();
          })
          .catch((err) => {
            console.log(err);
            message.error(err.msg || '操作失败');
          });
      }
    })
    .catch((err) => {
      console.log('表单校验失败');
    });
};

const handleCancel = () => {
  hideModal();
};

// 恢复表单初始状态
const resetModal = () => {
  myform.value?.resetFields();
  fileList.value = [];
};

// 关闭弹窗
const hideModal = () => {
  modal.visile = false;
};
</script>

<style scoped lang="less">
.product-container {
  min-height: 100%;
  background: #f0f2f5;
  padding: 24px;
}

/* 统计卡片样式 */
.statistics-cards {
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.09);
  margin-bottom: 16px;
  background: #fff;
  transition: all 0.3s;
  height: 100%;
  overflow: hidden;
  position: relative;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
  }

  &::after {
    content: '';
    position: absolute;
    top: 0;
    right: 0;
    width: 100px;
    height: 100%;
    background: linear-gradient(90deg, rgba(255, 255, 255, 0) 0%, rgba(255, 255, 255, 0.2) 100%);
    transform: skewX(-20deg) translateX(50px);
  }

  .stat-icon {
    font-size: 32px;
    margin-right: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 64px;
    height: 64px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.2);
  }

  .stat-content {
    flex: 1;

    .stat-title {
      font-size: 16px;
      color: rgba(255, 255, 255, 0.85);
      margin-bottom: 4px;
    }

    .stat-value {
      font-size: 28px;
      font-weight: bold;
      color: #fff;
      line-height: 1.2;
      margin-bottom: 4px;
    }

    .stat-desc {
      font-size: 12px;
      color: rgba(255, 255, 255, 0.65);
    }
  }
}

.card-primary {
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
}

.card-success {
  background: linear-gradient(135deg, #52c41a 0%, #389e0d 100%);
}

.card-warning {
  background: linear-gradient(135deg, #faad14 0%, #d48806 100%);
}

.card-danger {
  background: linear-gradient(135deg, #ff4d4f 0%, #cf1322 100%);
}

/* 筛选卡片样式 */
.filter-card {
  margin-bottom: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.09);

  .filter-row {
    margin-bottom: 0;
  }

  .filter-col {
    margin-bottom: 16px;
  }

  .search-input {
    width: 100%;
    border-radius: 4px;
  }

  .filter-select {
    width: 100%;
    border-radius: 4px;
  }

  .filter-btns {
    display: flex;
    justify-content: flex-end;
    flex-wrap: wrap;

    button {
      margin-left: 8px;
      margin-bottom: 8px;
      border-radius: 4px;
      display: flex;
      align-items: center;

      .anticon {
        margin-right: 4px;
      }
    }

    .btn-add {
      background: #1890ff;
      border-color: #1890ff;
    }

    .btn-delete {
      background: #ff4d4f;
      border-color: #ff4d4f;
      color: #fff;
    }
  }
}

/* 表格卡片样式 */
.table-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.09);

  :deep(.ant-table) {
    border-radius: 8px;
    overflow: hidden;
  }

  :deep(.ant-table-thead > tr > th) {
    background: #fafafa;
    font-weight: 600;
  }

  :deep(.ant-table-tbody > tr.ant-table-row:hover > td) {
    background: rgba(24, 144, 255, 0.05);
  }

  :deep(.ant-table-row.product-row-inactive) {
    background-color: rgba(0, 0, 0, 0.02);

    td {
      color: rgba(0, 0, 0, 0.45);
    }
  }

  :deep(.ant-table-row.product-row-low-stock) {
    background-color: rgba(250, 173, 20, 0.05);
  }

  .product-cell {
    display: flex;
    align-items: center;

    .product-image {
      width: 50px;
      height: 50px;
      min-width: 50px;
      border-radius: 4px;
      overflow: hidden;
      margin-right: 12px;
      background: #f5f5f5;
      display: flex;
      align-items: center;
      justify-content: center;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .no-image {
        color: #d9d9d9;
        font-size: 24px;
      }
    }

    .product-info {
      overflow: hidden;

      .product-name {
        font-weight: 500;
        margin-bottom: 4px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }

      .product-category {
        font-size: 12px;
        color: rgba(0, 0, 0, 0.45);
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }
  }

  .price-tag {
    font-weight: 600;
    color: #ff4d4f;
  }

  .status-tag {
    padding: 4px 8px;
    border-radius: 4px;
    font-weight: 500;

    .anticon {
      margin-right: 4px;
    }
  }

  .stock-count {
    margin-top: 4px;
    font-size: 12px;

    .stock-warning {
      color: #ff4d4f;
      font-weight: 600;
    }
  }

  .action-btns {
    display: flex;
    justify-content: center;

    button {
      margin: 0 4px;
      display: flex;
      align-items: center;

      .anticon {
        margin-right: 4px;
        font-size: 12px;
      }
    }
  }
}

/* 弹窗样式 */
:deep(.product-modal) {
  .ant-modal-content {
    border-radius: 8px;
    overflow: hidden;
  }

  .ant-modal-header {
    background: #fafafa;
  }

  .ant-tabs-nav {
    margin-bottom: 24px;
  }

  .upload-container {
    .upload-area {
      width: 100%;
      height: 200px;

      .upload-content {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        height: 100%;

        .preview-image {
          max-width: 100%;
          max-height: 160px;
          object-fit: contain;
        }

        .upload-icon {
          font-size: 32px;
          color: #d9d9d9;
          margin-bottom: 8px;
        }

        .upload-text {
          color: rgba(0, 0, 0, 0.45);
        }
      }
    }
  }
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
  .filter-btns {
    justify-content: flex-start;

    button {
      margin-left: 0;
      margin-right: 8px;
    }
  }

  .stat-card {
    .stat-icon {
      width: 48px;
      height: 48px;
      font-size: 24px;
    }

    .stat-content {
      .stat-value {
        font-size: 24px;
      }
    }
  }
}
</style>
