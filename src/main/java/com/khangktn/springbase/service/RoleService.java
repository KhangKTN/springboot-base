package com.khangktn.springbase.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.khangktn.springbase.dto.request.RoleRequest;
import com.khangktn.springbase.dto.response.RoleResponse;
import com.khangktn.springbase.entity.Permission;
import com.khangktn.springbase.entity.Role;
import com.khangktn.springbase.mapper.RoleMapper;
import com.khangktn.springbase.repository.PermissionRepository;
import com.khangktn.springbase.repository.RoleRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleMapper roleMapper;
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;

    @Transactional(rollbackFor = Exception.class)
    public RoleResponse create(final RoleRequest roleRequest) {
        final Role role = roleMapper.toRole(roleRequest);
        final List<Permission> permissionList = permissionRepository.findAllById(roleRequest.getPermissionSet());
        role.setPermissionSet(new HashSet<>(permissionList));

        final Role roleSave = roleRepository.save(role);
        return roleMapper.toRoleResponse(roleSave);
    }

    public List<RoleResponse> getAll() {
        final List<Role> roleList = roleRepository.findAll();
        return roleList.stream()
                .map(roleMapper::toRoleResponse)
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean delete(final String name) {
        final boolean isExistRole = roleRepository.existsById(name);
        if (!isExistRole) {
            return false;
        }
        roleRepository.deleteById(name);
        return !roleRepository.existsById(name);
    }
}
