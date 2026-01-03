<template>
  <div class="min-h-screen flex flex-col bg-base-100">
    <Header />
    <div class="flex-1 py-8 px-4">
      <div class="container mx-auto max-w-4xl">
        <div class="card bg-base-100 shadow-lg">
          <div class="card-body">
            <!-- 加载状态 -->
            <div v-if="loadingProduct" class="text-center py-12">
              <span class="loading loading-spinner loading-lg"></span>
              <p class="mt-4 text-base-content/60">加载商品数据中...</p>
            </div>
            
            <template v-else>
              <h1 class="card-title text-2xl mb-6">{{ isEditMode ? '编辑商品' : '创建商品' }}</h1>

              <!-- 商品基本信息表单 -->
              <form @submit.prevent="handleSubmitProductInfo" class="space-y-6">
                <!-- 商品基本信息 -->
                <div class="space-y-4">
                  <h2 class="text-xl font-semibold">商品基本信息</h2>
                  
                  <div class="form-control">
                    <label class="label">
                      <span class="label-text">商品名称 <span class="text-error">*</span></span>
                    </label>
                    <input
                      v-model="form.name"
                      type="text"
                      placeholder="请输入商品名称"
                      class="input input-bordered"
                      required
                      maxlength="200"
                    />
                  </div>

                  <div class="form-control">
                    <label class="label">
                      <span class="label-text">商品分类 <span class="text-error">*</span></span>
                    </label>
                    <select
                      v-model="form.categoryId"
                      class="select select-bordered w-full"
                      required
                    >
                      <option value="" disabled>请选择商品分类</option>
                      <option
                        v-for="category in flattenedCategories"
                        :key="category.categoryId"
                        :value="category.categoryId"
                      >
                        {{ '└─'.repeat(category.level) }} {{ category.name }}
                      </option>
                    </select>
                    <label class="label">
                      <span class="label-text-alt">选择分类有助于用户发现您的商品</span>
                    </label>
                  </div>

                  <div class="form-control">
                    <label class="label">
                      <span class="label-text">商品描述</span>
                    </label>
                    <textarea
                      v-model="form.description"
                      placeholder="请输入商品描述"
                      class="textarea textarea-bordered h-24"
                      maxlength="2000"
                    ></textarea>
                  </div>

                  <div class="form-control">
                    <label class="label">
                      <span class="label-text">商品主图URL</span>
                    </label>
                    <input
                      v-model="form.imageUrl"
                      type="url"
                      placeholder="请输入商品主图URL"
                      class="input input-bordered"
                    />
                  </div>
                </div>

                <!-- 编辑模式：只更新商品基本信息 -->
                <div v-if="isEditMode" class="flex gap-4 justify-end pt-4 border-t">
                  <button
                    type="button"
                    class="btn btn-ghost"
                    @click="goBack"
                  >
                    取消
                  </button>
                  <button
                    type="submit"
                    class="btn btn-primary"
                    :disabled="submittingProductInfo"
                  >
                    <span v-if="submittingProductInfo" class="loading loading-spinner loading-sm"></span>
                    <span v-else>更新商品信息</span>
                  </button>
                </div>
              </form>

              <!-- SKU管理区域 -->
              <div class="space-y-4 mt-8">
                <div class="flex items-center justify-between">
                  <h2 class="text-xl font-semibold">商品规格型号<span class="text-error">*</span></h2>
                  <button
                    v-if="isEditMode"
                    type="button"
                    class="btn btn-sm btn-primary"
                    @click="openAddSkuModal"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
                    </svg>
                    添加新规格
                  </button>
                  <button
                    v-else
                    type="button"
                    class="btn btn-sm btn-primary"
                    @click="addSku"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
                    </svg>
                    添加规格
                  </button>
                </div>

                <!-- 编辑模式：显示已有 SKU（只读规格和价格，可编辑图片和库存） -->
                <template v-if="isEditMode">
                  <div v-if="existingSkus.length === 0 && newSkus.length === 0" class="alert alert-warning">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                    </svg>
                    <span>暂无规格，请添加新规格</span>
                  </div>

                  <!-- 已有 SKU 列表 -->
                  <div
                    v-for="(sku, index) in existingSkus"
                    :key="sku.skuId"
                    class="card bg-base-200 p-4"
                  >
                    <div class="flex items-start justify-between mb-4">
                      <h3 class="font-semibold">规格 {{ index + 1 }} <span class="text-sm font-normal text-base-content/60">(已有)</span></h3>
                      <div class="badge badge-sm" :class="sku.stock === 0 ? 'badge-ghost' : 'badge-success'">
                        {{ sku.stock === 0 ? '无货' : `库存: ${sku.stock}` }}
                      </div>
                    </div>

                    <div class="space-y-4">
                      <!-- 规格属性（只读） -->
                      <div class="form-control">
                        <label class="label">
                          <span class="label-text">规格属性</span>
                          <span class="label-text-alt text-base-content/50">（不可修改）</span>
                        </label>
                        <div class="bg-base-300 p-3 rounded-lg">
                          <div class="text-sm">{{ formatSpec(sku.spec) || '默认规格' }}</div>
                        </div>
                      </div>

                      <div class="grid grid-cols-2 gap-4">
                        <!-- 价格（可编辑） -->
                        <div class="form-control">
                          <label class="label">
                            <span class="label-text">价格（元）</span>
                            <span class="label-text-alt text-base-content/50">（当前价格）</span>
                          </label>
                          <div class="bg-base-300 p-3 rounded-lg mb-2">
                            <span class="text-lg font-bold" style="color: rgb(255, 202, 57);">¥{{ sku.price || 0 }}</span>
                          </div>
                          <button
                            type="button"
                            class="btn btn-sm btn-primary w-full"
                            @click="openUpdatePriceModal(sku)"
                            :disabled="updatingPrice[sku.skuId!]"
                          >
                            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                              <path stroke-linecap="round" stroke-linejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                            </svg>
                            修改价格
                          </button>
                        </div>

                        <!-- 库存（只读显示，通过按钮操作） -->
                        <div class="form-control">
                          <label class="label">
                            <span class="label-text">当前库存</span>
                            <span class="label-text-alt text-base-content/50">（仅显示，不可直接修改）</span>
                          </label>
                          <div class="bg-base-300 p-3 rounded-lg">
                            <span class="text-lg font-bold" :class="sku.stock === 0 ? 'text-error' : 'text-base-content'">
                              {{ sku.stock === 0 ? '无货' : sku.stock }}
                            </span>
                          </div>
                          <div class="flex gap-2 mt-3">
                            <button
                              type="button"
                              class="btn btn-sm btn-success flex-1"
                              @click="openIncreaseStockModal(sku)"
                              :disabled="updatingStock[sku.skuId!]"
                            >
                              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                                <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
                              </svg>
                              增加库存
                            </button>
                            <button
                              type="button"
                              class="btn btn-sm btn-warning flex-1"
                              @click="openDecreaseStockModal(sku)"
                              :disabled="updatingStock[sku.skuId!] || sku.stock === 0"
                            >
                              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                                <path stroke-linecap="round" stroke-linejoin="round" d="M20 12H4" />
                              </svg>
                              减少库存
                            </button>
                            <button
                              type="button"
                              class="btn btn-sm btn-error flex-1"
                              @click="openSetStockToZeroModal(sku)"
                              :disabled="updatingStock[sku.skuId!] || sku.stock === 0"
                            >
                              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                                <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
                              </svg>
                              标记无货
                            </button>
                          </div>
                        </div>
                      </div>

                      <!-- SKU图片（可编辑） -->
                      <div class="form-control">
                        <label class="label">
                          <span class="label-text">SKU图片URL</span>
                        </label>
                        <div class="flex gap-2">
                          <input
                            v-model="sku.imageUrl"
                            type="url"
                            placeholder="请输入SKU图片URL（可选）"
                            class="input input-bordered flex-1"
                            :disabled="updatingImage[sku.skuId!]"
                          />
                          <button
                            type="button"
                            class="btn btn-sm btn-primary"
                            @click="updateSkuImage(sku)"
                            :disabled="updatingImage[sku.skuId!]"
                          >
                            <span v-if="updatingImage[sku.skuId!]" class="loading loading-spinner loading-xs"></span>
                            <span v-else>更新</span>
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                </template>

                <!-- 创建模式：显示可编辑的 SKU 列表 -->
                <template v-else>
                  <div v-if="form.skus.length === 0" class="alert alert-warning">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                    </svg>
                    <span>至少需要添加一个SKU规格</span>
                  </div>

                  <div
                    v-for="(sku, index) in form.skus"
                    :key="index"
                    class="card bg-base-200 p-4"
                  >
                    <div class="flex items-start justify-between mb-4">
                      <h3 class="font-semibold">规格 {{ index + 1 }}</h3>
                      <button
                        v-if="form.skus.length > 1"
                        type="button"
                        class="btn btn-sm btn-ghost btn-circle"
                        @click="removeSku(index)"
                      >
                        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-error" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                          <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
                        </svg>
                      </button>
                    </div>

                    <div class="space-y-4">
                      <!-- 规格属性 -->
                      <div class="form-control">
                        <div class="flex items-center justify-between mb-2">
                          <label class="label">
                            <span class="label-text">规格属性 <span class="text-error">*</span></span>
                          </label>
                          <button
                            type="button"
                            class="btn btn-xs btn-primary"
                            @click="addSpecAttribute(index)"
                          >
                            <svg xmlns="http://www.w3.org/2000/svg" class="h-3 w-3 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                              <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
                            </svg>
                            添加属性
                          </button>
                        </div>
                        
                        <div v-if="sku.specs.length === 0" class="alert alert-warning py-2">
                          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                            <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                          </svg>
                          <span class="text-xs">至少需要添加一个规格属性</span>
                        </div>

                        <div
                          v-for="(spec, specIndex) in sku.specs"
                          :key="specIndex"
                          class="flex gap-2 mb-2"
                        >
                          <input
                            v-model="spec.key"
                            type="text"
                            placeholder="属性名（如：颜色）"
                            class="input input-bordered input-sm flex-1"
                            required
                          />
                          <span class="self-center">:</span>
                          <input
                            v-model="spec.value"
                            type="text"
                            placeholder="属性值（如：红色）"
                            class="input input-bordered input-sm flex-1"
                            required
                          />
                          <button
                            v-if="sku.specs.length > 1"
                            type="button"
                            class="btn btn-sm btn-ghost btn-circle"
                            @click="removeSpecAttribute(index, specIndex)"
                          >
                            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-error" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                              <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
                            </svg>
                          </button>
                        </div>
                      </div>

                      <div class="grid grid-cols-2 gap-4">
                        <div class="form-control">
                          <label class="label">
                            <span class="label-text">价格（元） <span class="text-error">*</span></span>
                          </label>
                          <input
                            v-model.number="sku.price"
                            type="number"
                            step="0.01"
                            min="0"
                            placeholder="0.00"
                            class="input input-bordered"
                            required
                          />
                        </div>

                        <div class="form-control">
                          <label class="label">
                            <span class="label-text">库存 <span class="text-error">*</span></span>
                          </label>
                          <input
                            v-model.number="sku.stock"
                            type="number"
                            min="0"
                            placeholder="0"
                            class="input input-bordered"
                            required
                          />
                        </div>
                      </div>

                      <div class="form-control">
                        <label class="label">
                          <span class="label-text">SKU图片URL</span>
                        </label>
                        <input
                          v-model="sku.imageUrl"
                          type="url"
                          placeholder="请输入SKU图片URL（可选）"
                          class="input input-bordered"
                        />
                      </div>
                    </div>
                  </div>
                </template>
              </div>

              <!-- 创建模式：验证码和提交按钮 -->
              <template v-if="!isEditMode">
                <div class="space-y-4 mt-8">
                  <h2 class="text-xl font-semibold">安全验证</h2>
                  
                  <VerificationCodeInput
                    ref="codeInput"
                    v-model="form.code"
                    :email="userEmail"
                    :send-code-fn="sendCode"
                    label="邮箱验证码"
                    :hint="`验证码将发送到您的注册邮箱：${userEmail}`"
                    button-text="发送验证码"
                    :button-class="{ 'btn-primary': true }"
                    @code-send-error="(e) => pushToast(e.message, 'error')"
                  />
                </div>

                <div class="flex gap-4 justify-end pt-4 border-t mt-8">
                  <button
                    type="button"
                    class="btn btn-ghost"
                    @click="goBack"
                  >
                    取消
                  </button>
                  <button
                    type="button"
                    class="btn btn-primary"
                    :disabled="submitting"
                    @click="handleCreateProduct"
                  >
                    <span v-if="submitting" class="loading loading-spinner loading-sm"></span>
                    <span v-else>创建商品</span>
                  </button>
                </div>
              </template>
            </template>
          </div>
        </div>
      </div>
    </div>
    <Footer />

    <!-- 添加新 SKU 弹窗（编辑模式） -->
    <dialog v-if="isEditMode" ref="addSkuModal" class="modal">
      <div class="modal-box max-w-2xl">
        <h3 class="font-bold text-lg mb-4">添加新规格</h3>
        <form @submit.prevent="handleAddSku">
          <div class="space-y-4">
            <div
              v-for="(newSku, index) in newSkus"
              :key="index"
              class="card bg-base-200 p-4"
            >
              <div class="flex items-start justify-between mb-4">
                <h4 class="font-semibold">新规格 {{ index + 1 }}</h4>
                <button
                  v-if="newSkus.length > 1"
                  type="button"
                  class="btn btn-sm btn-ghost btn-circle"
                  @click="removeNewSku(index)"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-error" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>

              <div class="space-y-4">
                <!-- 规格属性 -->
                <div class="form-control">
                  <div class="flex items-center justify-between mb-2">
                    <label class="label">
                      <span class="label-text">规格属性 <span class="text-error">*</span></span>
                    </label>
                    <button
                      type="button"
                      class="btn btn-xs btn-primary"
                      @click="addNewSkuSpecAttribute(index)"
                    >
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-3 w-3 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
                      </svg>
                      添加属性
                    </button>
                  </div>
                  
                  <div
                    v-for="(spec, specIndex) in newSku.specs"
                    :key="specIndex"
                    class="flex gap-2 mb-2"
                  >
                    <input
                      v-model="spec.key"
                      type="text"
                      placeholder="属性名（如：颜色）"
                      class="input input-bordered input-sm flex-1"
                      required
                    />
                    <span class="self-center">:</span>
                    <input
                      v-model="spec.value"
                      type="text"
                      placeholder="属性值（如：红色）"
                      class="input input-bordered input-sm flex-1"
                      required
                    />
                    <button
                      v-if="newSku.specs.length > 1"
                      type="button"
                      class="btn btn-sm btn-ghost btn-circle"
                      @click="removeNewSkuSpecAttribute(index, specIndex)"
                    >
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-error" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
                      </svg>
                    </button>
                  </div>
                </div>

                <div class="grid grid-cols-2 gap-4">
                  <div class="form-control">
                    <label class="label">
                      <span class="label-text">价格（元） <span class="text-error">*</span></span>
                    </label>
                    <input
                      v-model.number="newSku.price"
                      type="number"
                      step="0.01"
                      min="0"
                      placeholder="0.00"
                      class="input input-bordered"
                      required
                    />
                  </div>

                  <div class="form-control">
                    <label class="label">
                      <span class="label-text">库存 <span class="text-error">*</span></span>
                    </label>
                    <input
                      v-model.number="newSku.stock"
                      type="number"
                      min="0"
                      placeholder="0"
                      class="input input-bordered"
                      required
                    />
                  </div>
                </div>

                <div class="form-control">
                  <label class="label">
                    <span class="label-text">SKU图片URL</span>
                  </label>
                  <input
                    v-model="newSku.imageUrl"
                    type="url"
                    placeholder="请输入SKU图片URL（可选）"
                    class="input input-bordered"
                  />
                </div>
              </div>
            </div>

            <button
              type="button"
              class="btn btn-sm btn-outline w-full"
              @click="addNewSku"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
              </svg>
              添加更多规格
            </button>
          </div>

          <div class="modal-action">
            <button type="button" class="btn btn-ghost" @click="closeAddSkuModal">取消</button>
            <button type="submit" class="btn btn-primary" :disabled="submittingAddSku">
              <span v-if="submittingAddSku" class="loading loading-spinner loading-sm"></span>
              <span v-else>确认添加</span>
            </button>
          </div>
        </form>
      </div>
      <form method="dialog" class="modal-backdrop">
        <button @click="closeAddSkuModal">关闭</button>
      </form>
    </dialog>

    <!-- 增加库存弹窗 -->
    <dialog v-if="isEditMode" ref="increaseStockModal" class="modal">
      <div class="modal-box max-w-md">
        <h3 class="font-bold text-lg mb-4">增加库存</h3>
        <div v-if="selectedSkuForStock" class="mb-4">
          <p class="font-semibold mb-2">规格：{{ formatSpec(selectedSkuForStock.spec) || '默认规格' }}</p>
          <p class="text-sm text-base-content/60">当前库存：{{ selectedSkuForStock.stock || 0 }}</p>
        </div>
        <form @submit.prevent="handleIncreaseStock">
          <div class="form-control mb-4">
            <label class="label">
              <span class="label-text">增加数量 <span class="text-error">*</span></span>
            </label>
            <input
              v-model.number="stockUpdateValue"
              type="number"
              step="1"
              min="1"
              placeholder="请输入要增加的数量"
              class="input input-bordered"
              required
            />
            <label class="label">
              <span class="label-text-alt">请输入要增加的库存数量（必须大于0）</span>
            </label>
          </div>
          <VerificationCodeInput
            ref="increaseStockCodeInput"
            v-model="stockUpdateForm.code"
            :email="userEmail"
            :send-code-fn="sendCode"
            label="邮箱验证码"
            @code-send-error="(e) => pushToast(e.message, 'error')"
          />
          <div class="modal-action">
            <button type="button" class="btn btn-ghost" @click="closeIncreaseStockModal">取消</button>
            <button type="submit" class="btn btn-success" :disabled="submittingStockUpdate">
              <span v-if="submittingStockUpdate" class="loading loading-spinner loading-sm"></span>
              <span v-else>确认增加</span>
            </button>
          </div>
        </form>
      </div>
      <form method="dialog" class="modal-backdrop">
        <button @click="closeIncreaseStockModal">关闭</button>
      </form>
    </dialog>

    <!-- 减少库存弹窗 -->
    <dialog v-if="isEditMode" ref="decreaseStockModal" class="modal">
      <div class="modal-box max-w-md">
        <h3 class="font-bold text-lg mb-4">减少库存</h3>
        <div v-if="selectedSkuForStock" class="mb-4">
          <p class="font-semibold mb-2">规格：{{ formatSpec(selectedSkuForStock.spec) || '默认规格' }}</p>
          <p class="text-sm text-base-content/60">当前库存：{{ selectedSkuForStock.stock || 0 }}</p>
        </div>
        <form @submit.prevent="handleDecreaseStock">
          <div class="form-control mb-4">
            <label class="label">
              <span class="label-text">减少数量 <span class="text-error">*</span></span>
            </label>
            <input
              v-model.number="stockUpdateValue"
              type="number"
              step="1"
              min="1"
              :max="selectedSkuForStock?.stock || 0"
              placeholder="请输入要减少的数量"
              class="input input-bordered"
              required
            />
            <label class="label">
              <span class="label-text-alt">请输入要减少的库存数量（不能超过当前库存）</span>
            </label>
          </div>
          <VerificationCodeInput
            ref="decreaseStockCodeInput"
            v-model="stockUpdateForm.code"
            :email="userEmail"
            :send-code-fn="sendCode"
            label="邮箱验证码"
            @code-send-error="(e) => pushToast(e.message, 'error')"
          />
          <div class="modal-action">
            <button type="button" class="btn btn-ghost" @click="closeDecreaseStockModal">取消</button>
            <button type="submit" class="btn btn-warning" :disabled="submittingStockUpdate">
              <span v-if="submittingStockUpdate" class="loading loading-spinner loading-sm"></span>
              <span v-else>确认减少</span>
            </button>
          </div>
        </form>
      </div>
      <form method="dialog" class="modal-backdrop">
        <button @click="closeDecreaseStockModal">关闭</button>
      </form>
    </dialog>

    <!-- 标记无货弹窗 -->
    <dialog v-if="isEditMode" ref="setStockToZeroModal" class="modal">
      <div class="modal-box max-w-md">
        <h3 class="font-bold text-lg mb-4">标记无货</h3>
        <div v-if="selectedSkuForStock" class="mb-4">
          <p class="font-semibold mb-2">规格：{{ formatSpec(selectedSkuForStock.spec) || '默认规格' }}</p>
          <p class="text-sm text-base-content/60">当前库存：{{ selectedSkuForStock.stock || 0 }}</p>
          <div class="alert alert-warning mt-3">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
            <span class="text-sm">标记为无货后，库存将清零，用户将无法下单购买此规格商品</span>
          </div>
        </div>
        <form @submit.prevent="handleSetStockToZero">
          <VerificationCodeInput
            ref="setStockToZeroCodeInput"
            v-model="stockUpdateForm.code"
            :email="userEmail"
            :send-code-fn="sendCode"
            label="邮箱验证码"
            @code-send-error="(e) => pushToast(e.message, 'error')"
          />
          <div class="modal-action">
            <button type="button" class="btn btn-ghost" @click="closeSetStockToZeroModal">取消</button>
            <button type="submit" class="btn btn-error" :disabled="submittingStockUpdate">
              <span v-if="submittingStockUpdate" class="loading loading-spinner loading-sm"></span>
              <span v-else>确认标记无货</span>
            </button>
          </div>
        </form>
      </div>
      <form method="dialog" class="modal-backdrop">
        <button @click="closeSetStockToZeroModal">关闭</button>
      </form>
    </dialog>

    <!-- 修改价格弹窗 -->
    <dialog v-if="isEditMode" ref="updatePriceModal" class="modal">
      <div class="modal-box max-w-md">
        <h3 class="font-bold text-lg mb-4">修改价格</h3>
        <div v-if="selectedSkuForPrice" class="mb-4">
          <p class="font-semibold mb-2">规格：{{ formatSpec(selectedSkuForPrice.spec) || '默认规格' }}</p>
          <p class="text-sm text-base-content/60">当前价格：<span class="font-bold" style="color: rgb(255, 202, 57);">¥{{ selectedSkuForPrice.price || 0 }}</span></p>
        </div>
        <form @submit.prevent="handleUpdatePrice">
          <div class="form-control mb-4">
            <label class="label">
              <span class="label-text">新价格（元） <span class="text-error">*</span></span>
            </label>
            <input
              v-model.number="priceUpdateValue"
              type="number"
              step="0.01"
              min="0"
              placeholder="请输入新价格"
              class="input input-bordered"
              required
            />
            <label class="label">
              <span class="label-text-alt">价格必须大于或等于0</span>
            </label>
          </div>
          <VerificationCodeInput
            ref="updatePriceCodeInput"
            v-model="priceUpdateForm.code"
            :email="userEmail"
            :send-code-fn="sendCode"
            label="邮箱验证码"
            @code-send-error="(e) => pushToast(e.message, 'error')"
          />
          <div class="modal-action">
            <button type="button" class="btn btn-ghost" @click="closeUpdatePriceModal">取消</button>
            <button type="submit" class="btn btn-primary" :disabled="submittingPriceUpdate">
              <span v-if="submittingPriceUpdate" class="loading loading-spinner loading-sm"></span>
              <span v-else>确认修改</span>
            </button>
          </div>
        </form>
      </div>
      <form method="dialog" class="modal-backdrop">
        <button @click="closeUpdatePriceModal">关闭</button>
      </form>
    </dialog>

    <!-- 更新图片弹窗 -->
    <dialog v-if="isEditMode" ref="updateImageModal" class="modal">
      <div class="modal-box max-w-md">
        <h3 class="font-bold text-lg mb-4">更新图片</h3>
        <div v-if="selectedSkuForImage" class="mb-4">
          <p class="font-semibold mb-2">规格：{{ formatSpec(selectedSkuForImage.spec) || '默认规格' }}</p>
        </div>
        <form @submit.prevent="handleUpdateImage">
          <div class="form-control mb-4">
            <label class="label">
              <span class="label-text">SKU图片URL</span>
            </label>
            <input
              v-model="imageUpdateForm.imageUrl"
              type="url"
              placeholder="请输入SKU图片URL"
              class="input input-bordered"
              required
            />
          </div>
          <VerificationCodeInput
            ref="imageCodeInput"
            v-model="imageUpdateForm.code"
            :email="userEmail"
            :send-code-fn="sendCode"
            label="邮箱验证码"
            @code-send-error="(e) => pushToast(e.message, 'error')"
          />
          <div class="modal-action">
            <button type="button" class="btn btn-ghost" @click="closeUpdateImageModal">取消</button>
            <button type="submit" class="btn btn-primary" :disabled="submittingImageUpdate">
              <span v-if="submittingImageUpdate" class="loading loading-spinner loading-sm"></span>
              <span v-else>确认更新</span>
            </button>
          </div>
        </form>
      </div>
      <form method="dialog" class="modal-backdrop">
        <button @click="closeUpdateImageModal">关闭</button>
      </form>
    </dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Header from '../components/Header.vue'
