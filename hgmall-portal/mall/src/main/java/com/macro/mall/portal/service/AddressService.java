package com.macro.mall.portal.service;

import com.macro.mall.common.domain.AddressVO;
import com.macro.mall.mapper.AddressMapper;
import com.macro.mall.model.Address;
import com.macro.mall.model.AddressExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 地址服务
 */
@Service
public class AddressService {

    @Autowired
    private AddressMapper addressMapper;

    /**
     * 获取用户的所有地址列表
     * 
     * @param userId 用户ID
     * @return 地址列表
     */
    public List<AddressVO> getAddressList(Long userId) {
        AddressExample example = new AddressExample();
        example.createCriteria().andUserIdEqualTo(userId);
        example.setOrderByClause("is_default DESC, create_time DESC");
        List<Address> addresses = addressMapper.selectByExample(example);
        
        return addresses.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 获取单个地址详情
     * 
     * @param addressId 地址ID
     * @param userId 用户ID（用于验证地址归属）
     * @return 地址信息，如果不存在或不属于该用户返回null
     */
    public AddressVO getAddress(Long addressId, Long userId) {
        Address address = addressMapper.selectByPrimaryKey(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            return null;
        }
        return convertToVO(address);
    }

    /**
     * 创建新地址
     * 
     * @param userId 用户ID
     * @param detail 详细地址
     * @param receiverName 收货人姓名
     * @param receiverPhone 收货人电话
     * @param isDefault 是否设置为默认地址
     * @return 创建的地址信息
     * @throws RuntimeException 创建失败时抛出异常
     */
    public AddressVO createAddress(Long userId, String detail, String receiverName, 
                                   String receiverPhone, Boolean isDefault) {
        // 如果设置为默认地址，先取消其他默认地址
        if (Boolean.TRUE.equals(isDefault)) {
            clearDefaultAddress(userId);
        }

        // 创建新地址
        Address address = new Address();
        address.setUserId(userId);
        address.setDetail(detail);
        address.setReceiverName(receiverName);
        address.setReceiverPhone(receiverPhone);
        address.setIsDefault(Boolean.TRUE.equals(isDefault));
        address.setCreateTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());
        
        addressMapper.insertSelective(address);
        
        return convertToVO(address);
    }

    /**
     * 更新地址
     * 
     * @param addressId 地址ID
     * @param userId 用户ID（用于验证地址归属）
     * @param detail 详细地址
     * @param receiverName 收货人姓名
     * @param receiverPhone 收货人电话
     * @param isDefault 是否设置为默认地址
     * @return 更新后的地址信息
     * @throws RuntimeException 更新失败时抛出异常
     */
    public AddressVO updateAddress(Long addressId, Long userId, String detail, 
                                   String receiverName, String receiverPhone, Boolean isDefault) {
        // 验证地址是否存在且属于该用户
        Address address = addressMapper.selectByPrimaryKey(addressId);
        if (address == null) {
            throw new RuntimeException("地址不存在");
        }
        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问该地址");
        }

        // 如果设置为默认地址，先取消其他默认地址
        if (Boolean.TRUE.equals(isDefault) && !Boolean.TRUE.equals(address.getIsDefault())) {
            clearDefaultAddress(userId);
        }

        // 更新地址信息
        address.setDetail(detail);
        address.setReceiverName(receiverName);
        address.setReceiverPhone(receiverPhone);
        address.setIsDefault(Boolean.TRUE.equals(isDefault));
        address.setUpdateTime(LocalDateTime.now());
        
        addressMapper.updateByPrimaryKeySelective(address);
        
        return convertToVO(address);
    }

    /**
     * 删除地址
     * 
     * @param addressId 地址ID
     * @param userId 用户ID（用于验证地址归属）
     * @throws RuntimeException 删除失败时抛出异常
     */
    public void deleteAddress(Long addressId, Long userId) {
        // 验证地址是否存在且属于该用户
        Address address = addressMapper.selectByPrimaryKey(addressId);
        if (address == null) {
            throw new RuntimeException("地址不存在");
        }
        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除该地址");
        }

        // 删除地址
        addressMapper.deleteByPrimaryKey(addressId);
    }

    /**
     * 设置默认地址
     * 
     * @param addressId 地址ID
     * @param userId 用户ID（用于验证地址归属）
     * @return 更新后的地址信息
     * @throws RuntimeException 设置失败时抛出异常
     */
    public AddressVO setDefaultAddress(Long addressId, Long userId) {
        // 验证地址是否存在且属于该用户
        Address address = addressMapper.selectByPrimaryKey(addressId);
        if (address == null) {
            throw new RuntimeException("地址不存在");
        }
        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问该地址");
        }

        // 如果已经是默认地址，直接返回
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            return convertToVO(address);
        }

        // 取消其他默认地址
        clearDefaultAddress(userId);

        // 设置当前地址为默认地址
        address.setIsDefault(true);
        address.setUpdateTime(LocalDateTime.now());
        addressMapper.updateByPrimaryKeySelective(address);

        return convertToVO(address);
    }

    /**
     * 清除用户的所有默认地址
     * 
     * @param userId 用户ID
     */
    private void clearDefaultAddress(Long userId) {
        AddressExample example = new AddressExample();
        example.createCriteria().andUserIdEqualTo(userId).andIsDefaultEqualTo(true);
        List<Address> defaultAddresses = addressMapper.selectByExample(example);
        
        for (Address addr : defaultAddresses) {
            addr.setIsDefault(false);
            addr.setUpdateTime(LocalDateTime.now());
            addressMapper.updateByPrimaryKeySelective(addr);
        }
    }

    /**
     * 将Address实体转换为AddressVO
     * 
     * @param address 地址实体
     * @return 地址VO
     */
    private AddressVO convertToVO(Address address) {
        AddressVO vo = new AddressVO();
        vo.setAddressId(address.getAddressId());
        vo.setUserId(address.getUserId());
        vo.setDetail(address.getDetail());
        vo.setReceiverName(address.getReceiverName());
        vo.setReceiverPhone(address.getReceiverPhone());
        vo.setIsDefault(address.getIsDefault());
        vo.setCreateTime(address.getCreateTime());
        vo.setUpdateTime(address.getUpdateTime());
        return vo;
    }
}

