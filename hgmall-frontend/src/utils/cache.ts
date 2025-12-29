// code by 陈诗懿(csy)
/**
 * 本地缓存工具
 * 用于缓存消息、文档等数据，避免重复请求
 */

// 缓存项接口
interface CacheItem<T> {
  data: T
  timestamp: number
  expiresIn?: number  // 过期时间（毫秒），可选
}

// 缓存管理器
class CacheManager {
  private messageCache = new Map<string, CacheItem<any[]>>()  // sessionId -> messages
  private chunkCache = new Map<string, CacheItem<any>>()  // chunkId -> chunkDetail
  private fullDocCache = new Map<string, CacheItem<any>>()  // `${fullDocId}_${kbId}` -> fullDoc
  
  // 默认缓存过期时间（1小时）
  private readonly DEFAULT_EXPIRES_IN = 60 * 60 * 1000
  
  /**
   * 检查缓存项是否过期
   */
  private isExpired<T>(item: CacheItem<T>): boolean {
    if (!item.expiresIn) return false
    return Date.now() - item.timestamp > item.expiresIn
  }
  
  /**
   * 获取消息缓存
   */
  getMessages(sessionId: string): any[] | null {
    const cached = this.messageCache.get(sessionId)
    if (!cached) return null
    if (this.isExpired(cached)) {
      this.messageCache.delete(sessionId)
      return null
    }
    return cached.data
  }
  
  /**
   * 设置消息缓存
   */
  setMessages(sessionId: string, messages: any[], expiresIn?: number): void {
    this.messageCache.set(sessionId, {
      data: messages,
      timestamp: Date.now(),
      expiresIn: expiresIn || this.DEFAULT_EXPIRES_IN,
    })
  }
  
  /**
   * 更新消息缓存（添加新消息）
   */
  appendMessage(sessionId: string, message: any): void {
    const cached = this.messageCache.get(sessionId)
    if (cached && !this.isExpired(cached)) {
      cached.data.push(message)
      cached.timestamp = Date.now()  // 更新时间戳
    }
  }
  
  /**
   * 更新消息缓存（替换所有消息）
   */
  updateMessages(sessionId: string, messages: any[]): void {
    const cached = this.messageCache.get(sessionId)
    if (cached) {
      cached.data = messages
      cached.timestamp = Date.now()
    } else {
      this.setMessages(sessionId, messages)
    }
  }
  
  /**
   * 清除消息缓存
   */
  clearMessages(sessionId?: string): void {
    if (sessionId) {
      this.messageCache.delete(sessionId)
    } else {
      this.messageCache.clear()
    }
  }
  
  /**
   * 获取chunk缓存
   */
  getChunk(chunkId: string): any | null {
    const cached = this.chunkCache.get(chunkId)
    if (!cached) return null
    if (this.isExpired(cached)) {
      this.chunkCache.delete(chunkId)
      return null
    }
    return cached.data
  }
  
  /**
   * 批量获取chunks缓存
   */
  getChunks(chunkIds: string[]): Map<string, any> {
    const result = new Map<string, any>()
    chunkIds.forEach(id => {
      const cached = this.getChunk(id)
      if (cached) {
        result.set(id, cached)
      }
    })
    return result
  }
  
  /**
   * 设置chunk缓存
   */
  setChunk(chunkId: string, chunk: any, expiresIn?: number): void {
    this.chunkCache.set(chunkId, {
      data: chunk,
      timestamp: Date.now(),
      expiresIn: expiresIn || this.DEFAULT_EXPIRES_IN,
    })
  }
  
  /**
   * 批量设置chunks缓存
   */
  setChunks(chunks: any[]): void {
    chunks.forEach(chunk => {
      if (chunk.id) {
        this.setChunk(chunk.id, chunk)
      }
    })
  }
  
  /**
   * 清除chunk缓存
   */
  clearChunk(chunkId?: string): void {
    if (chunkId) {
      this.chunkCache.delete(chunkId)
    } else {
      this.chunkCache.clear()
    }
  }
  
  /**
   * 获取完整文档缓存
   */
  getFullDoc(fullDocId: string, kbId: string): any | null {
    const key = `${fullDocId}_${kbId}`
    const cached = this.fullDocCache.get(key)
    if (!cached) return null
    if (this.isExpired(cached)) {
      this.fullDocCache.delete(key)
      return null
    }
    return cached.data
  }
  
  /**
   * 设置完整文档缓存
   */
  setFullDoc(fullDocId: string, kbId: string, doc: any, expiresIn?: number): void {
    const key = `${fullDocId}_${kbId}`
    this.fullDocCache.set(key, {
      data: doc,
      timestamp: Date.now(),
      expiresIn: expiresIn || this.DEFAULT_EXPIRES_IN,
    })
  }
  
  /**
   * 清除完整文档缓存
   */
  clearFullDoc(fullDocId?: string, kbId?: string): void {
    if (fullDocId && kbId) {
      const key = `${fullDocId}_${kbId}`
      this.fullDocCache.delete(key)
    } else {
      this.fullDocCache.clear()
    }
  }
  
  /**
   * 清除所有缓存
   */
  clearAll(): void {
    this.messageCache.clear()
    this.chunkCache.clear()
    this.fullDocCache.clear()
  }
  
  /**
   * 清理过期缓存
   */
  cleanup(): void {
    // 清理过期的消息缓存
    for (const [key, item] of this.messageCache.entries()) {
      if (this.isExpired(item)) {
        this.messageCache.delete(key)
      }
    }
    
    // 清理过期的chunk缓存
    for (const [key, item] of this.chunkCache.entries()) {
      if (this.isExpired(item)) {
        this.chunkCache.delete(key)
      }
    }
    
    // 清理过期的完整文档缓存
    for (const [key, item] of this.fullDocCache.entries()) {
      if (this.isExpired(item)) {
        this.fullDocCache.delete(key)
      }
    }
  }
}

// 导出单例
export const cacheManager = new CacheManager()

// 定期清理过期缓存（每5分钟）
if (typeof window !== 'undefined') {
  setInterval(() => {
    cacheManager.cleanup()
  }, 5 * 60 * 1000)
}