import Footer from '../components/Footer.vue'
import VerificationCodeInput from '../components/VerificationCodeInput.vue'
import { 
  apiCreateProduct, 
  apiUpdateProduct, 
  apiGetProductDetailForEdit,
  apiUpdateSkuImage,
  apiUpdateSkuStock,
  apiUpdateSkuPrice,
  apiAddSku
} from '../api/shopProduct'
import { apiSendVerificationCode } from '../api/auth'
import { apiGetUserSummary } from '../api/user'
import { apiGetCategoryTree, type CategoryTreeVO } from '../api/category'
import type { SkuVO } from '../api/generated/models'

const route = useRoute()
const router = useRouter()

const shopId = ref<number>(Number(route.params.shopId))
const productId = ref<number | null>(route.params.productId ? Number(route.params.productId) : null)
const isEditMode = computed(() => !!productId.value)
const userEmail = ref<string>('')
const codeInput = ref<InstanceType<typeof VerificationCodeInput> | null>(null)
const increaseStockCodeInput = ref<InstanceType<typeof VerificationCodeInput> | null>(null)
const decreaseStockCodeInput = ref<InstanceType<typeof VerificationCodeInput> | null>(null)
const setStockToZeroCodeInput = ref<InstanceType<typeof VerificationCodeInput> | null>(null)
const updatePriceCodeInput = ref<InstanceType<typeof VerificationCodeInput> | null>(null)
const imageCodeInput = ref<InstanceType<typeof VerificationCodeInput> | null>(null)
const submitting = ref(false)
const submittingProductInfo = ref(false)
const submittingAddSku = ref(false)
const submittingStockUpdate = ref(false)
const submittingPriceUpdate = ref(false)
const submittingImageUpdate = ref(false)
const categories = ref<CategoryTreeVO[]>([])
const flattenedCategories = ref<Array<{ categoryId: number; name: string; level: number }>>([])
const loadingCategories = ref(false)
const loadingProduct = ref(false)

