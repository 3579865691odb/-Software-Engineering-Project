<template>
  <div class="cart-container">
    <Header />
    <div class="cart-content">
      <div class="cart-main">
        <div class="cart-table">
          <div class="cart-table-header">
            <div class="col-checkbox">
              <a-checkbox v-model:checked="allChecked" @change="handleCheckAllChange">全选</a-checkbox>
            </div>
            <div class="col-product">商品</div>
            <div class="col-price">单价</div>
            <div class="col-quantity">数量</div>
            <div class="col-total">小计</div>
            <div class="col-action">操作</div>
          </div>

          <a-empty v-if="cartItems.length === 0" description="购物车还是空的，快去选购商品吧~">
            <a-button type="primary" @click="goShopping">去购物</a-button>
          </a-empty>

          <div v-else class="cart-items">
            <div v-for="(item, index) in cartItems" :key="index" class="cart-item">
              <div class="col-checkbox">
                <a-checkbox v-model:checked="item.checked" @change="updateTotalPrice"></a-checkbox>
              </div>
              <div class="col-product">
                <div class="product-info">
                  <img :src="item.cover" :alt="item.title" class="product-image">
                  <div class="product-detail">
                    <h3 class="product-title">{{ item.title }}</h3>
                    <p class="product-spec">{{ item.spec || '默认规格' }}</p>
                  </div>
                </div>
              </div>
              <div class="col-price">¥{{ item.price }}</div>
              <div class="col-quantity">
                <a-input-number
                  v-model:value="item.quantity"
                  :min="1"
                  :max="99"
                  @change="() => updateItemTotal(item)"
                />
              </div>
              <div class="col-total">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
              <div class="col-action">
                <a @click="removeItem(index)">删除</a>
              </div>
            </div>
          </div>
        </div>

        <div class="cart-footer">
          <div class="cart-footer-left">
            <a-button @click="clearCart">清空购物车</a-button>
            <span class="selected-count">已选择 {{ selectedCount }} 件商品</span>
          </div>
          <div class="cart-footer-right">
            <div class="total-price">
              <span>合计：</span>
              <span class="price">¥{{ totalPrice.toFixed(2) }}</span>
            </div>
            <a-button type="primary" size="large" @click="checkout" :disabled="selectedCount === 0">
              去结算
            </a-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import Header from './components/header.vue'
import { BASE_URL } from "/@/store/constants"

const router = useRouter()
const cartItems = ref([])
const allChecked = ref(false)

// 计算选中的商品数量
const selectedCount = computed(() => {
  return cartItems.value.filter(item => item.checked).length
})

// 计算总价
const totalPrice = ref(0)

onMounted(() => {
  loadCartItems()
})

// 加载购物车数据
const loadCartItems = () => {
  // 从本地存储加载购物车数据
  const storedCart = localStorage.getItem('cart')
  if (storedCart) {
    try {
      const parsed = JSON.parse(storedCart)
      // 确保每个商品都有正确的ID和其他必要属性
      cartItems.value = parsed.map(item => ({
        ...item,
        id: item.id, // 确保ID存在
        quantity: item.quantity || 1,
        checked: typeof item.checked === 'boolean' ? item.checked : true
      }))
      console.log('从本地存储加载的购物车数据:', cartItems.value)
      updateTotalPrice()
    } catch (error) {
      console.error('解析购物车数据失败:', error)
      cartItems.value = []
      saveCartToStorage()
    }
  } else {
    cartItems.value = []
    updateTotalPrice()
    saveCartToStorage()
  }
  checkAllChecked()
}

// 更新商品小计
const updateItemTotal = (item) => {
  item.total = item.price * item.quantity
  updateTotalPrice()
  saveCartToStorage()
}

// 更新总价
const updateTotalPrice = () => {
  totalPrice.value = cartItems.value
    .filter(item => item.checked)
    .reduce((sum, item) => sum + item.price * item.quantity, 0)
  checkAllChecked()
  saveCartToStorage()
}

// 检查是否全选
const checkAllChecked = () => {
  allChecked.value = cartItems.value.length > 0 && cartItems.value.every(item => item.checked)
}

// 全选/取消全选
const handleCheckAllChange = (e) => {
  const checked = e.target.checked
  cartItems.value.forEach(item => {
    item.checked = checked
  })
  updateTotalPrice()
}

