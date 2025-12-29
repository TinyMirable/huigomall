/**
 * 前端密码加密工具
 * 使用SHA-256对密码进行哈希，减少明文传输的风险
 * 注意：这不能替代HTTPS，真正的安全应该依赖HTTPS
 */

/**
 * 使用SHA-256对密码进行哈希
 * @param password 原始密码
 * @returns 哈希后的密码（十六进制字符串）
 */
export async function hashPassword(password: string): Promise<string> {
  // 将密码转换为Uint8Array
  const encoder = new TextEncoder()
  const data = encoder.encode(password)
  
  // 使用Web Crypto API进行SHA-256哈希
  const hashBuffer = await crypto.subtle.digest('SHA-256', data)
  
  // 将哈希结果转换为十六进制字符串
  const hashArray = Array.from(new Uint8Array(hashBuffer))
  const hashHex = hashArray.map(b => b.toString(16).padStart(2, '0')).join('')
  
  return hashHex
}