// 商品基本信息表单
const form = ref({
  name: '',
  categoryId: null as number | null,
  description: '',
  imageUrl: '',
  code: '',
  skus: [
    {
      specs: [{ key: '', value: '' }] as Array<{ key: string; value: string }>,
      price: 0,
      stock: 0,
      imageUrl: ''
    }
  ] as Array<{ specs: Array<{ key: string; value: string }>; price: number; stock: number; imageUrl: string }>
})

// 编辑模式：已有 SKU 列表
const existingSkus = ref<Array<SkuVO & { stock: number }>>([])

// 编辑模式：新添加的 SKU（待提交）
const newSkus = ref<Array<{
  specs: Array<{ key: string; value: string }>
  price: number
  stock: number
  imageUrl: string
}>>([])

// 操作状态跟踪
const updatingStock = ref<Record<number, boolean>>({})
const updatingPrice = ref<Record<number, boolean>>({})
const updatingImage = ref<Record<number, boolean>>({})

// 弹窗引用
const addSkuModal = ref<HTMLDialogElement | null>(null)
const increaseStockModal = ref<HTMLDialogElement | null>(null)
const decreaseStockModal = ref<HTMLDialogElement | null>(null)
const setStockToZeroModal = ref<HTMLDialogElement | null>(null)
const updatePriceModal = ref<HTMLDialogElement | null>(null)
const updateImageModal = ref<HTMLDialogElement | null>(null)

