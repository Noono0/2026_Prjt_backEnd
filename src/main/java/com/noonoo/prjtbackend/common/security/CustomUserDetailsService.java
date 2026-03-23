package com.noonoo.prjtbackend.common.security;

import com.noonoo.prjtbackend.member.dto.MemberDto;
import com.noonoo.prjtbackend.member.mapper.MemberMapper;
import com.noonoo.prjtbackend.member.mapper.MemberRoleMapper;
import com.noonoo.prjtbackend.role.dto.RoleMenuPermissionDto;
import com.noonoo.prjtbackend.role.mapper.RoleMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberMapper memberMapper;
    private final MemberRoleMapper memberRoleMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final AuthorityBuilder authorityBuilder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MemberDto member = memberMapper.findLoginMember(username);

        if (member == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }

        String status = member.getStatusCode() != null ? member.getStatusCode() : "ACTIVE";
        if (!"ACTIVE".equalsIgnoreCase(status)) {
            throw new DisabledException("계정 상태로 로그인할 수 없습니다: " + status);
        }

        List<String> roleCodes = memberRoleMapper.findRoleCodesByMemberSeq(member.getMemberSeq());
        if (roleCodes == null || roleCodes.isEmpty()) {
            roleCodes = List.of("USER");
        }

        List<RoleMenuPermissionDto> permissions =
                roleMenuMapper.findPermissionsByRoleCodes(roleCodes);

        List<SimpleGrantedAuthority> menuAuthorities =
                authorityBuilder.buildAuthoritiesDeduped(permissions);
        List<SimpleGrantedAuthority> systemRoles =
                authorityBuilder.buildSystemRoleAuthorities(roleCodes);
        List<SimpleGrantedAuthority> authorities =
                authorityBuilder.mergeAuthorities(menuAuthorities, systemRoles);

        return new CustomUserDetails(
                member.getMemberSeq(),
                member.getMemberId(),
                member.getMemberPwd(),
                member.getGradeCode(),
                member.getStatusCode(),
                roleCodes,
                authorities
        );
    }
}
