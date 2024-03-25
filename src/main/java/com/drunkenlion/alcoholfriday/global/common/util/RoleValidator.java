package com.drunkenlion.alcoholfriday.global.common.util;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;

public class RoleValidator {
    public static void validateRole(Member member, MemberRole role) {
        if (!member.getRole().equals(role)) {
            throw new BusinessException(HttpResponse.Fail.FORBIDDEN);
        }
    }

    public static void validateAdminOrSuperVisor(Member member) {
        if (!isAdminOrSuperVisor(member)) {
            throw new BusinessException(HttpResponse.Fail.FORBIDDEN);
        }
    }

    public static void validateAdminOrStoreManager(Member member) {
        if (!isAdminOrStoreManager(member)) {
            throw new BusinessException(HttpResponse.Fail.FORBIDDEN);
        }
    }

    public static void validateAdminOrOwner(Member member) {
        if (!isAdminOrOwner(member)) {
            throw new BusinessException(HttpResponse.Fail.FORBIDDEN);
        }
    }

    private static boolean isAdminOrSuperVisor(Member member) {
        return member.getRole().equals(MemberRole.ADMIN) || member.getRole().equals(MemberRole.SUPER_VISOR);
    }

    private static boolean isAdminOrStoreManager(Member member) {
        return member.getRole().equals(MemberRole.ADMIN) || member.getRole().equals(MemberRole.STORE_MANAGER);
    }

    private static boolean isAdminOrOwner(Member member) {
        return member.getRole().equals(MemberRole.ADMIN) || member.getRole().equals(MemberRole.OWNER);
    }
}