// 选中的 SKU（用于弹窗操作）
const selectedSkuForStock = ref<(SkuVO & { stock: number }) | null>(null)
const selectedSkuForPrice = ref<(SkuVO & { stock: number }) | null>(null)
const selectedSkuForImage = ref<(SkuVO & { stock: number }) | null>(null)

// 库存更新表单
const stockUpdateValue = ref<number>(0)
const stockUpdateForm = ref({
  code: ''
})

// 价格更新表单
const priceUpdateValue = ref<number>(0)
const priceUpdateForm = ref({
  code: ''
})

// 图片更新表单
const imageUpdateForm = ref({
  imageUrl: '',
  code: ''
})

// 格式化规格显示
function formatSpec(specJson?: string): string {
  if (!specJson) return ''
  try {
    const parsed = JSON.parse(specJson)
    if (typeof parsed === 'object' && parsed !== null) {
      return Object.entries(parsed)
        .map(([key, value]) => `${key}: ${value}`)
        .join(', ')
    }
  } catch (e) {
    console.error('Failed to parse spec JSON:', e)
  }
  return specJson
}

// 创建模式：添加 SKU
function addSku() {
  form.value.skus.push({
    specs: [{ key: '', value: '' }],
    price: 0,
    stock: 0,
    imageUrl: ''
  })
}

