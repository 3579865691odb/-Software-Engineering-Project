<template>
  <div class="content-margin">
    <div class="search-header">
      <div class="back-to-home" @click="backToHome">
        <i class="back-icon"></i>
        <span>返回首页</span>
      </div>
      <h1 class="search-name-box">{{ getSearchTitle() }}</h1>
    </div>
    <div class="search-tab-nav clearfix">
      <div class="tab-text">
        <span>与</span>
        <span class="strong">{{ getSearchTitle() }}</span>
        <span>相关的内容</span>
      </div>
    </div>
    <div class="content-list">
      <div class="thing-list">

        <a-spin :spinning="tData.loading" style="min-height: 200px;">
          <div class="things flex-view">
            <div class="thing-item item-column-4" v-for="item in tData.pageData" @click="handleDetail(item)">
              <div class="img-view">
                <img :src="item.cover"></div>
              <div class="info-view">
                <h3 class="thing-name">{{ item.title.substring(0, 12) }}</h3>
                <span>
                  <span class="a-price-symbol">¥</span>
                  <span class="a-price">{{ item.price }}</span>
                </span>
              </div>
            </div>
          </div>
          <div v-if="tData.pageData.length === 0 && !tData.loading" class="no-data">
            暂无相关商品数据
          </div>
        </a-spin>
        <div class="page-view" style="">
          <a-pagination v-model:value="tData.page" size="small" @change="changePage" :hideOnSinglePage="true"
                        :defaultPageSize="tData.pageSize" :total="tData.total"/>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {listApi as listThingList} from '/@/api/thing'
import {listApi as listClassificationList} from '/@/api/classification'
import {listApi as listTagList} from '/@/api/tag'
import {BASE_URL} from "/@/store/constants";
import {useUserStore} from "/@/store";

const userStore = useUserStore()
const router = useRouter();
const route = useRoute();

const tData = reactive({
  loading: false,
  keyword: '',
  categoryName: '',
  tagName: '',
  thingData: [],
  pageData: [],
  categoryList: [],
  tagList: [],

  page: 1,
  total: 0,
  pageSize: 20,
})

onMounted(() => {
  getCategories()
  getTags()
  search()
})

// 监听query参数
watch(() => route.query, (newPath, oldPath) => {
  search()
}, {immediate: false});

// 获取分类数据
const getCategories = () => {
  listClassificationList().then(res => {
    tData.categoryList = res.data
  }).catch(err => {
    console.log(err)
  })
}

// 获取标签数据
const getTags = () => {
  listTagList().then(res => {
    tData.tagList = res.data
  }).catch(err => {
    console.log(err)
  })
}

// 获取搜索标题
const getSearchTitle = () => {
  if (route.query.keyword) {
    return route.query.keyword
  } else if (route.query.c) {
    const category = tData.categoryList.find(item => item.id === route.query.c)
    return category ? category.title : '分类商品'
  } else if (route.query.tag) {
    const tag = tData.tagList.find(item => item.id === Number(route.query.tag))
    return tag ? tag.title : '标签商品'
  }
  return '搜索结果'
}

const search = () => {
  const searchParams = {}
  
  if (route.query.keyword) {
    tData.keyword = route.query.keyword.trim()
    searchParams.keyword = tData.keyword
  }
  
  if (route.query.c) {
    searchParams.c = route.query.c
  }
  
  if (route.query.tag) {
    searchParams.tag = route.query.tag
  }
  
  getThingList(searchParams)
}

// 返回首页
const backToHome = () => {
  router.push({ name: 'portal' })
}

// 分页事件
const changePage = (page) => {
  tData.page = page
  let start = (tData.page - 1) * tData.pageSize
  tData.pageData = tData.thingData.slice(start, start + tData.pageSize)
  console.log('第' + tData.page + '页')
}

const handleDetail = (item) => {
  // 跳转新页面
  let text = router.resolve({name: 'detail', query: {id: item.id}})
  window.open(text.href, '_blank')
}

const getThingList = (data) => {
  tData.loading = true
  listThingList(data).then(res => {
    res.data.forEach((item, index) => {
      if (item.cover) {
        item.cover = BASE_URL + '/api/staticfiles/image/' + item.cover
      }
    })
    tData.thingData = res.data
    tData.total = tData.thingData.length
    changePage(1)
    tData.loading = false
  }).catch(err => {
    console.log(err)
    tData.loading = false
  })
}

</script>
<style scoped lang="less">
.content-margin {
  margin: 156px 0 100px;
}

.page-view {
  width: 100%;
  text-align: center;
  margin-top: 48px;
}

.search-header {
  position: relative;
  background: #f5f9fb;
  height: 100px;
  position: fixed;
  top: 56px;
  left: 0;
  z-index: 1;
  width: calc(100% - 8px);
  
  .back-to-home {
    position: absolute;
    left: 20px;
    top: 50%;
    transform: translateY(-50%);
    display: flex;
    align-items: center;
    cursor: pointer;
    padding: 8px 16px;
    background-color: #fff;
    border-radius: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    transition: all 0.3s;
    
    &:hover {
      background-color: #ff4d4f;
      color: #fff;
    }
    
    .back-icon {
      display: inline-block;
      width: 16px;
      height: 16px;
      margin-right: 5px;
      background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M19 12H5M12 19l-7-7 7-7'/%3E%3C/svg%3E");
      background-size: contain;
      background-repeat: no-repeat;
    }
  }
}

.search-name-box {
  line-height: 100px;
  font-size: 20px;
  color: #152844;
  text-align: center;
  margin: 0;
}

.search-tab-nav {
  position: relative;
  padding: 24px 0 16px;
  text-align: center;

  .tab-text {
    float: left;
    color: #5f77a6;
    font-size: 14px;
  }

  .strong {
    color: #152844;
    font-weight: 600;
    margin: 0 4px;
  }
}

.things {
  -ms-flex-wrap: wrap;
  flex-wrap: wrap;
}

.flex-view {
  display: flex;
}

.thing-item {
  min-width: 255px;
  max-width: 255px;
  position: relative;
  flex: 1;
  margin-right: 20px;
  height: fit-content;
  overflow: hidden;
  margin-top: 26px;
  margin-bottom: 36px;
  cursor: pointer;
  transition: all 0.3s;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.08);
  }

  .img-view {
    height: 200px;
    width: 255px;
    background: #f9f9f9;
    border-radius: 8px;
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;

    img {
      height: 200px;
      width: 186px;
      margin: 0 auto;
      background-size: contain;
      object-fit: contain;
      transition: all 0.3s;
      
      &:hover {
        transform: scale(1.05);
      }
    }
  }

  .info-view {
    overflow: hidden;
    padding: 0 16px;

    .thing-name {
      line-height: 32px;
      margin-top: 12px;
      color: #333;
      font-size: 15px;
      font-weight: 500;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }
}

.a-price-symbol {
  top: -0.5em;
  font-size: 12px;
  color: #ff4d4f;
}

.a-price {
  color: #ff4d4f;
  font-size: 21px;
  font-weight: 600;
}

.no-data {
  width: 100%;
  text-align: center;
  padding: 50px 0;
  color: #999;
  font-size: 16px;
}
</style>
