
// @ts-nocheck
import { createRouter, createWebHistory } from 'vue-router'
import type { NavigationGuardNext, RouteLocationNormalized } from 'vue-router'

const HomePage = () => import('./pages/HomePage.vue')
const Login = () => import('./pages/Login.vue')
const Register = () => import('./pages/Register.vue')
const ForgotPassword = () => import('./pages/ForgotPassword.vue')
const Settings = () => import('./pages/Settings.vue')
const AddressManagement = () => import('./pages/AddressManagement.vue')
const ProductDetail = () => import('./pages/ProductDetail.vue')
const Cart = () => import('./pages/Cart.vue')
const Checkout = () => import('./pages/Checkout.vue')
const PaymentSuccess = () => import('./pages/PaymentSuccess.vue')
const OrderList = () => import('./pages/OrderList.vue')
const ProductList = () => import('./pages/ProductList.vue')
const MerchantShop = () => import('./pages/MerchantShop.vue')
const ShopDetail = () => import('./pages/ShopDetail.vue')
const CreateProduct = () => import('./pages/CreateProduct.vue')
const AdminPanel = () => import('./pages/AdminPanel.vue')

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: HomePage, name: 'home' },
    { path: '/login', component: Login, name: 'login' },
    { path: '/register', component: Register, name: 'register' },
    { path: '/forgot-password', component: ForgotPassword, name: 'forgot-password' },
    { path: '/settings', component: Settings, name: 'settings' },
    { path: '/addresses', component: AddressManagement, name: 'addresses' },
    { path: '/product/:id', component: ProductDetail, name: 'product-detail' },
    { path: '/cart', component: Cart, name: 'cart' },
    { path: '/checkout', component: Checkout, name: 'checkout' },
    { path: '/payment-success', component: PaymentSuccess, name: 'payment-success' },
    { path: '/orders', component: OrderList, name: 'orders' },
    { path: '/products', component: ProductList, name: 'products' },
    { path: '/merchant/shop', component: MerchantShop, name: 'merchant-shop' },
    { path: '/shop/:shopId', component: ShopDetail, name: 'shop-detail' },
    { path: '/shop/:shopId/create-product', component: CreateProduct, name: 'create-product' },
    { path: '/shop/:shopId/edit-product/:productId', component: CreateProduct, name: 'edit-product' },
    { path: '/admin', component: AdminPanel, name: 'admin-panel' },
  ],
})

// 登录守卫：没有 token 或访问受保护路由时跳转登录
router.beforeEach((to: RouteLocationNormalized, _from: RouteLocationNormalized, next: NavigationGuardNext) => {
  const token = localStorage.getItem('access_token')
  
  // 首页、登录、注册和忘记密码页面不需要 token
  if (to.name === 'home' || to.name === 'login' || to.name === 'register' || to.name === 'forgot-password') {
    // 如果已经有 token，访问登录/注册/忘记密码页时跳转到首页
    if (token && (to.name === 'login' || to.name === 'register' || to.name === 'forgot-password')) {
      next({ name: 'home' })
    } else {
      next()
    }
    return
  }
  
  // 其他受保护的页面需要 token
  if (!token) {
    next({ name: 'login' })
  } else {
    next()
  }
})