// 创建模式：添加规格属性
function addSpecAttribute(skuIndex: number) {
  const sku = form.value.skus[skuIndex]
  if (sku) {
    sku.specs.push({ key: '', value: '' })
  }
}

// 创建模式：删除规格属性
function removeSpecAttribute(skuIndex: number, specIndex: number) {
  const sku = form.value.skus[skuIndex]
  if (sku && sku.specs.length > 1) {
    sku.specs.splice(specIndex, 1)
  }
}

// 创建模式：删除 SKU
function removeSku(index: number) {
  if (form.value.skus.length > 1) {
    form.value.skus.splice(index, 1)
  }
}

// 编辑模式：添加新 SKU（弹窗中）
function addNewSku() {
  newSkus.value.push({
    specs: [{ key: '', value: '' }],
    price: 0,
    stock: 0,
    imageUrl: ''
  })
}

// 编辑模式：添加新 SKU 的规格属性
function addNewSkuSpecAttribute(skuIndex: number) {
  const sku = newSkus.value[skuIndex]
  if (sku) {
    sku.specs.push({ key: '', value: '' })
  }
}

// 编辑模式：删除新 SKU 的规格属性
function removeNewSkuSpecAttribute(skuIndex: number, specIndex: number) {
  const sku = newSkus.value[skuIndex]
  if (sku && sku.specs.length > 1) {
    sku.specs.splice(specIndex, 1)
  }
}

// 编辑模式：删除新 SKU
function removeNewSku(index: number) {
  if (newSkus.value.length > 1) {
    newSkus.value.splice(index, 1)
  }
}

// 将规格属性转换为JSON字符串
function buildSpecJson(specs: Array<{ key: string; value: string }>): string {
  const specObj: Record<string, string> = {}
  for (const spec of specs) {
    if (spec.key && spec.value) {
      specObj[spec.key.trim()] = spec.value.trim()
    }
  }
  return JSON.stringify(specObj)
}

