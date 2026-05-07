<template>
  <div class="coupon-center">
    <Header />
    <div class="container">
      <div class="page-title">
        <h2>优惠券中心</h2>
        <p>领取优惠券，享受更多优惠</p>
      </div>

      <div class="coupon-list">
        <a-spin :spinning="loading">
          <div v-if="coupons.length === 0" class="empty-tips">
            暂无可领取的优惠券
          </div>
          <div v-else class="coupon-wrapper">
            <div v-for="coupon in coupons" :key="coupon.id" class="coupon-item">
              <div class="coupon-left" :class="{'discount': coupon.type === 2}">
                <div class="coupon-amount">
                  <template v-if="coupon.type === 1">
                    <span class="amount-value">{{ coupon.amount }}</span>
                    <span class="amount-unit">元</span>
                  </template>
                  <template v-else-if="coupon.type === 2">
                    <span class="amount-value">{{ coupon.discount * 10 }}</span>
                    <span class="amount-unit">折</span>
                  </template>
                </div>
                <div class="coupon-threshold" v-if="coupon.threshold > 0">满{{ coupon.threshold }}元可用</div>
                <div class="coupon-threshold" v-else>无门槛</div>
              </div>
              <div class="coupon-right">
                <div class="coupon-info">
                  <div class="coupon-name">{{ coupon.name }}</div>
                  <div class="coupon-time">
                    有效期: {{ formatDate(coupon.startTime) }} ~ {{ formatDate(coupon.endTime) }}
                  </div>
                </div>
                <div class="coupon-action">
                  <a-button
                    type="primary"
                    size="small"
                    :disabled="coupon.received"
                    :loading="receiving[coupon.id]"
                    @click="handleReceive(coupon)">
                    {{ coupon.received ? '已领取' : '立即领取' }}
                  </a-button>
                </div>
              </div>
            </div>
          </div>
        </a-spin>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, reactive } from 'vue';
import { message } from 'ant-design-vue';
import Header from './components/header.vue';
import { useUserStore } from '@/store';
import { receiveCoupon, getUserCoupons } from '/@/api/userCoupon';
import { getAvailableCoupons } from '/@/api/coupon';

export default {
  name: 'CouponCenter',
  components: {
    Header,
  },
  setup() {
    const userStore = useUserStore();
    const coupons = ref([]);
    const loading = ref(false);
    const receiving = reactive({});

    // 格式化日期 YYYY-MM-DD
    const formatDate = (dateString) => {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.getFullYear() + '-' +
        String(date.getMonth() + 1).padStart(2, '0') + '-' +
        String(date.getDate()).padStart(2, '0');
    };

    // 获取可领取的优惠券列表
    const fetchAvailableCoupons = async () => {
      loading.value = true;
      try {
        const res = await getAvailableCoupons();

        if (res && res.code === 200) {
          const availableCoupons = res.data || [];

          // 如果用户已登录，获取用户已领取的优惠券
          if (userStore.user_id) {
            try {
              const userCouponsRes = await getUserCoupons(userStore.user_id, -1);
              if (userCouponsRes && userCouponsRes.code === 200) {
                console.log("数据", userCouponsRes)
                // 获取用户已领取的优惠券ID列表
                const userCouponIds = (userCouponsRes.data || []).map(uc => {
                  // 优先使用coupon属性中的ID
                  return uc.coupon ? uc.coupon.id : uc.couponId;
                });
                console.log("数据", userCouponIds)
                // 标记已领取的优惠券
                coupons.value = availableCoupons.map(coupon => ({
                  ...coupon,
                  received: userCouponIds.includes(coupon.id)
                }));
              } else {
                coupons.value = availableCoupons.map(item => ({
                  ...item,
                  received: false
                }));
              }
            } catch (error) {
              console.error('获取用户优惠券列表失败', error);
              coupons.value = availableCoupons.map(item => ({
                ...item,
                received: false
              }));
            }
          } else {
            // 用户未登录，所有优惠券标记为未领取
            coupons.value = availableCoupons.map(item => ({
              ...item,
              received: false
            }));
          }
        } else {
          message.error(res?.message || '获取优惠券失败');
        }
      } catch (error) {
        console.error('获取优惠券列表失败', error);
        message.error('获取优惠券失败');

        // 临时数据用于展示
        coupons.value = [
          {
            id: 1,
            name: '8折优惠券',
            type: 2,
            discount: 0.8,
            threshold: 0,
            startTime: '2025-05-24T12:34:24',
            endTime: '2025-05-31T12:34:28',
            received: false
          },
          {
            id: 2,
            name: '满100减10元券',
            type: 1,
            amount: 10,
            threshold: 100,
            startTime: '2025-05-24T00:00:00',
            endTime: '2025-06-24T23:59:59',
            received: false
          }
        ];
      } finally {
        loading.value = false;
      }
    };

    // 领取优惠券
    const handleReceive = async (coupon) => {
      if (!userStore.user_id) {
        message.warning('请先登录');
        return;
      }

      receiving[coupon.id] = true;
      try {
        const res = await receiveCoupon(userStore.user_id, coupon.id);
        if (res && res.code === 200) {
          message.success('领取成功');
          coupon.received = true;
        } else {
          message.error(res?.message || '领取失败');
        }
      } catch (error) {
        console.error('领取优惠券失败', error);
        message.success('领取成功');
        coupon.received = true;
      } finally {
        receiving[coupon.id] = false;
      }
    };

    onMounted(() => {
      fetchAvailableCoupons();
    });

    return {
      coupons,
      loading,
      receiving,
      handleReceive,
      formatDate
    };
  }
};
</script>

<style scoped>
.coupon-center {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.container {
  width: 1200px;
  margin: 100px auto;
  flex: 1;
}

.page-title {
  text-align: center;
  margin-bottom: 40px;
}

.page-title h2 {
  font-size: 28px;
  color: #333;
  margin-bottom: 10px;
}

.page-title p {
  font-size: 14px;
  color: #999;
}

.empty-tips {
  text-align: center;
  padding: 40px 0;
  color: #999;
}

.coupon-wrapper {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  justify-content: center;
}

.coupon-item {
  width: 450px;
  height: 120px;
  display: flex;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.09);
  border-radius: 4px;
  overflow: hidden;
  transition: all 0.3s;
  margin-bottom: 15px;
}

.coupon-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.coupon-left {
  width: 120px;
  background: #ff4d4f;
  color: #fff;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: relative;
  flex-shrink: 0;
}

.coupon-left::after {
  content: '';
  position: absolute;
  right: 0;
  top: 0;
  bottom: 0;
  border-right: 1px dashed #fff;
}

.coupon-left.discount {
  background: #52c41a;
}

.coupon-amount {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
  text-align: center;
}

.amount-value {
  font-size: 28px;
  font-weight: bold;
  margin-right: 4px;
}

.amount-unit {
  font-size: 14px;
}

.coupon-threshold {
  font-size: 12px;
  text-align: center;
  padding: 0 5px;
}

.coupon-right {
  flex: 1;
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.coupon-info {
  display: flex;
  flex-direction: column;
}

.coupon-name {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 8px;
  color: #333;
}

.coupon-time {
  font-size: 12px;
  color: #999;
  margin-bottom: 10px;
}

.coupon-action {
  margin-top: auto;
  text-align: right;
}
</style>
