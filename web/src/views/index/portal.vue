<template>
  <div class="portal-container">
    <Header />
    <div class="main-content">
      <Banner />
    </div>
  </div>
</template>

<script setup>
import Header from './components/header.vue'
import Banner from './components/banner.vue'
</script>

<style scoped lang="less">
.portal-container {
  background-color: #f5f7fa;
  min-height: 100vh;
}

.main-content {
  width: 1200px;
  margin: 0 auto;
  padding-bottom: 50px;
}
</style>