// 将JSON字符串转换为规格属性数组（保留以便将来使用）
// eslint-disable-next-line @typescript-eslint/no-unused-vars
// function parseSpecJson(specJson?: string): Array<{ key: string; value: string }> {
//   if (!specJson) return [{ key: '', value: '' }]
//   try {
//     const parsed = JSON.parse(specJson)
//     if (typeof parsed === 'object' && parsed !== null) {
//       return Object.entries(parsed).map(([key, value]) => ({
//         key: key,
//         value: String(value)
//       }))
//     }
//   } catch (e) {
//     console.error('Failed to parse spec JSON:', e)
//   }
//   return [{ key: '', value: '' }]
// }

// 发送验证码
async function sendCode(emailAddr: string): Promise<void> {
  if (!emailAddr) {
    throw new Error('无法获取用户邮箱')
  }
  await apiSendVerificationCode({ contact: emailAddr }, false)
  pushToast('验证码已发送，请查收邮箱', 'success')
}

// 更新商品基本信息（编辑模式）
async function handleSubmitProductInfo() {
  if (!form.value.categoryId) {
    pushToast('请选择商品分类', 'error')
    return
  }

  submittingProductInfo.value = true
  try {
    if (productId.value) {
      await apiUpdateProduct(productId.value, {
        name: form.value.name,
        categoryId: form.value.categoryId,
        description: form.value.description,
        imageUrl: form.value.imageUrl
      })
      pushToast('商品信息更新成功', 'success')
    }
  } catch (e: any) {
    pushToast(`更新失败：${e.message || e}`, 'error')
  } finally {
    submittingProductInfo.value = false
  }
}

// 创建商品（创建模式）
async function handleCreateProduct() {
  // 验证分类
  if (!form.value.categoryId) {
    pushToast('请选择商品分类', 'error')
    return
  }

  // 验证SKU
  if (form.value.skus.length === 0) {
    pushToast('至少需要添加一个SKU', 'error')
    return
  }

  // 验证每个SKU
  for (let i = 0; i < form.value.skus.length; i++) {
    const sku = form.value.skus[i]
    if (!sku) {
      pushToast(`规格 ${i + 1} 数据异常`, 'error')
      return
    }
    
    // 验证规格属性
    if (!sku.specs || sku.specs.length === 0) {
      pushToast(`规格 ${i + 1} 至少需要添加一个属性`, 'error')
      return
    }
    
    // 验证每个属性都有键和值
    for (let j = 0; j < sku.specs.length; j++) {
      const spec = sku.specs[j]
      if (!spec) {
        pushToast(`规格 ${i + 1} 的第 ${j + 1} 个属性数据异常`, 'error')
        return
      }
      if (!spec.key || !spec.key.trim()) {
        pushToast(`规格 ${i + 1} 的第 ${j + 1} 个属性名不能为空`, 'error')
        return
      }
      if (!spec.value || !spec.value.trim()) {
        pushToast(`规格 ${i + 1} 的第 ${j + 1} 个属性值不能为空`, 'error')
        return
      }
    }
    
    if (!sku.price || sku.price <= 0) {
      pushToast(`规格 ${i + 1} 的价格必须大于0`, 'error')
      return
    }
    if (sku.stock === undefined || sku.stock < 0) {
      pushToast(`规格 ${i + 1} 的库存不能为负数`, 'error')
      return
    }
  }

  submitting.value = true
  try {
    await apiCreateProduct(shopId.value, {
      name: form.value.name,
      categoryId: form.value.categoryId!,
      description: form.value.description,
      imageUrl: form.value.imageUrl,
      code: form.value.code,
      skus: form.value.skus.map(sku => ({
        spec: buildSpecJson(sku.specs),
        price: sku.price,
        stock: sku.stock,
        imageUrl: sku.imageUrl || undefined
      }))
    })
    pushToast('商品创建成功', 'success')
    // 验证成功，清除倒计时
    if (codeInput.value) {
      codeInput.value.clearCountdown()
    }
    // 返回店铺详情页
    router.push(`/shop/${shopId.value}?mode=management`)
  } catch (e: any) {
    pushToast(`创建失败：${e.message || e}`, 'error')
  } finally {
    submitting.value = false
  }
}

// 编辑模式：打开添加 SKU 弹窗
function openAddSkuModal() {
  newSkus.value = [{
    specs: [{ key: '', value: '' }],
    price: 0,
    stock: 0,
    imageUrl: ''
  }]
  addSkuModal.value?.showModal()
}

// 编辑模式：关闭添加 SKU 弹窗
function closeAddSkuModal() {
  addSkuModal.value?.close()
  newSkus.value = []
}

// 编辑模式：添加新 SKU
async function handleAddSku() {
  if (!productId.value) return

  // 验证新 SKU
  if (newSkus.value.length === 0) {
    pushToast('至少需要添加一个SKU', 'error')
    return
  }

  // 验证每个新 SKU
  for (let i = 0; i < newSkus.value.length; i++) {
    const sku = newSkus.value[i]
    if (!sku) {
      pushToast(`新规格 ${i + 1} 数据异常`, 'error')
      return
    }
    
    // 验证规格属性
    if (!sku.specs || sku.specs.length === 0) {
      pushToast(`新规格 ${i + 1} 至少需要添加一个属性`, 'error')
      return
    }
    
    // 验证每个属性都有键和值
    for (let j = 0; j < sku.specs.length; j++) {
      const spec = sku.specs[j]
      if (!spec) {
        pushToast(`新规格 ${i + 1} 的第 ${j + 1} 个属性数据异常`, 'error')
        return
      }
      if (!spec.key || !spec.key.trim()) {
        pushToast(`新规格 ${i + 1} 的第 ${j + 1} 个属性名不能为空`, 'error')
        return
      }
      if (!spec.value || !spec.value.trim()) {
        pushToast(`新规格 ${i + 1} 的第 ${j + 1} 个属性值不能为空`, 'error')
        return
      }
    }
    
    if (!sku.price || sku.price <= 0) {
      pushToast(`新规格 ${i + 1} 的价格必须大于0`, 'error')
      return
    }
    if (sku.stock === undefined || sku.stock < 0) {
      pushToast(`新规格 ${i + 1} 的库存不能为负数`, 'error')
      return
    }
  }

  submittingAddSku.value = true
  try {
    const addedSkus = await apiAddSku(productId.value, {
      skus: newSkus.value.map(sku => ({
        spec: buildSpecJson(sku.specs),
        price: sku.price,
        stock: sku.stock,
        imageUrl: sku.imageUrl || undefined
      })),
      code: form.value.code
    })
    
    pushToast('新规格添加成功', 'success')
    
    // 将新添加的 SKU 添加到已有 SKU 列表
    existingSkus.value.push(...addedSkus.map(sku => ({ ...sku, stock: sku.stock || 0 })))
    
    // 清除验证码倒计时
    if (codeInput.value) {
      codeInput.value.clearCountdown()
    }
    form.value.code = ''
    
    closeAddSkuModal()
  } catch (e: any) {
    pushToast(`添加失败：${e.message || e}`, 'error')
  } finally {
    submittingAddSku.value = false
  }
}

