package com.noonoo.prjtbackend.common.security;

import com.noonoo.prjtbackend.member.dto.MemberDto;
import com.noonoo.prjtbackend.member.dto.MemberLoginDto;
import com.noonoo.prjtbackend.member.mapper.MemberMapper;
import com.noonoo.prjtbackend.role.dto.RoleMenuPermissionDto;
import com.noonoo.prjtbackend.role.mapper.RoleMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberMapper memberMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final AuthorityBuilder authorityBuilder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MemberDto member = memberMapper.findLoginMember(username);

        if (member == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }

        List<RoleMenuPermissionDto> permissions =
                roleMenuMapper.findPermissionsByRoleCode(member.getRoleCode());

        return new CustomUserDetails(
                member.getMemberId(),
                member.getLoginId(),
                member.getPassword(),
                member.getRoleCode(),
                authorityBuilder.buildAuthorities(permissions)
        );
    }
}