// 移除商品
const removeItem = (index) => {
  cartItems.value.splice(index, 1)
  updateTotalPrice()
  saveCartToStorage()
  message.success('商品已从购物车中移除')
}

// 清空购物车
const clearCart = () => {
  cartItems.value = []
  totalPrice.value = 0
  allChecked.value = false
  saveCartToStorage()
  message.success('购物车已清空')
}

// 保存购物车到本地存储
const saveCartToStorage = () => {
  // 确保保存前所有商品数据格式正确
  const validCartItems = cartItems.value.map(item => ({
    id: item.id,
    title: item.title,
    price: item.price,
    quantity: item.quantity || 1,
    spec: item.spec || '默认规格',
    cover: item.cover,
    checked: item.checked
  }))
  
  console.log('保存到本地存储的购物车数据:', validCartItems)
  localStorage.setItem('cart', JSON.stringify(validCartItems))
}

// 去购物
const goShopping = () => {
  router.push({ name: 'portal' })
}

// 结算
const checkout = () => {
  const selectedItems = cartItems.value.filter(item => item.checked)
  if (selectedItems.length === 0) {
    message.warning('请先选择要结算的商品')
    return
  }

  // 确保选中的商品数据完整且格式一致
  const checkoutData = selectedItems.map(item => ({
    id: item.id,
    title: item.title,
    price: item.price,
    quantity: item.quantity || 1,
    spec: item.spec || '默认规格',
    cover: item.cover,
    checked: true
  }))

  // 将选中的商品传递到确认订单页面
  console.log('提交结算的商品:', checkoutData)
  localStorage.setItem('checkoutItems', JSON.stringify(checkoutData))
  router.push({ name: 'confirm' })
}
</script>

<style scoped lang="less">
.cart-container {
  background-color: #f5f7fa;
  min-height: 100vh;
}

.cart-content {
  width: 1200px;
  margin: 0 auto;
  padding: 80px 0 50px;
}

.cart-header {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);

  h1 {
    font-size: 24px;
    color: #333;
    margin-bottom: 20px;
  }

  .cart-steps {
    display: flex;
    align-items: center;
    justify-content: center;

    .step {
      display: flex;
      flex-direction: column;
      align-items: center;
      position: relative;

      .step-num {
        width: 30px;
        height: 30px;
        border-radius: 50%;
        background-color: #ccc;
        color: #fff;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 8px;
      }

      .step-text {
        font-size: 14px;
        color: #666;
      }

      &.active {
        .step-num {
          background-color: #ff4d4f;
        }

        .step-text {
          color: #ff4d4f;
          font-weight: 500;
        }
      }
    }

    .step-line {
      flex: 1;
      height: 2px;
      background-color: #eee;
      margin: 0 15px;
      margin-bottom: 30px;
    }
  }
}

.cart-main {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);

  .cart-table {
    width: 100%;

    .cart-table-header {
      display: flex;
      align-items: center;
      padding: 15px 0;
      border-bottom: 1px solid #f0f0f0;
      font-weight: 500;
      color: #333;
    }

    .col-checkbox {
      width: 80px;
      text-align: center;
    }

    .col-product {
      flex: 1;
    }

    .col-price, .col-quantity, .col-total {
      width: 120px;
      text-align: center;
    }

    .col-action {
      width: 80px;
      text-align: center;
    }
  }

  .cart-items {
    .cart-item {
      display: flex;
      align-items: center;
      padding: 20px 0;
      border-bottom: 1px solid #f0f0f0;

      .product-info {
        display: flex;
        align-items: center;

        .product-image {
          width: 80px;
          height: 80px;
          object-fit: contain;
          margin-right: 15px;
          background-color: #f9f9f9;
          border-radius: 4px;
        }

        .product-detail {
          .product-title {
            font-size: 14px;
            color: #333;
            margin-bottom: 5px;
          }

          .product-spec {
            font-size: 12px;
            color: #999;
          }
        }
      }

      .col-action {
        a {
          color: #ff4d4f;

          &:hover {
            text-decoration: underline;
          }
        }
      }
    }
  }
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;

  .cart-footer-left {
    display: flex;
    align-items: center;

    .selected-count {
      margin-left: 20px;
      color: #666;
    }
  }

  .cart-footer-right {
    display: flex;
    align-items: center;

    .total-price {
      margin-right: 20px;
      font-size: 16px;

      .price {
        color: #ff4d4f;
        font-size: 24px;
        font-weight: 500;
      }
    }
  }
}
</style>