// 编辑模式：打开增加库存弹窗
function openIncreaseStockModal(sku: SkuVO & { stock: number }) {
  if (!productId.value || !sku.skuId) return

  selectedSkuForStock.value = sku
  stockUpdateValue.value = 1
  stockUpdateForm.value.code = ''
  increaseStockModal.value?.showModal()
}

// 编辑模式：确认增加库存
async function handleIncreaseStock() {
  if (!productId.value || !selectedSkuForStock.value?.skuId) return

  if (!stockUpdateValue.value || stockUpdateValue.value <= 0) {
    pushToast('增加数量必须大于0', 'error')
    return
  }

  submittingStockUpdate.value = true
  try {
    const updatedSku = await apiUpdateSkuStock(
      productId.value,
      selectedSkuForStock.value.skuId,
      {
        delta: stockUpdateValue.value,
        code: stockUpdateForm.value.code
      }
    )

    // 更新本地 SKU 数据
    const index = existingSkus.value.findIndex(s => s.skuId === selectedSkuForStock.value?.skuId)
    if (index !== -1) {
      existingSkus.value[index] = { ...updatedSku, stock: updatedSku.stock || 0 }
    }

    pushToast('库存增加成功', 'success')
    
    // 清除验证码倒计时
    if (increaseStockCodeInput.value) {
      increaseStockCodeInput.value.clearCountdown()
    }
    stockUpdateForm.value.code = ''
    stockUpdateValue.value = 0
    
    closeIncreaseStockModal()
  } catch (e: any) {
    pushToast(`增加失败：${e.message || e}`, 'error')
  } finally {
    submittingStockUpdate.value = false
  }
}

// 编辑模式：关闭增加库存弹窗
function closeIncreaseStockModal() {
  increaseStockModal.value?.close()
  selectedSkuForStock.value = null
  stockUpdateForm.value.code = ''
  stockUpdateValue.value = 0
}

// 编辑模式：打开减少库存弹窗
function openDecreaseStockModal(sku: SkuVO & { stock: number }) {
  if (!productId.value || !sku.skuId) return

  selectedSkuForStock.value = sku
  stockUpdateValue.value = 1
  stockUpdateForm.value.code = ''
  decreaseStockModal.value?.showModal()
}

// 编辑模式：确认减少库存
async function handleDecreaseStock() {
  if (!productId.value || !selectedSkuForStock.value?.skuId) return

  if (!stockUpdateValue.value || stockUpdateValue.value <= 0) {
    pushToast('减少数量必须大于0', 'error')
    return
  }

  const currentStock = selectedSkuForStock.value.stock || 0
  if (stockUpdateValue.value > currentStock) {
    pushToast(`减少数量不能超过当前库存（${currentStock}）`, 'error')
    return
  }

  submittingStockUpdate.value = true
  try {
    const updatedSku = await apiUpdateSkuStock(
      productId.value,
      selectedSkuForStock.value.skuId,
      {
        delta: -stockUpdateValue.value,
        code: stockUpdateForm.value.code
      }
    )

    // 更新本地 SKU 数据
    const index = existingSkus.value.findIndex(s => s.skuId === selectedSkuForStock.value?.skuId)
    if (index !== -1) {
      existingSkus.value[index] = { ...updatedSku, stock: updatedSku.stock || 0 }
    }

    pushToast('库存减少成功', 'success')
    
    // 清除验证码倒计时
    if (decreaseStockCodeInput.value) {
      decreaseStockCodeInput.value.clearCountdown()
    }
    stockUpdateForm.value.code = ''
    stockUpdateValue.value = 0
    
    closeDecreaseStockModal()
  } catch (e: any) {
    pushToast(`减少失败：${e.message || e}`, 'error')
  } finally {
    submittingStockUpdate.value = false
  }
}

// 编辑模式：关闭减少库存弹窗
function closeDecreaseStockModal() {
  decreaseStockModal.value?.close()
  selectedSkuForStock.value = null
  stockUpdateForm.value.code = ''
  stockUpdateValue.value = 0
}

// 编辑模式：打开标记无货弹窗
function openSetStockToZeroModal(sku: SkuVO & { stock: number }) {
  if (!productId.value || !sku.skuId) return

  selectedSkuForStock.value = sku
  stockUpdateForm.value.code = ''
  setStockToZeroModal.value?.showModal()
}

// 编辑模式：确认标记无货
async function handleSetStockToZero() {
  if (!productId.value || !selectedSkuForStock.value?.skuId) return

  submittingStockUpdate.value = true
  try {
    const updatedSku = await apiUpdateSkuStock(
      productId.value,
      selectedSkuForStock.value.skuId,
      {
        absoluteStock: 0,
        code: stockUpdateForm.value.code
      }
    )

    // 更新本地 SKU 数据
    const index = existingSkus.value.findIndex(s => s.skuId === selectedSkuForStock.value?.skuId)
    if (index !== -1) {
      existingSkus.value[index] = { ...updatedSku, stock: 0 }
    }

    pushToast('已标记为无货', 'success')
    
    // 清除验证码倒计时
    if (setStockToZeroCodeInput.value) {
      setStockToZeroCodeInput.value.clearCountdown()
    }
    stockUpdateForm.value.code = ''
    
    closeSetStockToZeroModal()
  } catch (e: any) {
    pushToast(`标记失败：${e.message || e}`, 'error')
  } finally {
    submittingStockUpdate.value = false
  }
}

// 编辑模式：关闭标记无货弹窗
function closeSetStockToZeroModal() {
  setStockToZeroModal.value?.close()
  selectedSkuForStock.value = null
  stockUpdateForm.value.code = ''
}

// 编辑模式：打开修改价格弹窗
function openUpdatePriceModal(sku: SkuVO & { stock: number }) {
  if (!productId.value || !sku.skuId) return

  selectedSkuForPrice.value = sku
  priceUpdateValue.value = sku.price || 0
  priceUpdateForm.value.code = ''
  updatePriceModal.value?.showModal()
}

