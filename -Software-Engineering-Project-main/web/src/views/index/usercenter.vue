<template>
  <div class="user">

    <Header />

    <div class="user-content">
      <div class="user-content-left">
        <MineInfosView />
      </div>
      <div class="user-content-right">
        <router-view />
      </div>
    </div>

  </div>
</template>
<script>
import Header from '/@/views/index/components/header.vue'
import MineInfosView from '/@/views/index/user/mine-infos-view.vue'

export default {
  components: {
    MineInfosView,
    Header
  },
  data () {
    return {
      collapsed: false
    }
  }
}
</script>
<style scoped lang="less">
.user {
  display: block;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.user-content {
  display: flex;
  flex-direction: row;
  max-width: 1200px;
  min-width: 800px;
  margin: 50px auto 80px;
  position: relative;
  gap: 20px;
  padding: 20px 15px 0 15px;

  .user-content-left {
    position: sticky;
    top: 75px;
    height: fit-content;
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
    }
  }

  .user-content-right {
    flex: 1;
    padding: 25px;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
    overflow-y: auto;
    min-height: 600px;
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
    }
  }
}

@media (max-width: 768px) {
  .user-content {
    flex-direction: column;

    .user-content-left {
      position: relative;
      top: 0;
      width: 100%;
    }
  }
}
</style>
