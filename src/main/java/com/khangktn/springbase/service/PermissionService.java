package com.khangktn.springbase.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.khangktn.springbase.dto.request.PermissionListRequest;
import com.khangktn.springbase.dto.request.PermissionRequest;
import com.khangktn.springbase.dto.response.PermissionResponse;
import com.khangktn.springbase.entity.Permission;
import com.khangktn.springbase.mapper.PermissionMapper;
import com.khangktn.springbase.repository.PermissionRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponse create(final PermissionRequest request) {
        final Permission permission = permissionMapper.toPermission(request);
        final Permission permissionSave = permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permissionSave);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<PermissionResponse> bulkInsert(final PermissionListRequest permissionListRequest) {
        final List<Permission> permissionList = permissionListRequest.getPermissionList().stream()
                .map(permissionMapper::toPermission)
                .toList();
        final List<String> permissionIdList = permissionList.stream().map(Permission::getName).toList();
        final List<Permission> permissionInDbList = permissionRepository.findAllById(permissionIdList);
        final List<Permission> permissionListSave = permissionRepository.saveAll(permissionList);
        return permissionListSave.stream()
                .map(permissionMapper::toPermissionResponse)
                .toList();
    }

    public List<PermissionResponse> getAll() {
        final List<Permission> permissionList = permissionRepository.findAll();
        final List<PermissionResponse> permissionResponseList = permissionList.stream()
            .map(permissionMapper::toPermissionResponse)
            .toList();
        return permissionResponseList;
    }

    public boolean delete(final String name) {
        final boolean isExistPermission = permissionRepository.existsById(name);
        if (!isExistPermission) {
            return false;
        }
        permissionRepository.deleteById(name);
        return !permissionRepository.existsById(name);
    }
}
