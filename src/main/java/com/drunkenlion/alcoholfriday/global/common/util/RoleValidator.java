package com.drunkenlion.alcoholfriday.global.common.util;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;

import java.util.EnumSet;
import java.util.Set;

public class RoleValidator {
    private static boolean hasAnyRole(Member member, Set<MemberRole> roles) {
        return roles.contains(member.getRole());
    }

    public static void validateRole(Member member, MemberRole role) {
        if (!hasAnyRole(member, EnumSet.of(role))) {
            throw new BusinessException(HttpResponse.Fail.FORBIDDEN);
        }
    }

    public static void validateAdminOrSuperVisor(Member member) {
        if (!hasAnyRole(member, EnumSet.of(MemberRole.ADMIN, MemberRole.SUPER_VISOR))) {
            throw new BusinessException(HttpResponse.Fail.FORBIDDEN);
        }
    }

    public static void validateAdminOrStoreManager(Member member) {
        if (!hasAnyRole(member, EnumSet.of(MemberRole.ADMIN, MemberRole.STORE_MANAGER))) {
            throw new BusinessException(HttpResponse.Fail.FORBIDDEN);
        }
    }

    public static void validateAdminOrOwner(Member member) {
        if (!hasAnyRole(member, EnumSet.of(MemberRole.ADMIN, MemberRole.OWNER))) {
            throw new BusinessException(HttpResponse.Fail.FORBIDDEN);
        }
    }

    public static void validateAdminOrStoreManagerOrOwner(Member member) {
        if (!hasAnyRole(member, EnumSet.of(MemberRole.ADMIN, MemberRole.STORE_MANAGER, MemberRole.OWNER))) {
            throw new BusinessException(HttpResponse.Fail.FORBIDDEN);
        }
    }
}