// 编辑模式：确认修改价格
async function handleUpdatePrice() {
  if (!productId.value || !selectedSkuForPrice.value?.skuId) return

  if (priceUpdateValue.value === undefined || priceUpdateValue.value < 0) {
    pushToast('价格必须大于或等于0', 'error')
    return
  }

  submittingPriceUpdate.value = true
  try {
    const updatedSku = await apiUpdateSkuPrice(
      productId.value,
      selectedSkuForPrice.value.skuId,
      {
        price: priceUpdateValue.value,
        code: priceUpdateForm.value.code
      }
    )

    // 更新本地 SKU 数据
    const index = existingSkus.value.findIndex(s => s.skuId === selectedSkuForPrice.value?.skuId)
    if (index !== -1 && existingSkus.value[index]) {
      existingSkus.value[index] = { 
        ...existingSkus.value[index], 
        ...updatedSku,
        stock: existingSkus.value[index].stock || 0
      }
    }

    pushToast('价格修改成功', 'success')
    
    // 清除验证码倒计时
    if (updatePriceCodeInput.value) {
      updatePriceCodeInput.value.clearCountdown()
    }
    priceUpdateForm.value.code = ''
    priceUpdateValue.value = 0
    
    closeUpdatePriceModal()
  } catch (e: any) {
    pushToast(`修改失败：${e.message || e}`, 'error')
  } finally {
    submittingPriceUpdate.value = false
  }
}

// 编辑模式：关闭修改价格弹窗
function closeUpdatePriceModal() {
  updatePriceModal.value?.close()
  selectedSkuForPrice.value = null
  priceUpdateForm.value.code = ''
  priceUpdateValue.value = 0
}

// 编辑模式：更新 SKU 图片
async function updateSkuImage(sku: SkuVO & { stock: number }) {
  if (!productId.value || !sku.skuId) return

  selectedSkuForImage.value = sku
  imageUpdateForm.value.imageUrl = sku.imageUrl || ''
  updateImageModal.value?.showModal()
}

// 编辑模式：确认更新图片
async function handleUpdateImage() {
  if (!productId.value || !selectedSkuForImage.value?.skuId) return

  if (!imageUpdateForm.value.imageUrl) {
    pushToast('请输入图片URL', 'error')
    return
  }

  submittingImageUpdate.value = true
  try {
    const updatedSku = await apiUpdateSkuImage(
      productId.value,
      selectedSkuForImage.value.skuId,
      {
        imageUrl: imageUpdateForm.value.imageUrl,
        code: imageUpdateForm.value.code
      }
    )

    // 更新本地 SKU 数据
    const index = existingSkus.value.findIndex(s => s.skuId === selectedSkuForImage.value?.skuId)
    if (index !== -1 && existingSkus.value[index]) {
      existingSkus.value[index] = { 
        ...existingSkus.value[index], 
        ...updatedSku,
        stock: existingSkus.value[index].stock || 0
      }
    }

    pushToast('图片更新成功', 'success')
    
    // 清除验证码倒计时
    if (imageCodeInput.value) {
      imageCodeInput.value.clearCountdown()
    }
    imageUpdateForm.value.code = ''
    imageUpdateForm.value.imageUrl = ''
    
    closeUpdateImageModal()
  } catch (e: any) {
    pushToast(`更新失败：${e.message || e}`, 'error')
  } finally {
    submittingImageUpdate.value = false
  }
}

// 编辑模式：关闭更新图片弹窗
function closeUpdateImageModal() {
  updateImageModal.value?.close()
  selectedSkuForImage.value = null
  imageUpdateForm.value.code = ''
  imageUpdateForm.value.imageUrl = ''
}

function goBack() {
  router.back()
}

async function loadUserEmail() {
  try {
    const summary = await apiGetUserSummary()
    userEmail.value = summary.email || localStorage.getItem('user_email') || ''
    if (summary.email) {
      localStorage.setItem('user_email', summary.email)
    }
  } catch (e: any) {
    console.error('Failed to load user email:', e)
    userEmail.value = localStorage.getItem('user_email') || ''
  }
}

function pushToast(message: string, type: 'success' | 'error' | 'info' = 'success') {
  const container = document.getElementById('global-toast')
  if (!container) return
  const el = document.createElement('div')
  el.className = `alert ${type === 'success' ? 'alert-success' : type === 'error' ? 'alert-error' : 'alert-info'}`
  el.innerHTML = `<span>${message}</span>`
  container.appendChild(el)
  setTimeout(() => el.remove(), 2500)
}

// 递归扁平化分类树
function flattenCategoryTree(
  tree: CategoryTreeVO[],
  level: number = 0
): Array<{ categoryId: number; name: string; level: number }> {
  const result: Array<{ categoryId: number; name: string; level: number }> = []
  
  for (const category of tree) {
    if (category.categoryId !== undefined && category.name) {
      result.push({
        categoryId: category.categoryId,
        name: category.name,
        level: level
      })
    }
    
    // 递归处理子分类
    if (category.children && category.children.length > 0) {
      result.push(...flattenCategoryTree(category.children, level + 1))
    }
  }
  
  return result
}

async function loadCategories() {
  loadingCategories.value = true
  try {
    categories.value = await apiGetCategoryTree()
    // 扁平化分类树以便在下拉框中显示层级结构
    flattenedCategories.value = flattenCategoryTree(categories.value)
  } catch (e: any) {
    pushToast(`加载分类列表失败：${e.message || e}`, 'error')
    console.error('Failed to load categories:', e)
  } finally {
    loadingCategories.value = false
  }
}

async function loadProductData() {
  if (!isEditMode.value || !productId.value) return

  loadingProduct.value = true
  try {
    const productDetail = await apiGetProductDetailForEdit(productId.value)
    
    // 填充表单
    form.value.name = productDetail.name || ''
    form.value.description = productDetail.description || ''
    form.value.imageUrl = productDetail.imageUrl || ''
    form.value.categoryId = productDetail.categoryId || null
    
    // 填充已有 SKU 数据
    if (productDetail.skus && productDetail.skus.length > 0) {
      existingSkus.value = productDetail.skus.map(sku => ({
        ...sku,
        stock: sku.stock || 0
      }))
    }
  } catch (e: any) {
    pushToast(`加载商品数据失败：${e.message || e}`, 'error')
    console.error('Failed to load product data:', e)
    // 加载失败时返回上一页
    router.back()
  } finally {
    loadingProduct.value = false
  }
}

onMounted(async () => {
  await Promise.all([loadUserEmail(), loadCategories()])
  if (isEditMode.value) {
    await loadProductData()
  }
})
</script>

<style scoped>
</style>
