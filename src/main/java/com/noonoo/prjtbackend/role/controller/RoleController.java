package com.noonoo.prjtbackend.role.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.noonoo.prjtbackend.role.dto.RoleDto;
import com.noonoo.prjtbackend.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public Map<String, Object> findRoles() {
        System.out.println("=============> findRoles");
        List<RoleDto> items = roleService.findAllActiveRoles();

        Map<String, Object> result = new HashMap<>();
        result.put("items", items);
        result.put("total", items.size());
        return result;
    }
}
